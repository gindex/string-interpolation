import interpolation.{ParenthesisTemplate, CustomParsing}

object Main extends App {

    val variables = "fruit = lemons \nproduct = lemonade"

    val template = "When life gives you %(fruit), make %(product). \nThis is a percent-sign: %%."

    println(new ParenthesisTemplate(template).interpolate(CustomParsing.parse(variables)))
}
