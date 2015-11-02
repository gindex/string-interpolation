# Simple string interpolation in scala

This library provides functionality to perform string interpolation.
At the first stage templates are parsed and mentions of variables are extracted.
Next, variables are interpolated into template strings.

Example
-----

    $ //define variables
    $ val variables = "fruit = lemons \n product = lemonade"
    $
    $ //define template
    $ val template = "When life gives you %(fruit), make %(product).$
    $
    $ //perform string interpolation
    $ val interpolated = new CustomTemplate(template).interpolate(CustomParsing.parse(variables))
    $
    $ //this prints: When life gives you lemons, make lemonade.
    $ println(interpolatedString)
