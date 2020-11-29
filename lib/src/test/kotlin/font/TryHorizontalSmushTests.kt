package font

import helpers.*
import helpers.generators.*
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll
import layout.HorizontalSmushingRule

class TryHorizontalSmushTests : ShouldSpec({
    /**
     * Calculating universal smushing for the horizontal axis follows these rules:
     * 1. Hardblanks win over whitespace
     * 2. Visible characters win over hardblanks and whitespace
     * 3. Tiebreaker goes to the latter character
     *
     * Occurs when no smushing rules are specified.
     */
    context("No rules (universal)") {
        val hardblank = '$'.toInt()
        val font = fakeFontWithHorizontalRules(hardblank)

        should("return hardblank when given hardblank and whitespace") {
            val whitespace = ' '.toInt()

            font.tryHorizontalSmush(hardblank, whitespace) shouldBe hardblank
            font.tryHorizontalSmush(whitespace, hardblank) shouldBe hardblank
        }

        should("return visible character when given visible character and whitespace") {
            val visible = 'j'.toInt()
            val whitespace = ' '.toInt()

            font.tryHorizontalSmush(visible, whitespace) shouldBe visible
            font.tryHorizontalSmush(whitespace, visible) shouldBe visible
        }

        should("return visible character when given visible character and hardblank") {
            val visible = 'j'.toInt()

            font.tryHorizontalSmush(visible, hardblank) shouldBe visible
            font.tryHorizontalSmush(hardblank, visible) shouldBe visible
        }

        should("return latter character when both inputs are whitespace") {
            val left = ' '.toInt()
            val right = '\t'.toInt()

            font.tryHorizontalSmush(left, right) shouldBe right
        }

        should("return hardblank when both inputs are hardblanks") {
            font.tryHorizontalSmush(hardblank, hardblank) shouldBe hardblank
        }

        should("return latter character when both inputs are visible characters") {
            val left = 'j'.toInt()
            val right = 'k'.toInt()

            font.tryHorizontalSmush(left, right) shouldBe right
        }
    }

    context("Equal character") {
        val hardblank = '$'.toInt()
        val font = fakeFontWithHorizontalRules(hardblank, HorizontalSmushingRule.EqualCharacter)

        should("return left character when left and right are equal") {
            val expected = 'j'.toInt()

            font.tryHorizontalSmush(expected, expected) shouldBe expected
        }

        should("return null when left and right are not equal") {
            val left = 'j'.toInt()
            val right = 'k'.toInt()

            font.tryHorizontalSmush(left, right) shouldBe null
        }

        should("return null when left and right both equal the hardblank") {
            font.tryHorizontalSmush(hardblank, hardblank) shouldBe null
        }
    }

    context("Underscore") {
        val underscore = '_'.toInt()
        val font = fakeFontWithHorizontalRules(0, HorizontalSmushingRule.Underscore)

        // Underscore replacers are |, /, \, [, ], {, }, (, ), < and >
        should("return the provided underscore replacer when smushed with an underscore") {
            checkAll(underscoreReplacers) { replacer ->
                font.tryHorizontalSmush(replacer, underscore) shouldBe replacer
                font.tryHorizontalSmush(underscore, replacer) shouldBe replacer
            }
        }

        should("return null for any other character combination") {
            checkAll(notUnderscoreReplacers) { other ->
                font.tryHorizontalSmush(other, underscore) shouldBe null
                font.tryHorizontalSmush(underscore, other) shouldBe null
            }
        }
    }

    context("Hierarchy") {
        val font = fakeFontWithHorizontalRules(0, HorizontalSmushingRule.Hierarchy)

        should("smush correctly when both inputs are part of a class") {
            checkAll(charClassMembers, charClassMembers) { (left, leftClass), (right, rightClass) ->
                val result = font.tryHorizontalSmush(left, right)

                when {
                    leftClass > rightClass -> result shouldBe left
                    leftClass < rightClass -> result shouldBe right
                    else -> result shouldBe null
                }
            }
        }

        should("return null if any input is not part of a class") {
            checkAll(charClassMembers, notCharClassMembers) { (member, _), random ->
                font.tryHorizontalSmush(random, member) shouldBe null
                font.tryHorizontalSmush(member, random) shouldBe null
                font.tryHorizontalSmush(random, random) shouldBe null
            }
        }
    }

    context("Opposite pair") {
        val font = fakeFontWithHorizontalRules(0, HorizontalSmushingRule.OppositePair)

        // Fun fact: This function will not compile if the signature contains a '|' character
        should("return vertical bar when left and right are opposing brackets, braces, or parenthesis") {
            checkAll(oppositePairs) { (left, right) ->
                font.tryHorizontalSmush(left, right) shouldBe '|'.toInt()
            }
        }

        should("return null when left and right do not form a valid pair") {
            checkAll(notOppositePairs) { (left, right) ->
                font.tryHorizontalSmush(left, right) shouldBe null
            }
        }
    }

    context("Big X") {
        val forwardSlash = '/'.toInt()
        val backslash = '\\'.toInt()
        val greaterThan = '>'.toInt()
        val lessThan = '<'.toInt()
        val font = fakeFontWithHorizontalRules(0, HorizontalSmushingRule.BigX)

        should("return vertical bar when left is forward slash and right is backslash") {
            val verticalBar = '|'.toInt()

            font.tryHorizontalSmush(forwardSlash, backslash) shouldBe verticalBar
        }

        should("return 'Y' when left is backslash and right is forward slash") {
            val letterY = 'Y'.toInt()

            font.tryHorizontalSmush(backslash, forwardSlash) shouldBe letterY
        }

        should("return 'X' when left is greater-than and right is less-than") {
            val letterX = 'X'.toInt()

            font.tryHorizontalSmush(greaterThan, lessThan) shouldBe letterX
        }

        should("return null when left and right do not form a valid pair") {
            font.tryHorizontalSmush(lessThan, greaterThan) shouldBe null
        }
    }

    context("Hardblank") {
        val hardblank = '$'.toInt()
        val font = fakeFontWithHorizontalRules(hardblank, HorizontalSmushingRule.Hardblank)

        should("return hardblank when all inputs are hardblanks") {
            font.tryHorizontalSmush(hardblank, hardblank) shouldBe hardblank
        }

        should("return null when given any other combination of inputs") {
            val randomChar = 'j'.toInt()

            font.tryHorizontalSmush(randomChar, hardblank) shouldBe null
            font.tryHorizontalSmush(hardblank, randomChar) shouldBe null
        }
    }

    context("Multiple rules") {
        val hardblank = '$'.toInt()
        val font = fakeFontWithHorizontalRules(
            hardblank,
            HorizontalSmushingRule.EqualCharacter,
            HorizontalSmushingRule.Underscore,
            HorizontalSmushingRule.Hierarchy,
            HorizontalSmushingRule.OppositePair,
            HorizontalSmushingRule.BigX,
            HorizontalSmushingRule.Hardblank
        )

        should("return correct values when given any set of rule-matching inputs") {
            val lessThan = '<'.toInt()
            val greaterThan = '>'.toInt()
            val openParen = '('.toInt()
            val closeParen = ')'.toInt()

            font.tryHorizontalSmush(lessThan, lessThan) shouldBe lessThan       // Equal character
            font.tryHorizontalSmush('_'.toInt(), lessThan) shouldBe lessThan    // Underscore
            font.tryHorizontalSmush(openParen, lessThan) shouldBe lessThan      // Hierarchy
            font.tryHorizontalSmush(openParen, closeParen) shouldBe '|'.toInt() // Opposite pair
            font.tryHorizontalSmush(greaterThan, lessThan) shouldBe 'X'.toInt() // Big X
            font.tryHorizontalSmush(hardblank, hardblank) shouldBe hardblank    // Hardblank
        }
    }
})
