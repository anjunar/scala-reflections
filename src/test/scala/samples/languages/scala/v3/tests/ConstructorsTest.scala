package samples.languages.scala.v3.tests

import org.scalatest.funsuite.AnyFunSuite
import samples.Environment.resolver.findStaticClass
import samples.languages.scala.v3.models.Person

class ConstructorsTest extends AnyFunSuite {

  test("the constructor of Person") {
    val personClass = findStaticClass(classOf[Person])

    val personConstructor = personClass
      .declaredConstructors
      .head

    val personInstance = personConstructor
      .newInstance()
      .asInstanceOf[Person]

    assert(personInstance != null)
  }

}
