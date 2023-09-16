package samples.models

import scala.annotation.StaticAnnotation

class SecuredAnnotation(roles : Array[String]) extends StaticAnnotation {}
