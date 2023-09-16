package samples.tests

import org.scalatest.funsuite.AnyFunSuite
import samples.Environment.resolver.findStaticClass
import samples.models.Person

class FieldsTest extends AnyFunSuite {

  test("invoke field 'firstName' on Person") {
    val person = new Person
    person.firstName = "Max"
    person.lastName = "Mustermann"
    
    val personClass = findStaticClass(classOf[Person])
    
    val firstNameField = personClass
      .declaredField("firstName")
      .get

    val result = firstNameField.get(person)

    assert(result == "Max")
  }


}
