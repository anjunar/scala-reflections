package com.anjunar.reflections
package core.api.members

import com.anjunar.reflections.core.Utils
import com.anjunar.reflections.core.api.Visitor
import com.anjunar.reflections.core.api.annotations.ResolvedAnnotation
import com.anjunar.reflections.core.api.types.{ResolvedClass, ResolvedType}

trait ResolvedMethod extends ResolvedExecutable {

  protected var caller : ResolvedClass = _

  lazy val returnType: ResolvedType

  lazy val overridden: Array[ResolvedMethod]

  def invoke(instance : AnyRef, args : Any*) : Any

  def invokeStatic(args : Any*) : Any

  def bind(clazz : ResolvedClass) : ResolvedMethod = {
    caller = clazz
    this
  }

  override lazy val annotations: Array[ResolvedAnnotation] = declaredAnnotations ++ overridden.flatMap(_.declaredAnnotations).distinct

  override def accept(visitor: Visitor): Unit = visitor.visit(this)

  override def toString: String = super.toString + s" def $name${Utils.brackets(typeParameters)}(${parameters.mkString(", ")}) : $returnType [${overridden.length}]"

}
