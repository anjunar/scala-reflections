package com.anjunar.reflections

import com.anjunar.reflections.Reflections
import tastyquery.Symbols.{ClassSymbol, TypeMemberSymbol, TypeSymbol}
import tastyquery.Types.{AppliedType, LambdaType, TypeMappable, TypeRef}
import tastyquery.{Contexts, Symbols}

import scala.annotation.tailrec
import scala.collection.mutable.ArrayBuffer
import scala.reflect.runtime.universe

object PathResolver {
  def scala2ToScala3(symbol: universe.Symbol)(using context: Contexts.Context): Symbols.Symbol = {
    val fullName = symbol.owner.fullName + "." + symbol.name.decoded
    val segments = fullName.split("\\.")
    val packageSymbol = context.findPackage(segments.head)
    val option = scala2ToScala3(packageSymbol, segments.drop(1), symbol.isModuleClass || symbol.isModule)
    option.getOrElse({
      throw IllegalStateException(fullName + " not Found")
    })
  }

  def scala2ToScala3(symbol: Symbols.Symbol, segments: Array[String], isModule : Boolean)(using context: Contexts.Context): Option[Symbols.Symbol] = symbol match {
    case packageSymbol: Symbols.PackageSymbol =>
      packageSymbol
        .declarations
        .filter(decl => decl.name.decode.toString == segments.head && (decl.isClass || decl.isPackage))
        .map(decl => PathResolver.scala2ToScala3(decl, segments.drop(1), isModule))
        .find(_.isInstanceOf[Some[Symbols.Symbol]])
        .flatten
    case classSymbol: Symbols.ClassSymbol if segments.isEmpty =>
      isValidEnd(isModule, classSymbol)
    case classSymbol: Symbols.ClassSymbol =>
      classSymbol
        .declarations
        .filter(decl => decl.name.toString == segments.head)
        .map(decl => PathResolver.scala2ToScala3(decl, segments.drop(1), isModule))
        .find(_.isInstanceOf[Some[Symbols.Symbol]])
        .flatten
    case termSymbol: Symbols.TermSymbol =>
      val moduleClass = termSymbol.moduleClass
      if (moduleClass.isEmpty) {
        def recursion(typez: TypeMappable): TypeSymbol = typez match {
          case lambdaType: LambdaType => recursion(lambdaType.resultType)
          case appliedType: AppliedType => recursion(appliedType.tycon)
          case typeRef: TypeRef => typeRef.optSymbol.get
        }

        val value = recursion(termSymbol.declaredType)
        PathResolver.scala2ToScala3(value, segments, isModule)
      } else {
        PathResolver.scala2ToScala3(moduleClass.get, segments, isModule)
      }
    // Todo : Workaround, is Nothing referenced right?
    case typeMemberSymbol: TypeMemberSymbol if typeMemberSymbol.fullName.toString() == "scala.Nothing" =>
      Some(context.findStaticClass("scala.runtime.Nothing$"))
  }

  private def isValidEnd(isModule: Boolean, classSymbol: ClassSymbol) = {
    if (isModule) {
      if (classSymbol.isModuleClass) {
        Some(classSymbol)
      } else {
        None
      }
    } else {
      if (classSymbol.isModuleClass) {
        None
      } else {
        Some(classSymbol)
      }
    }
  }

  def scala3ToScala2(symbol: ClassSymbol): universe.Symbol = {
    val fullName = symbol.fullName.toString()
    val segments = fullName.split("\\.")
    val option = scala3ToScala2(Reflections.mirror.staticPackage(segments.head), segments.drop(1), symbol.isModuleClass)
    option.getOrElse({
      throw IllegalStateException(fullName + " not Found")
    })
  }

