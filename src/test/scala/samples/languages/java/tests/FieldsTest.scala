package samples.languages.java.tests

import org.scalatest.funsuite.AnyFunSuite
import samples.Environment.resolver.findStaticClass
import samples.languages.java.models.Person

class FieldsTest extends AnyFunSuite {

  test("invoke field 'firstName' on Person") {
    val person = new Person
    person.setFirstName("Max")
    person.setLastName("Mustermann")

    val personClass = findStaticClass(classOf[Person])

    val firstNameField = personClass
      .declaredField("firstName")
      .get

    val result = firstNameField.get(person)

    assert(result == "Max")
  }


}
