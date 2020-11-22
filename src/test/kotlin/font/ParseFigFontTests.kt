package font

import font.parse.parseFigFont
import helpers.fakeFigFontFile
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import layout.HorizontalLayoutMode
import layout.HorizontalSmushingRule
import layout.PrintDirection
import java.lang.Exception

class ParseFigFontTests : ShouldSpec({
    should("return font when given all valid parameters") {
        val fontFile = fakeFigFontFile("flf2a\$ 5 4 3 -1 1 0 2", "A comment", 1, 5)
        val font = parseFigFont(fontFile)

        font.height shouldBe 5
        font.baseline shouldBe 4
        font.maxLength shouldBe 3
        font.horizontalLayout shouldBe HorizontalLayoutMode.FullWidth
        font.printDirection shouldBe PrintDirection.LeftToRight
        font.horizontalSmushingRules shouldContain HorizontalSmushingRule.Underscore
        font.comments shouldBe "A comment"
    }

    should("return font when optional parameters are omitted") {
        val fontFile = fakeFigFontFile("flf2a\$ 5 4 3 -1 1", "A comment", 1, 5)
        val font = parseFigFont(fontFile)

        font.height shouldBe 5
        font.baseline shouldBe 4
        font.maxLength shouldBe 3
        font.horizontalLayout shouldBe HorizontalLayoutMode.FullWidth
        font.printDirection shouldBe PrintDirection.LeftToRight
        font.horizontalSmushingRules.shouldBeEmpty()
        font.comments shouldBe "A comment"
    }

    should("ignore extra header parameters") {
        val fontFile = fakeFigFontFile("flf2a\$ 1 1 2 -1 1 0 1 extra params here 234", "A comment", 1, 1)
        val font = parseFigFont(fontFile)

        font.height shouldBe 1
        font.baseline shouldBe 1
        font.maxLength shouldBe 2
        font.horizontalLayout shouldBe HorizontalLayoutMode.FullWidth
        font.printDirection shouldBe PrintDirection.LeftToRight
        font.horizontalSmushingRules shouldContain HorizontalSmushingRule.EqualCharacter
        font.comments shouldBe "A comment"
    }

    should("throw exception when given empty header") {
        val fontFile = fakeFigFontFile(" ", "A comment", 1, 1)

        shouldThrow<Exception> {
            parseFigFont(fontFile)
        }
    }

    should("throw exception when given invalid signature") {
        val fontFile = fakeFigFontFile("flanders\$ 1 1 2 -1 1 0 1", "A comment", 1, 1)

        shouldThrow<Exception> {
            parseFigFont(fontFile)
        }
    }

    should("throw exception when given multiple hardblank characters") {
        val fontFile = fakeFigFontFile("flf2a\$\$ 1 1 2 -1 1 0 1", "A comment", 1, 1)

        shouldThrow<Exception> {
            parseFigFont(fontFile)
        }
    }

    should("throw exception when given invalid hardblank character") {
        shouldThrow<Exception> { parseFigFont(fakeFigFontFile("flf2a\u0020 1 1 1 -1 1 0 1", "A comment", 1, 1)) }
        shouldThrow<Exception> { parseFigFont(fakeFigFontFile("flf2a\u000d 1 1 1 -1 1 0 1", "A comment", 1, 1)) }
        shouldThrow<Exception> { parseFigFont(fakeFigFontFile("flf2a\u000a 1 1 1 -1 1 0 1", "A comment", 1, 1)) }
        shouldThrow<Exception> { parseFigFont(fakeFigFontFile("flf2a\u0000 1 1 1 -1 1 0 1", "A comment", 1, 1)) }
    }

    should("throw exception when given non-numeric height parameter") {
        val fontFile = fakeFigFontFile("flf2a\$ height 1 2 -1 1 0 1", "A comment", 1, 1)

        shouldThrow<Exception> {
            parseFigFont(fontFile)
        }
    }

    should("throw exception when given height is larger than actual character height") {
        val actualCharacterHeight = 2
        val heightParam = actualCharacterHeight + 1
        val fontFile = fakeFigFontFile("flf2a\$ $heightParam 1 1 -1 1 0 1", "A comment", 1, actualCharacterHeight)

        shouldThrow<Exception> {
            parseFigFont(fontFile)
        }
    }

    should("throw exception when given non-numeric baseline parameter") {
        val fontFile = fakeFigFontFile("flf2a\$ 1 baseline 2 -1 1 0 1", "A comment", 1, 1)

        shouldThrow<Exception> {
            parseFigFont(fontFile)
        }
    }

    should("throw exception when given baseline is less than 1") {
        val baseline = 0
        val fontFile = fakeFigFontFile("flf2a\$ 1 $baseline 2 -1 1 0 1", "A comment", 1, 1)

        shouldThrow<Exception> {
            parseFigFont(fontFile)
        }
    }

    should("throw exception when given baseline is greater than given height") {
        val height = 1
        val baseline = height + 1
        val fontFile = fakeFigFontFile("flf2a\$ $height $baseline 2 -1 1 0 1", "A comment", 1, 1)

        shouldThrow<Exception> {
            parseFigFont(fontFile)
        }
    }

    should("throw exception when given non-numeric max length parameter") {
        val fontFile = fakeFigFontFile("flf2a\$ 1 1 maxLength -1 1 0 1", "A comment", 1, 1)

        shouldThrow<Exception> {
            parseFigFont(fontFile)
        }
    }

    should("throw exception when given max length is smaller than actual character width") {
        val actualCharacterWidth = 2
        val maxLengthParam = actualCharacterWidth - 1
        val fontFile = fakeFigFontFile("flf2a\$ 1 1 $maxLengthParam -1 1 0 1", "A comment", actualCharacterWidth, 1)

        shouldThrow<Exception> {
            parseFigFont(fontFile)
        }
    }

    should("throw exception when given non-numeric old layout parameter") {
        val fontFile = fakeFigFontFile("flf2a\$ 1 1 2 oldLayout 1 0 1", "A comment", 1, 1)

        shouldThrow<Exception> {
            parseFigFont(fontFile)
        }
    }

    should("throw exception when given non-numeric comment lines parameter") {
        val fontFile = fakeFigFontFile("flf2a\$ 1 1 2 -1 commentLines 0 1", "A comment", 1, 1)

        shouldThrow<Exception> {
            parseFigFont(fontFile)
        }
    }

    should("throw exception when given more comment lines than specified") {
        val comments = "A comment\nextra line"
        val commentLines = comments.lines().size - 1
        val fontFile = fakeFigFontFile("flf2a\$ 1 1 2 -1 $commentLines 0 1", comments, 1, 1)

        shouldThrow<Exception> {
            parseFigFont(fontFile)
        }
    }

    should("return left-to-right font when print direction parameter is 0") {
        val printDirectionParam = 0
        val fontFile = fakeFigFontFile("flf2a\$ 1 1 2 -1 1 $printDirectionParam 1", "A comment", 1, 1)
        val font = parseFigFont(fontFile)

        font.printDirection shouldBe PrintDirection.LeftToRight
    }

    should("return right-to-left font when print direction parameter is 1") {
        val printDirectionParam = 1
        val fontFile = fakeFigFontFile("flf2a\$ 1 1 2 -1 1 $printDirectionParam 1", "A comment", 1, 1)
        val font = parseFigFont(fontFile)

        font.printDirection shouldBe PrintDirection.RightToLeft
    }

    should("throw exception when given invalid print direction parameter") {
        // Valid values are 0 and 1
        val printDirection = 2
        val fontFile = fakeFigFontFile("flf2a\$ 1 1 2 -1 1 $printDirection 1", "A comment", 1, 1)

        shouldThrow<Exception> {
            parseFigFont(fontFile)
        }
    }

    should("throw exception when given non-numeric print direction parameter") {
        val fontFile = fakeFigFontFile("flf2a\$ 1 1 2 -1 1 printDirection 1", "A comment", 1, 1)

        shouldThrow<Exception> {
            parseFigFont(fontFile)
        }
    }

    should("throw exception when given non-numeric full layout parameter") {
        val fontFile = fakeFigFontFile("flf2a\$ 1 1 2 -1 1 0 fullLayout", "A comment", 1, 1)

        shouldThrow<Exception> {
            parseFigFont(fontFile)
        }
    }
})