package com.anjunar.reflections
package core.api.types

import core.api.Visitor

trait ResolvedGenericArrayType extends ResolvedType {

  lazy val declaredType : ResolvedType

  override def accept(visitor: Visitor): Unit = visitor.visit(this)
  override def toString = s"Array[$declaredType]"
}
