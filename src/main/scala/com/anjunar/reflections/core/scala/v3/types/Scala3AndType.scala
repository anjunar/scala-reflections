package com.anjunar.reflections
package core.scala.v3.types

import com.anjunar.reflections.core.api.nodes.ResolvedNode
import com.anjunar.reflections.core.api.types.{ResolvedAndType, ResolvedType}
import tastyquery.Contexts
import tastyquery.Types.AndType

class Scala3AndType(underlying : AndType, owner : ResolvedNode)(using context: Contexts.Context) extends Scala3Type(underlying, owner) with ResolvedAndType {
  
  lazy val lhs : ResolvedType = Scala3TypeResolver.resolve(underlying.first, this)

  lazy val rhs : ResolvedType = Scala3TypeResolver.resolve(underlying.second, this)
  
}
