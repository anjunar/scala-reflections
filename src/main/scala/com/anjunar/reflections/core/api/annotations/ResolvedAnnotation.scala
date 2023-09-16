package com.anjunar.reflections
package core.api.annotations

import com.anjunar.reflections.core.api.nodes.ResolvedNode
import com.anjunar.reflections.core.api.types.ResolvedType

trait ResolvedAnnotation extends ResolvedNode {

  val name: String
  
  val fullName : String

  val declaredType: ResolvedType

  val fields : Map[String, Object]
  override def toString = s"@$name(${fields.mkString(", ")}) "
}
