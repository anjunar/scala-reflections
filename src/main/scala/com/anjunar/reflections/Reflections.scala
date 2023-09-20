package com.anjunar.reflections

import com.anjunar.reflections.core.api.Visitor
import com.anjunar.reflections.core.api.types.ResolvedClass
import tastyquery.Classpaths.Classpath
import tastyquery.{Classpaths, Contexts}

import java.io.{ByteArrayOutputStream, InputStream}
import java.lang.reflect.{GenericArrayType, ParameterizedType, Type, TypeVariable, WildcardType}
import java.util
import scala.collection.mutable
import scala.jdk.CollectionConverters.*
import scala.reflect.runtime.universe
import scala.reflect.runtime.universe.runtimeMirror

object Reflections {

  val mirror: universe.Mirror = runtimeMirror(ClassLoader.getSystemClassLoader)

  private val symbolCache = new mutable.HashSet[universe.Symbol]()
  private val classCache = new mutable.HashSet[Class[_]]()
  private val blackList = List("scala.runtime.AnnotationDefault", "scala.Any")

  private def traverse(symbol : universe.Symbol) : Unit = symbol match {
    case classSymbol: universe.ClassSymbol =>
      if (!symbolCache.contains(symbol)) {
        symbolCache.add(symbol)
        if (! blackList.contains(classSymbol.fullName) && ! classSymbol.fullName.contains(">")) {
          val clazz = Class.forName(classSymbol.fullName)
          classCache.add(clazz)
        }
        if (classSymbol.companion.isModule) {
          traverse(classSymbol.companion)
        }
        classSymbol.annotations.foreach(annotation => traverse(annotation.tree.tpe.typeSymbol))
        classSymbol.info.members.foreach(member => traverse(member))
        classSymbol.info.typeParams.foreach(param => traverse(param))
        classSymbol.info.resultType match
          case classInfoType: universe.ClassInfoType => classInfoType.parents.foreach(parent => traverse(parent.typeSymbol))
      }
    case moduleSymbol: universe.ModuleSymbol =>
      if (!symbolCache.contains(symbol)) {
        symbolCache.add(symbol)
        val clazz = Class.forName(moduleSymbol.fullName)
        classCache.add(clazz)
        moduleSymbol.annotations.foreach(annotation => traverse(annotation.tree.tpe.typeSymbol))
        moduleSymbol.info.members.foreach(member => traverse(member))
        moduleSymbol.info.typeParams.foreach(param => traverse(param))
        moduleSymbol.info.resultType match
          case classInfoType: universe.ClassInfoType => classInfoType.parents.foreach(parent => traverse(parent.typeSymbol))
          case _ => {}
      }
    case methodSymbol: universe.MethodSymbol =>
      if (!symbolCache.contains(symbol)) {
        symbolCache.add(symbol)
        methodSymbol.annotations.foreach(annotation => traverse(annotation.tree.tpe.typeSymbol))
        methodSymbol.info.typeParams.foreach(param => traverse(param))
        methodSymbol.info.paramLists.foreach(list => list.foreach(param => traverse(param)))
        traverse(methodSymbol.returnType.typeSymbol)
      }
    case termSymbol: universe.TermSymbol =>
      if (!symbolCache.contains(symbol)) {
        symbolCache.add(symbol)
        termSymbol.annotations.foreach(annotation => traverse(annotation.tree.tpe.typeSymbol))
        traverse(termSymbol.info.typeSymbol)
      }
    case abstractTypeSymbol: universe.TypeSymbol =>
      if (!symbolCache.contains(symbol)) {
        symbolCache.add(symbol)
        abstractTypeSymbol.annotations.foreach(annotation => traverse(annotation.tree.tpe.typeSymbol))
        abstractTypeSymbol.typeParams.foreach(param => traverse(param))
      }
  }

  private def inputStreamToByteArray(inputStream: InputStream): IArray[Byte] = {
    val byteArrayOutputStream = new ByteArrayOutputStream
    val buffer = new Array[Byte](4096)

    var bytesRead = inputStream.read(buffer)
    while (bytesRead != -1)
      byteArrayOutputStream.write(buffer, 0, bytesRead)
      bytesRead = inputStream.read(buffer)
    IArray.from(byteArrayOutputStream.toByteArray)
  }

  def init(classes: util.Collection[Class[_]]): TypeResolver = init(classes.asScala)
  
  def init(classes: Iterable[Class[_]]): TypeResolver = {

    classes.foreach(clazz => traverse(mirror.staticClass(clazz.getName)))

    val value = classCache.groupBy(_.getPackage.getName)

    val packageData: Iterable[Classpaths.PackageData] = value.map((packageName: String, classes: mutable.HashSet[Class[_]]) => {
      val classData = new mutable.HashSet[Classpaths.ClassData]()
      classes.foreach(clazz => {
        val path = clazz.getName.replaceAll("\\.", "/") + ".class"
        val resource = ClassLoader.getSystemResource(path)
        if (resource != null) {
          val inputStream = resource.getContent.asInstanceOf[InputStream]
          val className = clazz.getName.replace(clazz.getPackage.getName, "").substring(1)
          classData.add(new Classpaths.ClassData(className, resource.toString, inputStreamToByteArray(inputStream)))
        } else {
          println(path)
        }
      })

      val tasties = new mutable.HashSet[Classpaths.TastyData]()
      classes.foreach(clazz => {
        val path = clazz.getName.replaceAll("\\.", "/") + ".tasty"
        val resource = ClassLoader.getSystemResource(path)
        if (resource != null) {
          val inputStream = resource.getContent.asInstanceOf[InputStream]
          val className = clazz.getName.replace(clazz.getPackage.getName, "").substring(1)
          tasties.add(new Classpaths.TastyData(className, resource.toString, inputStreamToByteArray(inputStream)))
        }
      })

      new Classpaths.PackageData(packageName, IArray.from(classData), IArray.from(tasties))
    })

    val entry = new Classpath.Entry(IArray.from(packageData))
    val classpath = new Classpath(IArray(entry))

    new TypeResolver(classpath)(using Contexts.init(classpath))
  }

  def resolveClasses(classes: Array[ResolvedClass]) = {

    val result = new mutable.HashSet[ResolvedClass]()

    classes.foreach(clazz => {
      result.addAll(resolveClass(clazz))
    })

    result.toArray
  }

  def resolveClass(clazz: ResolvedClass) = {
    val result = new mutable.HashSet[ResolvedClass]()

    clazz.accept(new Visitor {
      override def visit(node: ResolvedClass): Unit = {
        if (!Visitor.nodeCache.contains(node.fullName)) {
          result.add(node)
          super.visit(node)
        }
      }
    })

    result.toArray
  }
}
