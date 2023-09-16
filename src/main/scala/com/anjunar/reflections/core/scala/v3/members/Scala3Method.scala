package com.anjunar.reflections
package core.scala.v3.members

import com.anjunar.reflections.PathResolver
import com.anjunar.reflections.core.api.members.ResolvedMethod
import com.anjunar.reflections.core.api.nodes.ResolvedNode
import com.anjunar.reflections.core.api.types.{ResolvedClass, ResolvedParameterizedType, ResolvedType}
import tastyquery.Contexts
import tastyquery.Symbols.TermSymbol

class Scala3Method(underlying: TermSymbol, owner : ResolvedNode)(using context: Contexts.Context) extends Scala3Executable(underlying, owner) with ResolvedMethod {

  private lazy val javaMethod = {
    val clazz = PathResolver.scala3ToJava(underlying.owner.asType, owner.asInstanceOf[ResolvedClass].isModule)
    val array = parameters.map(parameter => {

      def recursion(resolvedType: ResolvedType): Class[_] = resolvedType match {
        case resolvedClass: ResolvedClass => PathResolver.scala3ToJava(resolvedClass.fullName, resolvedClass.isModule)
        case parameterizedType: ResolvedParameterizedType => recursion(parameterizedType.declaredType)
      }

      recursion(parameter.declaredType)
    })
    clazz.getMethod(name, array: _*)
  }
  override def invoke(instance: AnyRef, args: Any*): Any = javaMethod.invoke(instance, args : _*)
  override def invokeStatic(args: Any*): Any = invoke(null, args : _*)

  override lazy val overridden: Array[ResolvedMethod] = underlying
    .allOverriddenSymbols
    .map(symbol => Scala3MemberResolver.resolve[ResolvedMethod](symbol, owner))
    .toArray

}
