package samples.tests

import com.anjunar.reflections.core.api.types.ResolvedClass
import org.scalatest.funsuite.AnyFunSuite
import samples.Environment.resolver.findStaticClass
import samples.models.Person

class TypesTest extends AnyFunSuite {

  test("get type 'Name' from Person") {
    val personClass = findStaticClass(classOf[Person])

    val nameType = personClass
      .declaredAbstractType("Name")
      .get

    val aliasedType = nameType.aliasedType.asInstanceOf[ResolvedClass]
    assert(aliasedType.name == "String")
  }

}
