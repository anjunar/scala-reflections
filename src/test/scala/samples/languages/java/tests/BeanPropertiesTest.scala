package samples.languages.java.tests

import org.scalatest.funsuite.AnyFunSuite
import samples.Environment.introSpector
import samples.languages.java.models.Person

class BeanPropertiesTest extends AnyFunSuite {

  test("the bean property of a Person") {
    val person = new Person
    person.setFirstName("Max")
    person.setLastName("Mustermann")

    val personBean = introSpector.resolve(classOf[Person])

    val property = personBean
      .properties
      .find(_.name == "firstName")
      .get

    val value = property
      .get(person)
      .asInstanceOf[String]

    assert(value == "Max")

    if (! property.isReadOnly) {
      property.set(person, "Maxx")
    }

    assert(person.getFirstName == "Maxx")
  }
}
