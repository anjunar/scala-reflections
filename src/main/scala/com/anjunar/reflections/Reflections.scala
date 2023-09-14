package com.anjunar.reflections

import com.anjunar.reflections.core.api.Visitor
import com.anjunar.reflections.core.api.types.ResolvedClass
import tastyquery.Classpaths.Classpath
import tastyquery.{Classpaths, Contexts}

import java.io.{ByteArrayOutputStream, InputStream}
import java.lang.reflect.{GenericArrayType, ParameterizedType, Type, TypeVariable, WildcardType}
import scala.collection.mutable
import scala.reflect.runtime.universe
import scala.reflect.runtime.universe.runtimeMirror

object Reflections {

  val mirror: universe.Mirror = runtimeMirror(ClassLoader.getSystemClassLoader)

  private val typesCache = new mutable.HashSet[Type]()

  private val classCache = new mutable.HashSet[Class[_]]()

  private def traverse(types: Type): Unit = types match
    case clazz: Class[_] if !clazz.isArray && !clazz.isPrimitive =>
      if (!classCache.contains(clazz)) {
        classCache.add(clazz)
        clazz.getAnnotations.foreach(annotation => traverse(annotation.annotationType()))

        clazz.getDeclaredFields.foreach(field => {
          field.getAnnotations.foreach(annotation => traverse(annotation.annotationType()))
          traverse(field.getGenericType)
        })
        clazz.getDeclaredConstructors.foreach(constructor => {
          constructor.getAnnotations.foreach(annotation => traverse(annotation.annotationType()))
          constructor.getGenericParameterTypes.foreach(param => traverse(param))
          constructor.getParameterAnnotations.foreach(list => list.foreach(param => traverse(param.annotationType())))
          constructor.getTypeParameters.foreach(param => traverse(param))
        })
        clazz.getDeclaredMethods.foreach(method => {
          method.getAnnotations.foreach(annotation => traverse(annotation.annotationType()))
          method.getGenericParameterTypes.foreach(param => traverse(param))
          method.getTypeParameters.foreach(param => traverse(param))
          method.getParameterAnnotations.foreach(list => list.foreach(param => traverse(param.annotationType())))
          traverse(method.getGenericReturnType)
        })
        clazz.getGenericInterfaces.foreach(interface => traverse(interface))
        clazz.getDeclaredClasses.foreach(clazz => {
          traverse(clazz)
        })
        traverse(clazz.getGenericSuperclass)
      }
    case clazz: Class[_] => {} // Todo : Array or Primitive here
    case parameterized: ParameterizedType =>
      if (!typesCache.contains(parameterized)) {
        typesCache.add(parameterized)
        traverse(parameterized.getRawType)
        parameterized.getActualTypeArguments.foreach(arg => traverse(arg))
      }
    case genericArrayType: GenericArrayType =>
      if (!typesCache.contains(genericArrayType)) {
        typesCache.add(genericArrayType)
        traverse(genericArrayType.getGenericComponentType)
      }
    case typeVariable: TypeVariable[_] =>
      if (!typesCache.contains(typeVariable)) {
        typesCache.add(typeVariable)
        typeVariable.getAnnotations.foreach(annotation => traverse(annotation.annotationType()))
        try
          typeVariable.getBounds.foreach(bound => traverse(bound))
        catch
          case _ => {} // Todo : Workaround, jvm cannot find class scala.AnyKind
      }
    case wildCardType: WildcardType =>
      if (!typesCache.contains(wildCardType)) {
        typesCache.add(wildCardType)
        wildCardType.getUpperBounds.foreach(bound => traverse(bound))
        wildCardType.getLowerBounds.foreach(bound => traverse(bound))
      }
    case null => {}

  private def inputStreamToByteArray(inputStream: InputStream): IArray[Byte] = {
    val byteArrayOutputStream = new ByteArrayOutputStream
    val buffer = new Array[Byte](4096)

    var bytesRead = inputStream.read(buffer)
    while (bytesRead != -1)
      byteArrayOutputStream.write(buffer, 0, bytesRead)
      bytesRead = inputStream.read(buffer)
    IArray.from(byteArrayOutputStream.toByteArray)
  }

  def init(classes: Iterable[Class[_]]) = {

    classes.foreach(traverse(_))

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
