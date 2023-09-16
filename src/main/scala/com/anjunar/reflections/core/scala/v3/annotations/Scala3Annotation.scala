package com.anjunar.reflections
package core.scala.v3.annotations

import com.anjunar.reflections.core.Utils
import com.anjunar.reflections.core.api.Visitor
import com.anjunar.reflections.core.api.annotations.ResolvedAnnotation
import com.anjunar.reflections.core.api.nodes.ResolvedNode
import com.anjunar.reflections.core.api.types.{ResolvedClass, ResolvedType}
import com.anjunar.reflections.core.java.types.JavaClass
import com.anjunar.reflections.core.scala.v3.nodes.Scala3Node
import com.anjunar.reflections.core.scala.v3.types.Scala3TypeResolver
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
      val lhs: String = declaredType match
        case clazz : JavaClass => clazz.declaredMethods(index).name
        case _ => underlying.symbol.declarations(index).name.toString
      val rhs: Object = arg match
        case literal: Literal => literal.constant.get.asInstanceOf[Object]
        case _ => null
      Tuple2(lhs, rhs)
    }))
    .filter(arg => arg._2 != null)
    .toMap

  override def accept(visitor: Visitor): Unit = visitor.visit(this)

}
