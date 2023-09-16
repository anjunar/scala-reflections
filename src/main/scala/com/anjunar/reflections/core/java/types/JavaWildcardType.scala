package com.anjunar.reflections
package core.java.types

import com.anjunar.reflections.core.api.nodes.ResolvedNode
import com.anjunar.reflections.core.api.types.{ResolvedParameterizedType, ResolvedType, ResolvedWildcardType}
import tastyquery.Contexts

import java.lang.reflect.{ParameterizedType, WildcardType}

class JavaWildcardType(underlying : WildcardType, owner : ResolvedNode)(using context: Contexts.Context) extends JavaType(underlying, owner) with ResolvedWildcardType {

  override lazy val high: ResolvedType = {
    val upper = underlying.getUpperBounds.headOption.orNull
    if (upper == null) {
      null
    } else {
      JavaTypeResolver.resolve(upper, this)
    }
  }

  override lazy val low: ResolvedType = {
    val lower = underlying.getLowerBounds.headOption.orNull
    if (lower == null) {
      null
    } else {
      JavaTypeResolver.resolve(lower, this)
    }
  }

}
