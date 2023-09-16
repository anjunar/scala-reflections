package com.anjunar.reflections
package core.api.types

import com.anjunar.reflections.core.api.Visitor

trait ResolvedPrimitiveType extends ResolvedType {

  val name : String
  override def accept(visitor: Visitor): Unit = visitor.visit(this)

  override def toString = s"$name"
}
