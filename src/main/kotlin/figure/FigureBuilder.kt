package figure

import font.FigCharLine
import font.FigFont
import font.HorizontalLayoutMode
import font.Smusher
import util.toInt

class FigureBuilder(
    private val font: FigFont
) {
    private val lines = Array(font.height) { FigureLineBuilder() }

    fun append(text: String) {
        val horizontalLayout = font.layout.horizontalLayout
        val codePoints = text.codePoints()

        when (horizontalLayout) {
            HorizontalLayoutMode.FullWidth -> codePoints.forEach { appendFullWidth(it) }
            HorizontalLayoutMode.Kerning -> codePoints.forEach { appendKerning(it) }
            HorizontalLayoutMode.Smushing -> codePoints.forEach { appendSmushing(it) }
        }
    }

    private fun appendFullWidth(codePoint: Int) {
        val linesToAppend = font[codePoint]?.lines ?: return

        assert(lines.size == linesToAppend.size)

        lines.forEachIndexed { i, line ->
            line.append(linesToAppend[i])
        }
    }

    private fun appendKerning(codePoint: Int) {
        val linesToAppend = font[codePoint]?.lines ?: return
        val overlap = getKerningAdjustment(linesToAppend)

        assert(lines.size == linesToAppend.size)

        lines.forEachIndexed { i, line ->
            line.append(linesToAppend[i], overlap)
        }
    }

    private fun appendSmushing(codePoint: Int) {
        val linesToAppend = font[codePoint]?.lines ?: return
        val (adjustment, replacements) = getSmushingAdjustment(linesToAppend)

        // TODO: Look into a zip implementation

        assert(lines.size == linesToAppend.size)
        assert(lines.size == replacements.size)

        lines.forEachIndexed { i, line ->
            val smushResult = replacements[i]
            val originalLine = linesToAppend[i]
            val lineToAppend = when {
                smushResult != null -> originalLine butStartsWith smushResult
                else -> originalLine
            }

            line.append(lineToAppend, adjustment)
        }
    }

    private fun getKerningAdjustment(linesToAppend: List<FigCharLine>): Int {
        return lines
            .mapIndexed { i, line -> line.getKerningAdjustment(linesToAppend[i]) }
            .minOrNull() ?: 0
    }

    private fun getSmushingAdjustment(linesToAppend: List<FigCharLine>): Pair<Int, List<Int?>> {
        val hardblank = font.hardblank
        val horizontalSmushing = font.layout.smusher
        val adjustments = lines
            .mapIndexed { i, line ->
                line.getSmushingAdjustment(linesToAppend[i], hardblank, horizontalSmushing)
            }

        val smallestAdjustment = adjustments
            .minOf { (adjustment, _) -> adjustment }

        val replacements = adjustments
            .map { (adjustment, smushedChar) ->
                if (adjustment > smallestAdjustment) null else smushedChar
            }

        return Pair(smallestAdjustment, replacements)
    }

    fun buildFigure(): String {
        val hardblank = font.hardblank

        return lines
            .fold(StringBuilder()) { builder, next -> builder.append(next.toString(hardblank)) }
            .toString()
    }
}

private class FigureLineBuilder {
    private val subChars = mutableListOf<Int>()
    var trailingSpaces = 0
        private set

    fun getKerningAdjustment(figCharLine: FigCharLine): Int {
        return trailingSpaces + figCharLine.leadingSpaces
    }

    fun getSmushingAdjustment(figCharLine: FigCharLine, hardBlank: Int, smusher: Smusher): Pair<Int, Int?> {
        val kerningAdjustment = getKerningAdjustment(figCharLine)
        val smushedSubChar = getSmushedSubCharacter(figCharLine, hardBlank, smusher)
        val smushAdjustment = (smushedSubChar != null).toInt()

        return Pair(kerningAdjustment + smushAdjustment, smushedSubChar)
    }

    private fun getSmushedSubCharacter(figCharLine: FigCharLine, hardBlank: Int, smusher: Smusher): Int? {
        val left = subChars.lastOrNull() ?: return null
        val right = figCharLine.trimmedCodePoints.firstOrNull() ?: return null

        return smusher.tryHorizontalSmush(left, right, hardBlank)
    }

    fun append(line: FigCharLine, overlap: Int = 0) {
        if (line.isEmpty) {
            trailingSpaces += line.length - overlap
        } else {
            val spacesToAdd = when {
                subChars.isEmpty() -> line.leadingSpaces
                else -> trailingSpaces + line.leadingSpaces - overlap
            }

            if (spacesToAdd < 0) {
                trimEnd(-spacesToAdd)
            } else {
                appendSpaces(spacesToAdd)
            }

            appendCodePoints(line.trimmedCodePoints)
            trailingSpaces = line.trailingSpaces
        }
    }

    private fun trimEnd(trimCount: Int) {
        val trimStart = subChars.size - trimCount
        if (trimStart >= 0) {
            subChars.subList(trimStart, subChars.size).clear()
        }
    }

    private fun appendSpaces(numSpaces: Int) {
        val spaceCodePoint = ' '.toInt()

        subChars.apply {
            repeat(times = numSpaces) {
                add(spaceCodePoint)
            }
        }
    }

    private fun appendCodePoints(codePoints: List<Int>) {
        subChars.apply {
            codePoints.forEach {
                add(it)
            }
        }
    }

    override fun toString(): String {
        return toString('$'.toInt())
    }

    fun toString(hardblank: Int): String {
        val spaceCodePoint = ' '.toInt()
        val lineBuilder = StringBuilder().apply {
            subChars.forEach {
                val codePoint = if (it == hardblank) spaceCodePoint else it
                appendCodePoint(codePoint)
            }
        }

        return "${lineBuilder}${System.lineSeparator()}"
    }
}
