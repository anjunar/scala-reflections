package com.anjunar.reflections
package core.scala.v3.types

import com.anjunar.reflections.Dispatcher
import com.anjunar.reflections.core.api.nodes.ResolvedNode
import tastyquery.Symbols.{ClassSymbol, TermOrTypeSymbol, TypeMemberSymbol, TypeSymbolWithBounds}
import tastyquery.Types.*
import tastyquery.{Contexts, Types}

object Scala3TypeResolver {
  
  
  
  def resolve[R <: ResolvedNode](types : Types.TypeMappable, owner : ResolvedNode)(using context: Contexts.Context) : R = {
    val result = types match {
      case andType : AndType => new Scala3AndType(andType, owner)
      case orType : OrType => new Scala3OrType(orType, owner)
      case appliedType: AppliedType => new Scala3ParameterizedType(appliedType, owner)
      case wildcardType : WildcardTypeArg => new Scala3WildcardType(wildcardType, owner)
      case typeBounds : TypeBounds => new Scala3BoundsType(typeBounds, owner)

      case matchType: MatchType => resolve[R](matchType.scrutinee, owner)
      case lambdaType : LambdaType => resolve[R](lambdaType.resultType, owner)
      case typeRef : TypeRef =>
        val optSymbol = typeRef.optSymbol
        if (optSymbol.isEmpty) {
          resolve[R](typeRef.underlying, owner)
        } else {
          resolve[R](optSymbol.get, owner)
        }
      case thisType : ThisType => resolve[R](thisType.tref, owner)

      case typeParamRef: TypeParamRef => new Scala3TypeVariable(typeParamRef.paramName.toString, owner)
      case typeRefinement: TypeRefinement => new Scala3TypeVariable(typeRefinement.refinedName.toString, owner) // Todo : Is this the right handling of this type?
      case termParamRef: TermParamRef => new Scala3TypeVariable(termParamRef.paramName.toString, owner)
      case nothingType: NothingType => new Scala3TypeVariable("Nothing", owner)
      case anyKindType: AnyKindType => new Scala3TypeVariable("Any", owner)
      case constantType : ConstantType => new Scala3TypeVariable(constantType.value.toString, owner) // Todo : Is this handling right?

      case annotatedType: AnnotatedType => resolve[R](annotatedType.typ, owner) // Todo : What is a annotated type?
      case byNameType: ByNameType => resolve[R](byNameType.resultType, owner)
      case termRefinement: TermRefinement => resolve[R](termRefinement.refinedType, owner) // Todo : Is this the right handling of this type?
      case termRef : TermRef => resolve[R](termRef.optSymbol.get.declaredType, owner)
    }
    result.asInstanceOf[R]
  }
  

  def resolve[R <: ResolvedNode](symbol : TermOrTypeSymbol, owner : ResolvedNode)(using context: Contexts.Context) : R = {
    val result = symbol match {
      case classSymbol : ClassSymbol => Dispatcher.finalResolve(classSymbol, owner)
      case typeMemberSymbol: TypeMemberSymbol if typeMemberSymbol.name.toString == "String" => Scala3TypeResolver.resolve[R](typeMemberSymbol.aliasedType.dealias, owner)
      case typeSymbol: TypeSymbolWithBounds => new Scala3TypeVariableWithBounds(typeSymbol, owner)
    }
    result.asInstanceOf[R]
  }

}
