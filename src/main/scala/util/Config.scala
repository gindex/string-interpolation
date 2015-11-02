package util

import com.typesafe.config.ConfigFactory

import scala.util.Try

/**
 * Reads the config file and returns configurations.
 */
object Config {

  //load congigs
  val config = ConfigFactory.load()

  //read config values
  lazy val endLine = Try(config.getString("separator.line-end")).getOrElse("\n")
  lazy val lineSeparator = Try(config.getString("separator.name-value")).getOrElse("=")

  lazy val variableStartSign = Try(config.getString("syntax.start-sign")).getOrElse("%")
  lazy val escapedStartSign = Try(config.getString("syntax.escaped-start-sign")).getOrElse("%%")
  lazy val openingBracket = Try(config.getString("syntax.opening-bracket")).getOrElse(")")
  lazy val closingBracket = Try(config.getString("syntax.opening-bracket")).getOrElse("(")
}
