package com.anjunar.reflections
package core.scala.v3.members

import com.anjunar.reflections.core.api.annotations.ResolvedAnnotation
import com.anjunar.reflections.core.api.members.ResolvedExecutable
import com.anjunar.reflections.core.api.members.ResolvedExecutable.ResolvedParameter
import com.anjunar.reflections.core.api.nodes.ResolvedNode
import com.anjunar.reflections.core.api.types.ResolvedType
import com.anjunar.reflections.core.scala.v3.annotations.Scala3Annotation
import com.anjunar.reflections.core.scala.v3.nodes.Scala3Node
import com.anjunar.reflections.core.scala.v3.types.Scala3TypeResolver
import tastyquery.Contexts
import tastyquery.Symbols.TermSymbol
import tastyquery.Trees.DefDef

object Scala3Executable {
  
  class Scala3Parameter(underlying : TermSymbol, owner : ResolvedNode)(using context: Contexts.Context) extends Scala3Node(underlying, owner) with ResolvedExecutable.ResolvedParameter {
    
    val name : String = underlying.name.toString

    lazy val declaredType = Scala3TypeResolver.resolve(underlying.declaredType, this)

    override lazy val declaredAnnotations: Array[ResolvedAnnotation] = underlying
      .annotations
      .map(annotation => new Scala3Annotation(annotation, this))
      .toArray

  }
  
}

abstract class Scala3Executable(underlying: TermSymbol, owner : ResolvedNode)(using context: Contexts.Context) extends Scala3Member(underlying, owner) with ResolvedExecutable{

  override val name : String = underlying.name.toString

  override lazy val parameters: Array[ResolvedParameter] = underlying.tree match
    case Some(DefDef(_, paramLists, _, _, _)) =>
      paramLists
        .filter(_.isLeft).flatMap { case Left(valDefs) => valDefs.map(variable => new Scala3Executable.Scala3Parameter(variable.symbol, this)) }
        .toArray
    case None => Array()

  override lazy val typeParameters: Array[ResolvedType] = underlying.tree match
    case Some(DefDef(_, paramLists, _, _, _)) =>
      paramLists
        .filter(_.isRight).flatMap { case Right(typeParams) => typeParams.map(variable => Scala3TypeResolver.resolve[ResolvedType](variable.symbol, this))}
        .toArray
    case None => Array()
    
  lazy val returnType : ResolvedType = Scala3TypeResolver.resolve(underlying.declaredType, this)


}
