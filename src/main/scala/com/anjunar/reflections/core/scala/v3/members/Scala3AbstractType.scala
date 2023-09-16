package com.anjunar.reflections
package core.scala.v3.members

import com.anjunar.reflections.core.api.members.ResolvedAbstractType
import com.anjunar.reflections.core.api.nodes.ResolvedNode
import com.anjunar.reflections.core.api.types.ResolvedType
import com.anjunar.reflections.core.scala.v3.types.Scala3TypeResolver
import tastyquery.Contexts
import tastyquery.Symbols.{TermSymbol, TypeMemberSymbol}

class Scala3AbstractType(underlying: TypeMemberSymbol, owner : ResolvedNode)(using context: Contexts.Context) extends Scala3Member(underlying, owner) with ResolvedAbstractType {

  override val name: String = underlying.name.toString

  override lazy val aliasedType: ResolvedType = Scala3TypeResolver.resolve[ResolvedType](underlying.aliasedType, this)

}
