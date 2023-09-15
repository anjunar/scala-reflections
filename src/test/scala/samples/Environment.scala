package samples

import com.anjunar.reflections.{Reflections, TypeResolver}
import com.anjunar.reflections.beans.Introspector
import com.google.common.reflect.ClassPath

import scala.collection.mutable
import scala.jdk.CollectionConverters.*

object Environment {

  val packages: List[String] = List("com.anjunar", "scala", "samples")

  val classPath: ClassPath = ClassPath.from(ClassLoader.getSystemClassLoader)
  
  val allClasses: mutable.Set[Class[_]] = classPath
    .getAllClasses
    .asScala
    .filter(clazz => packages.exists(p => clazz.getPackageName.startsWith(p)))
    .map(_.load())

  val resolver: TypeResolver = Reflections.init(allClasses)
  
  val introSpector: Introspector = new Introspector(resolver)


}
