package com.anjunar.reflections
package core.scala.v3.types

import com.anjunar.reflections.core.api.nodes.ResolvedNode
import com.anjunar.reflections.core.api.types.ResolvedType
import com.anjunar.reflections.core.scala.v3.nodes.Scala3Node
import tastyquery.Contexts
import tastyquery.Types.Type

abstract class Scala3Type(underlying : Object,owner : ResolvedNode)(using context: Contexts.Context) extends Scala3Node(underlying, owner) with ResolvedType {

}
