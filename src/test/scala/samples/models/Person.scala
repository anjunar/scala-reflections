package samples.models

import jakarta.validation.constraints.Size

import scala.beans.BeanProperty


class Person extends Identity {

  @SecuredAnnotation(roles = Array("Admin"))
  @Size(min = 3, max = 80)
  @BeanProperty
  var firstName: String = _

  @Size(min = 3, max = 80)
  @BeanProperty
  var lastName: String = _
  
  @Size(min = 3, max = 80)
  override val category : String = "Person"
  
  override def toString = s"Person($firstName, $lastName)"
}
