package com.anjunar.reflections
package core.scala.v3.members

import com.anjunar.reflections.PathResolver
import core.api.members.ResolvedConstructor
import core.api.nodes.ResolvedNode
import core.api.types.{ResolvedClass, ResolvedParameterizedType, ResolvedType}
import tastyquery.Contexts
import tastyquery.Symbols.TermSymbol

class Scala3Constructor(underlying: TermSymbol, owner : ResolvedNode)(using context: Contexts.Context) extends Scala3Executable(underlying, owner) with ResolvedConstructor {

  private lazy val javaConstructor = {
    val clazz = PathResolver.scala3ToJava(underlying.owner.asType)
    val array = parameters.map(parameter => {

      def recursion(resolvedType: ResolvedType): Class[_] = resolvedType match {
        case resolvedClass: ResolvedClass => PathResolver.scala3ToJava(resolvedClass.fullName)
        case parameterizedType: ResolvedParameterizedType => recursion(parameterizedType.declaredType)
      }

      recursion(parameter.declaredType)
    })
    clazz.getConstructor(array: _*)
  }
  override def invoke(args: Any*): Any = javaConstructor.newInstance(args : _*)

  override lazy val overridden: Array[ResolvedConstructor] = underlying
    .allOverriddenSymbols
    .map(symbol => Scala3MemberResolver.resolve[ResolvedConstructor](symbol, owner))
    .toArray

}