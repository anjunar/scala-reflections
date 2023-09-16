package com.anjunar.reflections
package core.java.types

import com.anjunar.reflections.core.api.nodes.ResolvedNode
import com.anjunar.reflections.core.api.types.ResolvedType
import com.anjunar.reflections.core.java.nodes.JavaNode
import tastyquery.Contexts

import java.lang.reflect.Type

abstract class JavaType(underlying : Object, owner : ResolvedNode)(using context: Contexts.Context) extends JavaNode(underlying, owner) with ResolvedType {

}
