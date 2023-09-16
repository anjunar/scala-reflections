package samples.languages.java.tests

import org.scalatest.funsuite.AnyFunSuite
import samples.Environment.resolver.findStaticClass
import samples.languages.java.models.Person

class MethodsTest extends AnyFunSuite {
  
  test("invoke method 'toString' on Person") {
    val person = new Person
    person.setFirstName("Max")
    person.setLastName("Mustermann")

    val personClass = findStaticClass(classOf[Person])

    val toStringMethod = personClass
      .methods("toString")
      .head

    val result = toStringMethod.invoke(person)

    assert(result != null)
  }

}
