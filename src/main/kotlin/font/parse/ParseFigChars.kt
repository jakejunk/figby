package font.parse

import font.FigChar
import font.FigCharRow
import java.io.BufferedReader
import kotlin.streams.toList

internal fun parseChars(src: BufferedReader, maxLength: Int, height: Int): Map<Int, FigChar> {
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
    val firstRow = readSubCharRow(src, maxLength)
    val rows = mutableListOf(firstRow)

    repeat(height - 1) {
        val row = readSubCharRow(src, maxLength)
        if (row.length != firstRow.length) {
            throw Exception("Expected a width of ${firstRow.length}, found ${row.length} characters")
        }

        rows.add(row)
    }

    return FigChar(rows)
}

private fun readSubCharRow(src: BufferedReader, maxLength: Int): FigCharRow {
    val subCharRow = src.readLine()
        ?: throw Exception("Unexpected end of sub-character input")

    if (subCharRow.length > maxLength) {
        throw Exception("Sub-character row width exceeds specified max length (${subCharRow.length} > $maxLength)")
    }

    val subChars = subCharRow.split('@')[0]
        .codePoints()
        .toList()

    return FigCharRow(subChars)
}