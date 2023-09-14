package com.anjunar.reflections
package core.api.members

import core.Utils
import core.api.Visitor
import core.api.annotations.ResolvedAnnotation
import core.api.types.ResolvedType

trait ResolvedMethod extends ResolvedExecutable {

  lazy val returnType: ResolvedType

  lazy val overridden: Array[ResolvedMethod]

  def invoke(instance : AnyRef, args : Any*) : Any

  override lazy val annotations: Array[ResolvedAnnotation] = declaredAnnotations ++ overridden.flatMap(_.declaredAnnotations).distinct

  override def accept(visitor: Visitor): Unit = visitor.visit(this)

  override def toString: String = super.toString + s" def $name${Utils.brackets(typeParameters)}(${parameters.mkString(", ")}) : $returnType [${overridden.length}]"

}
