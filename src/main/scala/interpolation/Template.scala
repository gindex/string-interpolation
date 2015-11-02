package interpolation

import scala.util.matching.Regex

//Respresents templates and allows to produce strings with interpolated variables
trait Template {

  //treats escaped chars
  def treatEscapedStartSign(s: String): String

  //removes start sign and brackets and retruns cleaned variable indentifier
  def unpackIdentifier(s: String): String

  //defines opening sign of variable identifier
  def startSign():String

  //checks if variable start sign was escaped correctly
  def checkStartSignSyntax(s: String): Boolean

  //checks if identifier consists of one or more ascii characters, digits, hyphen and underscore
  def checkIdentifierSyntax(identifier: String): Boolean

  //definies regex for extracting variable identifier
  def variableRegex(): Regex

  //splits template string into tokens
  def tokens():Seq[String]

  type TemplateProperties = Seq[(Option[(String,Int,Boolean)], Option[(Int,Boolean)])]

  //extracts varibale mentions from template string and checks syntax
  val preProcessTemplate: Seq[String] => TemplateProperties = tokens =>
    for((token, index) <- tokens.zipWithIndex if token.contains(startSign)) yield {
      val extractVariableIdentifier = variableRegex.findFirstIn(token)
      //test if the token is a variable identifier
      if(extractVariableIdentifier.isDefined) {
        //syntax check
        val syntaxCheck = checkIdentifierSyntax( extractVariableIdentifier.get)
        (Some((extractVariableIdentifier.get, index, syntaxCheck)), None)
      } else {
        //test if start sign of identifier was escaped correctly
        val syntaxCheck = checkStartSignSyntax(token)
        (None, Some(index, syntaxCheck))
      }
    }

  //creates a map of mentioned variables with corresponding index w.r.t the template
  val findVariableMentionIndexes: TemplateProperties => Seq[(String, (String, Int))] =
    processedTemplate => processedTemplate.filter(_._1.isDefined).map(token =>
      (unpackIdentifier(token._1.get._1),(token._1.get._1, token._1.get._2)))

  //creates an array of unescaped template tokens
  val createTemplate: TemplateProperties => Seq[String] => Array[String] =
    processedTemplate => tokens => {
      val escaped = processedTemplate.filter(_._2.isDefined).map(_._2.get._1)
      for((token, index) <- tokens.zipWithIndex) yield {
        //treat escaped characters
        if(escaped.contains(index)) treatEscapedStartSign(token)
        else token
      }
    }.toArray

  //process template
  lazy val processedTemplate: TemplateProperties = preProcessTemplate(tokens)
  lazy val variableMentionIndexes = findVariableMentionIndexes(processedTemplate)
  lazy val templateAsArrayOfStrings = createTemplate(processedTemplate)(tokens)

  //checks the syntax
  //is true, if template was syntactic correct
  val checkSytax: TemplateProperties => Boolean = processedTemplate =>
   processedTemplate.foldLeft(true){ (g,token) =>
    val check = if(token._1.isDefined) token._1.get._3 else token._2.get._2
    check && g
  }

  //rejects wrong formatted templates
  if(!checkSytax(processedTemplate))
    throw new TemplateSyntaxtException("The syntax of a template was incorrect: \n "
      + tokens.mkString(" "))


  //creates new string with interpolated variables
  def interpolate(variables: Map[String,String]) = {
    for((identifier,tuple) <- variableMentionIndexes) {
      variables.get(identifier) match {
        case Some(t) =>
          templateAsArrayOfStrings(tuple._2) =
            templateAsArrayOfStrings(tuple._2).replace(tuple._1, t)
        case None => throw new NotDefinedVariableException(s"Variable $identifier was not defined.")
      }
    }
    templateAsArrayOfStrings.mkString(" ")
  }

}

//Exceptions
case class TemplateSyntaxtException(msg: String) extends Exception(msg)
case class NotDefinedVariableException(msg: String) extends Exception(msg)

//Custom template implementation
class CustomTemplate(template: String) extends Template {

  def treatEscapedStartSign(s: String) = s.replaceAll("%%","%")

  def unpackIdentifier(s:String) = s.substring(2,s.length-1)

  def startSign = "%"

  def checkStartSignSyntax(s: String) = s.matches("(%%)+[^a-zA-Z_0-9%]*")

  def checkIdentifierSyntax(identifier: String) = identifier.matches("(%\\()(\\w|-|_)+(\\))")

  def variableRegex = "%\\(.*?\\)".r

  def tokens = template.split(" ")

}
