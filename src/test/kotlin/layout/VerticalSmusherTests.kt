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

        @Test
        fun `trySmush returns null when top and bottom both equal the hardblank`() {
            val char = 'j'.toInt()
            val result = smusher.trySmush(char, char, hardblank = char)

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
                    val resultTop = smusher.trySmush(input, underscore, 0)
                    val resultBottom = smusher.trySmush(underscore, input, 0)

                    assertEquals(input, resultTop)
                    assertEquals(input, resultBottom)
                }
            }

        @Test
        fun `trySmush returns null for any other character combination`() {
            val underscore = '_'.toInt()
            val other = 'j'.toInt()
            val result = smusher.trySmush(underscore, other, 0)

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
}
