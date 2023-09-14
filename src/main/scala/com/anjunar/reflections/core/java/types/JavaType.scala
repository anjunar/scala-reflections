package com.anjunar.reflections
package core.java.types

import core.api.nodes.ResolvedNode
import core.api.types.ResolvedType
import core.java.nodes.JavaNode

import tastyquery.Contexts

import java.lang.reflect.Type

abstract class JavaType(underlying : Object, owner : ResolvedNode)(using context: Contexts.Context) extends JavaNode(underlying, owner) with ResolvedType {

}
