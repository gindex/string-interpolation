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

  //definies regex for extracting varibale identifier
  def variableRegex(): Regex

  //split template string into tokens
  def tokens():Seq[(String,Int)]

  type TemplateProperties = Seq[(Option[(String,Int,Boolean)], Option[(Int,Boolean)])]

  //extracts varibale mentions from template string and checks syntax
  lazy val procesedTemplate: TemplateProperties =
    for((token, index) <- tokens if token.contains(startSign)) yield {
      val extractVariableIdentifier = variableRegex.findFirstIn(token)
      //test if the token is a variable identifier
      if(extractVariableIdentifier.isDefined) {
        //syntax check
        val syntaxCheck = checkIdentifierSyntax( extractVariableIdentifier.get)
        (Some((extractVariableIdentifier.get, index, syntaxCheck)), None)
      } else {
        //test if variable identifier sign was escaped correctly
        val syntaxCheck = checkStartSignSyntax(token)
        (None, Some(index, syntaxCheck))
      }
  }

  //creates a map of mentiond variables with corresponding index within the template
  lazy val variableMentionIndexes = procesedTemplate.filter(_._1.isDefined).map(token =>
    (unpackIdentifier(token._1.get._1),(token._1.get._1, token._1.get._2))).toMap

  //create a sequence of unescaped template template
  lazy val templateAsArrayOfStrings = {
    val escaped = procesedTemplate.filter(_._2.isDefined).map(_._2.get._1)
    for((token, index) <- tokens) yield {
      //treat escaped characters
      if(escaped.contains(index)) treatEscapedStartSign(token)
      else token
    }
  }.toArray

  //creates new string with interpolated variables
  def interpolate(variables: Map[String,String]) = {
    for(identifier <- variables.keys) {
      variableMentionIndexes.get(identifier) match {
        case Some(t) =>
          templateAsArrayOfStrings(t._2) =
            templateAsArrayOfStrings(t._2).replace(t._1, variables.get(identifier).get)
        case None => throw new NonDefinedVariableException(s"Variable $identifier was not defined.")
      }
    }
    templateAsArrayOfStrings.mkString(" ")
  }

  //is true, if template was syntactic correct
  lazy val isSyntacticCorrect = procesedTemplate.foldLeft(true){ (g,token) =>
    val check =  if(token._1.isDefined) token._1.get._3 else token._2.get._2
    check && g
  }

  //reject wrong formatted templates
  if(!isSyntacticCorrect)
    throw new TemplateSyntaxtException("The syntax of the template is incorrect: \n "
      + tokens.map(_._1).mkString(" "))

}

class ParenthesisTemplate(template: String) extends Template {

  def treatEscapedStartSign(s: String) = s.replaceAll("%%","%")

  def unpackIdentifier(s:String) = s.substring(2,s.length-1)

  def startSign = "%"

  def checkStartSignSyntax(s: String) = s.matches("(%%)+(\\W)*")

  def checkIdentifierSyntax(identifier: String) = identifier.matches("(%\\()(\\w|-|_)+(\\))")

  def variableRegex = "%\\(.*?\\)".r

  def tokens = template.split(" ").zipWithIndex

}

case class TemplateSyntaxtException(msg: String) extends Exception(msg)

case class NonDefinedVariableException(msg: String) extends Exception(msg)
