package samples.tests

import jakarta.validation.constraints.Size
import org.scalatest.funsuite.AnyFunSuite
import samples.Environment.resolver.*
import samples.models.Person

class AnnotationsTest extends AnyFunSuite {

  test("get the 'Size' annotation from Person") {
    val personClass = findStaticClass(classOf[Person])

    val firstNameField = personClass
      .declaredField("firstName")
      .get

    val sizeAnnotation = firstNameField
      .declaredAnnotation(classOf[Size])
      .get

    val min = sizeAnnotation
      .fields("min")
      .asInstanceOf[Int]
    assert(min == 3)

    val max = sizeAnnotation
      .fields("max")
      .asInstanceOf[Int]
    assert(max == 80)
  }

  test("get all annotations from field 'name' of Person") {
    val personClass = findStaticClass(classOf[Person])

    val firstNameField = personClass
      .declaredField("category")
      .get

    val annotations = firstNameField.annotations

    assert(annotations.length == 3)
  }

}
