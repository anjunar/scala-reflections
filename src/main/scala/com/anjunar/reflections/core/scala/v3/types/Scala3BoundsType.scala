package com.anjunar.reflections
package core.scala.v3.types

import com.anjunar.reflections.core.api.nodes.ResolvedNode
import com.anjunar.reflections.core.api.types.{ResolvedBoundsType, ResolvedType}
import tastyquery.Contexts
import tastyquery.Types.TypeBounds

class Scala3BoundsType(underlying : TypeBounds, owner : ResolvedNode)(using context: Contexts.Context) extends Scala3Type(underlying, owner) with ResolvedBoundsType {
  
  lazy val high : ResolvedType = Scala3TypeResolver.resolve(underlying.high, this)

  lazy val low : ResolvedType = Scala3TypeResolver.resolve(underlying.low, this)
  
}
