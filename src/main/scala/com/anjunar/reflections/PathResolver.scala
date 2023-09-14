package com.anjunar.reflections

import com.anjunar.reflections.Reflections
import tastyquery.Symbols.{TypeMemberSymbol, TypeSymbol}
import tastyquery.Types.{AppliedType, LambdaType, TypeMappable, TypeRef}
import tastyquery.{Contexts, Symbols}

import scala.annotation.tailrec
import scala.reflect.runtime.universe

object PathResolver {
  def scala2ToScala3(symbol: universe.TypeSymbol)(using context: Contexts.Context): Symbols.ClassSymbol = {
    val fullName = symbol.owner.fullName + "." + symbol.name.decoded
    val segments = fullName.split("\\.")
    val packageSymbol = context.findPackage(segments.head)
    scala2ToScala3(packageSymbol, segments.drop(1))
  }

  @tailrec
  def scala2ToScala3(symbol: Symbols.Symbol, segments: Array[String])(using context: Contexts.Context): Symbols.ClassSymbol = symbol match {
    case packageSymbol: Symbols.PackageSymbol =>
      val option = packageSymbol.declarations.find(decl => decl.name.decode.toString == segments.head)
      if (option.isEmpty) {
        throw new IllegalStateException("Not found")
      } else {
        PathResolver.scala2ToScala3(option.get, segments.drop(1))
      }
    case classSymbol: Symbols.ClassSymbol if segments.isEmpty => classSymbol
    case classSymbol: Symbols.ClassSymbol =>
      val option = classSymbol.declarations.find(decl => decl.name.toString == segments.head)
      if (option.isEmpty) {
        val option = classSymbol.companionClass.get.declarations.find(decl => decl.name.toString == segments.head)
        if (option.isEmpty) {
          throw new IllegalStateException("Not found")
        } else {
          PathResolver.scala2ToScala3(option.get, segments.drop(1))
        }
      } else {
        PathResolver.scala2ToScala3(option.get, segments.drop(1))
      }
    case termSymbol: Symbols.TermSymbol =>
      val moduleClass = termSymbol.moduleClass
      if (moduleClass.isEmpty) {
        def recursion(typez: TypeMappable): TypeSymbol = typez match {
          case lambdaType: LambdaType => recursion(lambdaType.resultType)
          case appliedType: AppliedType => recursion(appliedType.tycon)
          case typeRef: TypeRef => typeRef.optSymbol.get
        }

        val value = recursion(termSymbol.declaredType)
        PathResolver.scala2ToScala3(value.asClass, segments)
      } else {
        PathResolver.scala2ToScala3(moduleClass.get.asClass, segments)
      }
    // Todo : Workaround, is Nothing referenced right?
    case typeMemberSymbol: TypeMemberSymbol if typeMemberSymbol.fullName.toString() == "scala.Nothing" => context.findStaticClass("scala.runtime.Nothing$")
  }

  def scala3ToScala2(symbol: TypeSymbol): universe.Symbol = {
    val fullName = symbol.fullName.toString()
    val segments = fullName.split("\\.")
    scala3ToScala2(Reflections.mirror.staticPackage(segments.head), segments.drop(1))
  }

  def scala3ToScala2(symbol: universe.Symbol, segments: Array[String]): universe.Symbol = symbol match {
    case classSymbol: universe.ClassSymbol if segments.isEmpty => classSymbol
    case typeSymbol: universe.TypeSymbol if segments.isEmpty => typeSymbol
    case packageSymbol: universe.ModuleSymbol if segments.isEmpty => packageSymbol
    case packageSymbol: universe.ModuleSymbol =>
      val option = packageSymbol.typeSignature.decls.find(decl => decl.name.decoded == segments.head)
      if (option.isEmpty) {
        // Todo : Workaround: This Block is due to the fact, that classloading with scala reflection is buggy
        val value = s"${symbol.fullName}.${segments.head}"
        try
          PathResolver.scala3ToScala2(Reflections.mirror.staticPackage(PathResolver.convert(value)), segments.drop(1))
        catch
          case _ => PathResolver.scala3ToScala2(Reflections.mirror.staticClass(PathResolver.convert(value)), segments.drop(1))
      } else {
        PathResolver.scala3ToScala2(option.get, segments.drop(1))
      }
    case classSymbol: universe.ClassSymbol =>
      val option = classSymbol.typeSignature.decls.find(decl => decl.name.toString == segments.head)
      if (option.isEmpty) {
        val option = classSymbol.companion.typeSignature.decls.find(decl => decl.name.toString == segments.head)
        if (option.isEmpty) {
          throw new IllegalStateException("Not found")
        } else {
          PathResolver.scala3ToScala2(option.get, segments.drop(1))
        }
      } else {
        PathResolver.scala3ToScala2(option.get, segments.drop(1))
      }
    case methodSymbol: universe.MethodSymbol =>
      PathResolver.scala3ToScala2(methodSymbol.returnType.typeSymbol, segments)
  }

  def scala3ToJava(symbol: TypeSymbol): Class[_] = {
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
