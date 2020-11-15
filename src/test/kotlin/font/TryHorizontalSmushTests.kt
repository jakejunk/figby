package font

import helpers.cartesianProduct
import helpers.fakeFontWithHorizontalRules
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TryHorizontalSmushTests {
    /**
     * Calculating universal smushing for the horizontal axis follows these rules:
     * 1. Hardblanks win over whitespace
     * 2. Visible characters win over hardblanks and whitespace
     * 3. Tiebreaker goes to the latter character
     *
     * Occurs when no smushing rules are specified.
     */
    class Universal {
        private val hardblank = '$'.toInt()
        private val font = fakeFontWithHorizontalRules(hardblank)

        @Test
        fun `trySmush returns hardblank when given hardblank and whitespace`() {
            val whitespace = ' '.toInt()

            assertEquals(hardblank, font.tryHorizontalSmush(hardblank, whitespace))
            assertEquals(hardblank, font.tryHorizontalSmush(whitespace, hardblank))
        }

        @Test
        fun `trySmush returns visible character when given visible character and whitespace`() {
            val visible = 'j'.toInt()
            val whitespace = ' '.toInt()

            assertEquals(visible, font.tryHorizontalSmush(visible, whitespace))
            assertEquals(visible, font.tryHorizontalSmush(whitespace, visible))
        }

        @Test
        fun `trySmush returns visible character when given visible character and hardblank`() {
            val visible = 'j'.toInt()

            assertEquals(visible, font.tryHorizontalSmush(visible, hardblank))
            assertEquals(visible, font.tryHorizontalSmush(hardblank, visible))
        }

        @Test
        fun `trySmush returns latter character when both inputs are whitespace`() {
            val left = ' '.toInt()
            val right = '\t'.toInt()

            assertEquals(right, font.tryHorizontalSmush(left, right))
        }

        @Test
        fun `trySmush returns hardblank when both inputs are hardblanks`() {
            assertEquals(hardblank, font.tryHorizontalSmush(hardblank, hardblank))
        }

        @Test
        fun `trySmush returns latter character when both inputs are visible characters`() {
            val left = 'j'.toInt()
            val right = 'k'.toInt()

            assertEquals(right, font.tryHorizontalSmush(left, right))
        }
    }

    class EqualCharacter {
        private val hardblank = '$'.toInt()
        private val font = fakeFontWithHorizontalRules(hardblank, HorizontalSmushingRule.EqualCharacter)

        @Test
        fun `trySmush returns left character when left and right are equal`() {
            val expected = 'j'.toInt()

            assertEquals(expected, font.tryHorizontalSmush(expected, expected))
        }

        @Test
        fun `trySmush returns null when left and right are not equal`() {
            val left = 'j'.toInt()
            val right = 'k'.toInt()

            assertNull(font.tryHorizontalSmush(left, right))
        }

        @Test
        fun `trySmush returns null when left and right both equal the hardblank`() {
            assertNull(font.tryHorizontalSmush(hardblank, hardblank))
        }
    }

    class Underscore {
        private val font = fakeFontWithHorizontalRules(0, HorizontalSmushingRule.Underscore)
        private val underscoreReplacers = listOf(
            '|', '/', '\\', '[', ']', '{', '}', '(', ')', '<', '>'
        ).map { it.toInt() }

        @TestFactory
        fun `trySmush returns correct character when smushed with an underscore`() = underscoreReplacers
            .map { input ->
                DynamicTest.dynamicTest("'$input' can smush '_'") {
                    val underscore = '_'.toInt()

                    assertEquals(input, font.tryHorizontalSmush(input, underscore))
                    assertEquals(input, font.tryHorizontalSmush(underscore, input))
                }
            }

        @Test
        fun `trySmush returns null for any other character combination`() {
            val underscore = '_'.toInt()
            val other = 'j'.toInt()

            assertNull(font.tryHorizontalSmush(underscore, other))
        }
    }

    class Hierarchy {
        private val font = fakeFontWithHorizontalRules(0, HorizontalSmushingRule.Hierarchy)
        private val charClassMap = mapOf(
            '|' to 1,
            '/' to 2, '\\' to 2,
            '[' to 3, ']' to 3,
            '{' to 4, '}' to 4,
            '(' to 5, ')' to 5,
            '<' to 6, '>' to 6,
        ).mapKeys { it.key.toInt() }

        @TestFactory
        fun `trySmush where left and right are part of a class`() = charClassMap.keys
            .toList()
            .let { keys ->
                cartesianProduct(keys, keys) { left, right ->
                    val leftClass = charClassMap.getValue(left)
                    val rightClass = charClassMap.getValue(right)
                    val result = font.tryHorizontalSmush(left, right)

                    when {
                        leftClass > rightClass -> DynamicTest.dynamicTest("$left can smush $right") {
                            assertEquals(left, result)
                        }
                        leftClass < rightClass -> DynamicTest.dynamicTest("$left can be smushed by $right") {
                            assertEquals(right, result)
                        }
                        else -> DynamicTest.dynamicTest("$left cannot smush $right") {
                            assertNull(result)
                        }
                    }
                }
            }.toList()

        @Test
        fun `trySmush returns null if any input is not part of a class`() {
            val randomChar = 'j'.toInt()
            val classMember = '/'.toInt()

            assertNull(font.tryHorizontalSmush(randomChar, classMember))
            assertNull(font.tryHorizontalSmush(classMember, randomChar))
            assertNull(font.tryHorizontalSmush(randomChar, randomChar))
        }
    }

    class OppositePair {
        private val font = fakeFontWithHorizontalRules(0, HorizontalSmushingRule.OppositePair)
        private val pairs = mapOf(
            '[' to ']',
            ']' to '[',
            '{' to '}',
            '}' to '{',
            '(' to ')',
            ')' to '(',
        ).entries.associate { (key, value) ->
            key.toInt() to value.toInt()
        }

        // Fun fact: This function will not compile if the signature contains a '|' character
        @TestFactory
        fun `trySmush returns vertical bar when left and right are opposing brackets, braces, or parenthesis`() = pairs
            .map { (left, right) ->
                DynamicTest.dynamicTest("'$left' can smush '$right'") {
                    val expected = '|'.toInt()

                    assertEquals(expected, font.tryHorizontalSmush(left, right))
                }
            }

        @Test
        fun `trySmush returns null when left and right do not form a valid pair`() {
            val left = '{'.toInt()
            val right = ']'.toInt()

            assertNull(font.tryHorizontalSmush(left, right))
        }
    }

    class BigX {
        private val font = fakeFontWithHorizontalRules(0, HorizontalSmushingRule.BigX)

        @Test
        fun `trySmush returns vertical bar when left is forward slash and right is backslash`() {
            val expected = '|'.toInt()
            val left = '/'.toInt()
            val right = '\\'.toInt()

            assertEquals(expected, font.tryHorizontalSmush(left, right))
        }

        @Test
        fun `trySmush returns 'Y' when left is backslash and right is forward slash`() {
            val expected = 'Y'.toInt()
            val left = '\\'.toInt()
            val right = '/'.toInt()

            assertEquals(expected, font.tryHorizontalSmush(left, right))
        }

        @Test
        fun `trySmush returns 'X' when left is greater-than and right is less-than`() {
            val expected = 'X'.toInt()
            val left = '>'.toInt()
            val right = '<'.toInt()

            assertEquals(expected, font.tryHorizontalSmush(left, right))
        }

        @Test
        fun `trySmush returns null when left and right do not form a valid pair`() {
            val left = '<'.toInt()
            val right = '>'.toInt()

            assertNull(font.tryHorizontalSmush(left, right))
        }
    }

    class Hardblank {
        private val hardblank = '$'.toInt()
        private val font = fakeFontWithHorizontalRules(hardblank, HorizontalSmushingRule.Hardblank)

        @Test
        fun `trySmush returns hardblank when all inputs are hardblanks`() {
            assertEquals(hardblank, font.tryHorizontalSmush(hardblank, hardblank))
        }

        @Test
        fun `trySmush returns null when given any other combination of inputs`() {
            val randomChar = 'j'.toInt()

            assertNull(font.tryHorizontalSmush(randomChar, hardblank))
            assertNull(font.tryHorizontalSmush(hardblank, randomChar))
        }
    }

    class MultipleRules {
        private val hardblank = '$'.toInt()
        private val font = fakeFontWithHorizontalRules(hardblank,
            HorizontalSmushingRule.EqualCharacter,
            HorizontalSmushingRule.Underscore,
            HorizontalSmushingRule.Hierarchy,
            HorizontalSmushingRule.OppositePair,
            HorizontalSmushingRule.BigX,
            HorizontalSmushingRule.Hardblank
        )

        @Test
        fun `trySmush returns values when given any set of rule-matching inputs`() {
            val lessThan = '<'.toInt()
            val greaterThan = '>'.toInt()
            val openParen = '('.toInt()
            val closeParen = ')'.toInt()

            assertEquals(lessThan, font.tryHorizontalSmush(lessThan, lessThan))       // Equal character
            assertEquals(lessThan, font.tryHorizontalSmush('_'.toInt(), lessThan))    // Underscore
            assertEquals(lessThan, font.tryHorizontalSmush(openParen, lessThan))      // Hierarchy
            assertEquals('|'.toInt(), font.tryHorizontalSmush(openParen, closeParen)) // Opposite pair
            assertEquals('X'.toInt(), font.tryHorizontalSmush(greaterThan, lessThan)) // Big X
            assertEquals(hardblank, font.tryHorizontalSmush(hardblank, hardblank))    // Hardblank
        }
    }
}
