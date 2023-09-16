package com.anjunar.reflections
package core.java.annotations

import core.api.Visitor
import core.api.annotations.ResolvedAnnotation
import core.api.nodes.ResolvedNode
import core.api.types.ResolvedType
import core.java.nodes.JavaNode
import core.java.types.JavaTypeResolver

import tastyquery.Contexts

import java.lang.annotation.Annotation
import java.lang.reflect.Modifier

class JavaAnnotation(underlying: Annotation, owner: ResolvedNode)(using context: Contexts.Context) extends JavaNode(underlying, owner) with ResolvedAnnotation {


  override val name: String = underlying.annotationType().getSimpleName
  
  override val fullName: String = underlying.annotationType().getName
  
  override val declaredType: ResolvedType = JavaTypeResolver.resolve[ResolvedType](underlying.annotationType(), this)


  override val fields: Map[String, Object] = underlying
    .getClass
    .getDeclaredMethods
    .filter(method => Modifier.isPublic(method.getModifiers) && method.getParameterCount == 0)
    .map(method => Tuple2(method.getName, method.invoke(underlying)))
    .toMap

  override def accept(visitor: Visitor): Unit = visitor.visit(this)

}
