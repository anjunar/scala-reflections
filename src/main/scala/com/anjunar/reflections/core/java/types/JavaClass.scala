package com.anjunar.reflections
package core.java.types

import core.api.annotations.ResolvedAnnotation
import core.api.members.*
import core.api.nodes.ResolvedNode
import core.api.types.{ResolvedClass, ResolvedType}
import core.java.annotations.JavaAnnotation
import core.java.members.JavaMemberResolver
import javassist.{ClassClassPath, ClassPool}
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
    .map(field => JavaMemberResolver.resolve[ResolvedField](underlying.getField(field.getName), this))

  lazy val declaredAbstractTypes : Array[ResolvedAbstractType] = Array()

  lazy val declaredConstructors: Array[ResolvedConstructor] = underlyingAlternative
    .getDeclaredConstructors
    .map(constructor => JavaMemberResolver.resolve[ResolvedConstructor](underlying.getConstructor(constructor.getParameterTypes.map(param => Class.forName(param.getName)) :_*), this))

  lazy val declaredMethods: Array[ResolvedMethod] = underlyingAlternative
    .getDeclaredMethods
    .filter(method => ! method.getName.startsWith("lambda$"))
    .map(method => JavaMemberResolver.resolve[ResolvedMethod](underlying.getMethod(method.getName, method.getParameterTypes.map(param => Class.forName(param.getName)) :_*), this))

  override lazy val declaredAnnotations: Array[ResolvedAnnotation] = underlying
    .getDeclaredAnnotations
    .map(annotation => new JavaAnnotation(annotation, this))


}
