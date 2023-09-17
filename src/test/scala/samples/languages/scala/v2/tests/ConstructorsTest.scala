package samples.languages.scala.v2.tests

import org.scalatest.funsuite.AnyFunSuite
import samples.Environment.resolver

import scala.collection.mutable

class ConstructorsTest extends AnyFunSuite {

  test("create A Hashmap") {

    val resolvedClass = resolver.findStaticModule(classOf[mutable.HashSet[String]])

    val applyMethods = resolvedClass
      .methods("apply")

    val applyMethod = applyMethods.head


    val hashSet : mutable.HashSet[String] = applyMethod
      .bind(resolvedClass)
      .invokeStatic("Foo Baa")
      .asInstanceOf[mutable.HashSet[String]]

    assert(hashSet.head == "Foo Baa")

  }

}
