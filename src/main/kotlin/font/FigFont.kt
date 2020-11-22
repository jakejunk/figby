package font

import font.internal.*
import font.internal.parseFigFontHeader
import font.internal.parseFullLayout
import font.internal.parseOldLayout
import java.io.BufferedReader
import java.io.InputStream
import kotlin.streams.toList

/**
 * Describes the layout and graphical arrangement of sub-characters representing [FigChar]s.
 */
public data class FigFont internal constructor(
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
    private val figCharMap: Map<Int, FigChar>
) {
    /**
     * The horizontal smushing rules for this font.
     * @see HorizontalSmushingRule
     * @see tryHorizontalSmush
     */
    val horizontalSmushingRules: List<HorizontalSmushingRule>
        get() = figFontSmusher.horizontalRules

    /**
     * The vertical smushing rules for this font.
     * @see VerticalSmushingRule
     * @see tryVerticalSmush
     */
    val verticalSmushingRules: List<VerticalSmushingRule>
        get() = figFontSmusher.verticalRules

    /**
     * Returns the [FigChar] associated with the provided code point.
     * If [codePoint] is not represented in this font, the FigChar for
     * code point `0` will be returned, if present.
     */
    public operator fun get(codePoint: Int): FigChar? {
        return figCharMap[codePoint] ?: figCharMap[0]
    }

    /**
     * Attempts to smush [left] and [right] in accordance with this font's [horizontalSmushingRules],
     * returning the smushed code point if successful.
     * @see HorizontalSmushingRule
     */
    public fun tryHorizontalSmush(left: Int, right: Int): Int? {
        return figFontSmusher.tryHorizontalSmush(left, right, hardblank)
    }

    /**
     * Attempts to smush [top] and [bottom] in accordance with this font's [verticalSmushingRules],
     * returning the smushed code point if successful.
     * @see VerticalSmushingRule
     */
    public fun tryVerticalSmush(top: Int, bottom: Int): Int? {
        return figFontSmusher.tryVerticalSmush(top, bottom, hardblank)
    }
}

/**
 * Creates a [FigFont] from the given font file (`.flf`).
 */
public fun parseFigFont(fontFile: InputStream): FigFont = fontFile.bufferedReader().use { reader ->
    val headerLine = reader.readLine() ?: throw Exception("Could not read header line")
    val header = parseFigFontHeader(headerLine)
    val printDirection = parsePrintDirection(header.printDirection)
    val layout = when (header.fullLayout) {
        null -> parseOldLayout(header.oldLayout)
        else -> parseFullLayout(header.fullLayout)
    }

    val comments = readComments(header.commentLines, reader)
    val chars = parseChars(reader, header.maxLength, header.height)

    FigFont(
        hardblank = header.hardblank,
        height = header.height,
        baseline = header.baseline,
        maxLength = header.maxLength,
        horizontalLayout = layout.horizontalLayout,
        verticalLayout = layout.verticalLayout,
        printDirection = printDirection,
        comments = comments,
        figFontSmusher = layout.figFontSmusher,
        figCharMap = chars
    )
}

private fun readComments(numLines: Int, reader: BufferedReader): String {
    val lines = List(numLines) {
        reader.readLine()
            ?: throw Exception("Expected $numLines lines of comments, only received $it")
    }

    return lines.joinToString(separator = System.lineSeparator())
}

private fun parseChars(src: BufferedReader, maxLength: Int, height: Int): Map<Int, FigChar> {
    val requiredChars = parseRequiredChars(src, maxLength, height)
    val additionalChars = parseAdditionalChars(src, maxLength, height)

    return requiredChars + additionalChars
}

private fun parseRequiredChars(src: BufferedReader, maxLength: Int, height: Int): Map<Int, FigChar> {
    val requiredChars = (32..126) + listOf(196, 214, 220, 223, 228, 246, 252)

    return requiredChars.map {
        it to parseSingleLetter(src, maxLength, height)
    }.toMap()
}

private fun parseAdditionalChars(src: BufferedReader, maxLength: Int, height: Int): Map<Int, FigChar> {
    return generateSequence {
        parseCodeTag(src)?.let {
            it to parseSingleLetter(src, maxLength, height)
        }
    }.toMap()
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

private fun parseSingleLetter(src: BufferedReader, maxLength: Int, height: Int): FigChar {
    val firstLine = readLetterLine(src, maxLength)
    val lines = mutableListOf(firstLine)

    repeat(height - 1) {
        val line = readLetterLine(src, maxLength)
        if (line.length != firstLine.length) {
            throw Exception("Expected a width of ${firstLine.length}, found ${line.length} characters")
        }

        lines.add(line)
    }

    return FigChar(lines)
}

private fun readLetterLine(src: BufferedReader, maxLength: Int): FigCharLine {
    val line = src.readLine()
        ?: throw Exception("Unexpected end of sub-character input")

    if (line.length > maxLength) {
        throw Exception("Character line width exceeds specified max length (${line.length} > $maxLength)")
    }

    val subChars = line.split('@')[0]
        .codePoints()
        .toList()

    return FigCharLine(subChars)
}
