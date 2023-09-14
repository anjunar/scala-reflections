package com.anjunar.reflections
package core.scala.v3.types

import core.api.nodes.ResolvedNode
import core.api.types.{ResolvedOrType, ResolvedType}

import tastyquery.Contexts
import tastyquery.Types.OrType

class Scala3OrType(underlying : OrType, owner : ResolvedNode)(using context: Contexts.Context) extends Scala3Type(underlying, owner) with ResolvedOrType {

  lazy val lhs: ResolvedType = Scala3TypeResolver.resolve(underlying.first, this)

  lazy val rhs: ResolvedType = Scala3TypeResolver.resolve(underlying.second, this)


}
