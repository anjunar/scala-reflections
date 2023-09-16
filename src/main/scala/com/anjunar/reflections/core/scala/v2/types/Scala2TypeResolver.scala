package com.anjunar.reflections
package core.scala.v2.types

import com.anjunar.reflections.core.api.nodes.ResolvedNode
import com.anjunar.reflections.core.api.types.ResolvedType
import com.anjunar.reflections.{Dispatcher, PathResolver}
import tastyquery.Contexts

import scala.reflect.runtime.universe.{AppliedTypeTree, ClassSymbol, PolyType, RefinedType, Symbol, Type, TypeBounds, TypeRef, TypeSymbol}

object Scala2TypeResolver {

  def resolve[R <: ResolvedType](types: Type, owner: ResolvedNode)(using context: Contexts.Context): R = {
    val result = types match
      case appliedType: AppliedTypeTree => new Scala2ParameterizedType(appliedType, owner)
      case typeBounds: TypeBounds => new Scala2BoundsType(typeBounds, owner)
      case polyType: PolyType => resolve[R](polyType.resultType, owner)
      case refinedType: RefinedType => resolve[R](refinedType.parents(0), owner)
      case typeRef: TypeRef => resolve[R](typeRef.sym, owner)
      case _ => resolve[R](types.typeSymbol, owner)
    result.asInstanceOf[R]
  }

  def resolve[R <: ResolvedNode](symbol: Symbol, owner: ResolvedNode)(using context: Contexts.Context): R = {
    val result = symbol match {
      case classSymbol: ClassSymbol =>
        val fullName = classSymbol.owner.fullName + "." + classSymbol.name.decoded

        if (classSymbol.name.toString == "String") {
          val dealias = classSymbol.info.dealias.typeSymbol
          new Scala2Class(dealias.asClass, owner)
        } else {
          // Todo : Workaround, because of Scala2 syntax for args: <repeat>
          if (fullName.contains("<") && fullName.contains(">")) {
            new Scala2Class(classSymbol, owner)
          } else {
            Dispatcher.finalResolve(PathResolver.scala2ToScala3(classSymbol, classSymbol.isModule), owner)         
          }
        }
    case typeSymbol: TypeSymbol =>
      typeSymbol.typeSignature match
        case typeBounds: TypeBounds => new Scala2TypeVariableWithBounds(typeSymbol, owner)
        case _ => new Scala2TypeVariable(typeSymbol.name.toString, owner)
    }
  
    result.asInstanceOf[R]
}

}
