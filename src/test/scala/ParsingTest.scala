import org.scalatest._
import scala.util.{Success, Failure}

import interpolation.Parsing

class ParsingTest extends FlatSpec with Matchers {

  //test extractPair function
  val extractPair = Parsing.extractPair("=")

  //test extraction of correct formated variable
  val pair1 = "fruit = lemons"
  val expectedResult1 = ("fruit","lemons")

  s"""Parsing of "$pair1" """ should "be (fruit,lemons)" in {
    val result = extractPair(pair1) match {
      case Success(pair) =>
        if(pair._1.equals(expectedResult1._1) && pair._2.equals(expectedResult1._2)) true
        else false
      case Failure(e) => false
    }
    result should be (right = true)
  }

  //test wrong formated variable
  val pair2 = "lemons"
  s"""Parsing of "$pair2" """ should "throw exception" in {
    val result = extractPair(pair2) match {
      case Success(pair) => false
      case Failure(e) => true
    }
    result should be (right = true)
  }

  //test parseVariables function
  val parseVariables = Parsing.parseVariables(";")(Parsing.extractPair("="))

  //correctly formated
  val variables1 = "fruit = lemons; product = lemonade"
  val expectedMap1 = Map("fruit" -> "lemons", "product" -> "lemonade")

  s"""Parsing of "$variables1" """ should "be a Map(fruit -> lemons, product -> lemonade)" in {
    val result = parseVariables(variables1)
    val difference = result.toSet diff expectedMap1.toSet
    difference.isEmpty should be (right = true)
  }

  //wrongly formated
  val variables2 = "fruit = lemons; lemonade"
  val expectedMap2 = Map("fruit" -> "lemons")

  s"""Parsing of "$variables2" """ should "be a Map(fruit -> lemons)" in {
    val result = parseVariables(variables2)
    val difference = result.toSet diff expectedMap2.toSet
    difference.isEmpty should be (right = true)
  }
}
