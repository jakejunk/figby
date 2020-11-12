package layout

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LayoutModeTests {
    class Horizontal {
        @Test
        fun `parseHorizontalLayoutMode returns Smushing when bit 7 is enabled`() {
            assertEquals(HorizontalLayoutMode.Smushing, parseHorizontalLayoutMode(0b10000000))
            assertEquals(HorizontalLayoutMode.Smushing, parseHorizontalLayoutMode(0b10000111))
            assertEquals(HorizontalLayoutMode.Smushing, parseHorizontalLayoutMode(0b11111111))
        }

        @Test
        fun `parseHorizontalLayoutMode returns Smushing when bits 6 and 7 are enabled`() {
            assertEquals(HorizontalLayoutMode.Smushing, parseHorizontalLayoutMode(0b11000000))
            assertEquals(HorizontalLayoutMode.Smushing, parseHorizontalLayoutMode(0b11100110))
            assertEquals(HorizontalLayoutMode.Smushing, parseHorizontalLayoutMode(0b11111111))
        }

        @Test
        fun `parseHorizontalLayoutMode returns Kerning when bit 6 is enabled and bit 7 is disabled`() {
            assertEquals(HorizontalLayoutMode.Kerning, parseHorizontalLayoutMode(0b01000000))
            assertEquals(HorizontalLayoutMode.Kerning, parseHorizontalLayoutMode(0b01100110))
            assertEquals(HorizontalLayoutMode.Kerning, parseHorizontalLayoutMode(0b01111111))
        }

        @Test
        fun `parseHorizontalLayoutMode returns FullWidth when bits 6 and 7 are disabled`() {
            assertEquals(HorizontalLayoutMode.FullWidth, parseHorizontalLayoutMode(0b00000000))
            assertEquals(HorizontalLayoutMode.FullWidth, parseHorizontalLayoutMode(0b00100110))
            assertEquals(HorizontalLayoutMode.FullWidth, parseHorizontalLayoutMode(0b00111111))
        }
    }

    class Vertical {
        @Test
        fun `parseVerticalLayoutMode returns Smushing when bit 14 is enabled`() {
            assertEquals(VerticalLayoutMode.Smushing, parseVerticalLayoutMode(0b100000000100110))
            assertEquals(VerticalLayoutMode.Smushing, parseVerticalLayoutMode(0b100001110100110))
            assertEquals(VerticalLayoutMode.Smushing, parseVerticalLayoutMode(0b111111110100110))
        }

        @Test
        fun `parseVerticalLayoutMode returns Smushing when bits 13 and 14 are enabled`() {
            assertEquals(VerticalLayoutMode.Smushing, parseVerticalLayoutMode(0b110000000100110))
            assertEquals(VerticalLayoutMode.Smushing, parseVerticalLayoutMode(0b111001100100110))
            assertEquals(VerticalLayoutMode.Smushing, parseVerticalLayoutMode(0b111111110100110))
        }

        @Test
        fun `parseVerticalLayoutMode returns VerticalFitting when bit 13 is enabled and bit 14 is disabled`() {
            assertEquals(VerticalLayoutMode.VerticalFitting, parseVerticalLayoutMode(0b010000000100110))
            assertEquals(VerticalLayoutMode.VerticalFitting, parseVerticalLayoutMode(0b011001100100110))
            assertEquals(VerticalLayoutMode.VerticalFitting, parseVerticalLayoutMode(0b011111110100110))
        }

        @Test
        fun `parseVerticalLayoutMode returns FullHeight when bits 13 and 14 are disabled`() {
            assertEquals(VerticalLayoutMode.FullHeight, parseVerticalLayoutMode(0b000000000100110))
            assertEquals(VerticalLayoutMode.FullHeight, parseVerticalLayoutMode(0b001001100100110))
            assertEquals(VerticalLayoutMode.FullHeight, parseVerticalLayoutMode(0b001111110100110))
        }
    }
}
