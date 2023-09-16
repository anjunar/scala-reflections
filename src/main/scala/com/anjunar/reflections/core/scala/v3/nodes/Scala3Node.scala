package com.anjunar.reflections
package core.scala.v3.nodes

import com.anjunar.reflections.core.api.nodes.ResolvedNode

abstract class Scala3Node(val underlying : Object, owner : ResolvedNode) extends ResolvedNode(owner) {

}
