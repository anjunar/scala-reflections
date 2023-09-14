package com.anjunar.reflections
package core.java.members

import core.api.annotations.ResolvedAnnotation
import core.api.members.ResolvedField
import core.api.nodes.ResolvedNode
import core.api.types.{ResolvedClass, ResolvedParameterizedType, ResolvedType}
import core.java.annotations.JavaAnnotation
import core.java.types.{JavaClass, JavaTypeResolver}

import tastyquery.Contexts

import java.lang.reflect.{Field, Modifier}
import scala.collection.mutable.ListBuffer

class JavaField(underlying : Field, owner : ResolvedNode)(using context: Contexts.Context) extends JavaMember(underlying, owner) with ResolvedField {
  
  override def get(instance: AnyRef): Any = underlying.get(instance)

  override def set(instance: AnyRef, arg : Any): Any = underlying.set(instance, arg)

  override lazy val overridden: Array[ResolvedField] = owner match {
    case clazz: ResolvedClass => extract(clazz.superClass, underlying)
  }

  private def extract(owner: ResolvedNode, underlying: Field): Array[ResolvedField] = owner match
    case clazz: JavaClass =>
      val results = ListBuffer[ResolvedField]()

      try
        results.addOne(new JavaField(clazz.underlying.getDeclaredField(underlying.getName), this))
      catch
        case e: NoSuchFieldException => {}

      results.addAll(clazz.parents.flatMap({
        case parent: JavaClass =>
          try
            Array(new JavaField(parent.underlying.getDeclaredField(underlying.getName), this))
          catch
            case e: NoSuchFieldException => Array[JavaField]()
        case parameterizedType: ResolvedParameterizedType => extract(parameterizedType.declaredType, underlying)
      }))

      results.toArray ++ extract(clazz.superClass, underlying)
    case parameterizedType: ResolvedParameterizedType => extract(parameterizedType.declaredType, underlying)
    case _ => Array()

  override val name : String = underlying.getName
  
  override val isVal: Boolean = underlying.getModifiers == Modifier.FINAL
  
  override val isVar: Boolean = ! isVal
  
  override lazy val declaredType: ResolvedType = JavaTypeResolver.resolve(underlying.getGenericType, this)

  override lazy val declaredAnnotations: Array[ResolvedAnnotation] = underlying
    .getDeclaredAnnotations
    .map(annotation => new JavaAnnotation(annotation, this))


}
