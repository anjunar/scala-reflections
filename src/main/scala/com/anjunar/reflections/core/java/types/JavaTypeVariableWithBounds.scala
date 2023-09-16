package com.anjunar.reflections
package core.java.types

import com.anjunar.reflections.core.api.annotations.ResolvedAnnotation
import com.anjunar.reflections.core.api.nodes.ResolvedNode
import com.anjunar.reflections.core.api.types.{ResolvedBoundsType, ResolvedTypeVariableWithBounds}
import com.anjunar.reflections.core.java.annotations.JavaAnnotation
import tastyquery.Contexts

import java.lang.reflect.TypeVariable

class JavaTypeVariableWithBounds(underlying : TypeVariable[_], owner : ResolvedNode)(using context: Contexts.Context) extends JavaTypeVariable(underlying.getName, owner) with ResolvedTypeVariableWithBounds {

  override lazy val bounds: ResolvedBoundsType = new JavaBoundsType(underlying, this)

  override lazy val declaredAnnotations: Array[ResolvedAnnotation] = underlying
    .getDeclaredAnnotations
    .map(annotation => new JavaAnnotation(annotation, this))


}
