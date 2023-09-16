package samples.models

import jakarta.validation.constraints.NotBlank

import scala.beans.BeanProperty


abstract class Identity {

  @BeanProperty
  @NotBlank
  var id : String = _
  
  @BeanProperty
  @NotBlank
  val category : String

}
