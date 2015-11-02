package examples

import interpolation.{CustomTemplate, CustomParsing}

object Main extends App {

    val variables = "fruit = lemons \n product = lemonade \n car = volkswagen"

    val template = "When life gives you %(fruit), make %(product). \nThis is a percent-sign: %%."

    //creates following string:
    //  ￼￼￼When life gives you lemons, make lemonade.
    //  This is a percent-sign: %.
    println(new CustomTemplate(template).interpolate(CustomParsing.parse(variables)))

}
