package com.anjunar.reflections

import com.anjunar.reflections.core.api.nodes.ResolvedNode
import com.anjunar.reflections.core.api.types.ResolvedType
import com.anjunar.reflections.core.java.types.{JavaClass, JavaTypeResolver}
import com.anjunar.reflections.core.scala.v2.types.{Scala2Class, Scala2TypeResolver, Scala2TypeVariable, Scala2TypeVariableWithBounds}
import com.anjunar.reflections.core.scala.v3.members.Scala3MemberResolver
import com.anjunar.reflections.core.scala.v3.types.{Scala3Class, Scala3TypeResolver}
import tastyquery.Modifiers.TermSymbolKind
import tastyquery.Symbols.{ClassSymbol, TermOrTypeSymbol, TermSymbol, TypeSymbol}
import tastyquery.{Contexts, SourceLanguage, Symbols}

import scala.reflect.runtime.universe

object Dispatcher {


  def finalResolve(symbol: Symbols.Symbol, owner: ResolvedNode)(using context: Contexts.Context): ResolvedType = symbol match {
    case symbol : ClassSymbol => symbol.sourceLanguage match {
      case SourceLanguage.Java =>
        val classSymbol = PathResolver.scala3ToJava(symbol)
        new JavaClass(classSymbol, owner)
      case SourceLanguage.Scala2 =>
        val classSymbol = PathResolver.scala3ToScala2(symbol)
        classSymbol match
          case classSymbol: universe.ClassSymbol => new Scala2Class(classSymbol, owner)
          case typeSymbol: universe.TypeSymbol => {
            // Todo : Workaround, is it really a TypeVariableWithBounds?
            typeSymbol.typeSignature match
              case bounds: universe.TypeBounds => new Scala2TypeVariableWithBounds(typeSymbol, owner)
              case _ => new Scala2TypeVariable(typeSymbol.name.toString, owner)
          }
          case moduleSymbol: universe.ModuleSymbol => new Scala2Class(moduleSymbol, owner)
      case SourceLanguage.Scala3 => new Scala3Class(symbol, owner)
    }
  }

  def resolveBegin[T <: ResolvedNode](symbol: TermOrTypeSymbol, owner: ResolvedNode)(using context: Contexts.Context): T = symbol match {
    case symbol: TermSymbol if symbol.kind == TermSymbolKind.Module => finalResolve(symbol.moduleClass.get.asClass, owner).asInstanceOf[T]
    case symbol: TermSymbol => symbol.sourceLanguage match
      case SourceLanguage.Java => throw new IllegalStateException("Members can only be defined in Java classes")
      case SourceLanguage.Scala2 => ???
      case SourceLanguage.Scala3 => Scala3MemberResolver.resolve[T](symbol, owner)
    case symbol: ClassSymbol => symbol.sourceLanguage match {
      case SourceLanguage.Java => JavaTypeResolver.resolve[T](PathResolver.scala3ToJava(symbol), owner)
      case SourceLanguage.Scala2 => Scala2TypeResolver.resolve[T](PathResolver.scala3ToScala2(symbol), owner)
      case SourceLanguage.Scala3 => Scala3TypeResolver.resolve[T](symbol, owner)
    }
  }

}
