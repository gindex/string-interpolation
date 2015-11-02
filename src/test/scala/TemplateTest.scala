import org.scalatest._

import interpolation.Template

class TemplateTest extends FlatSpec with Matchers {

  val template = "When life gives you %(fruit), make %(product). \nThis is a percent-sign: %%."

  //create custom template by inheriting the trait
  val customTemplate = new Template{
    def treatEscapedStartSign(s: String) = s.replaceAll("%%","%")
    def unpackIdentifier(s:String) = s.substring(2,s.length-1)
    def startSign = "%"
    def checkStartSignSyntax(s: String) = s.matches("(%%)+[^a-zA-Z_0-9%]*")
    def checkIdentifierSyntax(identifier: String) = identifier.matches("(%\\()(\\w|-|_)+(\\))")
    def variableRegex = "%\\(.*?\\)".r
    def tokens = template.split(" ")
  }


  //test preProcessTemplate function
  val tokens1 = Seq("life","gives", "%(y0u)", "%%," , "%(y0u)")
  val expectedResult1 = List((Some(("%(y0u)", 2, true)),None), (None, Some((3,true))), (Some(("%(y0u)", 4, true)),None))

  val tokens2 = Seq("life","gives", "%(you!)", "%%%,")
  val expectedResult2 = Seq((Some(("%(you!)", 2, false)),None), (None, Some((3,false))))

  type PropType = customTemplate.TemplateProperties
  def equalsParsingResults(s1: PropType, s2: PropType ) = {
    for((t1,t2) <- s1.zip(s2)) yield {
      if(t1._1.isDefined && t2._1.isDefined) {
        t1._1.get._1.equals(t2._1.get._1) && t1._1.get._2.equals(t2._1.get._2) && t1._1.get._3.equals(t2._1.get._3)
      }
      else if(t1._2.isDefined && t2._2.isDefined) {
        t1._2.get._1.equals(t2._2.get._1) && t1._2.get._2.equals(t2._2.get._2)
      }
      else false
    }
  }.reduce(_ && _)

  s"""Pre-processing of "$tokens1" """ should s"""be "$expectedResult1" """ in {
    equalsParsingResults(customTemplate.preProcessTemplate(tokens1),expectedResult1) should be (right = true)
  }

  s"""Pre-processing of "$tokens2" """ should s"""be "$expectedResult2" """ in {
    equalsParsingResults(customTemplate.preProcessTemplate(tokens2),expectedResult2) should be (right = true)
  }


  //test findVariableMentionIndexes function
  val expectedIndexes = Seq(("y0u", ("%(y0u)", 2)),("y0u", ("%(y0u)", 4)))

  def compareSeqs(s1: Seq[(String,(String,Int))], s2: Seq[(String, (String,Int))]) = {
    {for((i1,i2) <- s1.zip(s2)) yield {
      i1._1.equals(i2._1) && i1._2._1.equals(i2._2._1) && i1._2._2.equals(i2._2._2)
    }}.reduce(_ && _)
  }

  s""" Indexes of "$expectedResult1" """ should s""" be "$expectedIndexes" """ in {
    customTemplate.findVariableMentionIndexes(expectedResult1).equals(expectedIndexes) should be (right = true)
  }


  //test createTemplate function
  val templateProps = expectedResult1
  val tokens = tokens1
  val expectedTemplate = Array("life","gives", "%(y0u)", "%,")

  def comparaTemplateArrays(a1: Array[String], a2: Array[String]) =
    (for((i1,i2)  <- a1.zip(a2)) yield i1.equals(i2)).reduce(_ && _)

  s""" Transoformation of "tokens" to template """ should s""" create  "$expectedTemplate """ in {
    val createdTemplate = customTemplate.createTemplate(templateProps)(tokens)
    comparaTemplateArrays(createdTemplate,expectedTemplate) should be (right = true)
  }

  //test syntax check
  //correct syntax
  s"""Synatax check of "$expectedResult1" """ should "return true" in {
    customTemplate.checkSytax(expectedResult1) should be (right = true)
  }

  //wrong syntax
  s"""Synatax check of "$expectedResult2" """ should "return false" in {
    customTemplate.checkSytax(expectedResult2) should be (right = false)
  }


}
