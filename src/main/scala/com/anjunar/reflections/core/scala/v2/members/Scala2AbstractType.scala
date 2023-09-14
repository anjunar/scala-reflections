package com.anjunar.reflections
package core.scala.v2.members

import core.api.members.ResolvedAbstractType
import core.api.nodes.ResolvedNode
import core.api.types.ResolvedType
import core.scala.v2.types.Scala2TypeResolver

import tastyquery.Contexts

import scala.reflect.runtime.universe.TypeSymbol

class Scala2AbstractType(underlying : TypeSymbol, owner : ResolvedNode)(using context: Contexts.Context) extends Scala2Member(underlying, owner) with ResolvedAbstractType {

  val name : String = underlying.name.toString

  lazy val aliasedType : ResolvedType = Scala2TypeResolver.resolve[ResolvedType](underlying.typeSignature, this)

}
