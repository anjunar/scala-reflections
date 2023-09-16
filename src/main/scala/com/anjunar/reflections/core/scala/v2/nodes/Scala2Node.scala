package com.anjunar.reflections
package core.scala.v2.nodes

import com.anjunar.reflections.core.api.nodes.ResolvedNode

abstract class Scala2Node(val underlying : Object, owner : ResolvedNode) extends ResolvedNode(owner) {

}
