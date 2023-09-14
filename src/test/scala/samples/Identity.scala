package samples

import jakarta.validation.constraints.NotBlank

import scala.beans.BeanProperty


abstract class Identity {

  @BeanProperty
  @NotBlank
  var id : String = _

}
