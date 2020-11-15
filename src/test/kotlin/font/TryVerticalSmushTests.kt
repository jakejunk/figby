package font

import helpers.cartesianProduct
import helpers.fakeFontWithVerticalRules
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TryVerticalSmushTests {
    /**
     * Calculating universal smushing for the vertical axis follows these rules:
     * 1. Hardblanks win over whitespace
     * 2. Visible characters win over hardblanks and whitespace
     * 3. Tiebreaker goes to the bottom character
     *
     * Occurs when no smushing rules are specified.
     */
    class Universal {
        private val hardblank = '$'.toInt()
        private val font = fakeFontWithVerticalRules(hardblank)

        @Test
        fun `trySmush returns hardblank when given hardblank and whitespace`() {
            val whitespace = ' '.toInt()

            assertEquals(hardblank, font.tryVerticalSmush(hardblank, whitespace))
            assertEquals(hardblank, font.tryVerticalSmush(whitespace, hardblank))
        }

        @Test
        fun `trySmush returns visible character when given visible character and whitespace`() {
            val visible = 'j'.toInt()
            val whitespace = ' '.toInt()

            assertEquals(visible, font.tryVerticalSmush(visible, whitespace))
            assertEquals(visible, font.tryVerticalSmush(whitespace, visible))
        }

        @Test
        fun `trySmush returns visible character when given visible character and hardblank`() {
            val visible = 'j'.toInt()

            assertEquals(visible, font.tryVerticalSmush(visible, hardblank))
            assertEquals(visible, font.tryVerticalSmush(hardblank, visible))
        }

        @Test
        fun `trySmush returns bottom character when both inputs are whitespace`() {
            val top = ' '.toInt()
            val bottom = '\t'.toInt()

            assertEquals(bottom, font.tryVerticalSmush(top, bottom))
        }

        @Test
        fun `trySmush returns hardblank when both inputs are hardblanks`() {
            assertEquals(hardblank, font.tryVerticalSmush(hardblank, hardblank))
        }

        @Test
        fun `trySmush returns bottom character when both inputs are visible characters`() {
            val top = 'j'.toInt()
            val bottom = 'k'.toInt()

            assertEquals(bottom, font.tryVerticalSmush(top, bottom))
        }
    }

    class EqualCharacter {
        private val font = fakeFontWithVerticalRules(0, VerticalSmushingRule.EqualCharacter)

        @Test
        fun `trySmush returns top character when top and bottom are equal`() {
            val expected = 'j'.toInt()

            assertEquals(expected, font.tryVerticalSmush(expected, expected))
        }

        @Test
        fun `trySmush returns null when top and bottom are not equal`() {
            val top = 'j'.toInt()
            val bottom = 'k'.toInt()

            assertNull(font.tryVerticalSmush(top, bottom))
        }
    }

    class Underscore {
        private val font = fakeFontWithVerticalRules(0, VerticalSmushingRule.Underscore)
        private val underscoreReplacers = listOf(
            '|', '/', '\\', '[', ']', '{', '}', '(', ')', '<', '>'
        ).map { it.toInt() }

        @TestFactory
        fun `trySmush returns correct character when smushed with an underscore`() = underscoreReplacers
            .map { input ->
                DynamicTest.dynamicTest("'$input' can smush '_'") {
                    val underscore = '_'.toInt()

                    assertEquals(input, font.tryVerticalSmush(input, underscore))
                    assertEquals(input, font.tryVerticalSmush(underscore, input))
                }
            }

        @Test
        fun `trySmush returns null for any other character combination`() {
            val underscore = '_'.toInt()
            val other = 'j'.toInt()

            assertNull(font.tryVerticalSmush(other, underscore))
            assertNull(font.tryVerticalSmush(underscore, other))
        }
    }

    class Hierarchy {
        private val font = fakeFontWithVerticalRules(0, VerticalSmushingRule.Hierarchy)
        private val charClassMap = mapOf(
            '|' to 1,
            '/' to 2, '\\' to 2,
            '[' to 3, ']' to 3,
            '{' to 4, '}' to 4,
            '(' to 5, ')' to 5,
            '<' to 6, '>' to 6,
        ).mapKeys { it.key.toInt() }

        @TestFactory
        fun `trySmush where top and bottom are part of a class`() = charClassMap.keys
            .toList()
            .let { keys ->
                cartesianProduct(keys, keys) { top, bottom ->
                    val topClass = charClassMap[top]!!
                    val bottomClass = charClassMap[bottom]!!
                    val result = font.tryVerticalSmush(top, bottom)

                    when {
                        topClass > bottomClass -> DynamicTest.dynamicTest("$top can smush $bottom") {
                            assertEquals(top, result)
                        }
                        topClass < bottomClass -> DynamicTest.dynamicTest("$top can be smushed by $bottom") {
                            assertEquals(bottom, result)
                        }
                        else -> DynamicTest.dynamicTest("$top cannot smush $bottom") {
                            assertNull(result)
                        }
                    }
                }
            }.toList()

        @Test
        fun `trySmush returns null if any input is not part of a class`() {
            val randomChar = 'j'.toInt()
            val classMember = '/'.toInt()

            assertNull(font.tryVerticalSmush(randomChar, classMember))
            assertNull(font.tryVerticalSmush(classMember, randomChar))
            assertNull(font.tryVerticalSmush(randomChar, randomChar))
        }
    }

    class HorizontalLine {
        private val font = fakeFontWithVerticalRules(0, VerticalSmushingRule.HorizontalLine)

        @Test
        fun `trySmush returns '=' when top is '-' and bottom is '_'`() {
            val expected = '='.toInt()
            val top = '-'.toInt()
            val bottom = '_'.toInt()

            assertEquals(expected, font.tryVerticalSmush(top, bottom))
        }

        @Test
        fun `trySmush returns '=' when top is '_' and bottom is '-'`() {
            val expected = '='.toInt()
            val top = '_'.toInt()
            val bottom = '-'.toInt()

            assertEquals(expected, font.tryVerticalSmush(top, bottom))
        }

        @Test
        fun `trySmush returns null when given any other combination of inputs`() {
            val hyphen = '-'.toInt()
            val underscore = '_'.toInt()
            val notAline = '$'.toInt()

            assertNull(font.tryVerticalSmush(hyphen, hyphen))
            assertNull(font.tryVerticalSmush(underscore, underscore))
            assertNull(font.tryVerticalSmush(hyphen, notAline))
            assertNull(font.tryVerticalSmush(underscore, notAline))
            assertNull(font.tryVerticalSmush(notAline, hyphen))
            assertNull(font.tryVerticalSmush(notAline, underscore))
            assertNull(font.tryVerticalSmush(notAline, notAline))
        }
    }

    class VerticalLine {
        private val font = fakeFontWithVerticalRules(0, VerticalSmushingRule.VerticalLine)

        @Test
        fun `trySmush returns vertical bar when top and bottom are also vertical bars`() {
            val verticalBar = '|'.toInt()

            assertEquals(verticalBar, font.tryVerticalSmush(verticalBar, verticalBar))
        }

        @Test
        fun `trySmush returns null when given any other combination of inputs`() {
            val verticalBar = '|'.toInt()
            val notAVerticalBar = '$'.toInt()

            assertNull(font.tryVerticalSmush(verticalBar, notAVerticalBar))
            assertNull(font.tryVerticalSmush(notAVerticalBar, verticalBar))
            assertNull(font.tryVerticalSmush(notAVerticalBar, notAVerticalBar))
        }
    }

    class MultipleRules {
        private val font = fakeFontWithVerticalRules(0,
            VerticalSmushingRule.EqualCharacter,
            VerticalSmushingRule.Underscore,
            VerticalSmushingRule.Hierarchy,
            VerticalSmushingRule.HorizontalLine,
            VerticalSmushingRule.VerticalLine
        )

        @Test
        fun `trySmush returns values when given any set of rule-matching inputs`() {
            val openParen = '('.toInt()
            val verticalBar = '|'.toInt()
            val underscore = '_'.toInt()

            assertEquals(verticalBar, font.tryVerticalSmush(verticalBar, verticalBar)) // Equal character
            assertEquals(verticalBar, font.tryVerticalSmush(underscore, verticalBar))  // Underscore
            assertEquals(openParen, font.tryVerticalSmush(openParen, verticalBar))     // Hierarchy
            assertEquals('='.toInt(), font.tryVerticalSmush(underscore, '-'.toInt()))  // Horizontal line
            assertEquals(verticalBar, font.tryVerticalSmush(verticalBar, verticalBar)) // Vertical line
        }
    }
}
