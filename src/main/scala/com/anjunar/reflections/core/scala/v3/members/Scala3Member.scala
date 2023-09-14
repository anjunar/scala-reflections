package com.anjunar.reflections
package core.scala.v3.members

import core.api.annotations.ResolvedAnnotation
import core.api.members.ResolvedMember
import core.api.nodes.ResolvedNode
import core.scala.v3.annotations.Scala3Annotation
import core.scala.v3.nodes.Scala3Node

import tastyquery.Contexts
import tastyquery.Symbols.TermOrTypeSymbol
import tastyquery.Trees.{DefDef, TypeMember, ValOrDefDef}

abstract class Scala3Member(underlying : TermOrTypeSymbol, owner : ResolvedNode)(using context: Contexts.Context) extends Scala3Node(underlying, owner) with ResolvedMember {

  override val sourcePosition: Int = underlying.tree.getOrElse(-1) match
    case typeMember: TypeMember =>
      try
        typeMember.pos.startLine
      catch
        case e : UnsupportedOperationException => -1
    case defDef: ValOrDefDef =>
      try
        defDef.pos.startLine
      catch
        case e : UnsupportedOperationException => -1
    case _ => -1

  override def compareTo(o: ResolvedMember): Int = Integer.compare(o.sourcePosition, sourcePosition)

  override val modifier: ResolvedMember.Modifier = underlying match
    case symbol: TermOrTypeSymbol if symbol.isPrivate => ResolvedMember.Modifier.Private
    case symbol: TermOrTypeSymbol if symbol.isPublic => ResolvedMember.Modifier.Public
    case symbol: TermOrTypeSymbol if ! symbol.isPublic && ! symbol.isPrivate => ResolvedMember.Modifier.Protected

  override lazy val declaredAnnotations: Array[ResolvedAnnotation] = underlying
    .annotations
    .map(annotation => new Scala3Annotation(annotation, this))
    .toArray


}
