package com.anjunar.reflections
package core.api.members

import core.api.Visitor
import core.api.annotations.ResolvedAnnotation
import core.api.types.ResolvedType

trait ResolvedField extends ResolvedMember {

  val name: String

  val isVal: Boolean

  val isVar: Boolean
  def get(instance : AnyRef) : Any
  def set(instance : AnyRef, arg : Any) : Any

  lazy val declaredType: ResolvedType

  lazy val overridden: Array[ResolvedField]

  override lazy val annotations: Array[ResolvedAnnotation] = declaredAnnotations ++ overridden.flatMap(_.declaredAnnotations).distinct

  override def accept(visitor: Visitor): Unit = visitor.visit(this)

  override def toString: String = super.toString + s" ${if isVal then "val" else "var"} $name : $declaredType [${overridden.length}]"
}
