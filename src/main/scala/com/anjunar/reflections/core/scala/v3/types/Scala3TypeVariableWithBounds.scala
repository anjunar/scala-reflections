package com.anjunar.reflections
package core.scala.v3.types

import com.anjunar.reflections.core.api.annotations.ResolvedAnnotation
import com.anjunar.reflections.core.api.nodes.ResolvedNode
import com.anjunar.reflections.core.api.types.{ResolvedBoundsType, ResolvedTypeVariableWithBounds}
import com.anjunar.reflections.core.scala.v3.annotations.Scala3Annotation
import tastyquery.Contexts
import tastyquery.Symbols.TypeSymbolWithBounds

class Scala3TypeVariableWithBounds(underlying: TypeSymbolWithBounds, owner : ResolvedNode)(using context: Contexts.Context) extends Scala3TypeVariable(underlying.name.toString, owner) with ResolvedTypeVariableWithBounds {

  lazy val bounds : ResolvedBoundsType = Scala3TypeResolver.resolve(underlying.declaredBounds, this)

  override lazy val declaredAnnotations: Array[ResolvedAnnotation] = underlying
    .annotations
    .map(annotation => new Scala3Annotation(annotation, this))
    .toArray


}
