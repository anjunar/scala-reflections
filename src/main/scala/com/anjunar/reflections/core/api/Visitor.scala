package com.anjunar.reflections
package core.api

import core.api.annotations.ResolvedAnnotation
import core.api.members.*
import core.api.members.ResolvedExecutable.ResolvedParameter
import core.api.nodes.ResolvedNode
import core.api.types.*

import java.util
import scala.collection.mutable

object Visitor {

  val nodeCache = new util.ArrayList[String]

}



trait Visitor {
  

  def visit(node : ResolvedAbstractType) : Unit = {
    node.aliasedType.accept(this)
    node.declaredAnnotations.foreach(annotation => annotation.accept(this))
  }

  def visit(node: ResolvedParameter): Unit = {
    node.declaredType.accept(this)
    node.declaredAnnotations.foreach(annotation => annotation.accept(this))
  }

  def visit(node: ResolvedConstructor): Unit = {
    node.parameters.foreach(param => param.accept(this))
    node.declaredAnnotations.foreach(annotation => annotation.accept(this))
    node.typeParameters.foreach(param => param.accept(this))
  }

  def visit(node: ResolvedField): Unit = {
    node.declaredType.accept(this)
    node.declaredAnnotations.foreach(annotation => annotation.accept(this))
  }

  def visit(node: ResolvedMethod): Unit = {
    node.parameters.foreach(param => param.accept(this))
    node.typeParameters.foreach(param => param.accept(this))
    node.returnType.accept(this)
    node.declaredAnnotations.foreach(annotation => annotation.accept(this))
  }

  def visit(node: ResolvedAndType): Unit = {
    node.lhs.accept(this)
    node.rhs.accept(this)
  }

  def visit(node: ResolvedBoundsType): Unit = {
    if node.low != null then
      node.low.accept(this)

    if node.high != null then
      node.high.accept(this)
  }

  def visit(node: ResolvedClass): Unit = {
    val fullName = node.fullName
    if (! Visitor.nodeCache.contains(fullName)) {
      Visitor.nodeCache.add(fullName)
      node.declaredTypes.foreach(elem => elem.accept(this))
      node.declaredFields.foreach(elem => elem.accept(this))
      node.declaredMethods.foreach(elem => elem.accept(this))
      node.declaredConstructors.foreach(elem => elem.accept(this))
      node.parents.foreach(elem => elem.accept(this))
      node.declaredAnnotations.foreach(annotation => annotation.accept(this))
      node.declaredClasses.foreach(clazz => clazz.accept(this))
      if (node.superClass != null) then node.superClass.accept(this)
    }
  }

  def visit(node: ResolvedPrimitiveType): Unit = {}


  def visit(node: ResolvedGenericArrayType): Unit = {
    node.declaredType.accept(this)
  }

  def visit(node: ResolvedOrType): Unit = {
    node.lhs.accept(this)
    node.rhs.accept(this)
  }

  def visit(node: ResolvedParameterizedType): Unit = {
    node.declaredType.accept(this)
//    node.typeArguments.foreach(elem => elem.accept(this))
  }

  def visit(node: ResolvedTypeVariable): Unit = {}

  def visit(node: ResolvedTypeVariableWithBounds): Unit = {
    node.bounds.accept(this)
    node.declaredAnnotations.foreach(annotation => annotation.accept(this))
  }

  def visit(node: ResolvedWildcardType): Unit = {
    if node.low != null then
      node.low.accept(this)

    if node.high != null then
      node.high.accept(this)
  }

  def visit(node : ResolvedAnnotation) : Unit = {
    node.declaredType.accept(this)
  }

}
