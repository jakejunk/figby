package figure

import font.FigCharRow
import font.FigFont
import layout.HorizontalLayoutMode
import kotlin.streams.toList

internal class FigureBuilder(
    private val font: FigFont
) {
    private val rows = Array(font.height) { FigureRowBuilder() }
    private var lineStarted = false

    fun append(text: String) {
        val horizontalLayout = font.horizontalLayout
        val rowsToAppend = text.codePoints().toList().mapNotNull { codePoint ->
            font[codePoint]?.rows
        }

        rowsToAppend.forEach { figCharRows ->
            // TODO: Handling newlines

            when {
                !lineStarted -> appendFullWidth(figCharRows)
                horizontalLayout == HorizontalLayoutMode.FullWidth -> appendFullWidth(figCharRows)
                horizontalLayout == HorizontalLayoutMode.Kerning -> appendKerning(figCharRows)
                horizontalLayout == HorizontalLayoutMode.Smushing -> appendSmushing(figCharRows)
            }

            lineStarted = true
        }
    }

    private fun appendFullWidth(rowsToAppend: List<FigCharRow>) {
        (rows zip rowsToAppend).forEach { (row, rowToAppend) ->
            row.append(rowToAppend)
        }
    }

    private fun appendKerning(rowsToAppend: List<FigCharRow>) {
        val adjustment = getKerningAdjustment(rowsToAppend)

        (rows zip rowsToAppend).forEach { (row, rowToAppend) ->
            row.append(rowToAppend, adjustment)
        }
    }

    private fun appendSmushing(rowsToAppend: List<FigCharRow>) {
        val (adjustment, smushResults) = getSmushingAdjustment(rowsToAppend)

        (rows zip rowsToAppend zip smushResults).forEach { (rows, smushResult) ->
            val (row, rowToAppend) = rows
            val smushedRowToAppend = when (smushResult) {
                null -> rowToAppend
                else -> rowToAppend butStartsWith smushResult
            }

            row.append(smushedRowToAppend, adjustment)
        }
    }

    private fun getKerningAdjustment(rowsToAppend: List<FigCharRow>): Int {
        return (rows zip rowsToAppend)
            .map { (row, rowToAppend) -> row.getKerningAdjustment(rowToAppend) }
            .minOrNull() ?: 0
    }

    private fun getSmushingAdjustment(rowsToAppend: List<FigCharRow>): Pair<Int, List<Int?>> {
        val adjustments = (rows zip rowsToAppend)
            .map { (row, rowToAppend) -> row.getSmushingAdjustment(rowToAppend, font) }

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

        return rows
            .fold(StringBuilder()) { builder, next -> builder.append(next.toString(hardblank)) }
            .toString()
    }
}
