package com.anjunar.reflections
package core

import core.api.types.{ResolvedClass, ResolvedType}
import core.java.types.JavaClass
import core.scala.v2.types.Scala2Class
import core.scala.v3.types.Scala3Class

import tastyquery.Exceptions.{InvalidProgramStructureException, MemberNotFoundException}
import tastyquery.Names.{nme, termName, typeName}
import tastyquery.Symbols.{ClassSymbol, DeclaringSymbol, PackageSymbol, TermSymbol}
import tastyquery.{Contexts, Symbols}

object Utils {

  def renderToConsole(clazz: ResolvedClass): Unit = {
    println("--------------------------------------------------------------------------------------------------------------------------------------------------------------")
    val category = clazz match
      case java: JavaClass => "Java"
      case scala2: Scala2Class => "Scala 2"
      case scala3: Scala3Class => "Scala 3"
    val annotations = clazz.annotations.mkString("")
    val modifier = clazz.modifier.value
    val classType = if clazz.isEnum then "enum " else if clazz.isTrait then "trait " else "class "
    val typeParameters = Utils.brackets(clazz.typeParameters)
    val superClass = if clazz.superClass == null then "" else " extends " + clazz.superClass
    val traits = if clazz.parents.length > 0 then " with " + clazz.parents.mkString(" with ") else ""
    println(s"$category")
    println(s"$annotations$modifier $classType${clazz}$typeParameters$superClass$traits")
    println(s"${clazz.declaredMembers.mkString("\n")}")
  }

  def high(value : ResolvedType) : String = {
    if value == null then ""
    else value.toString match
      case "Object" | "null" | "Any" | "Nothing" => ""
      case _ => s" <: $value"
  }

  def low(value: ResolvedType): String = {
    if value == null then ""
    else value.toString match
      case "Object" | "null" | "Any" | "Nothing" => ""
      case _ => s" :> $value"
  }

  private def leftBracket(list: Iterable[_]): String = if list.nonEmpty then "[" else ""

  private def rightBracket(list: Iterable[_]): String = if list.nonEmpty then "]" else ""

  def brackets(list: Iterable[_]): String = s"${leftBracket(list)}${list.mkString(", ")}${rightBracket(list)}"

}
