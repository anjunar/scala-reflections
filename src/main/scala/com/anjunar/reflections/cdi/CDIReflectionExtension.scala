package com.anjunar.reflections.cdi

import com.anjunar.reflections.beans.Introspector
import com.anjunar.reflections.{Reflections, TypeResolver}
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes
import jakarta.enterprise.inject.spi.{AfterBeanDiscovery, Extension, ProcessAnnotatedType}

import java.util

class CDIReflectionExtension extends Extension {

  private val classes = new util.HashSet[Class[_]]()

  def onProcessAnnotatedType[X](@Observes event: ProcessAnnotatedType[X]): Unit = {
    val clazz = event.getAnnotatedType.getJavaClass
    classes.add(clazz)
  }

  def onAfterBeanDiscovery(@Observes event: AfterBeanDiscovery): Unit = {

    val resolver: TypeResolver = Reflections.init(classes)
    event.addBean()
      .scope(classOf[ApplicationScoped])
      .types(classOf[TypeResolver])
      .id("Created by " + classOf[TypeResolver])
      .createWith(e => resolver)

    val introSpector: Introspector = new Introspector(resolver)
    event.addBean()
      .scope(classOf[ApplicationScoped])
      .types(classOf[Introspector])
      .id("Created by " + classOf[Introspector])
      .createWith(e => introSpector)

  }

}
