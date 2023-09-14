package com.anjunar.reflections
package core.api.types

import core.Utils
import core.api.Visitor

trait ResolvedBoundsType extends ResolvedType {

  lazy val high: ResolvedType

  lazy val low: ResolvedType

  override def accept(visitor: Visitor): Unit = visitor.visit(this)
  override def toString = s"${Utils.high(high)}${Utils.low(low)}"
}
