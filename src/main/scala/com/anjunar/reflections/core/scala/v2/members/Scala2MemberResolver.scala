package com.anjunar.reflections
package core.scala.v2.members

import core.api.members.ResolvedMember
import core.api.nodes.ResolvedNode
import core.scala.v2.types.Scala2Class

import tastyquery.Contexts

import scala.reflect.runtime.universe.{MethodSymbol, ModuleSymbol, Symbol, TermSymbol, TypeSymbol}


object Scala2MemberResolver {

  def resolve[R <: ResolvedNode](symbol : Symbol, owner : ResolvedNode)(using context: Contexts.Context)  : R = {
    val result = symbol match {
      case constructorSymbol : MethodSymbol if constructorSymbol.isConstructor => new Scala2Constructor(constructorSymbol, owner)
      case methodSymbol : MethodSymbol => new Scala2Method(methodSymbol, owner)
      case termSymbol : TermSymbol if termSymbol.isVal || termSymbol.isVar  => new Scala2Field(termSymbol, owner)
      case termSymbol : TypeSymbol => new Scala2AbstractType(termSymbol, owner)
      case moduleSymbol : ModuleSymbol => new Scala2Class(moduleSymbol.owner.asClass, owner)
    }
    
    result.asInstanceOf[R]
  }

}
