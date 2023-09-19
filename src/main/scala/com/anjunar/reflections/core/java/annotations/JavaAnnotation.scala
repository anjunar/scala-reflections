package com.anjunar.reflections
package core.java.annotations

import com.anjunar.reflections.core.api.Visitor
import com.anjunar.reflections.core.api.annotations.ResolvedAnnotation
import com.anjunar.reflections.core.api.nodes.ResolvedNode
import com.anjunar.reflections.core.api.types.ResolvedType
import com.anjunar.reflections.core.java.nodes.JavaNode
import com.anjunar.reflections.core.java.types.JavaTypeResolver
import tastyquery.Contexts

import java.lang.annotation.Annotation
import java.lang.reflect
import java.lang.reflect.Modifier

class JavaAnnotation(underlying: Annotation, owner: ResolvedNode)(using context: Contexts.Context) extends JavaNode(underlying, owner) with ResolvedAnnotation {


  override val name: String = underlying.annotationType().getSimpleName
  
  override val fullName: String = underlying.annotationType().getName
  
  override val declaredType: ResolvedType = JavaTypeResolver.resolve[ResolvedType](underlying.annotationType(), this)

  override lazy val fields: Map[String, Object] = underlying
    .getClass
    .getMethods
    .filter(method => method.getParameterCount == 0 && underlying.annotationType().getDeclaredMethods.exists(_.getName == method.getName))
    .map(method => Tuple2(method.getName, method.invoke(underlying)))
    .toMap

  override def accept(visitor: Visitor): Unit = visitor.visit(this)

}
