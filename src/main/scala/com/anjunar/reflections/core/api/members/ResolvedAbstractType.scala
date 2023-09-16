package com.anjunar.reflections
package core.api.members

import com.anjunar.reflections.core.api.Visitor
import com.anjunar.reflections.core.api.annotations.ResolvedAnnotation
import com.anjunar.reflections.core.api.types.ResolvedType

trait ResolvedAbstractType extends ResolvedMember {

  val name: String

  lazy val aliasedType: ResolvedType

  override lazy val annotations: Array[ResolvedAnnotation] = Array()

  override def accept(visitor: Visitor): Unit = visitor.visit(this)

  override def toString = super.toString + s" type $name"
}
