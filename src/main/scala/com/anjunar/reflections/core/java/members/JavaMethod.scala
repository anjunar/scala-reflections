package com.anjunar.reflections
package core.java.members

import com.anjunar.reflections.core.api.annotations.ResolvedAnnotation
import com.anjunar.reflections.core.api.members.{ResolvedConstructor, ResolvedMethod}
import com.anjunar.reflections.core.api.nodes.ResolvedNode
import com.anjunar.reflections.core.api.types.{ResolvedClass, ResolvedParameterizedType, ResolvedType}
import com.anjunar.reflections.core.java.annotations.JavaAnnotation
import com.anjunar.reflections.core.java.types.{JavaClass, JavaTypeResolver}
import tastyquery.Contexts

import java.lang.reflect.Method
import scala.collection.mutable.ListBuffer

class JavaMethod(underlying : Method, owner : ResolvedNode)(using context: Contexts.Context) extends JavaExecutable(underlying, owner) with ResolvedMethod {
  
  override def invoke(instance: AnyRef, args: Any*): Any = underlying.invoke(instance, args :_*)
  
  override def invokeStatic(args: Any*): Any = invoke(null, args : _*)

  override lazy val overridden: Array[ResolvedMethod] = owner match {
    case clazz: ResolvedClass => extract(clazz.superClass, underlying)
  }
  private def extract(owner: ResolvedNode, underlying: Method): Array[ResolvedMethod] = owner match
    case clazz: JavaClass =>
      val results = ListBuffer[ResolvedMethod]()

      try
        results.addOne(new JavaMethod(clazz.underlying.getDeclaredMethod(underlying.getName, underlying.getParameterTypes: _*), this))
      catch
        case e: NoSuchMethodException => {}

      results.addAll(clazz.parents.flatMap({
        case parent: JavaClass =>
          try
            Array(new JavaMethod(parent.underlying.getDeclaredMethod(underlying.getName, underlying.getParameterTypes: _*), this))
          catch
            case e: NoSuchMethodException => Array[JavaMethod]()
        case parameterizedType: ResolvedParameterizedType => extract(parameterizedType.declaredType, underlying)
      }))

      results.toArray ++ extract(clazz.superClass, underlying)
    case parameterizedType: ResolvedParameterizedType => extract(parameterizedType.declaredType, underlying)
    case _ => Array()


  override lazy val returnType: ResolvedType = JavaTypeResolver.resolve(underlying.getGenericReturnType, this)

  override lazy val declaredAnnotations: Array[ResolvedAnnotation] = underlying
    .getDeclaredAnnotations
    .map(annotation => new JavaAnnotation(annotation, this))

}
