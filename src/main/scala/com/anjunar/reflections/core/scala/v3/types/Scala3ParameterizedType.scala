package com.anjunar.reflections
package core.scala.v3.types

import com.anjunar.reflections.core.api.nodes.ResolvedNode
import com.anjunar.reflections.core.api.types.{ResolvedParameterizedType, ResolvedType}
import tastyquery.Contexts
import tastyquery.Types.AppliedType

class Scala3ParameterizedType(underlying : AppliedType, owner : ResolvedNode)(using context: Contexts.Context) extends Scala3Type(underlying, owner) with ResolvedParameterizedType {
  
  lazy val declaredType: ResolvedType = Scala3TypeResolver.resolve[ResolvedType](underlying.tycon, this)

  lazy val typeArguments: Array[ResolvedType] = underlying
    .args
    .map(arg => Scala3TypeResolver.resolve[ResolvedType](arg, this))
    .toArray
  
}
