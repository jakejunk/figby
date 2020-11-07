package figure

import font.FigCharLine
import font.FigFont
import layout.HorizontalLayoutMode

class FigureBuilder(
    private val font: FigFont
) {
    private val lines = Array(font.info.height) { FigureLineBuilder() }

    fun append(text: String) {
        val horizontalLayout = font.info.layout.horizontalLayout
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
        val hardblank = font.info.hardblank
        val horizontalSmushing = font.info.layout.horizontalSmushing
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
        val hardblank = font.info.hardblank

        return lines
            .fold(StringBuilder()) { builder, next -> builder.append(next.toString(hardblank)) }
            .toString()
    }
}
