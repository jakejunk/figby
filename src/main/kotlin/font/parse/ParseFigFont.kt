package font.parse

import font.FigFont
import font.parseFigFontHeader
import layout.PrintDirection
import java.io.BufferedReader
import java.io.InputStream

internal fun parseFigFont(fontFile: InputStream): FigFont = fontFile.bufferedReader().use { reader ->
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
        printDirection = printDirection,
        comments = comments,
        layout = layout,
        figCharMap = chars
    )
}

private fun parsePrintDirection(printDirection: Int): PrintDirection {
    return when (printDirection) {
        0 -> PrintDirection.LeftToRight
        1 -> PrintDirection.RightToLeft
        else -> throw Exception("Print direction must be either 0 or 1")
    }
}

private fun readComments(numLines: Int, reader: BufferedReader): String {
    val lines = List(numLines) {
        reader.readLine()
            ?: throw Exception("Expected $numLines lines of comments, only received $it")
    }

    return lines.joinToString(separator = System.lineSeparator())
}

