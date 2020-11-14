package layout

import font.PrintDirection
import font.parsePrintDirection
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.Exception
import kotlin.test.assertEquals

class PrintDirectionTests {
    @Test
    fun `parsePrintDirection returns LeftToRight when given 0`() {
        assertEquals(PrintDirection.LeftToRight, parsePrintDirection(0))
    }

    @Test
    fun `parsePrintDirection returns RightToLeft when given 1`() {
        assertEquals(PrintDirection.RightToLeft, parsePrintDirection(1))
    }

    @Test
    fun `parsePrintDirection throws error when given any other value`() {
        assertThrows<Exception> {
            parsePrintDirection(9001)
        }
    }
}
