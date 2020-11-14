package layout

import cartesianProduct
import font.HorizontalSmushingRule
import font.Smusher
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import kotlin.test.assertEquals
import kotlin.test.assertNull

class HorizontalSmusherTests {
    /**
     * Calculating universal smushing for the horizontal axis follows these rules:
     * 1. Hardblanks win over whitespace
     * 2. Visible characters win over hardblanks and whitespace
     * 3. Tiebreaker goes to the latter character
     *
     * Occurs when no smushing rules are specified.
     */
    class Universal {
        private val smusher = Smusher()

        @Test
        fun `trySmush returns hardblank when given hardblank and whitespace`() {
            val hardblank = '$'.toInt()
            val whitespace = ' '.toInt()

            assertEquals(hardblank, smusher.tryHorizontalSmush(hardblank, whitespace, hardblank))
            assertEquals(hardblank, smusher.tryHorizontalSmush(whitespace, hardblank, hardblank))
        }

        @Test
        fun `trySmush returns visible character when given visible character and whitespace`() {
            val visible = 'j'.toInt()
            val whitespace = ' '.toInt()

            assertEquals(visible, smusher.tryHorizontalSmush(visible, whitespace, 0))
            assertEquals(visible, smusher.tryHorizontalSmush(whitespace, visible, 0))
        }

        @Test
        fun `trySmush returns visible character when given visible character and hardblank`() {
            val visible = 'j'.toInt()
            val hardblank = '$'.toInt()

            assertEquals(visible, smusher.tryHorizontalSmush(visible, hardblank, hardblank))
            assertEquals(visible, smusher.tryHorizontalSmush(hardblank, visible, hardblank))
        }

        @Test
        fun `trySmush returns latter character when both inputs are whitespace`() {
            val left = ' '.toInt()
            val right = '\t'.toInt()

            assertEquals(right, smusher.tryHorizontalSmush(left, right, 0))
        }

        @Test
        fun `trySmush returns hardblank when both inputs are hardblanks`() {
            val hardblank = '$'.toInt()

            assertEquals(hardblank, smusher.tryHorizontalSmush(hardblank, hardblank, hardblank))
        }

        @Test
        fun `trySmush returns latter character when both inputs are visible characters`() {
            val left = 'j'.toInt()
            val right = 'k'.toInt()

            assertEquals(right, smusher.tryHorizontalSmush(left, right, 0))
        }
    }

    class EqualCharacter {
        private val smusher = Smusher(horizontalRules = listOf(HorizontalSmushingRule.EqualCharacter))

        @Test
        fun `trySmush returns left character when left and right are equal`() {
            val expected = 'j'.toInt()
            val result = smusher.tryHorizontalSmush(expected, expected, 0)

            assertEquals(expected, result)
        }

        @Test
        fun `trySmush returns null when left and right are not equal`() {
            val left = 'j'.toInt()
            val right = 'k'.toInt()
            val result = smusher.tryHorizontalSmush(left, right, 0)

            assertNull(result)
        }

        @Test
        fun `trySmush returns null when left and right both equal the hardblank`() {
            val char = 'j'.toInt()
            val result = smusher.tryHorizontalSmush(char, char, hardblank = char)

            assertNull(result)
        }
    }

    class Underscore {
        private val smusher = Smusher(horizontalRules = listOf(HorizontalSmushingRule.Underscore))
        private val underscoreReplacers = listOf(
            '|', '/', '\\', '[', ']', '{', '}', '(', ')', '<', '>'
        ).map { it.toInt() }

        @TestFactory
        fun `trySmush returns correct character when smushed with an underscore`() = underscoreReplacers
            .map { input ->
                DynamicTest.dynamicTest("'$input' can smush '_'") {
                    val underscore = '_'.toInt()
                    val resultLeft = smusher.tryHorizontalSmush(input, underscore, 0)
                    val resultRight = smusher.tryHorizontalSmush(underscore, input, 0)

                    assertEquals(input, resultLeft)
                    assertEquals(input, resultRight)
                }
            }

        @Test
        fun `trySmush returns null for any other character combination`() {
            val underscore = '_'.toInt()
            val other = 'j'.toInt()
            val result = smusher.tryHorizontalSmush(underscore, other, 0)

            assertNull(result)
        }
    }

    class Hierarchy {
        private val smusher = Smusher(horizontalRules = listOf(HorizontalSmushingRule.Hierarchy))
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
                    val leftClass = charClassMap[left]!!
                    val rightClass = charClassMap[right]!!
                    val result = smusher.tryHorizontalSmush(left, right, 0)

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

