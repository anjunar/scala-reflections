package com.anjunar.reflections
package beans

import core.api.annotations.ResolvedAnnotation
import core.api.members.{ResolvedField, ResolvedMethod}

class Property(val name : String, getter : ResolvedMethod, setter : ResolvedMethod, field : ResolvedField) {

  def get(instance : AnyRef) : Any = getter.invoke(instance)
  def set(instance: AnyRef, arg : Any) : Unit = setter.invoke(instance, arg)

  def isReadOnly : Boolean = setter == null

  lazy val annotations : Array[ResolvedAnnotation] =
    getter.annotations ++
      (if setter == null then Array[ResolvedAnnotation]() else setter.annotations ++
        (if field == null then Array[ResolvedAnnotation]() else field.annotations))

  override def toString = s"${annotations.mkString(" ")}$name"
}
