package com.anjunar.reflections
package core.api.members

import core.api.Visitor
import core.api.annotations.{ResolvedAnnotated, ResolvedAnnotation}
import core.api.members.ResolvedExecutable.ResolvedParameter
import core.api.nodes.ResolvedNode
import core.api.types.ResolvedType

object ResolvedExecutable {

  trait ResolvedParameter extends ResolvedNode with ResolvedAnnotated {

    val name: String

    lazy val declaredType: ResolvedType

    lazy val annotations: Array[ResolvedAnnotation] = owner match
      case constructor: ResolvedConstructor => declaredAnnotations ++ constructor
        .overridden
        .flatMap(overrides => {
          if overrides.parameters.isEmpty then
            Array[ResolvedAnnotation]()
          else
            overrides.parameters(constructor.parameters.indexOf(this)).declaredAnnotations
        })
        .distinct
      case method: ResolvedMethod => declaredAnnotations ++ method
        .overridden
        .flatMap(overrides => {
          if overrides.parameters.isEmpty then
            Array[ResolvedAnnotation]()
          else
            overrides.parameters(method.parameters.indexOf(this)).declaredAnnotations
        })
        .distinct
    override def accept(visitor: Visitor): Unit = visitor.visit(this)

    override def toString = s"${annotations.mkString("")}$name : $declaredType"
  }

}

trait ResolvedExecutable extends ResolvedMember {

  val name: String

  lazy val parameters: Array[ResolvedParameter]

  lazy val typeParameters: Array[ResolvedType]

}
