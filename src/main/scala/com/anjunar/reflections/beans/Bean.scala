package com.anjunar.reflections
package beans

import core.api.types.ResolvedClass

class Bean(resolvedClass: ResolvedClass) {

  val properties: Array[Property] = resolvedClass
    .methods
    .filter(method => (method.name.startsWith("get") || method.name.startsWith("is")) && method.parameters.isEmpty)
    .map(method => {
      if (method.name.startsWith("get")) {
        val propertyName = method.name.substring(3, 4).toLowerCase + method.name.substring(4)
        val setter = resolvedClass
          .methods("set" + method.name.substring(3))
          .headOption
          .orNull

        val field = resolvedClass
          .field(propertyName)
          .orNull
        new Property(propertyName, method, setter, field)
      } else {
        val propertyName = method.name.substring(2,3).toLowerCase + method.name.substring(3)
        val setter = resolvedClass
          .methods("set" + method.name.substring(2))
          .headOption
          .orNull

        val field = resolvedClass
          .field(propertyName)
          .orNull
        new Property(propertyName, method, setter, field)
      }
    })

  override def toString = s"${resolvedClass.name}\n${properties.mkString("\n")}"
}
