package com.anjunar.reflections
package core.java.members

import com.anjunar.reflections.core.api.annotations.ResolvedAnnotation
import com.anjunar.reflections.core.api.members.ResolvedExecutable
import com.anjunar.reflections.core.api.nodes.ResolvedNode
import com.anjunar.reflections.core.api.types.ResolvedType
import com.anjunar.reflections.core.java.annotations.JavaAnnotation
import com.anjunar.reflections.core.java.nodes.JavaNode
import com.anjunar.reflections.core.java.types.JavaTypeResolver
import tastyquery.Contexts

import java.lang.reflect.{Executable, Parameter}

object JavaExecutable {
  
  class JavaParameter(underlying : Parameter, owner : ResolvedNode)(using context: Contexts.Context) extends JavaNode(underlying, owner) with ResolvedExecutable.ResolvedParameter {

    override val name: String = underlying.getName

    override lazy val declaredType: ResolvedType = JavaTypeResolver.resolve(underlying.getParameterizedType, this)
    
    override lazy val declaredAnnotations: Array[ResolvedAnnotation] = underlying
      .getDeclaredAnnotations
      .map(annotation => new JavaAnnotation(annotation, this))
  }
  
}

abstract class JavaExecutable(underlying : Executable, owner : ResolvedNode)(using context: Contexts.Context) extends JavaMember(underlying, owner) with ResolvedExecutable{

  override val name : String = underlying.getName
  
  override lazy val parameters: Array[ResolvedExecutable.ResolvedParameter] = underlying.getParameters.map(parameter => new JavaExecutable.JavaParameter(parameter, this))
  
  override lazy val typeParameters: Array[ResolvedType] = underlying.getTypeParameters.map(parameter => JavaTypeResolver.resolve(parameter, this))

  override lazy val declaredAnnotations: Array[ResolvedAnnotation] = underlying
    .getDeclaredAnnotations
    .map(annotation => new JavaAnnotation(annotation, this))

}
