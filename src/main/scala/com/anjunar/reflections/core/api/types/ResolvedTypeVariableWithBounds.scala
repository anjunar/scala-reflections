package com.anjunar.reflections
package core.api.types

import com.anjunar.reflections.core.api.Visitor
import com.anjunar.reflections.core.api.annotations.{ResolvedAnnotated, ResolvedAnnotation}

trait ResolvedTypeVariableWithBounds extends ResolvedTypeVariable with ResolvedAnnotated {

  lazy val bounds: ResolvedBoundsType

  override lazy val annotations: Array[ResolvedAnnotation] = declaredAnnotations

  override def accept(visitor: Visitor): Unit = visitor.visit(this)
  override def toString = s"${declaredAnnotations.mkString(" ")}$name$bounds"
}
