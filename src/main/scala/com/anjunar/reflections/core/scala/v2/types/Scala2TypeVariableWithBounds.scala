package com.anjunar.reflections
package core.scala.v2.types

import core.api.annotations.ResolvedAnnotation
import core.api.nodes.ResolvedNode
import core.api.types.{ResolvedBoundsType, ResolvedTypeVariableWithBounds}
import core.scala.v2.annotations.Scala2Annotation

import tastyquery.Contexts

import scala.reflect.runtime.universe.{TypeBounds, TypeSymbol}

class Scala2TypeVariableWithBounds(underlying : TypeSymbol, owner : ResolvedNode)(using context: Contexts.Context) extends Scala2TypeVariable(underlying.name.toString, owner) with ResolvedTypeVariableWithBounds {

  override lazy val bounds: ResolvedBoundsType = Scala2TypeResolver.resolve[ResolvedBoundsType](underlying.typeSignature, this)

  override lazy val declaredAnnotations: Array[ResolvedAnnotation] = underlying
    .annotations
    .map(annotation => new Scala2Annotation(annotation, this))
    .toArray

}
