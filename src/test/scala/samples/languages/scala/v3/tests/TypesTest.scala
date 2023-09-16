package samples.languages.scala.v3.tests

import com.anjunar.reflections.core.api.types.ResolvedClass
import org.scalatest.funsuite.AnyFunSuite
import samples.Environment.resolver.findStaticClass
import samples.languages.scala.v3.models.Person

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
