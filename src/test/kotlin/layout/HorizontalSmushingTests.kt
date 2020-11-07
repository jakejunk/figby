package layout

import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import kotlin.test.assertEquals
import kotlin.test.assertNull

class HorizontalSmushingTests {
    class EqualCharacter {
        private val smusher = HorizontalSmushing(
            listOf(
                HorizontalSmushing.Rule.EqualCharacter
            )
        )

        @Test
        fun `trySmush returns left character when left and right are equal`() {
            val expected = 'j'.toInt()
            val result = smusher.trySmush(expected, expected, 0)

            assertEquals(expected, result)
        }

        @Test
        fun `trySmush returns null when left and right are not equal`() {
            val left = 'j'.toInt()
            val right = 'k'.toInt()
            val result = smusher.trySmush(left, right, 0)

            assertNull(result)
        }

        @Test
        fun `trySmush returns null when left and right both equal the hardblank`() {
            val char = 'j'.toInt()
            val result = smusher.trySmush(char, char, hardblank = char)

            assertNull(result)
        }
    }

    class Underscore {
        private val smusher = HorizontalSmushing(
            listOf(
                HorizontalSmushing.Rule.Underscore
            )
        )

        private val underscoreReplacers = listOf(
            '|', '/', '\\', '[', ']', '{', '}', '(', ')', '<', '>'
        ).map { it.toInt() }

        @TestFactory
        fun `trySmush returns correct character when smushed with an underscore`() = underscoreReplacers
            .map { input ->
                DynamicTest.dynamicTest("$input can smush an underscore") {
                    val underscore = '_'.toInt()
                    val resultLeft = smusher.trySmush(input, underscore, 0)
                    val resultRight = smusher.trySmush(underscore, input, 0)

                    assertEquals(input, resultLeft)
                    assertEquals(input, resultRight)
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