import util.Config

object StringInterpolation {

  def apply(variables: String, template: String): String = ???

  def apply(variables: String, template: String, parse: String => Map[String,String],
    interpolate: Map[String,String] => String => String): String = interpolate(parse(variables))(template)

}
