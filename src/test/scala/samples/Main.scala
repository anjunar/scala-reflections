package samples

import com.anjunar.reflections.Reflections
import com.anjunar.reflections.beans.Introspector
import com.google.common.reflect.ClassPath

import scala.jdk.CollectionConverters.*

object Main {

  def main(args: Array[String]): Unit = {
    val start = System.currentTimeMillis()

    val packages = List("com.anjunar", "scala", "samples")

    val classPath = ClassPath.from(ClassLoader.getSystemClassLoader())
    val allClasses = classPath
      .getAllClasses
      .asScala
      .filter(clazz => packages.exists(p => clazz.getPackageName.startsWith(p)))
      .map(_.load())

    val resolver = Reflections.init(allClasses)
    val introSpector = new Introspector(resolver)
    val personBean = introSpector.resolve(classOf[Person])

    val person = new Person()
    person.id = "1"
    person.firstName = "Max"
    person.lastName = "Mustermann"

    val property = personBean
      .properties
      .find(_.name == "firstName")
      .get

    val value = property.get(person)

    println(value)

  }

}
