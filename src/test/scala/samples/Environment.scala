package samples

import com.anjunar.reflections.beans.Introspector
import com.anjunar.reflections.{Reflections, TypeResolver}
import com.google.common.reflect.ClassPath

import java.util.stream.Collectors

object Environment {

  val packages: List[String] = List("com.anjunar", "scala", "samples")

  val classPath: ClassPath = ClassPath.from(ClassLoader.getSystemClassLoader)
  
  val allClasses: java.util.Set[Class[_]] = classPath
    .getAllClasses
    .stream()
    .filter(clazz => packages.exists(p => clazz.getPackageName.startsWith(p)))
    .map(_.load())
    .collect(Collectors.toSet)

  val resolver: TypeResolver = Reflections.init(allClasses)
  
  val introSpector: Introspector = new Introspector(resolver)


}
