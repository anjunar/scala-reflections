package com.anjunar.reflections
package core.api.nodes

import com.anjunar.reflections.core.api.Visitor

trait ResolvedNode(val owner : ResolvedNode) {

  val underlying : Object
  def accept(visitor : Visitor) : Unit

  override def hashCode(): Int = underlying.hashCode()

  override def equals(obj: Any): Boolean = obj match {
    case node : ResolvedNode => node.underlying.equals(underlying)
    case _ => false
  }
}
