package com.anjunar.reflections
package core.scala.v3.types

import core.api.nodes.ResolvedNode
import core.api.types.ResolvedTypeVariable

import tastyquery.Contexts

class Scala3TypeVariable(val name : String, owner : ResolvedNode)(using context: Contexts.Context) extends Scala3Type(name, owner) with ResolvedTypeVariable
