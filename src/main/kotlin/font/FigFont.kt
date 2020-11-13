package font

import layout.Layout
import layout.parseFullLayout
import layout.parseOldLayout
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
     * Includes all details on how this font should be displayed.
     * @see [Layout]
     */
    val layout: Layout,
    /**
     * The comments included as part of this font, which may include
     * anything from the font name to the author's contact info.
     */
    val comments: String,
    private val chars: Map<Int, FigChar>
) {
    operator fun get(charCode: Int): FigChar? {
        return chars[charCode] ?: chars[0]
    }
}

fun parseFigFont(fontFile: InputStream): FigFont {
    val reader = fontFile.bufferedReader()
    val headerLine = reader.readLine() ?: throw Exception("Could not read header line")
    val params = headerLine.split(" ")
    if (params.size < 6) {
        throw Exception("Malformed header, TODO")
    }

    // Required values [0, 5]
    val hardblank = parseSignature(params[0])
    val height = parseNumericParam(params[1], "height")
    val baseline = parseNumericParam(params[2], "baseline")
    val maxLength = parseNumericParam(params[3], "max length")
    val oldLayout = parseNumericParam(params[4], "old layout")
    val commentLines = parseNumericParam(params[5], "comment lines")

    // Optional values [6, 7]
    val printDirection = params.getOrNull(6)?.toIntOrNull() ?: 0
    val layout = when (val fullLayout = params.getOrNull(7)?.toIntOrNull()) {
        null -> parseOldLayout(oldLayout, printDirection)
        else -> parseFullLayout(fullLayout, printDirection)
    }

    // There's also a "Codetag_Count" parameter, but it doesn't seem useful

    val comments = readComments(commentLines, reader)
    val chars = parseChars(reader, height)

    return FigFont(
        hardblank = hardblank,
        height = height,
        baseline = baseline,
        maxLength = maxLength,
        layout = layout,
        comments = comments,
        chars = chars
    )
}

private fun parseSignature(signatureAndHardblank: String): Int {
    val signature = "flf2a"
    val signatureParts = signatureAndHardblank.split(signature)

    if (signatureParts.size != 2 || signatureParts[0] != "") {
        throw Exception("Malformed signature")
    }

    return signatureParts[1].codePointAt(0)
}

private fun parseNumericParam(param: String, paramName: String): Int {
    return param.toIntOrNull() ?: throw Exception("Could not parse $paramName")
}

private fun readComments(numLines: Int, reader: BufferedReader): String {
    val builder = StringBuilder()

    repeat(numLines) {
        val line = reader.readLine()
            ?: throw Exception("Expected $numLines lines of comments, only received $it")

        builder.append(line)
        builder.append('\n')
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
