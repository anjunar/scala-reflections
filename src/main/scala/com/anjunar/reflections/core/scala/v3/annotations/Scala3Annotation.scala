package com.anjunar.reflections
package core.scala.v3.annotations

import com.anjunar.reflections.core.Utils
import core.api.Visitor
import core.api.annotations.ResolvedAnnotation
import core.api.nodes.ResolvedNode
import core.api.types.{ResolvedClass, ResolvedType}
import core.scala.v3.nodes.Scala3Node
import core.scala.v3.types.Scala3TypeResolver
import tastyquery.Annotations.Annotation
import tastyquery.Names.TermName
import tastyquery.Trees.*
import tastyquery.{Contexts, Trees}

class Scala3Annotation (underlying : Annotation, owner : ResolvedNode)(using context: Contexts.Context) extends Scala3Node(underlying, owner) with ResolvedAnnotation {

  override val name: String = underlying.symbol.name.toString
  
  override val fullName: String = underlying.symbol.fullName.toString()
  
  override val declaredType: ResolvedClass = Scala3TypeResolver.resolve[ResolvedClass](underlying.symbol, this)

  override val fields: Map[String, Object] = underlying
    .arguments
    .map(Utils.doIndexed((index, arg) => {
      val lhs: String = declaredType.declaredMethods(index).name
      val rhs: Object = arg match
        case literal: Literal => literal.constant.get.asInstanceOf[Object]
        case _ => null
      Tuple2(lhs, rhs)
    }))
    .filter(arg => arg._2 != null)
    .toMap

  override def accept(visitor: Visitor): Unit = visitor.visit(this)

}
