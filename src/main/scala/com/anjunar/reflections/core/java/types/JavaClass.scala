package com.anjunar.reflections
package core.java.types

import com.anjunar.reflections.core.api.annotations.ResolvedAnnotation
import com.anjunar.reflections.core.api.members.*
import com.anjunar.reflections.core.api.nodes.ResolvedNode
import com.anjunar.reflections.core.api.types.{ResolvedClass, ResolvedType}
import com.anjunar.reflections.core.java.annotations.JavaAnnotation
import com.anjunar.reflections.core.java.members.JavaMemberResolver
import javassist.{ClassClassPath, ClassPool, CtClass}
import tastyquery.Contexts

import java.lang.reflect.Modifier

class JavaClass(override val underlying : Class[_], owner : ResolvedNode)(using context: Contexts.Context) extends JavaType(underlying, owner) with ResolvedClass {

  private val underlyingAlternative = {
    val default = ClassPool.getDefault
    default.insertClassPath(new ClassClassPath(underlying));
    default.get(underlying.getName)
  }

  override val name: String = underlying.getSimpleName

  override val fullName: String = underlying.getName
  
  override val isArray: Boolean = underlying.isArray

  override val isTrait: Boolean = underlying.isInterface

  override val isEnum: Boolean = underlying.isEnum
  
  override val isModule: Boolean = false
  
  override lazy val isAnnotation: Boolean = underlying.isAnnotation

  override val modifier: ResolvedClass.Modifier =
    if Modifier.isPublic(underlying.getModifiers) then ResolvedClass.Modifier.Public else
      if Modifier.isPrivate(underlying.getModifiers) then ResolvedClass.Modifier.Private else
        if Modifier.isProtected(underlying.getModifiers) then ResolvedClass.Modifier.Protected else
          ResolvedClass.Modifier.Protected

  lazy val parents: Array[ResolvedType] = underlying
    .getGenericInterfaces
    .map(parent => JavaTypeResolver.resolve[ResolvedType](parent, this))

  lazy val superClass: ResolvedType = {
    if (underlying.getGenericSuperclass == null) {
      null
    } else {
      JavaTypeResolver.resolve[ResolvedType](underlying.getGenericSuperclass, this)
    }
  }

  override lazy val typeParameters: Array[ResolvedType] = underlying
    .getTypeParameters
    .map(parameter => JavaTypeResolver.resolve[ResolvedType](parameter, this))


  override lazy val declaredMembers: Array[ResolvedMember] = declaredFields ++ declaredConstructors ++ declaredMethods

  lazy val declaredClasses : Array[ResolvedClass] = underlying
    .getDeclaredClasses
    .map(clazz => new JavaClass(clazz, this))

  lazy val declaredFields: Array[ResolvedField] = underlyingAlternative
    .getDeclaredFields
    .map(field => try
      JavaMemberResolver.resolve[ResolvedField](underlying.getDeclaredField(field.getName), this)
    catch
      case _ => {
        print("")
        null
      }
    )

  lazy val declaredAbstractTypes : Array[ResolvedAbstractType] = Array()

  lazy val declaredConstructors: Array[ResolvedConstructor] = underlyingAlternative
    .getDeclaredConstructors
    .map(constructor => {
      val parameters = constructor.getParameterTypes.map(param => toJavaClass(param))
      JavaMemberResolver.resolve[ResolvedConstructor](underlying.getDeclaredConstructor(parameters: _*), this)
    })

  lazy val declaredMethods: Array[ResolvedMethod] = underlyingAlternative
    .getDeclaredMethods
    .filter(method => ! method.getName.startsWith("lambda$") && method.getLongName != "sun.misc.Unsafe.getUnsafe()")
    .map(method => {
      val parameters = method.getParameterTypes.map(param => toJavaClass(param))
      JavaMemberResolver.resolve[ResolvedMethod](underlying.getDeclaredMethod(method.getName, parameters: _*), this)
    })

  private def toJavaClass(param: CtClass) : Class[_] = param.getName match {
    case "short" => classOf[Short]
    case "long" => classOf[Long]
    case "int" => classOf[Int]
    case "char" => classOf[Char]
    case "float" => classOf[Float]
    case "double" => classOf[Double]
    case "byte" => classOf[Byte]
    case "boolean" => classOf[Boolean]

    case _ =>
      if (param.isArray) {
        val name = array(param)
        Class.forName(name, false, ClassLoader.getSystemClassLoader)
      } else {
        Class.forName(param.getName, false, ClassLoader.getSystemClassLoader)
      }
  }

  private def array(componentType: CtClass) : String = {
    if (componentType.isPrimitive) {
      val primitive = componentType.getName match
        case "short" => "S"
        case "long" => "J"
        case "int" => "I"
        case "char" => "C"
        case "float" => "F"
        case "double" => "D"
        case "byte" => "B"
        case "boolean" => "Z"
      primitive
    } else if (componentType.isArray) {
      "[" + array(componentType.getComponentType)
    } else {
      "L" + componentType.getName + ";"
    }
  }

  override lazy val declaredAnnotations: Array[ResolvedAnnotation] = underlying
    .getDeclaredAnnotations
    .map(annotation => new JavaAnnotation(annotation, this))


}
