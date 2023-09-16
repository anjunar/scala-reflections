package com.anjunar.reflections
package core.java.types

import com.anjunar.reflections.core.api.nodes.ResolvedNode
import com.anjunar.reflections.core.api.types.ResolvedTypeVariable
import tastyquery.Contexts

class JavaTypeVariable(val name : String, owner : ResolvedNode)(using context: Contexts.Context) extends JavaType(name, owner) with ResolvedTypeVariable {
}
