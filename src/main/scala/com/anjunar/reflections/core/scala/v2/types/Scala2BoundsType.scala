package com.anjunar.reflections
package core.scala.v2.types

import com.anjunar.reflections.core.api.nodes.ResolvedNode
import com.anjunar.reflections.core.api.types.{ResolvedBoundsType, ResolvedType}
import tastyquery.Contexts

import scala.reflect.runtime.universe.TypeBounds

class Scala2BoundsType(underlying: TypeBounds, owner : ResolvedNode)(using context: Contexts.Context) extends Scala2Type(underlying, owner) with ResolvedBoundsType {
  
  override lazy val high: ResolvedType = Scala2TypeResolver.resolve[ResolvedType](underlying.hi, this)
  
  override lazy val low: ResolvedType = Scala2TypeResolver.resolve[ResolvedType](underlying.lo, this)
  
}
