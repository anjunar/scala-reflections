package samples

import com.anjunar.reflections.beans.Introspector
import com.anjunar.reflections.core.Utils
import com.anjunar.reflections.{Reflections, TypeResolver}
import com.google.common.reflect.ClassPath
import samples.languages.java.models.Person

import java.util.stream.Collectors
import scala.collection.immutable.HashSet

object Environment {

  val packages: List[String] = List(
    "scala", 
    "samples.languages.java.models", 
    "samples.languages.scala.v2.models", 
    "samples.languages.scala.v3.models"
  )

  val classPath: ClassPath = ClassPath.from(ClassLoader.getSystemClassLoader)
  
  val allClasses: java.util.Set[Class[_]] = classPath
    .getAllClasses
    .stream()
    .filter(clazz => packages.exists(p => clazz.getPackageName.startsWith(p)))
    .map(_.load())
    .collect(Collectors.toSet)

  val resolver: TypeResolver = Reflections.init(allClasses)
  
  val introSpector: Introspector = new Introspector(resolver)

  resolver.findClassesInClasspath.foreach(Utils.renderToConsole)

}
