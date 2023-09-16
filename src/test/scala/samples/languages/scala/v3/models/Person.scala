package samples.languages.scala.v3.models

import jakarta.validation.constraints.Size

import scala.beans.BeanProperty


class Person extends Identity with Logger {

  type Name = String

  @Secured(roles = Array("Admin"))
  @Size(min = 3, max = 80)
  @BeanProperty
  var firstName: Name = _

  @Size(min = 3, max = 80)
  @BeanProperty
  var lastName: Name = _

  @Size(min = 3, max = 80)
  override val category : String = "Person"

  override def toString = s"Person($firstName, $lastName)"
}
