package com.anjunar.reflections
package core.api.annotations

import core.api.nodes.ResolvedNode

trait ResolvedAnnotated extends ResolvedNode {
  
  lazy val declaredAnnotations : Array[ResolvedAnnotation]
  def declaredAnnotation(clazz : Class[_]) : Option[ResolvedAnnotation] = declaredAnnotations.find(_.fullName == clazz.getName)

  lazy val annotations: Array[ResolvedAnnotation]
  def annotations(clazz : Class[_]) : Array[ResolvedAnnotation] = annotations.filter(_.fullName == clazz.getName)

}
