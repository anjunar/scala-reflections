package com.anjunar.reflections
package core.scala.v3.members

import com.anjunar.reflections.PathResolver
import core.api.members.ResolvedField
import core.api.nodes.ResolvedNode
import core.api.types.ResolvedType
import core.scala.v3.types.Scala3TypeResolver
import tastyquery.Contexts
import tastyquery.Symbols.TermSymbol

class Scala3Field(underlying: TermSymbol, owner : ResolvedNode)(using context: Contexts.Context) extends Scala3Member(underlying, owner) with ResolvedField {

  private lazy val javaField = {
    val clazz = PathResolver.scala3ToJava(underlying.owner.asType)
    val field = clazz.getDeclaredField(name)
    field.setAccessible(true)
    field
  }

  val name : String = underlying.name.toString
  
  val isVal : Boolean = underlying.isFinalMember
  
  val isVar : Boolean = ! isVal

  override def get(instance: AnyRef): Any = javaField.get(instance)

  override def set(instance: AnyRef, arg: Any): Any = javaField.set(instance, arg)

  lazy val declaredType : ResolvedType = Scala3TypeResolver.resolve[ResolvedType](underlying.declaredType, this)

  override lazy val overridden: Array[ResolvedField] = underlying
    .allOverriddenSymbols
    .filter(symbol => ! symbol.isMethod) // Todo : Why are there Methods in this List?
    .map(symbol => Scala3MemberResolver.resolve[ResolvedField](symbol, owner))
    .toArray


}
