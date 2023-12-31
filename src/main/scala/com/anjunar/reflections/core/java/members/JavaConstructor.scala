package com.anjunar.reflections
package core.java.members

import com.anjunar.reflections.core.api.annotations.ResolvedAnnotation
import com.anjunar.reflections.core.api.members.ResolvedConstructor
import com.anjunar.reflections.core.api.nodes.ResolvedNode
import com.anjunar.reflections.core.api.types.{ResolvedClass, ResolvedParameterizedType}
import com.anjunar.reflections.core.java.annotations.JavaAnnotation
import com.anjunar.reflections.core.java.types.JavaClass
import tastyquery.Contexts

import java.lang.reflect.Constructor
import scala.collection.mutable.ListBuffer

class JavaConstructor(underlying : Constructor[_], owner : ResolvedNode)(using context: Contexts.Context) extends JavaExecutable(underlying, owner) with ResolvedConstructor {
  
  override def newInstance(args: Any*): Any = underlying.newInstance(args : _*)

  override lazy val overridden: Array[ResolvedConstructor] = owner match {
    case clazz : ResolvedClass => extract(clazz.superClass, underlying)
  }

  private def extract(owner: ResolvedNode, underlying: Constructor[_]): Array[ResolvedConstructor] = owner match
    case clazz: JavaClass =>
      val results = ListBuffer[ResolvedConstructor]()

      try
        results.addOne(new JavaConstructor(clazz.underlying.getDeclaredConstructor(underlying.getParameterTypes: _*), this))
      catch
        case e : NoSuchMethodException => {}

      results.addAll(clazz.parents.flatMap({
        case parent: JavaClass =>
          try
            Array(new JavaConstructor(parent.underlying.getDeclaredConstructor(underlying.getParameterTypes: _*), this))
          catch
            case e : NoSuchMethodException => Array[ResolvedConstructor]()
        case parameterizedType: ResolvedParameterizedType => extract(parameterizedType.declaredType, underlying)
      }))
      
      results.toArray ++ extract(clazz.superClass, underlying)
    case parameterizedType: ResolvedParameterizedType => extract(parameterizedType.declaredType, underlying)
    case _ => Array()
  
  override lazy val declaredAnnotations: Array[ResolvedAnnotation] = underlying
    .getDeclaredAnnotations
    .map(annotation => new JavaAnnotation(annotation, this))

  
}
