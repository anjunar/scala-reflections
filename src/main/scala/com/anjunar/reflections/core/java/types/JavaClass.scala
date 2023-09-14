package com.anjunar.reflections
package core.java.types

import core.api.annotations.ResolvedAnnotation
import core.api.members.*
import core.api.nodes.ResolvedNode
import core.api.types.{ResolvedClass, ResolvedType}
import core.java.annotations.JavaAnnotation
import core.java.members.JavaMemberResolver

import tastyquery.Contexts

import java.lang.reflect.Modifier

class JavaClass(override val underlying : Class[_], owner : ResolvedNode)(using context: Contexts.Context) extends JavaType(underlying, owner) with ResolvedClass {

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

  lazy val declaredFields: Array[ResolvedField] = underlying
    .getDeclaredFields
    .map(field => JavaMemberResolver.resolve[ResolvedField](field, this))

  lazy val declaredTypes : Array[ResolvedAbstractType] = Array()

  lazy val declaredConstructors: Array[ResolvedConstructor] = underlying
    .getDeclaredConstructors
    .map(constructor => JavaMemberResolver.resolve[ResolvedConstructor](constructor, this))

  lazy val declaredMethods: Array[ResolvedMethod] = underlying
    .getDeclaredMethods
    .filter(method => ! method.getName.startsWith("lambda$") && ! method.isBridge)
    .map(method => JavaMemberResolver.resolve[ResolvedMethod](method, this))

  override lazy val declaredAnnotations: Array[ResolvedAnnotation] = underlying
    .getDeclaredAnnotations
    .map(annotation => new JavaAnnotation(annotation, this))


}
