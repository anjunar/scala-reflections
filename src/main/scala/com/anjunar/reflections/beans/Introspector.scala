package com.anjunar.reflections
package beans

import com.anjunar.reflections.TypeResolver

class Introspector(typeResolver : TypeResolver) {
  def resolve(clazz: Class[_]): Bean = {
    val resolvedClass = typeResolver.findStaticClass(clazz)
    new Bean(resolvedClass)
  }
  
}
