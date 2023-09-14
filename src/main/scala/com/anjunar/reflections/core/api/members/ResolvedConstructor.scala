package com.anjunar.reflections
package core.api.members

import core.Utils
import core.api.Visitor
import core.api.annotations.ResolvedAnnotation

trait ResolvedConstructor extends ResolvedExecutable {

  def invoke(args : Any*) : Any
  
  lazy val overridden : Array[ResolvedConstructor]

  override lazy val annotations: Array[ResolvedAnnotation] = declaredAnnotations ++ overridden.flatMap(_.declaredAnnotations).distinct

  override def accept(visitor: Visitor): Unit = visitor.visit(this)
  override def toString: String = super.toString + s" def this${Utils.brackets(typeParameters)}(${parameters.mkString(", ")}) [${overridden.length}]"

}
