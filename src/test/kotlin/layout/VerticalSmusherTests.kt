package layout

import cartesianProduct
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import kotlin.test.assertEquals
import kotlin.test.assertNull

class VerticalSmusherTests {
    /**
     * Calculating universal smushing for the vertical axis follows these rules:
     * 1. Hardblanks win over whitespace
     * 2. Visible characters win over hardblanks and whitespace
     * 3. Tiebreaker goes to the bottom character
     *
     * Occurs when no smushing rules are specified.
     */
    class Universal {
        private val smusher = VerticalSmusher()

        @Test
        fun `trySmush returns hardblank when given hardblank and whitespace`() {
            val hardblank = '$'.toInt()
            val whitespace = ' '.toInt()

            assertEquals(hardblank, smusher.trySmush(hardblank, whitespace, hardblank))
            assertEquals(hardblank, smusher.trySmush(whitespace, hardblank, hardblank))
        }

        @Test
        fun `trySmush returns visible character when given visible character and whitespace`() {
            val visible = 'j'.toInt()
            val whitespace = ' '.toInt()

            assertEquals(visible, smusher.trySmush(visible, whitespace, 0))
            assertEquals(visible, smusher.trySmush(whitespace, visible, 0))
        }

        @Test
        fun `trySmush returns visible character when given visible character and hardblank`() {
            val visible = 'j'.toInt()
            val hardblank = '$'.toInt()

            assertEquals(visible, smusher.trySmush(visible, hardblank, hardblank))
            assertEquals(visible, smusher.trySmush(hardblank, visible, hardblank))
        }

        @Test
        fun `trySmush returns bottom character when both inputs are whitespace`() {
            val top = ' '.toInt()
            val bottom = '\t'.toInt()

            assertEquals(bottom, smusher.trySmush(top, bottom, 0))
        }

        @Test
        fun `trySmush returns hardblank when both inputs are hardblanks`() {
            val hardblank = '$'.toInt()

            assertEquals(hardblank, smusher.trySmush(hardblank, hardblank, hardblank))
        }

        @Test
        fun `trySmush returns bottom character when both inputs are visible characters`() {
            val top = 'j'.toInt()
            val bottom = 'k'.toInt()

            assertEquals(bottom, smusher.trySmush(top, bottom, 0))
        }
    }

    class EqualCharacter {
        private val smusher = VerticalSmusher(VerticalSmusher.Rule.EqualCharacter)

        @Test
        fun `trySmush returns top character when top and bottom are equal`() {
            val expected = 'j'.toInt()
            val result = smusher.trySmush(expected, expected, 0)

            assertEquals(expected, result)
        }

        @Test
        fun `trySmush returns null when top and bottom are not equal`() {
            val top = 'j'.toInt()
            val bottom = 'k'.toInt()
            val result = smusher.trySmush(top, bottom, 0)

            assertNull(result)
        }
    }

    class Underscore {
        private val smusher = VerticalSmusher(VerticalSmusher.Rule.Underscore)
        private val underscoreReplacers = listOf(
            '|', '/', '\\', '[', ']', '{', '}', '(', ')', '<', '>'
        ).map { it.toInt() }

        @TestFactory
        fun `trySmush returns correct character when smushed with an underscore`() = underscoreReplacers
            .map { input ->
                DynamicTest.dynamicTest("'$input' can smush '_'") {
                    val underscore = '_'.toInt()

                    assertEquals(input, smusher.trySmush(input, underscore, 0))
                    assertEquals(input, smusher.trySmush(underscore, input, 0))
                }
            }

        @Test
        fun `trySmush returns null for any other character combination`() {
            val underscore = '_'.toInt()
            val other = 'j'.toInt()

            assertNull(smusher.trySmush(other, underscore, 0))
            assertNull(smusher.trySmush(underscore, other, 0))
        }
    }

    class Hierarchy {
        private val smusher = VerticalSmusher(VerticalSmusher.Rule.Hierarchy)
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
                    val result = smusher.trySmush(top, bottom, 0)

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

            assertNull(smusher.trySmush(randomChar, classMember, 0))
            assertNull(smusher.trySmush(classMember, randomChar, 0))
            assertNull(smusher.trySmush(randomChar, randomChar, 0))
        }
    }

    class HorizontalLine {
        private val smusher = VerticalSmusher(VerticalSmusher.Rule.HorizontalLine)

        @Test
        fun `trySmush returns '=' when top is '-' and bottom is '_'`() {
            val expected = '='.toInt()
            val top = '-'.toInt()
            val bottom = '_'.toInt()

            assertEquals(expected, smusher.trySmush(top, bottom, 0))
        }

        @Test
        fun `trySmush returns '=' when top is '_' and bottom is '-'`() {
            val expected = '='.toInt()
            val top = '_'.toInt()
            val bottom = '-'.toInt()

            assertEquals(expected, smusher.trySmush(top, bottom, 0))
        }

        @Test
        fun `trySmush returns null when given any other combination of inputs`() {
            val hyphen = '-'.toInt()
            val underscore = '_'.toInt()
            val notAline = '$'.toInt()

            assertNull(smusher.trySmush(hyphen, hyphen, 0))
            assertNull(smusher.trySmush(underscore, underscore, 0))
            assertNull(smusher.trySmush(hyphen, notAline, 0))
            assertNull(smusher.trySmush(underscore, notAline, 0))
            assertNull(smusher.trySmush(notAline, hyphen, 0))
            assertNull(smusher.trySmush(notAline, underscore, 0))
            assertNull(smusher.trySmush(notAline, notAline, 0))
        }
    }

    class VerticalLine {
        private val smusher = VerticalSmusher(VerticalSmusher.Rule.VerticalLine)

        @Test
        fun `trySmush returns vertical bar when top and bottom are also vertical bars`() {
            val verticalBar = '|'.toInt()

            assertEquals(verticalBar, smusher.trySmush(verticalBar, verticalBar, 0))
        }

        @Test
        fun `trySmush returns null when given any other combination of inputs`() {
            val verticalBar = '|'.toInt()
            val notAVerticalBar = '$'.toInt()

            assertNull(smusher.trySmush(verticalBar, notAVerticalBar, 0))
            assertNull(smusher.trySmush(notAVerticalBar, verticalBar, 0))
            assertNull(smusher.trySmush(notAVerticalBar, notAVerticalBar, 0))
        }
    }
}
