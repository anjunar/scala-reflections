package com.anjunar.reflections
package core.api.types

import core.api.Visitor

trait ResolvedTypeVariable extends ResolvedType {

  val name: String

  override def accept(visitor: Visitor): Unit = visitor.visit(this)
  override def toString = s"$name"
}
