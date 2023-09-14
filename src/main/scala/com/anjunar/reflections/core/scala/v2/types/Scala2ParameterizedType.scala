package com.anjunar.reflections
package core.scala.v2.types

import core.api.nodes.ResolvedNode
import core.api.types.{ResolvedParameterizedType, ResolvedType}

import tastyquery.Contexts

import scala.reflect.runtime.universe.AppliedTypeTree

class Scala2ParameterizedType(underlying : AppliedTypeTree, owner : ResolvedNode)(using context: Contexts.Context) extends Scala2Type(underlying, owner) with ResolvedParameterizedType {
  
  override lazy val declaredType: ResolvedType = Scala2TypeResolver.resolve(underlying.tpe, this)
  
  override lazy val typeArguments: Array[ResolvedType] = underlying
    .args
    .map(arg => Scala2TypeResolver.resolve[ResolvedType](arg.tpe, this))
    .toArray
  
}
