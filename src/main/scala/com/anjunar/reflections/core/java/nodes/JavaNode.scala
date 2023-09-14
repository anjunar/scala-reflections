package com.anjunar.reflections
package core.java.nodes

import core.api.nodes.ResolvedNode

abstract class JavaNode(val underlying : Object, owner : ResolvedNode) extends ResolvedNode(owner) {

}
