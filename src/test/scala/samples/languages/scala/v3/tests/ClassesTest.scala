package samples.languages.scala.v3.tests

import org.scalatest.funsuite.AnyFunSuite
import samples.Environment.resolver.findExtendingClasses
import samples.languages.scala.v3.models.{Identity}

class ClassesTest extends AnyFunSuite {

  test("get all extending classes from Identity") {

    val classes = findExtendingClasses(classOf[Identity])

    assert(classes.length == 1)
  }

}
