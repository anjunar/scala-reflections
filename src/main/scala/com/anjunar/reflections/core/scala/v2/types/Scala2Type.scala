package com.anjunar.reflections
package core.scala.v2.types

import core.api.nodes.ResolvedNode
import core.api.types.ResolvedType
import core.scala.v2.nodes.Scala2Node

import tastyquery.Contexts

import scala.reflect.runtime.universe.Symbol

abstract class Scala2Type(underlying : Object, owner : ResolvedNode)(using context: Contexts.Context) extends Scala2Node(underlying, owner) with ResolvedType {

}
