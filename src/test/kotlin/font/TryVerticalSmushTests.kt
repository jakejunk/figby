package font

import helpers.*
import helpers.generators.*
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll

class TryVerticalSmushTests : ShouldSpec({
    /**
     * Calculating universal smushing for the vertical axis follows these rules:
     * 1. Hardblanks win over whitespace
     * 2. Visible characters win over hardblanks and whitespace
     * 3. Tiebreaker goes to the bottom character
     *
     * Occurs when no smushing rules are specified.
     */
    context("No rules (universal)") {
        val hardblank = '$'.toInt()
        val font = fakeFontWithVerticalRules(hardblank)

        should("return hardblank when given hardblank and whitespace") {
            val whitespace = ' '.toInt()

            font.tryVerticalSmush(hardblank, whitespace) shouldBe hardblank
            font.tryVerticalSmush(whitespace, hardblank) shouldBe hardblank
        }

        should("return visible character when given visible character and whitespace") {
            val visible = 'j'.toInt()
            val whitespace = ' '.toInt()

            font.tryVerticalSmush(visible, whitespace) shouldBe visible
            font.tryVerticalSmush(whitespace, visible) shouldBe visible
        }

        should("return visible character when given visible character and hardblank") {
            val visible = 'j'.toInt()

            font.tryVerticalSmush(visible, hardblank) shouldBe visible
            font.tryVerticalSmush(hardblank, visible) shouldBe visible
        }

        should("return bottom character when both inputs are whitespace") {
            val top = ' '.toInt()
            val bottom = '\t'.toInt()

            font.tryVerticalSmush(top, bottom) shouldBe bottom
        }

        should("return hardblank when both inputs are hardblanks") {
            font.tryVerticalSmush(hardblank, hardblank) shouldBe hardblank
        }

        should("return bottom character when both inputs are visible characters") {
            val top = 'j'.toInt()
            val bottom = 'k'.toInt()

            font.tryVerticalSmush(top, bottom) shouldBe bottom
        }
    }

    context("Equal character") {
        val font = fakeFontWithVerticalRules(0, VerticalSmushingRule.EqualCharacter)

        should("return top character when top and bottom are equal") {
            val expected = 'j'.toInt()

            font.tryVerticalSmush(expected, expected) shouldBe expected
        }

        should("return null when top and bottom are not equal") {
            val top = 'j'.toInt()
            val bottom = 'k'.toInt()

            font.tryVerticalSmush(top, bottom) shouldBe null
        }
    }

    context("Underscore") {
        val underscore = '_'.toInt()
        val font = fakeFontWithVerticalRules(0, VerticalSmushingRule.Underscore)

        // Underscore replacers are |, /, \, [, ], {, }, (, ), < and >
        should("return the provided underscore replacer when smushed with an underscore") {
            checkAll(underscoreReplacers) { replacer ->
                font.tryVerticalSmush(replacer, underscore) shouldBe replacer
                font.tryVerticalSmush(underscore, replacer) shouldBe replacer
            }
        }

        should("return null for any other character combination") {
            checkAll(notUnderscoreReplacers) { other ->
                font.tryVerticalSmush(other, underscore) shouldBe null
                font.tryVerticalSmush(underscore, other) shouldBe null
            }
        }
    }

    context("Hierarchy") {
        val font = fakeFontWithVerticalRules(0, VerticalSmushingRule.Hierarchy)

        should("smush correctly when both inputs are part of a class") {
            checkAll(charClassMembers, charClassMembers) { (top, topClass), (bottom, bottomClass) ->
                val result = font.tryVerticalSmush(top, bottom)

                when {
                    topClass > bottomClass -> result shouldBe top
                    topClass < bottomClass -> result shouldBe bottom
                    else -> result shouldBe null
                }
            }
        }

        should("return null if any input is not part of a class") {
            checkAll(charClassMembers, notCharClassMembers) { (member, _), random ->
                font.tryVerticalSmush(random, member) shouldBe null
                font.tryVerticalSmush(member, random) shouldBe null
                font.tryVerticalSmush(random, random) shouldBe null
            }
        }
    }

    context("Horizontal line") {
        val font = fakeFontWithVerticalRules(0, VerticalSmushingRule.HorizontalLine)

        should("return '=' when top is '-' and bottom is '_'") {
            val expected = '='.toInt()
            val top = '-'.toInt()
            val bottom = '_'.toInt()

            font.tryVerticalSmush(top, bottom) shouldBe expected
        }

        should("return '=' when top is '_' and bottom is '-'") {
            val expected = '='.toInt()
            val top = '_'.toInt()
            val bottom = '-'.toInt()

            font.tryVerticalSmush(top, bottom) shouldBe expected
        }

        should("return null when given any other combination of inputs") {
            val hyphen = '-'.toInt()
            val underscore = '_'.toInt()
            val notAline = '$'.toInt()

            font.tryVerticalSmush(hyphen, hyphen) shouldBe null
            font.tryVerticalSmush(underscore, underscore) shouldBe null
            font.tryVerticalSmush(hyphen, notAline) shouldBe null
            font.tryVerticalSmush(underscore, notAline) shouldBe null
            font.tryVerticalSmush(notAline, hyphen) shouldBe null
            font.tryVerticalSmush(notAline, underscore) shouldBe null
            font.tryVerticalSmush(notAline, notAline) shouldBe null
        }
    }

    context("Vertical line") {
        val font = fakeFontWithVerticalRules(0, VerticalSmushingRule.VerticalLine)

        should("return vertical bar when top and bottom are also vertical bars") {
            val verticalBar = '|'.toInt()

            font.tryVerticalSmush(verticalBar, verticalBar) shouldBe verticalBar
        }

        should("return null when given any other combination of inputs") {
            val verticalBar = '|'.toInt()
            val notAVerticalBar = '$'.toInt()

            font.tryVerticalSmush(verticalBar, notAVerticalBar) shouldBe null
            font.tryVerticalSmush(notAVerticalBar, verticalBar) shouldBe null
            font.tryVerticalSmush(notAVerticalBar, notAVerticalBar) shouldBe null
        }
    }

    context("Multiple rules") {
        val font = fakeFontWithVerticalRules(
            0,
            VerticalSmushingRule.EqualCharacter,
            VerticalSmushingRule.Underscore,
            VerticalSmushingRule.Hierarchy,
            VerticalSmushingRule.HorizontalLine,
            VerticalSmushingRule.VerticalLine
        )

        should("return values when given any set of rule-matching inputs") {
            val openParen = '('.toInt()
            val verticalBar = '|'.toInt()
            val underscore = '_'.toInt()

            font.tryVerticalSmush(verticalBar, verticalBar) shouldBe verticalBar // Equal character
            font.tryVerticalSmush(underscore, verticalBar) shouldBe verticalBar  // Underscore
            font.tryVerticalSmush(openParen, verticalBar) shouldBe openParen     // Hierarchy
            font.tryVerticalSmush(underscore, '-'.toInt()) shouldBe '='.toInt()  // Horizontal line
            font.tryVerticalSmush(verticalBar, verticalBar) shouldBe verticalBar // Vertical line
        }
    }
})
