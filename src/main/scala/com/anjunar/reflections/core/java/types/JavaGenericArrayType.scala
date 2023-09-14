package com.anjunar.reflections
package core.java.types

import core.api.nodes.ResolvedNode
import core.api.types.{ResolvedGenericArrayType, ResolvedType}

import tastyquery.Contexts

import java.lang.reflect.GenericArrayType

class JavaGenericArrayType(underlying : GenericArrayType, owner : ResolvedNode)(using context: Contexts.Context) extends JavaType(underlying, owner) with ResolvedGenericArrayType {

  lazy val declaredType : ResolvedType = JavaTypeResolver.resolve(underlying.getGenericComponentType, this)

}
