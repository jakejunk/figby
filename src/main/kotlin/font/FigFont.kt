package font

import font.internal.*
import font.internal.parseFigFontHeader
import font.internal.parseFullLayout
import font.internal.parseOldLayout
import java.io.BufferedReader
import java.io.InputStream
import kotlin.streams.toList

data class FigFont internal constructor(
    /**
     * The code point representing the hardblank character for this font.
     */
    val hardblank: Int,
    /**
     * The height of every [FigChar] within this font, measured in sub-characters.
     */
    val height: Int,
    /**
     * The height of a [FigChar], ignoring any descenders.
     */
    val baseline: Int,
    /**
     * Greater than or equal to the width of the widest [FigChar], plus 2.
     */
    val maxLength: Int,
    /**
     * Defines the layout mode for the horizontal axis,
     * which specifies how much spacing to place between [FigChar]s.
     */
    val horizontalLayout: HorizontalLayoutMode,
    /**
     * Defines the layout mode for the vertical axis,
     * which specifies how much spacing to place between [FigChar]s.
     */
    val verticalLayout: VerticalLayoutMode,
    /**
     * Specifies whether this font should be printed left-to-right or right-to-left.
     */
    val printDirection: PrintDirection,
    /**
     * The comments included as part of this font, which may include
     * anything from the font name to the author's contact info.
     */
    val comments: String,
    private val figFontSmusher: FigFontSmusher,
    private val chars: Map<Int, FigChar>
) {
    operator fun get(charCode: Int): FigChar? {
        return chars[charCode] ?: chars[0]
    }

    fun tryHorizontalSmush(left: Int, right: Int): Int? {
        return figFontSmusher.tryHorizontalSmush(left, right, hardblank)
    }

    fun tryVerticalSmush(top: Int, bottom: Int): Int? {
        return figFontSmusher.tryVerticalSmush(top, bottom, hardblank)
    }
}

fun parseFigFont(fontFile: InputStream): FigFont {
    val reader = fontFile.bufferedReader()

    val headerLine = reader.readLine() ?: throw Exception("Could not read header line")
    val header = parseFigFontHeader(headerLine)
    val printDirection = parsePrintDirection(header.printDirection)
    val layout = when (header.fullLayout) {
        null -> parseOldLayout(header.oldLayout)
        else -> parseFullLayout(header.fullLayout)
    }

    val comments = readComments(header.commentLines, reader)
    val chars = parseChars(reader, header.height)

    return FigFont(
        hardblank = header.hardblank,
        height = header.height,
        baseline = header.baseline,
        maxLength = header.maxLength,
        horizontalLayout = layout.horizontalLayout,
        verticalLayout = layout.verticalLayout,
        printDirection = printDirection,
        comments = comments,
        figFontSmusher = layout.figFontSmusher,
        chars = chars
    )
}

private fun readComments(numLines: Int, reader: BufferedReader): String {
    val builder = StringBuilder()

    repeat(numLines) {
        val line = reader.readLine()
            ?: throw Exception("Expected $numLines lines of comments, only received $it")

        builder.append(line)
        builder.append(System.lineSeparator())
    }

    return builder.toString()
}

private fun parseChars(src: BufferedReader, height: Int): Map<Int, FigChar> {
    val requiredChars = parseRequiredChars(src, height)
    val additionalChars = parseAdditionalChars(src, height)

    return requiredChars + additionalChars
}

private fun parseRequiredChars(src: BufferedReader, height: Int): Map<Int, FigChar> {
    val requiredChars = (32 .. 126) + listOf(196, 214, 220, 223, 228, 246, 252)

    return requiredChars.map {
        it to parseSingleLetter(src, height)
    }.toMap()
}

private fun parseAdditionalChars(src: BufferedReader, height: Int): Map<Int, FigChar> {
    val charCodePairs = mutableListOf<Pair<Int, FigChar>>()

    while (true) {
        val charCode = parseCodeTag(src) ?: break

        charCodePairs += (charCode to parseSingleLetter(src, height))
    }

    return charCodePairs.toMap()
}

private fun parseCodeTag(src: BufferedReader): Int? {
    val codeTag = src.readLine() ?: return null
    val charCodeStr = codeTag.split(" ")[0]
    val charCode = when {
        charCodeStr.startsWith("0x", ignoreCase = true) -> {
            charCodeStr.substring(2).toIntOrNull(16)
        }
        charCodeStr.startsWith("0") -> {
            charCodeStr.substring(1).toIntOrNull(8)
        }
        else -> charCodeStr.toIntOrNull()
    }

    return charCode ?: throw Error("Expected numeric character code in code tag: $codeTag")
}

private fun parseSingleLetter(src: BufferedReader, height: Int): FigChar {
    val firstLine = readLetterLine(src)
    val lines = mutableListOf(firstLine)

    repeat(height - 1) {
        val line = readLetterLine(src)
        if (line.length != firstLine.length) {
            throw Exception("Expected a width of ${firstLine.length}, found ${line.length} characters")
        }

        lines.add(line)
    }

    return FigChar(lines)
}

private fun readLetterLine(src: BufferedReader): FigCharLine {
    val line = src.readLine()
        ?: throw Exception("Unexpected end of sub-character input")

    val subChars = line.split('@')[0]
        .codePoints()
        .toList()

    return FigCharLine(subChars)
}
