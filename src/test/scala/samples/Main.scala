package samples

import com.anjunar.reflections.Reflections
import com.anjunar.reflections.beans.Introspector
import com.google.common.reflect.ClassPath

import scala.jdk.CollectionConverters.*

object Main {

  def main(args: Array[String]): Unit = {
    val start = System.currentTimeMillis()

    val classPath = ClassPath.from(ClassLoader.getSystemClassLoader())
    val allClasses = classPath
      .getAllClasses
      .asScala
      .filter(clazz => clazz.getPackageName.startsWith("com.anjunar") || clazz.getPackageName.startsWith("scala") || clazz.getPackageName.startsWith("samples"))
      .map(_.load())

    println("\n" + (System.currentTimeMillis() - start) / 1000 + " seconds")

    val resolver = Reflections.init(allClasses)

    println("\n" + (System.currentTimeMillis() - start) / 1000 + " seconds")

/*    val classes = resolver.findExtendingClasses(classOf[Identity])

    classes.foreach(Utils.renderToConsole)

    println("\n" + (System.currentTimeMillis() - start) / 1000 + " seconds")
*/    
    val introspector = new Introspector(resolver)
    
    println(introspector.resolve(classOf[Person]))

  }

}
