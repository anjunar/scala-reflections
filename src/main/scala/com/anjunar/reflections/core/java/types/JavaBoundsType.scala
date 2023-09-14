package com.anjunar.reflections
package core.java.types

import core.api.nodes.ResolvedNode
import core.api.types.{ResolvedBoundsType, ResolvedType}

import tastyquery.Contexts

import java.lang.reflect.TypeVariable

class JavaBoundsType(underlying : TypeVariable[_], owner: ResolvedNode)(using context: Contexts.Context) extends JavaType(underlying, owner) with ResolvedBoundsType {

  override lazy val high: ResolvedType = JavaTypeResolver.resolve[ResolvedType](underlying.getBounds.head, this)

  override lazy val low: ResolvedType = null
  
}
