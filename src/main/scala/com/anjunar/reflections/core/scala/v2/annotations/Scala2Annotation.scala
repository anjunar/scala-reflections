package com.anjunar.reflections
package core.scala.v2.annotations

import core.api.Visitor
import core.api.annotations.ResolvedAnnotation
import core.api.nodes.ResolvedNode
import core.api.types.ResolvedType
import core.scala.v2.nodes.Scala2Node
import core.scala.v2.types.Scala2TypeResolver

import tastyquery.Contexts

import java.lang
import scala.reflect.runtime.universe.{Annotation, Apply, Literal, NamedArg, Select, Tree}

class Scala2Annotation(underlying: Annotation, owner: ResolvedNode)(using context: Contexts.Context) extends Scala2Node(underlying, owner) with ResolvedAnnotation {

  override val name: String = underlying.tree.tpe.typeSymbol.name.toString

  override val fullName: String = underlying.tree.tpe.typeSymbol.fullName

  override val declaredType: ResolvedType = Scala2TypeResolver.resolve[ResolvedType](underlying.tree.tpe, this)

  override val fields: Map[String, Object] = {
    val tree = underlying.tree

    def recursion(tree : Tree) : List[Tuple2[String, Object]] = {
      tree match
        case apply: Apply => apply
          .args
          .filter({
            case namedArg: NamedArg => true
            case _ => false
          })
          .flatMap({
            case namedArg: NamedArg =>
              namedArg.rhs match
                case literal: Literal => List(Tuple2(namedArg.lhs.toString(), literal.value.value.asInstanceOf[Object]))
                case apply: Apply => recursion(apply)
          })
    }

    recursion(tree).toMap
  }

  override def accept(visitor: Visitor): Unit = visitor.visit(this)

}
