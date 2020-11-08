package layout

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
}
