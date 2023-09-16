package com.anjunar.reflections
package core.api.members

import com.anjunar.reflections.core.api.annotations.{ResolvedAnnotated, ResolvedAnnotation}
import com.anjunar.reflections.core.api.nodes.ResolvedNode

object ResolvedMember {
  
  enum Modifier(val value : String) {
    case Private extends Modifier("private")
    case Protected extends Modifier("protected")
    case Public extends Modifier("public")
  }
  
}

trait ResolvedMember extends ResolvedNode with ResolvedAnnotated with Comparable[ResolvedMember] {
  
  val modifier : ResolvedMember.Modifier
  
  val sourcePosition : Int

  override def toString : String = s"${annotations.mkString(" ")}${modifier.value}"
}
