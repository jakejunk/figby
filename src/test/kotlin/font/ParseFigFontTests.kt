package font

import helpers.fakeFigFontFile
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.Exception
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ParseFigFontTests {
    @Test
    fun `parseFigFont returns font when given all valid parameters`() {
        val fontFile = fakeFigFontFile("flf2a\$ 5 4 3 -1 1 0 2", "A comment", 1, 5)
        val font = parseFigFont(fontFile)

        assertEquals(5, font.height)
        assertEquals(4, font.baseline)
        assertEquals(3, font.maxLength)
        assertEquals(HorizontalLayoutMode.FullWidth, font.horizontalLayout)
        assertEquals(PrintDirection.LeftToRight, font.printDirection)
        assertTrue(font.horizontalSmushingRules.contains(HorizontalSmushingRule.Underscore))
        assertEquals("A comment", font.comments)
    }

    @Test
    fun `parseFigFont ignores extra header parameters`() {
        val fontFile = fakeFigFontFile("flf2a\$ 1 1 2 -1 1 0 1 extra params here 234", "A comment", 1, 1)
        val font = parseFigFont(fontFile)

        assertEquals(1, font.height)
        assertEquals(1, font.baseline)
        assertEquals(2, font.maxLength)
        assertEquals(HorizontalLayoutMode.FullWidth, font.horizontalLayout)
        assertEquals(PrintDirection.LeftToRight, font.printDirection)
        assertTrue(font.horizontalSmushingRules.contains(HorizontalSmushingRule.EqualCharacter))
        assertEquals("A comment", font.comments)
    }

    @Test
    fun `parseFigFont throws exception when given empty header`() {
        val fontFile = fakeFigFontFile(" ", "A comment", 1, 1)

        assertThrows<Exception> {
            parseFigFont(fontFile)
        }
    }

    @Test
    fun `parseFigFont throws exception when given invalid signature`() {
        val fontFile = fakeFigFontFile("flanders\$ 1 1 2 -1 1 0 1", "A comment", 1, 1)

        assertThrows<Exception> {
            parseFigFont(fontFile)
        }
    }

    @Test
    fun `parseFigFont throws exception when given multiple hardblank characters`() {
        val fontFile = fakeFigFontFile("flf2a\$\$ 1 1 2 -1 1 0 1", "A comment", 1, 1)

        assertThrows<Exception> {
            parseFigFont(fontFile)
        }
    }

    @Test
    fun `parseFigFont throws exception when given invalid hardblank character`() {
        assertThrows<Exception> { parseFigFont(fakeFigFontFile("flf2a\u0020 1 1 1 -1 1 0 1", "A comment", 1, 1)) }
        assertThrows<Exception> { parseFigFont(fakeFigFontFile("flf2a\u000d 1 1 1 -1 1 0 1", "A comment", 1, 1)) }
        assertThrows<Exception> { parseFigFont(fakeFigFontFile("flf2a\u000a 1 1 1 -1 1 0 1", "A comment", 1, 1)) }
        assertThrows<Exception> { parseFigFont(fakeFigFontFile("flf2a\u0000 1 1 1 -1 1 0 1", "A comment", 1, 1)) }
    }

    @Test
    fun `parseFigFont throws exception when given non-numeric height parameter`() {
        val fontFile = fakeFigFontFile("flf2a\$ height 1 2 -1 1 0 1", "A comment", 1, 1)

        assertThrows<Exception> {
            parseFigFont(fontFile)
        }
    }

    @Test
    fun `parseFigFont throws exception when given height is larger than actual character height`() {
        val actualCharacterHeight = 2
        val heightParam = actualCharacterHeight + 1
        val fontFile = fakeFigFontFile("flf2a\$ $heightParam 1 1 -1 1 0 1", "A comment", 1, actualCharacterHeight)

        assertThrows<Exception> {
            parseFigFont(fontFile)
        }
    }

    @Test
    fun `parseFigFont throws exception when given non-numeric baseline parameter`() {
        val fontFile = fakeFigFontFile("flf2a\$ 1 baseline 2 -1 1 0 1", "A comment", 1, 1)

        assertThrows<Exception> {
            parseFigFont(fontFile)
        }
    }

    @Test
    fun `parseFigFont throws exception when given baseline is less than 1`() {
        val baseline = 0
        val fontFile = fakeFigFontFile("flf2a\$ 1 $baseline 2 -1 1 0 1", "A comment", 1, 1)

        assertThrows<Exception> {
            parseFigFont(fontFile)
        }
    }

    @Test
    fun `parseFigFont throws exception when given baseline is greater than given height`() {
        val height = 1
        val baseline = height + 1
        val fontFile = fakeFigFontFile("flf2a\$ $height $baseline 2 -1 1 0 1", "A comment", 1, 1)

        assertThrows<Exception> {
            parseFigFont(fontFile)
        }
    }

    @Test
    fun `parseFigFont throws exception when given non-numeric max length parameter`() {
        val fontFile = fakeFigFontFile("flf2a\$ 1 1 maxLength -1 1 0 1", "A comment", 1, 1)

        assertThrows<Exception> {
            parseFigFont(fontFile)
        }
    }

    @Test
    fun `parseFigFont throws exception when given max length is smaller than actual character width`() {
        val actualCharacterWidth = 2
        val maxLengthParam = actualCharacterWidth - 1
        val fontFile = fakeFigFontFile("flf2a\$ 1 1 $maxLengthParam -1 1 0 1", "A comment", actualCharacterWidth, 1)

        assertThrows<Exception> {
            parseFigFont(fontFile)
        }
    }

    @Test
    fun `parseFigFont throws exception when given non-numeric old layout parameter`() {
        val fontFile = fakeFigFontFile("flf2a\$ 1 1 2 oldLayout 1 0 1", "A comment", 1, 1)

        assertThrows<Exception> {
            parseFigFont(fontFile)
        }
    }

    @Test
    fun `parseFigFont throws exception when given non-numeric comment lines parameter`() {
        val fontFile = fakeFigFontFile("flf2a\$ 1 1 2 -1 commentLines 0 1", "A comment", 1, 1)

        assertThrows<Exception> {
            parseFigFont(fontFile)
        }
    }

    @Test
    fun `parseFigFont throws exception when given more comment lines than specified`() {
        val comments = "A comment\nextra line"
        val commentLines = comments.lines().size - 1
        val fontFile = fakeFigFontFile("flf2a\$ 1 1 2 -1 $commentLines 0 1", comments, 1, 1)

        assertThrows<Exception> {
            parseFigFont(fontFile)
        }
    }

    @Test
    fun `parseFigFont throws exception when given non-numeric print direction parameter`() {
        val fontFile = fakeFigFontFile("flf2a\$ 1 1 2 -1 1 printDirection 1", "A comment", 1, 1)

        assertThrows<Exception> {
            parseFigFont(fontFile)
        }
    }

    @Test
    fun `parseFigFont throws exception when given invalid print direction parameter`() {
        // Valid values are 0 and 1
        val printDirection = 2
        val fontFile = fakeFigFontFile("flf2a\$ 1 1 2 -1 1 $printDirection 1", "A comment", 1, 1)

        assertThrows<Exception> {
            parseFigFont(fontFile)
        }
    }

    @Test
    fun `parseFigFont throws exception when given non-numeric full layout parameter`() {
        val fontFile = fakeFigFontFile("flf2a\$ 1 1 2 -1 1 0 fullLayout", "A comment", 1, 1)

        assertThrows<Exception> {
            parseFigFont(fontFile)
        }
    }
}