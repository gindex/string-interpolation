package interpolation

import scala.util.Try

trait Parsing {

  //extracts variable identifiers and value of a single line
  //returns a tuple
  //e.g. fruit = lemons => (fruit,lemons)
  val extractPair: String => String => Try[(String,String)] = separator => line =>  {
    val pair = line.split(separator)
    Try((pair(0).trim, pair(1).trim))
  }

  //transforms a list of variables wich are given as a string into a map of variable identifiers and values
  //applies given separator to split string into lines
  //pairExtractor function shoud split single line into varibale's variable identifier and value
  val parseVariables: String => (String => Try[(String,String)]) => String =>
   Map[String,String] =  separator => pairExtractor => variables =>
   variables.split(separator).map(pairExtractor(_)).filter(_.isSuccess).map(_.get).toMap


   def parse(variables: String): Map[String,String]
}

object CustomParsing extends Parsing {

  //parses variables and returns a map of identifiers and values
  def parse(variables: String) = parseVariables("\n")(extractPair("="))(variables)

}
