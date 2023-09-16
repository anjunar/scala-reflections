package com.anjunar.reflections
package core.api.types

import com.anjunar.reflections.core.Utils
import com.anjunar.reflections.core.api.Visitor

trait ResolvedParameterizedType extends ResolvedType {

  lazy val declaredType: ResolvedType

  lazy val typeArguments: Array[ResolvedType]

  override def accept(visitor: Visitor): Unit = visitor.visit(this)

  override def toString: String = {
    var cursor = owner
    while (cursor != null) {
      if (cursor.equals(this)) {
        return ""
      }
      cursor = cursor.owner
    }
    s"$declaredType${Utils.brackets(typeArguments)}"
  }
}
