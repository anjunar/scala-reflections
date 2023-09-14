package com.anjunar.reflections
package core.java.types

import core.api.nodes.ResolvedNode
import core.api.types.{ResolvedParameterizedType, ResolvedType}

import tastyquery.Contexts

import java.lang.reflect.ParameterizedType

class JavaParameterizedType(underlying : ParameterizedType, owner : ResolvedNode)(using context: Contexts.Context) extends JavaType(underlying, owner) with ResolvedParameterizedType {

  override lazy val declaredType: ResolvedType = JavaTypeResolver
    .resolve(underlying.getRawType, this)

  override lazy val typeArguments: Array[ResolvedType] = underlying
    .getActualTypeArguments
    .map(param => JavaTypeResolver.resolve(param, this))

}
