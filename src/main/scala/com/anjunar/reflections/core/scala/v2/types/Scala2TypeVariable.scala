package com.anjunar.reflections
package core.scala.v2.types

import core.api.nodes.ResolvedNode
import core.api.types.ResolvedTypeVariable

import tastyquery.Contexts

class Scala2TypeVariable(val name : String, owner : ResolvedNode)(using context: Contexts.Context) extends Scala2Type(name, owner) with ResolvedTypeVariable {
  
  
}
