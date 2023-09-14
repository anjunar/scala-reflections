package samples

import scala.beans.BeanProperty


class Person(id: String, 
             @BeanProperty firstName: String, 
             @BeanProperty lastName: String) extends Identity(id) {


}
