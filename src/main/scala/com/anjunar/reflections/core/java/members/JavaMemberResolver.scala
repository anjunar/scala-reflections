package com.anjunar.reflections
package core.java.members

import core.api.members.ResolvedMember
import core.api.nodes.ResolvedNode

import tastyquery.Contexts

import java.lang.reflect.{Constructor, Field, Member, Method}

object JavaMemberResolver {
  
  def resolve[R <: ResolvedMember](member : Member, owner : ResolvedNode)(using context: Contexts.Context) : R = {
    val result = member match {
      case constructor: Constructor[_] => new JavaConstructor(constructor, owner)
      case field : Field => new JavaField(field, owner)
      case method : Method => new JavaMethod(method, owner)
    }
    result.asInstanceOf[R]
  }
  
}
