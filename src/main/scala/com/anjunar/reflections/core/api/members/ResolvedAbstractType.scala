package com.anjunar.reflections
package core.api.members

import core.api.Visitor
import core.api.annotations.ResolvedAnnotation
import core.api.types.ResolvedType

trait ResolvedAbstractType extends ResolvedMember {

  val name: String

  lazy val aliasedType: ResolvedType

  override lazy val annotations: Array[ResolvedAnnotation] = Array()

  override def accept(visitor: Visitor): Unit = visitor.visit(this)

  override def toString = super.toString + s" type $name"
}
