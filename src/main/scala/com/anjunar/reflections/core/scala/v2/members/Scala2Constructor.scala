package com.anjunar.reflections
package core.scala.v2.members

import com.anjunar.reflections.Reflections
import com.anjunar.reflections.core.api.members.ResolvedConstructor
import com.anjunar.reflections.core.api.nodes.ResolvedNode
import com.anjunar.reflections.core.api.types.ResolvedClass
import tastyquery.Contexts

import scala.reflect.runtime.universe.MethodSymbol

class Scala2Constructor(underlying : MethodSymbol, owner : ResolvedNode)(using context: Contexts.Context) extends Scala2Executable(underlying, owner) with ResolvedConstructor{

  override def newInstance(args: Any*): Any = {
    val classMirror = Reflections.mirror.reflectClass(underlying.owner.asClass)
    val methodMirror = classMirror.reflectConstructor(underlying)
    methodMirror.apply(args : _*)
  }

  override lazy val overridden: Array[ResolvedConstructor] = underlying
    .overrides
    .map(symbol => Scala2MemberResolver.resolve[ResolvedConstructor](symbol, this))
    .toArray

}