            assertNull(smusher.tryHorizontalSmush(randomChar, classMember, 0))
            assertNull(smusher.tryHorizontalSmush(classMember, randomChar, 0))
            assertNull(smusher.tryHorizontalSmush(randomChar, randomChar, 0))
        }
    }

    class OppositePair {
        private val smusher = Smusher(horizontalRules = listOf(HorizontalSmushingRule.OppositePair))
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
                    val result = smusher.tryHorizontalSmush(left, right, 0)

                    assertEquals(expected, result)
                }
            }

        @Test
        fun `trySmush returns null when left and right do not form a valid pair`() {
            val left = '{'.toInt()
            val right = ']'.toInt()
            val result = smusher.tryHorizontalSmush(left, right, 0)

            assertNull(result)
        }
    }

    class BigX {
        private val smusher = Smusher(horizontalRules = listOf(HorizontalSmushingRule.BigX))

        @Test
        fun `trySmush returns vertical bar when left is forward slash and right is backslash`() {
            val expected = '|'.toInt()
            val left = '/'.toInt()
            val right = '\\'.toInt()
            val result = smusher.tryHorizontalSmush(left, right, 0)

            assertEquals(expected, result)
        }

        @Test
        fun `trySmush returns 'Y' when left is backslash and right is forward slash`() {
            val expected = 'Y'.toInt()
            val left = '\\'.toInt()
            val right = '/'.toInt()
            val result = smusher.tryHorizontalSmush(left, right, 0)

            assertEquals(expected, result)
        }

        @Test
        fun `trySmush returns 'X' when left is greater-than and right is less-than`() {
            val expected = 'X'.toInt()
            val left = '>'.toInt()
            val right = '<'.toInt()
            val result = smusher.tryHorizontalSmush(left, right, 0)

            assertEquals(expected, result)
        }

        @Test
        fun `trySmush returns null when left and right do not form a valid pair`() {
            val left = '<'.toInt()
            val right = '>'.toInt()
            val result = smusher.tryHorizontalSmush(left, right, 0)

            assertNull(result)
        }
    }

    class Hardblank {
        private val smusher = Smusher(horizontalRules = listOf(HorizontalSmushingRule.Hardblank))

        @Test
        fun `trySmush returns hardblank when all inputs are hardblanks`() {
            val hardblank = '$'.toInt()
            val result = smusher.tryHorizontalSmush(hardblank, hardblank, hardblank)

            assertEquals(hardblank, result)
        }

        @Test
        fun `trySmush returns null when given any other combination of inputs`() {
            val randomChar = 'j'.toInt()
            val hardblank = '$'.toInt()

            assertNull(smusher.tryHorizontalSmush(randomChar, hardblank, hardblank))
            assertNull(smusher.tryHorizontalSmush(hardblank, randomChar, hardblank))
            assertNull(smusher.tryHorizontalSmush(hardblank, hardblank, randomChar))
        }
    }

    class MultipleRules {
        private val smusher = Smusher(
            horizontalRules = listOf(
                HorizontalSmushingRule.EqualCharacter,
                HorizontalSmushingRule.Underscore,
                HorizontalSmushingRule.Hierarchy,
                HorizontalSmushingRule.OppositePair,
                HorizontalSmushingRule.BigX,
                HorizontalSmushingRule.Hardblank
            )
        )

        @Test
        fun `trySmush returns values when given any set of rule-matching inputs`() {
            val lessThan = '<'.toInt()
            val greaterThan = '>'.toInt()
            val openParen = '('.toInt()
            val closeParen = ')'.toInt()

            assertEquals(lessThan, smusher.tryHorizontalSmush(lessThan, lessThan, 0))        // Equal character
            assertEquals(lessThan, smusher.tryHorizontalSmush('_'.toInt(), lessThan, 0))     // Underscore
            assertEquals(lessThan, smusher.tryHorizontalSmush(openParen, lessThan, 0))       // Hierarchy
            assertEquals('|'.toInt(), smusher.tryHorizontalSmush(openParen, closeParen, 0))  // Opposite pair
            assertEquals('X'.toInt(), smusher.tryHorizontalSmush(greaterThan, lessThan, 0))  // Big X
            assertEquals(lessThan, smusher.tryHorizontalSmush(lessThan, lessThan, lessThan)) // Hardblank
        }
    }
}
