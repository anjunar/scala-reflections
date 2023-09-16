package com.anjunar.reflections
package core.scala.v2.members

import com.anjunar.reflections.core.api.annotations.ResolvedAnnotation
import com.anjunar.reflections.core.api.members.ResolvedMember
import com.anjunar.reflections.core.api.nodes.ResolvedNode
import com.anjunar.reflections.core.scala.v2.annotations.Scala2Annotation
import com.anjunar.reflections.core.scala.v2.nodes.Scala2Node
import tastyquery.Contexts

import scala.reflect.runtime.universe.{MethodSymbol, Symbol, TermSymbol}

abstract class Scala2Member(underlying : Symbol, owner : ResolvedNode)(using context: Contexts.Context) extends Scala2Node(underlying, owner) with ResolvedMember {
  override def compareTo(o: ResolvedMember): Int = ???
  
  override val sourcePosition: Int = -1
  
  override val modifier: ResolvedMember.Modifier = underlying match
    case symbol : TermSymbol if symbol.isPrivate => ResolvedMember.Modifier.Private
    case symbol : TermSymbol if symbol.isProtected => ResolvedMember.Modifier.Protected
    case symbol : TermSymbol if symbol.isPublic => ResolvedMember.Modifier.Public
    case _ => ResolvedMember.Modifier.Public

  override lazy val declaredAnnotations: Array[ResolvedAnnotation] = underlying
    .annotations
    .map(annotation => new Scala2Annotation(annotation, this))
    .toArray


}
