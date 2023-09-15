package samples

import jakarta.validation.constraints.Size
import org.scalatest.funsuite.AnyFunSuite
import samples.Environment.resolver.*

class AnnotationsTest extends AnyFunSuite {

  test("get the size annotation from person") {
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
  }

}
