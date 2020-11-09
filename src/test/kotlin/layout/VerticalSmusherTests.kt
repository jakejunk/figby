package layout

import cartesianProduct
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import kotlin.test.assertEquals
import kotlin.test.assertNull

class VerticalSmusherTests {
    class EqualCharacter {
        private val smusher = VerticalSmusher(VerticalSmusher.Rule.EqualCharacter)

        @Test
        fun `trySmush returns top character when top and bottom are equal`() {
            val expected = 'j'.toInt()
            val result = smusher.trySmush(expected, expected)

            assertEquals(expected, result)
        }

        @Test
        fun `trySmush returns null when top and bottom are not equal`() {
            val top = 'j'.toInt()
            val bottom = 'k'.toInt()
            val result = smusher.trySmush(top, bottom)

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
                    val resultTop = smusher.trySmush(input, underscore)
                    val resultBottom = smusher.trySmush(underscore, input)

                    assertEquals(input, resultTop)
                    assertEquals(input, resultBottom)
                }
            }

        @Test
        fun `trySmush returns null for any other character combination`() {
            val underscore = '_'.toInt()
            val other = 'j'.toInt()
            val result = smusher.trySmush(underscore, other)

            assertNull(result)
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
                    val result = smusher.trySmush(top, bottom)

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

            assertNull(smusher.trySmush(randomChar, classMember))
            assertNull(smusher.trySmush(classMember, randomChar))
            assertNull(smusher.trySmush(randomChar, randomChar))
        }
    }

    class HorizontalLine {
        private val smusher = VerticalSmusher(VerticalSmusher.Rule.HorizontalLine)

        @Test
        fun `trySmush returns '=' when top is '-' and bottom is '_'`() {
            val expected = '='.toInt()
            val top = '-'.toInt()
            val bottom = '_'.toInt()
            val result =  smusher.trySmush(top, bottom)

            assertEquals(expected, result)
        }

        @Test
        fun `trySmush returns '=' when top is '_' and bottom is '-'`() {
            val expected = '='.toInt()
            val top = '_'.toInt()
            val bottom = '-'.toInt()
            val result =  smusher.trySmush(top, bottom)

            assertEquals(expected, result)
        }

        @Test
        fun `trySmush returns null when given any other combination of inputs`() {
            val hyphen = '-'.toInt()
            val underscore = '_'.toInt()
            val notAline = '$'.toInt()

            assertNull(smusher.trySmush(hyphen, hyphen))
            assertNull(smusher.trySmush(underscore, underscore))
            assertNull(smusher.trySmush(hyphen, notAline))
            assertNull(smusher.trySmush(underscore, notAline))
            assertNull(smusher.trySmush(notAline, hyphen))
            assertNull(smusher.trySmush(notAline, underscore))
            assertNull(smusher.trySmush(notAline, notAline))
        }
    }

    class VerticalLine {
        private val smusher = VerticalSmusher(VerticalSmusher.Rule.VerticalLine)

        @Test
        fun `trySmush returns vertical bar when top and bottom are also vertical bars`() {
            val verticalBar = '|'.toInt()
            val result =  smusher.trySmush(verticalBar, verticalBar)

            assertEquals(verticalBar, result)
        }

        @Test
        fun `trySmush returns null when given any other combination of inputs`() {
            val verticalBar = '|'.toInt()
            val notAVerticalBar = '$'.toInt()

            assertNull(smusher.trySmush(verticalBar, notAVerticalBar))
            assertNull(smusher.trySmush(notAVerticalBar, verticalBar))
            assertNull(smusher.trySmush(notAVerticalBar, notAVerticalBar))
        }
    }

    // TODO: Don't forget about universal smushing
}
