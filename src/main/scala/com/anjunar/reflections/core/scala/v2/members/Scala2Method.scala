package com.anjunar.reflections
package core.scala.v2.members

import com.anjunar.reflections.Reflections
import com.anjunar.reflections.core.api.members.ResolvedMethod
import com.anjunar.reflections.core.api.nodes.ResolvedNode
import com.anjunar.reflections.core.api.types.{ResolvedClass, ResolvedType}
import com.anjunar.reflections.core.scala.v2.types.Scala2TypeResolver
import tastyquery.Contexts

import scala.reflect.runtime.universe.MethodSymbol

class Scala2Method(underlying : MethodSymbol, owner : ResolvedNode)(using context: Contexts.Context) extends Scala2Executable(underlying, owner) with ResolvedMethod {
  
  override def invoke(instance: AnyRef, args: Any*): Any = {
    val instanceMirror = Reflections.mirror.reflect(instance)
    val methodMirror = instanceMirror.reflectMethod(underlying)
    methodMirror.apply(args : _*)
  }

  override lazy val overridden: Array[ResolvedMethod] = underlying
    .overrides
    .filter(_.isMethod)
    .map(symbol => Scala2MemberResolver.resolve[ResolvedMethod](symbol, this))
    .toArray
  
  override lazy val returnType: ResolvedType = Scala2TypeResolver.resolve(underlying.returnType, this)


}
