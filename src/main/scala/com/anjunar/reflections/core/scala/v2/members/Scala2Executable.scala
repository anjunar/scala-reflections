package com.anjunar.reflections
package core.scala.v2.members

import com.anjunar.reflections.core.api.annotations.ResolvedAnnotation
import com.anjunar.reflections.core.api.members.ResolvedExecutable
import com.anjunar.reflections.core.api.members.ResolvedExecutable.ResolvedParameter
import com.anjunar.reflections.core.api.nodes.ResolvedNode
import com.anjunar.reflections.core.api.types.ResolvedType
import com.anjunar.reflections.core.scala.v2.annotations.Scala2Annotation
import com.anjunar.reflections.core.scala.v2.nodes.Scala2Node
import com.anjunar.reflections.core.scala.v2.types.Scala2TypeResolver
import tastyquery.Contexts

import scala.reflect.runtime.universe.{MethodSymbol, Symbol}

object Scala2Executable {
  
  class Scala2Parameter(underlying : Symbol, owner : ResolvedNode)(using context: Contexts.Context) extends Scala2Node(underlying, owner) with ResolvedExecutable.ResolvedParameter {

    override val name : String = underlying.name.toString

    override lazy val declaredType: ResolvedType = Scala2TypeResolver.resolve[ResolvedType](underlying.typeSignature, owner)

    override lazy val declaredAnnotations: Array[ResolvedAnnotation] = underlying
      .annotations
      .map(annotation => new Scala2Annotation(annotation, this))
      .toArray

  }
  
}

abstract class Scala2Executable(underlying : MethodSymbol, owner : ResolvedNode)(using context: Contexts.Context) extends Scala2Member(underlying, owner) with ResolvedExecutable {

  override val name: String = underlying.name.toString

  override lazy val parameters: Array[ResolvedParameter] = underlying
    .paramLists
    .headOption
    .getOrElse(List[Symbol]())
    .map(parameter => new Scala2Executable.Scala2Parameter(parameter, this))
    .toArray

  override lazy val typeParameters: Array[ResolvedType] =
    if (underlying.paramLists.size == 2) {
      List[ResolvedType]().toArray
//      val value : List[ResolvedType] = underlying.paramLists.last.map(parameter => Scala2TypeResolver.resolve[ResolvedType](parameter, this))
//      value.toArray
    }
    else
      List[ResolvedType]().toArray
}
