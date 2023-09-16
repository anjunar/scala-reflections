package com.anjunar.reflections
package core.scala.v3.types

import com.anjunar.reflections.core.api.annotations.ResolvedAnnotation
import com.anjunar.reflections.core.api.members.*
import com.anjunar.reflections.core.api.nodes.ResolvedNode
import com.anjunar.reflections.core.api.types.{ResolvedClass, ResolvedType}
import com.anjunar.reflections.core.scala.v3.annotations.Scala3Annotation
import com.anjunar.reflections.core.scala.v3.members.Scala3MemberResolver
import tastyquery.Modifiers.TermSymbolKind
import tastyquery.Modifiers.TermSymbolKind.*
import tastyquery.Symbols.ClassSymbol
import tastyquery.Types.{AppliedType, LambdaType, TypeMappable, TypeRef}
import tastyquery.{Contexts, Symbols}

class Scala3Class(underlying: ClassSymbol, owner: ResolvedNode)(using context: Contexts.Context) extends Scala3Type(underlying, owner) with ResolvedClass {

  override val name: String = underlying.name.toString

  override val fullName: String = underlying.fullName.toString()

  override val isArray: Boolean = false

  override val isTrait: Boolean = underlying.isTrait

  override val isEnum: Boolean = underlying.isEnum
  
  override val isModule: Boolean = underlying.isModuleClass
  
  override lazy val isAnnotation: Boolean = superClass match {
    case clazz: ResolvedClass => clazz.fullName == "scala.annotation.Annotation" || clazz.fullName == "scala.annotation.StaticAnnotation"
    case _ => false
  }

  override val modifier: ResolvedClass.Modifier = underlying match
    case symbol: ClassSymbol if symbol.isPublic => ResolvedClass.Modifier.Public
    case symbol: ClassSymbol if symbol.isPrivate => ResolvedClass.Modifier.Private
    case symbol: ClassSymbol if !symbol.isPrivate && !symbol.isPublic => ResolvedClass.Modifier.Protected

  override lazy val parents: Array[ResolvedType] = underlying
    .parents
    .drop(1)
    .map(member => Scala3TypeResolver.resolve[ResolvedType](member, this))
    .toArray

  override lazy val superClass: ResolvedType = {
    val clazz = underlying
      .parents
      .headOption
      .orNull

    if clazz == null then null else Scala3TypeResolver.resolve[ResolvedType](clazz, this)
  }

  override lazy val typeParameters: Array[ResolvedType] = underlying
    .typeParams
    .map(param => Scala3TypeResolver.resolve[ResolvedType](param, this))
    .toArray

  override lazy val declaredClasses: Array[ResolvedClass] = underlying
    .declarations
    .filter(member => member.isTerm && member.asTerm.kind == TermSymbolKind.Module)
    .map(declaration => {
      def recursion(typez: TypeMappable): ClassSymbol = typez match {
        case lambdaType: LambdaType => recursion(lambdaType.resultType)
        case appliedType: AppliedType => recursion(appliedType.tycon)
        case typeRef: TypeRef => typeRef.optSymbol.get.asClass
      }
      new Scala3Class(recursion(declaration.asTerm.declaredType), this)
    })
    .toArray


  override lazy val declaredMembers : Array[ResolvedMember] = underlying
    .declarations
    .filter(member => member.isType || (member.isTerm && member.asTerm.kind != TermSymbolKind.Module))
    .map(declaration => Scala3MemberResolver.resolve[ResolvedMember](declaration, this))
    .toArray
    .sorted((lhs, rhs) => lhs.sourcePosition.compareTo(rhs.sourcePosition))

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
    .map(annotation => new Scala3Annotation(annotation, this))
    .toArray

}
