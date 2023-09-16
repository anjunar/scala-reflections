package com.anjunar.reflections
package core.api.members

import com.anjunar.reflections.core.Utils
import com.anjunar.reflections.core.api.Visitor
import com.anjunar.reflections.core.api.annotations.ResolvedAnnotation

trait ResolvedConstructor extends ResolvedExecutable {

  def newInstance(args : Any*) : Any
  
  lazy val overridden : Array[ResolvedConstructor]

  override lazy val annotations: Array[ResolvedAnnotation] = declaredAnnotations ++ overridden.flatMap(_.declaredAnnotations).distinct

  override def accept(visitor: Visitor): Unit = visitor.visit(this)
  override def toString: String = super.toString + s" def this${Utils.brackets(typeParameters)}(${parameters.mkString(", ")}) [${overridden.length}]"

}
