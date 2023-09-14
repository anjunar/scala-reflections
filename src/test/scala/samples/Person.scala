package samples

import jakarta.validation.constraints.Size

import scala.beans.BeanProperty


class Person extends Identity {

  @Size(min = 3, max = 80)
  @BeanProperty
  private var firstName: String = _

  @Size(min = 3, max = 80)
  @BeanProperty
  private var lastName: String = _

}
