package figure

import font.FigCharLine
import font.FigFont
import layout.HorizontalLayoutMode
import kotlin.streams.toList

internal class FigureBuilder(
    private val font: FigFont
) {
    private val lines = Array(font.height) { FigureLineBuilder() }

    fun append(text: String) {
        val horizontalLayout = font.horizontalLayout
        val linesToAppend = text.codePoints().toList().mapNotNull { codePoint ->
            font[codePoint]?.lines
        }

        // TODO: Handling newlines

        when (horizontalLayout) {
            HorizontalLayoutMode.FullWidth -> linesToAppend.forEach { lines ->
                appendFullWidth(lines)
            }
            HorizontalLayoutMode.Kerning -> linesToAppend.forEach { lines ->
                appendKerning(lines)
            }
            HorizontalLayoutMode.Smushing -> linesToAppend.forEach { lines ->
                appendSmushing(lines)
            }
        }
    }

    private fun appendFullWidth(linesToAppend: List<FigCharLine>) {
        assert(lines.size == linesToAppend.size)

        lines.forEachIndexed { i, line ->
            line.append(linesToAppend[i])
        }
    }

    private fun appendKerning(linesToAppend: List<FigCharLine>) {
        val overlap = getKerningAdjustment(linesToAppend)

        assert(lines.size == linesToAppend.size)

        lines.forEachIndexed { i, line ->
            line.append(linesToAppend[i], overlap)
        }
    }

    private fun appendSmushing(linesToAppend: List<FigCharLine>) {
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
        val adjustments = lines
            .mapIndexed { i, line ->
                line.getSmushingAdjustment(linesToAppend[i], font)
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
