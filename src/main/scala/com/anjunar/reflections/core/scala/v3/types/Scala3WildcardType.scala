package com.anjunar.reflections
package core.scala.v3.types

import com.anjunar.reflections.core.api.nodes.ResolvedNode
import com.anjunar.reflections.core.api.types.{ResolvedType, ResolvedWildcardType}
import tastyquery.Contexts
import tastyquery.Types.WildcardTypeArg

class Scala3WildcardType(underlying : WildcardTypeArg, owner : ResolvedNode)(using context: Contexts.Context) extends Scala3Type(underlying, owner) with ResolvedWildcardType {
  
  lazy val high : ResolvedType = Scala3TypeResolver.resolve(underlying.bounds.high, this)

  lazy val low : ResolvedType = Scala3TypeResolver.resolve(underlying.bounds.low, this)
  
}
