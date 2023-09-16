package com.anjunar.reflections
package core.scala.v2.types

import com.anjunar.reflections.core.api.annotations.ResolvedAnnotation
import com.anjunar.reflections.core.api.members.*
import com.anjunar.reflections.core.api.nodes.ResolvedNode
import com.anjunar.reflections.core.api.types.ResolvedClass.Modifier.Public
import com.anjunar.reflections.core.api.types.{ResolvedClass, ResolvedType}
import com.anjunar.reflections.core.scala.v2.annotations.Scala2Annotation
import com.anjunar.reflections.core.scala.v2.members.Scala2MemberResolver
import tastyquery.Contexts

import scala.reflect.runtime.universe.{ClassInfoType, ClassSymbol, ModuleSymbol, PolyType}

class Scala2Class(underlying: ClassSymbol, owner: ResolvedNode)(using context: Contexts.Context) extends Scala2Type(underlying, owner) with ResolvedClass {

  override val name: String = underlying.name.toString

  override val fullName: String = underlying.fullName

  override val isArray: Boolean = false

  override val isTrait: Boolean = underlying.isTrait

  override val isEnum: Boolean = underlying.isJavaEnum

  override lazy val isAnnotation: Boolean = superClass match {
    case clazz: ResolvedClass => clazz.fullName == "scala.annotation.Annotation" || clazz.fullName == "scala.annotation.StaticAnnotation"
    case _ => false
  }

  override val modifier: ResolvedClass.Modifier = underlying match
    case symbol: ClassSymbol if symbol.isPublic => ResolvedClass.Modifier.Public
    case symbol: ClassSymbol if symbol.isPrivate => ResolvedClass.Modifier.Private
    case symbol: ClassSymbol if symbol.isProtected => ResolvedClass.Modifier.Protected
    case _ => Public

  override lazy val parents: Array[ResolvedType] = underlying.typeSignature.resultType match
    case classInfoType : ClassInfoType => classInfoType
      .parents
      .drop(1)
      .map(base => Scala2TypeResolver.resolve[ResolvedType](base, this))
      .toArray
    case _ => Array()

  override lazy val superClass: ResolvedType = underlying.typeSignature match {
    case polyType : PolyType =>
      val base = underlying
        .typeSignature
        .resultType
        .asInstanceOf[ClassInfoType]
        .parents
        .headOption
        .orNull

      if base == null then null else Scala2TypeResolver.resolve[ResolvedType](base, this)
    case _ => null
  }

  override lazy val typeParameters: Array[ResolvedType] = underlying
    .typeParams
    .map(param => Scala2TypeResolver.resolve[ResolvedType](param, this))
    .toArray

  override lazy val declaredClasses: Array[ResolvedClass] = underlying
    .toType
    .decls
    .filter(member => member.isClass)
    .map(member => new Scala2Class(member.asClass, this))
    .toArray

  override lazy val declaredMembers: Array[ResolvedMember] = underlying
    .toType
    .decls
    .filter({
      case moduleSymbol: ModuleSymbol => false
      case _ => true
    })
    .map(member => Scala2MemberResolver.resolve[ResolvedMember](member, this))
    .toArray


  override lazy val declaredFields: Array[ResolvedField] = declaredMembers
    .filter(member => member.isInstanceOf[ResolvedField])
    .map(_.asInstanceOf[ResolvedField])


  override lazy val declaredAbstractTypes: Array[ResolvedAbstractType] = declaredMembers
    .filter(member => member.isInstanceOf[ResolvedAbstractType])
    .map(_.asInstanceOf[ResolvedAbstractType])

  override lazy val declaredConstructors: Array[ResolvedConstructor] = declaredMembers
    .filter(member => member.isInstanceOf[ResolvedConstructor])
    .map(_.asInstanceOf[ResolvedConstructor])

  override lazy val declaredMethods: Array[ResolvedMethod] = declaredMembers
    .filter(member => member.isInstanceOf[ResolvedMethod])
    .map(_.asInstanceOf[ResolvedMethod])

  override lazy val declaredAnnotations: Array[ResolvedAnnotation] = underlying
    .annotations
    .map(annotation => new Scala2Annotation(annotation, this))
    .toArray

}
