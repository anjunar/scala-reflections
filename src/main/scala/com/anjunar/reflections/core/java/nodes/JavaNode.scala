package com.anjunar.reflections
package core.java.nodes

import com.anjunar.reflections.core.api.nodes.ResolvedNode

abstract class JavaNode(val underlying : Object, owner : ResolvedNode) extends ResolvedNode(owner) {

}
