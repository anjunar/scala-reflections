package com.anjunar.reflections
package core.scala.v3.members

import com.anjunar.reflections.core.api.members.ResolvedMember
import com.anjunar.reflections.core.api.nodes.ResolvedNode
import com.anjunar.reflections.core.scala.v3.types.Scala3Class
import tastyquery.Contexts
import tastyquery.Modifiers.TermSymbolKind
import tastyquery.Modifiers.TermSymbolKind.*
import tastyquery.Names.nme
import tastyquery.Symbols.{ClassSymbol, Symbol, TermSymbol, TypeMemberSymbol}

object Scala3MemberResolver {

  def resolve[R <: ResolvedNode](symbol : Symbol, owner : ResolvedNode)(using context: Contexts.Context) : R = {
    val result = symbol match {
      case symbol: TermSymbol if symbol.name == nme.Constructor && symbol.isMethod => new Scala3Constructor(symbol, owner)
      case symbol: TermSymbol if symbol.kind == Def => new Scala3Method(symbol, owner)
      case symbol: TermSymbol if symbol.kind == Val || symbol.kind == Var || symbol.kind == LazyVal => new Scala3Field(symbol, owner)
      case symbol: TypeMemberSymbol if symbol.isType => new Scala3AbstractType(symbol, owner)
      case symbol: TermSymbol if symbol.kind == TermSymbolKind.Module => new Scala3Class(symbol.moduleClass.get, owner)
      case symbol: ClassSymbol => new Scala3Class(symbol, owner)
    }
    result.asInstanceOf[R]
  }

}
