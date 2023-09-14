package com.anjunar.reflections
package core.java.types

import com.anjunar.reflections.Dispatcher
import core.Utils
import core.api.nodes.ResolvedNode
import core.api.types.{ResolvedClass, ResolvedType}
import tastyquery.Contexts
import tastyquery.Exceptions.MemberNotFoundException

import java.lang.reflect.*

object JavaTypeResolver {

  def resolve[R <: ResolvedNode](category : Type, owner : ResolvedNode)(using context: Contexts.Context) : R = {
    val result = category match {
      case javaClass : Class[_] if javaClass.isPrimitive => new JavaPrimitiveType(javaClass, owner)
      case javaClass : Class[_] if javaClass.isArray => new JavaClass(javaClass.componentType(), owner)
      case javaClass : Class[_] => {
        if (javaClass.getName.contains("$")) {
          new JavaClass(javaClass, owner)
        } else {
          val fullName = javaClass.getName
          try
            val classSymbol = context.findStaticClass(fullName)
            Dispatcher.finalResolve(classSymbol, owner)
          catch
            // Todo : Workaround, because we only can access static resources
            case e: MemberNotFoundException => {
              val path = fullName.split("\\.")
              val reducedPath = path.dropRight(1).mkString(".")
              val className = javaClass.getSimpleName
              val classSymbol = context.findStaticClass(reducedPath)
              val clazz = Dispatcher.resolve[ResolvedClass](classSymbol, owner)
              val option = clazz.declaredClasses.find(clazz => clazz.name == className)
              option.get.asInstanceOf[R]
            }

        }
      }
      case parameterized : ParameterizedType => new JavaParameterizedType(parameterized, owner)
      case genericArray : GenericArrayType => new JavaGenericArrayType(genericArray, owner)
      case typeVariable : TypeVariable[_] => new JavaTypeVariableWithBounds(typeVariable, owner)
      case wildCard : WildcardType => new JavaWildcardType(wildCard, owner)
    }
    result.asInstanceOf[R]
  }

}
