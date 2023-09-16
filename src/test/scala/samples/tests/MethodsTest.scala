package samples.tests

import org.scalatest.funsuite.AnyFunSuite
import samples.Environment.resolver.findStaticClass
import samples.models.Person

class MethodsTest extends AnyFunSuite {
  
  test("invoke method 'toString' on Person") {
    val person = new Person
    person.firstName = "Max"
    person.lastName = "Mustermann"

    val personClass = findStaticClass(classOf[Person])

    val toStringMethod = personClass
      .declaredMethods("toString")
      .head

    val result = toStringMethod.invoke(person)

    assert(result == "Person(Max, Mustermann)")
  }

}
