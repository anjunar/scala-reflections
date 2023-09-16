package com.anjunar.reflections

import com.anjunar.reflections.Dispatcher
import com.anjunar.reflections.core.api.types.ResolvedClass
import tastyquery.Classpaths.Classpath
import tastyquery.Classpaths.Classpath.Entry
import tastyquery.Contexts.Context

class TypeResolver(classPath: Classpath)(using context: Context) {

  lazy val findClassesInClasspath: Array[ResolvedClass] = {
    val classPathEntries: Array[Entry] = classPath.entries.asInstanceOf[Array[Entry]]
    classPathEntries
      .flatMap(entry => {
        if (!entry.packages.isEmpty) {
          val value = context.findSymbolsByClasspathEntry(entry)
          value
            .filter(element => element.fullName.toString() != "module-info")
            .map(element => Dispatcher.resolve[ResolvedClass](element, null))
        } else {
          List[ResolvedClass]()
        }
      })
      .distinct
  }

  def findStaticClass(clazz: Class[_]): ResolvedClass = Dispatcher.resolve[ResolvedClass](context.findStaticClass(clazz.getName), null)
  def findStaticModule(clazz: Class[_]): ResolvedClass = Dispatcher.resolve[ResolvedClass](context.findStaticModuleClass(clazz.getName), null)
  
  def findExtendingClasses(clazz: Class[_]): Array[ResolvedClass] = {
    val resolvedClass = findStaticClass(clazz)
    val classpath = findClassesInClasspath
    classpath.filter(_.isSubType(resolvedClass))
  }
}
