package font

import java.io.BufferedReader
import java.io.InputStream
import kotlin.streams.toList

data class FigFont internal constructor(
    val info: FigFontInfo,
    private val chars: Map<Int, FigChar>
) {
    operator fun get(charCode: Int): FigChar? {
        return chars[charCode] ?: chars[0]
    }
}

fun parseFigFont(fontFile: InputStream): FigFont {
    val reader = fontFile.bufferedReader()

    val fontInfo = parseHeader(reader)
    val chars = parseChars(reader, fontInfo)

    return FigFont(fontInfo, chars)
}

private fun parseHeader(reader: BufferedReader): FigFontInfo {
    val headerLine = reader.readLine() ?: throw Exception("Could not read header line")
    val (fontInfo, commentLines) = parseFigFontHeader(headerLine)

    skipComments(commentLines, reader)

    return fontInfo
}

private fun skipComments(numLines: Int, reader: BufferedReader) {
    repeat(numLines) {
        reader.readLine()
            ?: throw Exception("Expected $numLines lines of comments, only received $it")
    }
}

private fun parseChars(src: BufferedReader, fontInfo: FigFontInfo): Map<Int, FigChar> {
    val height = fontInfo.height
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