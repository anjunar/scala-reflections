package samples.tests

import org.scalatest.funsuite.AnyFunSuite
import samples.Environment.resolver.findStaticClass
import samples.models.Person

class TypesTest extends AnyFunSuite {

  test("get type 'Name' from Person") {
    val personClass = findStaticClass(classOf[Person])

    val nameType = personClass
      .declaredAbstractType("Name")
      .get

    assert(nameType.aliasedType != null)
  }

}
