package com.anjunar.reflections
package core.java.types

import core.api.nodes.ResolvedNode
import core.api.types.ResolvedPrimitiveType

import tastyquery.Contexts

class JavaPrimitiveType(underlying : Class[_], owner : ResolvedNode)(using context: Contexts.Context) extends JavaType(underlying, owner) with ResolvedPrimitiveType {
  
  val name : String = underlying.getName

}
