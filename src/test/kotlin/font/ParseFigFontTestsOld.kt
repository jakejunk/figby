package font

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.Exception

class ParseFigFontTestsOld {
    class LayoutModeTests {
        class Horizontal {
            @Test
            fun `parseHorizontalLayoutMode returns Smushing when bit 7 is enabled`() {
                parseHorizontalLayoutMode(0b10000000) shouldBe HorizontalLayoutMode.Smushing
                parseHorizontalLayoutMode(0b10000111) shouldBe HorizontalLayoutMode.Smushing
                parseHorizontalLayoutMode(0b11111111) shouldBe HorizontalLayoutMode.Smushing
            }

            @Test
            fun `parseHorizontalLayoutMode returns Smushing when bits 6 and 7 are enabled`() {
                parseHorizontalLayoutMode(0b11000000) shouldBe HorizontalLayoutMode.Smushing
                parseHorizontalLayoutMode(0b11100110) shouldBe HorizontalLayoutMode.Smushing
                parseHorizontalLayoutMode(0b11111111) shouldBe HorizontalLayoutMode.Smushing
            }

            @Test
            fun `parseHorizontalLayoutMode returns Kerning when bit 6 is enabled and bit 7 is disabled`() {
                parseHorizontalLayoutMode(0b01000000) shouldBe HorizontalLayoutMode.Kerning
                parseHorizontalLayoutMode(0b01100110) shouldBe HorizontalLayoutMode.Kerning
                parseHorizontalLayoutMode(0b01111111) shouldBe HorizontalLayoutMode.Kerning
            }

            @Test
            fun `parseHorizontalLayoutMode returns FullWidth when bits 6 and 7 are disabled`() {
                parseHorizontalLayoutMode(0b00000000) shouldBe HorizontalLayoutMode.FullWidth
                parseHorizontalLayoutMode(0b00100110) shouldBe HorizontalLayoutMode.FullWidth
                parseHorizontalLayoutMode(0b00111111) shouldBe HorizontalLayoutMode.FullWidth
            }
        }

        class Vertical {
            @Test
            fun `parseVerticalLayoutMode returns Smushing when bit 14 is enabled`() {
                parseVerticalLayoutMode(0b100000000100110) shouldBe VerticalLayoutMode.Smushing
                parseVerticalLayoutMode(0b100001110100110) shouldBe VerticalLayoutMode.Smushing
                parseVerticalLayoutMode(0b111111110100110) shouldBe VerticalLayoutMode.Smushing
            }

            @Test
            fun `parseVerticalLayoutMode returns Smushing when bits 13 and 14 are enabled`() {
                parseVerticalLayoutMode(0b110000000100110) shouldBe VerticalLayoutMode.Smushing
                parseVerticalLayoutMode(0b111001100100110) shouldBe VerticalLayoutMode.Smushing
                parseVerticalLayoutMode(0b111111110100110) shouldBe VerticalLayoutMode.Smushing
            }

            @Test
            fun `parseVerticalLayoutMode returns VerticalFitting when bit 13 is enabled and bit 14 is disabled`() {
                parseVerticalLayoutMode(0b010000000100110) shouldBe VerticalLayoutMode.VerticalFitting
                parseVerticalLayoutMode(0b011001100100110) shouldBe VerticalLayoutMode.VerticalFitting
                parseVerticalLayoutMode(0b011111110100110) shouldBe VerticalLayoutMode.VerticalFitting
            }

            @Test
            fun `parseVerticalLayoutMode returns FullHeight when bits 13 and 14 are disabled`() {
                parseVerticalLayoutMode(0b000000000100110) shouldBe VerticalLayoutMode.FullHeight
                parseVerticalLayoutMode(0b001001100100110) shouldBe VerticalLayoutMode.FullHeight
                parseVerticalLayoutMode(0b001111110100110) shouldBe VerticalLayoutMode.FullHeight
            }
        }
    }

    class PrintDirectionTests {
        @Test
        fun `parsePrintDirection returns LeftToRight when given 0`() {
            parsePrintDirection(0) shouldBe PrintDirection.LeftToRight
        }

        @Test
        fun `parsePrintDirection returns RightToLeft when given 1`() {
            parsePrintDirection(1) shouldBe PrintDirection.RightToLeft
        }

        @Test
        fun `parsePrintDirection throws error when given any other value`() {
            assertThrows<Exception> {
                parsePrintDirection(9001)
            }
        }
    }
}