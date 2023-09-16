package com.anjunar.reflections
package core.api.types

import com.anjunar.reflections.core.Utils
import com.anjunar.reflections.core.api.Visitor
import com.anjunar.reflections.core.scala.v3.types.Scala3TypeResolver

trait ResolvedWildcardType extends ResolvedType {

  lazy val high: ResolvedType

  lazy val low: ResolvedType

  override def accept(visitor: Visitor): Unit = visitor.visit(this)
  override def toString = s"_${Utils.high(high)}${Utils.low(low)}"
}
