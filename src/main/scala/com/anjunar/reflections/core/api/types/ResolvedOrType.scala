package com.anjunar.reflections
package core.api.types

import core.api.Visitor

trait ResolvedOrType extends ResolvedType {

  lazy val lhs: ResolvedType

  lazy val rhs: ResolvedType

  override def accept(visitor: Visitor): Unit = visitor.visit(this)

  override def toString = s"$lhs & $rhs"

}
