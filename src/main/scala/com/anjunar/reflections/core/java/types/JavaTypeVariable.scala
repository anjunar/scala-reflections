package com.anjunar.reflections
package core.java.types

import core.api.nodes.ResolvedNode
import core.api.types.ResolvedTypeVariable

import tastyquery.Contexts

class JavaTypeVariable(val name : String, owner : ResolvedNode)(using context: Contexts.Context) extends JavaType(name, owner) with ResolvedTypeVariable {
}
