package interpolation

import scala.util.Try

//provides functionality to parse lists of variables
trait Parsing {

  //extracts variable identifiers and value within a single line
  //e.g. fruit = lemons => (fruit,lemons)
  val extractPair: String => String => Try[(String,String)] = separator => line =>  {
    val pair = line.split(separator)
    Try((pair(0).trim, pair(1).trim))
  }

  //transforms a list of variables wich is given as a string into a map of identifiers and values
  //applies given separator to split the string into lines
  val parseVariables: String => (String => Try[(String,String)]) => String =>
   Map[String,String] =  separator => pairExtractor => variables =>
   variables.split(separator).map(pairExtractor(_)).filter(_.isSuccess).map(_.get).toMap


   def parse(variables: String): Map[String,String]
}

object CustomParsing extends Parsing {

  //parses variables and returns a map of identifiers and values
  def parse(variables: String) = parseVariables("\n")(extractPair("="))(variables)

}
