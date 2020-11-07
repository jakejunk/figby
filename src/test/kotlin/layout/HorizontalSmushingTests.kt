package layout

import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import kotlin.test.assertEquals
import kotlin.test.assertNull

class HorizontalSmushingTests {
    class EqualCharacter {
        private val smusher = HorizontalSmusher(HorizontalSmusher.Rule.EqualCharacter)

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
        private val smusher = HorizontalSmusher(HorizontalSmusher.Rule.Underscore)
        private val underscoreReplacers = listOf(
            '|', '/', '\\', '[', ']', '{', '}', '(', ')', '<', '>'
        ).map { it.toInt() }

        @TestFactory
        fun `trySmush returns correct character when smushed with an underscore`() = underscoreReplacers
            .map { input ->
                DynamicTest.dynamicTest("'$input' can smush '_'") {
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

    class Hierarchy {
        private val smusher = HorizontalSmusher(HorizontalSmusher.Rule.Hierarchy)
        private val charClassMap = mapOf(
            '|' to 1,
            '/' to 2, '\\' to 2,
            '[' to 3, ']' to 3,
            '{' to 4, '}' to 4,
            '(' to 5, ')' to 5,
            '<' to 6, '>' to 6,
        ).mapKeys { it.key.toInt() }

        private fun <T, U, V> cartesianProduct(
            leftList: List<T>,
            rightList: List<U>,
            operation: (left: T, right: U) -> V?
        ): Sequence<V> = sequence {
            leftList.forEach { left ->
                rightList.forEach { right ->
                    operation(left, right)?.apply {
                        yield(this)
                    }
                }
            }
        }

        @TestFactory
        fun `trySmush where both inputs are part of a class`() = charClassMap
            .keys.toList()
            .let { keys -> cartesianProduct(keys, keys) { left, right ->
                val leftClass = charClassMap[left]!!
                val rightClass = charClassMap[right]!!
                val result = smusher.trySmush(left, right, 0)

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
            } }.toList()

        @Test
        fun `trySmush returns null if either input isn't part of a class`() {
            val randomChar = 'j'.toInt()
            val classMember = '/'.toInt()

            assertNull(smusher.trySmush(randomChar, classMember, 0))
            assertNull(smusher.trySmush(classMember, randomChar, 0))
            assertNull(smusher.trySmush(randomChar, randomChar, 0))
        }
    }
}