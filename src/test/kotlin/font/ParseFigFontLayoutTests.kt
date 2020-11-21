package font

import helpers.fakeFigFontFile
import helpers.generators.oldLayoutsWithPattern
import helpers.generators.fullLayoutsWithPattern
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll

class ParseFigFontLayoutTests : ShouldSpec({
    context("Full layout") {
        should("have full-width layout when full layout param is 0") {
            val fullLayoutParam = 0
            val fontFile = fakeFigFontFile("flf2a\$ 1 1 2 -1 1 0 $fullLayoutParam", "A comment", 1, 1)
            val font = parseFigFont(fontFile)

            font.horizontalLayout shouldBe HorizontalLayoutMode.FullWidth
        }

        should("have horizontal equal character smushing when full layout param has bit 0 enabled") {
            checkAll(fullLayoutsWithPattern(0b1)) { fullLayoutParam ->
                val fontFile = fakeFigFontFile("flf2a\$ 1 1 2 -1 1 0 $fullLayoutParam", "A comment", 1, 1)
                val font = parseFigFont(fontFile)

                font.horizontalSmushingRules shouldContain HorizontalSmushingRule.EqualCharacter
            }
        }

        should("have horizontal underscore smushing when full layout param has bit 1 enabled") {
            checkAll(fullLayoutsWithPattern(0b10)) { fullLayoutParam ->
                val fontFile = fakeFigFontFile("flf2a\$ 1 1 2 -1 1 0 $fullLayoutParam", "A comment", 1, 1)
                val font = parseFigFont(fontFile)

                font.horizontalSmushingRules shouldContain HorizontalSmushingRule.Underscore
            }
        }

        should("have horizontal hierarchy smushing when full layout param has bit 2 enabled") {
            checkAll(fullLayoutsWithPattern(0b100)) { fullLayoutParam ->
                val fontFile = fakeFigFontFile("flf2a\$ 1 1 2 -1 1 0 $fullLayoutParam", "A comment", 1, 1)
                val font = parseFigFont(fontFile)

                font.horizontalSmushingRules shouldContain HorizontalSmushingRule.Hierarchy
            }
        }

        should("have opposite pair smushing when full layout param has bit 3 enabled") {
            checkAll(fullLayoutsWithPattern(0b1000)) { fullLayoutParam ->
                val fontFile = fakeFigFontFile("flf2a\$ 1 1 2 -1 1 0 $fullLayoutParam", "A comment", 1, 1)
                val font = parseFigFont(fontFile)

                font.horizontalSmushingRules shouldContain HorizontalSmushingRule.OppositePair
            }
        }

        should("have 'Big X' smushing when full layout param has bit 4 enabled") {
            checkAll(fullLayoutsWithPattern(0b10000)) { fullLayoutParam ->
                val fontFile = fakeFigFontFile("flf2a\$ 1 1 2 -1 1 0 $fullLayoutParam", "A comment", 1, 1)
                val font = parseFigFont(fontFile)

                font.horizontalSmushingRules shouldContain HorizontalSmushingRule.BigX
            }
        }

        should("have hardblank smushing when full layout param has bit 5 enabled") {
            checkAll(fullLayoutsWithPattern(0b100000)) { fullLayoutParam ->
                val fontFile = fakeFigFontFile("flf2a\$ 1 1 2 -1 1 0 $fullLayoutParam", "A comment", 1, 1)
                val font = parseFigFont(fontFile)

                font.horizontalSmushingRules shouldContain HorizontalSmushingRule.Hardblank
            }
        }

        should("have kerning layout when full layout param has bit 6 enabled and bit 7 disabled") {
            checkAll(
                fullLayoutsWithPattern(
                    enabledMask = 0b01000000,
                    disabledMask = 0b10000000
                )
            ) { fullLayoutParam ->
                val fontFile = fakeFigFontFile("flf2a\$ 1 1 2 -1 1 0 $fullLayoutParam", "A comment", 1, 1)
                val font = parseFigFont(fontFile)

                font.horizontalLayout shouldBe HorizontalLayoutMode.Kerning
            }
        }

        // Horizontal smushing layout overrides kerning layout
        should("have horizontal smushing layout when full layout param has bits 6 and 7 enabled") {
            checkAll(fullLayoutsWithPattern(enabledMask = 0b11000000)) { fullLayoutParam ->
                val fontFile = fakeFigFontFile("flf2a\$ 1 1 2 -1 1 0 $fullLayoutParam", "A comment", 1, 1)
                val font = parseFigFont(fontFile)

                font.horizontalLayout shouldBe HorizontalLayoutMode.Smushing
            }
        }

        should("have horizontal smushing layout when full layout param has bit 7 enabled") {
            checkAll(fullLayoutsWithPattern(enabledMask = 0b10000000)) { fullLayoutParam ->
                val fontFile = fakeFigFontFile("flf2a\$ 1 1 2 -1 1 0 $fullLayoutParam", "A comment", 1, 1)
                val font = parseFigFont(fontFile)

                font.horizontalLayout shouldBe HorizontalLayoutMode.Smushing
            }
        }

        should("have vertical equal character smushing when full layout param has bit 8 enabled") {
            checkAll(fullLayoutsWithPattern(enabledMask = 0b100000000)) { fullLayoutParam ->
                val fontFile = fakeFigFontFile("flf2a\$ 1 1 2 -1 1 0 $fullLayoutParam", "A comment", 1, 1)
                val font = parseFigFont(fontFile)

                font.verticalSmushingRules shouldContain VerticalSmushingRule.EqualCharacter
            }
        }

        should("have vertical underscore smushing when full layout param has bit 9 enabled") {
            checkAll(fullLayoutsWithPattern(enabledMask = 0b1000000000)) { fullLayoutParam ->
                val fontFile = fakeFigFontFile("flf2a\$ 1 1 2 -1 1 0 $fullLayoutParam", "A comment", 1, 1)
                val font = parseFigFont(fontFile)

                font.verticalSmushingRules shouldContain VerticalSmushingRule.Underscore
            }
        }

        should("have hierarchy underscore smushing when full layout param has bit 10 enabled") {
            checkAll(fullLayoutsWithPattern(enabledMask = 0b10000000000)) { fullLayoutParam ->
                val fontFile = fakeFigFontFile("flf2a\$ 1 1 2 -1 1 0 $fullLayoutParam", "A comment", 1, 1)
                val font = parseFigFont(fontFile)

                font.verticalSmushingRules shouldContain VerticalSmushingRule.Hierarchy
            }
        }

        should("have horizontal line smushing when full layout param has bit 11 enabled") {
            checkAll(fullLayoutsWithPattern(enabledMask = 0b100000000000)) { fullLayoutParam ->
                val fontFile = fakeFigFontFile("flf2a\$ 1 1 2 -1 1 0 $fullLayoutParam", "A comment", 1, 1)
                val font = parseFigFont(fontFile)

                font.verticalSmushingRules shouldContain VerticalSmushingRule.HorizontalLine
            }
        }

        should("have vertical line smushing when full layout param has bit 12 enabled") {
            checkAll(fullLayoutsWithPattern(enabledMask = 0b1000000000000)) { fullLayoutParam ->
                val fontFile = fakeFigFontFile("flf2a\$ 1 1 2 -1 1 0 $fullLayoutParam", "A comment", 1, 1)
                val font = parseFigFont(fontFile)

                font.verticalSmushingRules shouldContain VerticalSmushingRule.VerticalLine
            }
        }

        should("have vertical fitting layout when full layout param has bit 13 enabled and bit 14 disabled") {
            checkAll(
                fullLayoutsWithPattern(
                    enabledMask = 0b010000000000000,
                    disabledMask = 0b100000000000000
                )
            ) { fullLayoutParam ->
                val fontFile = fakeFigFontFile("flf2a\$ 1 1 2 -1 1 0 $fullLayoutParam", "A comment", 1, 1)
                val font = parseFigFont(fontFile)

                font.verticalLayout shouldBe VerticalLayoutMode.VerticalFitting
            }
        }

        // Vertical smushing layout overrides vertical fitting layout
        should("have vertical smushing layout when full layout param has bits 13 and 14 enabled") {
            checkAll(fullLayoutsWithPattern(enabledMask = 0b110000000000000)) { fullLayoutParam ->
                val fontFile = fakeFigFontFile("flf2a\$ 1 1 2 -1 1 0 $fullLayoutParam", "A comment", 1, 1)
                val font = parseFigFont(fontFile)

                font.verticalLayout shouldBe VerticalLayoutMode.Smushing
            }
        }

        should("have vertical smushing layout when full layout param has bit 14 enabled") {
            checkAll(fullLayoutsWithPattern(enabledMask = 0b100000000000000)) { fullLayoutParam ->
                val fontFile = fakeFigFontFile("flf2a\$ 1 1 2 -1 1 0 $fullLayoutParam", "A comment", 1, 1)
                val font = parseFigFont(fontFile)

                font.verticalLayout shouldBe VerticalLayoutMode.Smushing
            }
        }
    }

    context("Old layout") {
        should("have full-width layout when old layout param is -1") {
            val oldLayoutParam = -1
            val fontFile = fakeFigFontFile("flf2a\$ 1 1 2 $oldLayoutParam 1", "A comment", 1, 1)
            val font = parseFigFont(fontFile)

            font.horizontalLayout shouldBe HorizontalLayoutMode.FullWidth
        }

        should("have kerning layout when old layout param is 0") {
            val oldLayoutParam = 0
            val fontFile = fakeFigFontFile("flf2a\$ 1 1 2 $oldLayoutParam 1", "A comment", 1, 1)
            val font = parseFigFont(fontFile)

            font.horizontalLayout shouldBe HorizontalLayoutMode.Kerning
        }

        should("have horizontal equal character smushing when old layout param has bit 0 enabled") {
            checkAll(oldLayoutsWithPattern(0b1)) { oldLayoutParam ->
                val fontFile = fakeFigFontFile("flf2a\$ 1 1 2 $oldLayoutParam 1", "A comment", 1, 1)
                val font = parseFigFont(fontFile)

                font.horizontalSmushingRules shouldContain HorizontalSmushingRule.EqualCharacter
            }
        }

        should("have horizontal underscore smushing when old layout param has bit 1 enabled") {
            checkAll(oldLayoutsWithPattern(0b10)) { oldLayoutParam ->
                val fontFile = fakeFigFontFile("flf2a\$ 1 1 2 $oldLayoutParam 1", "A comment", 1, 1)
                val font = parseFigFont(fontFile)

                font.horizontalSmushingRules shouldContain HorizontalSmushingRule.Underscore
            }
        }

        should("have horizontal hierarchy smushing when old layout param has bit 2 enabled") {
            checkAll(oldLayoutsWithPattern(0b100)) { oldLayoutParam ->
                val fontFile = fakeFigFontFile("flf2a\$ 1 1 2 $oldLayoutParam 1", "A comment", 1, 1)
                val font = parseFigFont(fontFile)

                font.horizontalSmushingRules shouldContain HorizontalSmushingRule.Hierarchy
            }
        }

        should("have opposite pair smushing when old layout param has bit 3 enabled") {
            checkAll(oldLayoutsWithPattern(0b1000)) { oldLayoutParam ->
                val fontFile = fakeFigFontFile("flf2a\$ 1 1 2 $oldLayoutParam 1", "A comment", 1, 1)
                val font = parseFigFont(fontFile)

                font.horizontalSmushingRules shouldContain HorizontalSmushingRule.OppositePair
            }
        }

        should("have 'Big X' smushing when old layout param has bit 4 enabled") {
            checkAll(oldLayoutsWithPattern(0b10000)) { oldLayoutParam ->
                val fontFile = fakeFigFontFile("flf2a\$ 1 1 2 $oldLayoutParam 1", "A comment", 1, 1)
                val font = parseFigFont(fontFile)

                font.horizontalSmushingRules shouldContain HorizontalSmushingRule.BigX
            }
        }

        should("have hardblank smushing when old layout param has bit 5 enabled") {
            checkAll(oldLayoutsWithPattern(0b100000)) { oldLayoutParam ->
                val fontFile = fakeFigFontFile("flf2a\$ 1 1 2 $oldLayoutParam 1", "A comment", 1, 1)
                val font = parseFigFont(fontFile)

                font.horizontalSmushingRules shouldContain HorizontalSmushingRule.Hardblank
            }
        }
    }
})