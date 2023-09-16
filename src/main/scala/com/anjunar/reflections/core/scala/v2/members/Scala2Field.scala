package com.anjunar.reflections
package core.scala.v2.members

import com.anjunar.reflections.Reflections
import com.anjunar.reflections.core.api.members.ResolvedField
import com.anjunar.reflections.core.api.nodes.ResolvedNode
import com.anjunar.reflections.core.api.types.ResolvedType
import com.anjunar.reflections.core.scala.v2.types.Scala2TypeResolver
import tastyquery.Contexts

import scala.reflect.runtime.universe.TermSymbol

class Scala2Field(underlying : TermSymbol, owner : ResolvedNode)(using context: Contexts.Context) extends Scala2Member(underlying, owner) with ResolvedField {

  override val name: String = underlying.name.toString
  
  override val isVal: Boolean = underlying.isVal
  
  override val isVar: Boolean = ! isVal
  
  override def get(instance: AnyRef): Any = {
    val instanceMirror = Reflections.mirror.reflect(instance)
    val fieldMirror = instanceMirror.reflectField(underlying)
    fieldMirror.get
  }
  
  override def set(instance: AnyRef, arg: Any): Any = {
    val instanceMirror = Reflections.mirror.reflect(instance)
    val fieldMirror = instanceMirror.reflectField(underlying)
    fieldMirror.set(arg)
  }

  override lazy val declaredType: ResolvedType = Scala2TypeResolver.resolve[ResolvedType](underlying.typeSignature, this)

  override lazy val overridden: Array[ResolvedField] = underlying
    .overrides
    .filter(! _.isMethod)
    .map(symbol => Scala2MemberResolver.resolve[ResolvedField](symbol, this))
    .toArray

}
