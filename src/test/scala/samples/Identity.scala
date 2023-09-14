package samples

import jakarta.validation.constraints.NotBlank

import scala.beans.BeanProperty


abstract class Identity {

  @BeanProperty
  @NotBlank
  private var id : String = _

}
