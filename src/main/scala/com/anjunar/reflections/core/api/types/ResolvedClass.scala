package com.anjunar.reflections
package core.api.types

import com.anjunar.reflections.core.api.Visitor
import com.anjunar.reflections.core.api.annotations.{ResolvedAnnotated, ResolvedAnnotation}
import com.anjunar.reflections.core.api.members.*

import scala.collection.mutable.ListBuffer

object ResolvedClass {

  enum Modifier(val value: String) {
    case Private extends Modifier("private")
    case Protected extends Modifier("protected")
    case Public extends Modifier("public")
  }

}

trait ResolvedClass extends ResolvedType with ResolvedAnnotated {

  val name: String

  val fullName: String

  val isArray: Boolean

  val isTrait: Boolean

  val isEnum: Boolean
  
  val isModule : Boolean

  val isClass: Boolean = !isEnum && !isArray && !isModule

  lazy val isAnnotation: Boolean

  val modifier: ResolvedClass.Modifier

  lazy val superClass: ResolvedType

  lazy val parents: Array[ResolvedType]

  lazy val typeParameters: Array[ResolvedType]

  def isSubType(clazz: ResolvedClass): Boolean = parentsHierarchy.contains(clazz)

  lazy val parentsHierarchy: Array[ResolvedClass] = {
    val result = ListBuffer[ResolvedClass]()

    def recursion(clazz: ResolvedType): Unit = clazz match
      case resolvedClass: ResolvedClass =>
        result.addOne(resolvedClass)
        // Todo : Workaround because of a Stack overflow
        if (result.size > 1000) {
          return
        }
        resolvedClass.superClass match
          case clazz: ResolvedClass => recursion(clazz)
          case parameterizedType: ResolvedParameterizedType => recursion(parameterizedType.declaredType)
          case _ => {}

        resolvedClass.parents
          .filter(! _.isInstanceOf[ResolvedTypeVariable])
          .foreach({
            case clazz: ResolvedClass => recursion(clazz)
            case parameterizedType: ResolvedParameterizedType => recursion(parameterizedType.declaredType)
          })
      case parameterizedType: ResolvedParameterizedType => recursion(parameterizedType.declaredType)
      case _ => {}

    recursion(superClass)
    parents.foreach(recursion)

    result.distinct.toArray
  }

  override lazy val annotations: Array[ResolvedAnnotation] = declaredAnnotations ++ parentsHierarchy.flatMap(_.declaredAnnotations).distinct

  lazy val members: Array[ResolvedMember] = {

    val resolvedMembers = (Array(this) ++ parentsHierarchy).flatMap(parent => parent.declaredMembers)

    val overridden = resolvedMembers
      .filter({
        case field: ResolvedField => !field.overridden.isEmpty
        case constructor: ResolvedConstructor => !constructor.overridden.isEmpty
        case method: ResolvedMethod => !method.overridden.isEmpty
        case _ => false
      })
      .flatMap({
        case field: ResolvedField => field.overridden
        case constructor: ResolvedConstructor => constructor.overridden
        case method: ResolvedMethod => method.overridden
      })

    resolvedMembers.filter(member => !overridden.contains(member) && member.modifier == ResolvedMember.Modifier.Public)
  }

  lazy val fields: Array[ResolvedField] = members
    .filter(_.isInstanceOf[ResolvedField])
    .map(_.asInstanceOf[ResolvedField])

  def field(name: String): Option[ResolvedField] = fields.find(_.name == name)

  lazy val abstractTypes: Array[ResolvedAbstractType] = members
    .filter(_.isInstanceOf[ResolvedAbstractType])
    .map(_.asInstanceOf[ResolvedAbstractType])
  
  def abstractType(name : String) : Option[ResolvedAbstractType] = abstractTypes.find(_.name == name)

  lazy val constructors: Array[ResolvedConstructor] = members
    .filter(_.isInstanceOf[ResolvedConstructor])
    .map(_.asInstanceOf[ResolvedConstructor])

  lazy val methods: Array[ResolvedMethod] = members
    .filter(_.isInstanceOf[ResolvedMethod])
    .map(_.asInstanceOf[ResolvedMethod])

  def methods(name: String): Array[ResolvedMethod] = methods.filter(_.name == name)


  lazy val declaredClasses: Array[ResolvedClass]

  lazy val declaredMembers: Array[ResolvedMember]

  lazy val declaredFields: Array[ResolvedField]

  def declaredField(name: String): Option[ResolvedField] = declaredFields.find(_.name == name)

  lazy val declaredAbstractTypes: Array[ResolvedAbstractType]
  
  def declaredAbstractType(name : String) : Option[ResolvedAbstractType] = declaredAbstractTypes.find(_.name == name)

  lazy val declaredConstructors: Array[ResolvedConstructor]

  lazy val declaredMethods: Array[ResolvedMethod]

  def declaredMethods(name: String): Array[ResolvedMethod] = declaredMethods.filter(_.name == name)

  override def accept(visitor: Visitor): Unit = visitor.visit(this)

  override def toString: String = {
    val annotation = if isAnnotation then "@" else ""
    val arrayStart = if isArray then "Array[" else ""
    val arrayEnd = if isArray then "]" else ""
    s"$annotation$arrayStart$name${arrayEnd}"
  }
}
