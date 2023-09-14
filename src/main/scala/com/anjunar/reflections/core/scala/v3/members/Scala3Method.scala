package com.anjunar.reflections
package core.scala.v3.members

import com.anjunar.reflections.PathResolver
import core.api.members.ResolvedMethod
import core.api.nodes.ResolvedNode
import core.api.types.{ResolvedClass, ResolvedParameterizedType, ResolvedType}
import tastyquery.Contexts
import tastyquery.Symbols.TermSymbol

class Scala3Method(underlying: TermSymbol, owner : ResolvedNode)(using context: Contexts.Context) extends Scala3Executable(underlying, owner) with ResolvedMethod {

  private lazy val javaMethod = {
    val clazz = PathResolver.scala3ToJava(underlying.owner.asType)
    val array = parameters.map(parameter => {

      def recursion(resolvedType: ResolvedType): Class[_] = resolvedType match {
        case resolvedClass: ResolvedClass => PathResolver.scala3ToJava(resolvedClass.fullName)
        case parameterizedType: ResolvedParameterizedType => recursion(parameterizedType.declaredType)
      }

      recursion(parameter.declaredType)
    })
    clazz.getMethod(name, array: _*)
  }
  override def invoke(instance: AnyRef, args: Any*): Any = javaMethod.invoke(instance, args : _*) 

  override lazy val overridden: Array[ResolvedMethod] = underlying
    .allOverriddenSymbols
    .map(symbol => Scala3MemberResolver.resolve[ResolvedMethod](symbol, owner))
    .toArray

}