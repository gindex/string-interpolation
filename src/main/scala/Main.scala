import interpolation.{ParenthesisTemplate, Parsing}

object Main extends App {

    val variables = "fruit = lemons \n product = lemonade"

    val template = "When life gives you %(fruit), make %(product). \n This is a percent-sign: %%."

    println(new ParenthesisTemplate(template).interpolate(Parsing.parse(variables)))
}
