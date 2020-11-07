package layout

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class HorizontalSmushingTests {
    class EqualCharacterSmushing {
        private val smusher = HorizontalSmushing(listOf(
            HorizontalSmushing.Rule.EqualCharacter
        ))

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
            val result = smusher.trySmush(char, char, char)

            assertNull(result)
        }
    }
}