  def scala3ToScala2(symbol: universe.Symbol, segments: Array[String], isModule : Boolean): Option[universe.Symbol] = symbol match {
    case classSymbol: universe.ClassSymbol if segments.isEmpty =>
      isValidEnd(isModule, classSymbol)
    case typeSymbol: universe.TypeSymbol if segments.isEmpty =>
      isValidEnd(isModule, typeSymbol)
    case moduleSymbol: universe.ModuleSymbol if segments.isEmpty =>
      isValidEnd(isModule, moduleSymbol)
    case packageSymbol: universe.ModuleSymbol =>
      val option = packageSymbol.typeSignature.decls.filter(decl => decl.name.decoded == segments.head)
      if (option.isEmpty) {
        // Todo : Workaround: This Block is due to the fact, that classloading with scala reflection is buggy
        val value = s"${symbol.fullName}.${segments.head}"
        val firstModuleSymbol = ArrayBuffer[universe.Symbol]()
        try {
          firstModuleSymbol.addOne(Reflections.mirror.staticPackage(PathResolver.convert(value)))
        }
        catch
          case _ =>
            try
              firstModuleSymbol.addOne(Reflections.mirror.staticModule(PathResolver.convert(value)))
            catch
              case _ =>
            try
              firstModuleSymbol.addOne(Reflections.mirror.staticClass(PathResolver.convert(value)))
            catch
              case _ =>
        firstModuleSymbol
          .map(decl => PathResolver.scala3ToScala2(decl, segments.drop(1), isModule))
          .find(_.isInstanceOf[Some[universe.Symbol]])
          .flatten
      } else {
        option
          .map(decl => PathResolver.scala3ToScala2(decl, segments.drop(1), isModule))
          .find(_.isInstanceOf[Some[universe.Symbol]])
          .flatten
      }
    case classSymbol: universe.ClassSymbol =>
      val option = classSymbol.typeSignature.decls.find(decl => decl.name.toString == segments.head)
      if (option.isEmpty) {
        val option = classSymbol.companion.typeSignature.decls.find(decl => decl.name.toString == segments.head)
        if (option.isEmpty) {
          throw new IllegalStateException("Not found")
        } else {
          PathResolver.scala3ToScala2(option.get, segments.drop(1), isModule)
        }
      } else {
        PathResolver.scala3ToScala2(option.get, segments.drop(1), isModule)
      }
    case methodSymbol: universe.MethodSymbol =>
      PathResolver.scala3ToScala2(methodSymbol.returnType.typeSymbol, segments, isModule)
  }

  private def isValidEnd(isModule: Boolean, classSymbol: universe.Symbol) = {
    if (isModule) {
      if (classSymbol.isModuleClass || classSymbol.isModule) {
        Some(classSymbol)
      } else {
        None
      }
    } else {
      if (classSymbol.isModuleClass || classSymbol.isModule) {
        None
      } else {
        Some(classSymbol)
      }
    }
  }

  def scala3ToJava(symbol: ClassSymbol): Class[_] = {
    val fullName = symbol.fullName.toString()
    scala3ToJava(fullName)
  }

  def scala3ToJava(fullName: String): Class[_] = {
    val segments = fullName.split("\\.")
    val packages = segments.filter(segment => segment.matches("^[a-z]\\w+"))
    val firstClass = {
      if (packages.length == segments.length) {
        segments
      } else {
        packages ++ Array(segments(packages.length))
      }
    }
    val classSymbol = scala3ToJava(Class.forName(firstClass.mkString("."), false, getClass.getClassLoader), segments.drop(firstClass.length))
    classSymbol
  }

  def scala3ToJava(value: Class[_], segments: Array[String]): Class[_] = {
    if (segments.isEmpty) {
      value
    } else {
      val option = value.getDeclaredClasses.find(clazz => clazz.getSimpleName == segments.head)
      if (option.isEmpty) {
        throw new IllegalStateException("Not found")
      } else {
        scala3ToJava(option.get, segments.drop(1))
      }
    }
  }

  def convert(value: String): String = {
    value
      .replace("=", "$eq")
      .replace(":", "$colon")
      .replace("<", "$less")
  }

}
