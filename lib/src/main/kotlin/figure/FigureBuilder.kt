package dev.junker.figby.figure

import dev.junker.figby.font.FigCharRow
import dev.junker.figby.font.FigFont
import dev.junker.figby.layout.HorizontalLayoutMode
import kotlin.streams.toList

internal class FigureBuilder(
    private val font: FigFont
) {
    fun buildFigure(text: String): String {
        val rows = Array(font.height) { FigureRowBuilder() }
        val horizontalLayout = font.horizontalLayout
        val rowsToAppend = text.codePoints().toList().mapNotNull { codePoint ->
            font[codePoint]?.rows
        }

        var lineStarted = false

        rowsToAppend.forEach { figCharRows ->
            // TODO: Handling newlines

            when {
                !lineStarted -> rows.appendFullWidth(figCharRows)
                horizontalLayout == HorizontalLayoutMode.FullWidth -> rows.appendFullWidth(figCharRows)
                horizontalLayout == HorizontalLayoutMode.Kerning -> rows.appendKerning(figCharRows)
                horizontalLayout == HorizontalLayoutMode.Smushing -> rows.appendSmushing(figCharRows)
            }

            lineStarted = true
        }

        return buildString {
            rows.forEach { rowBuilder ->
                append(rowBuilder.toString(font.hardblank))
            }
        }
    }

    private fun Array<FigureRowBuilder>.appendFullWidth(rowsToAppend: List<FigCharRow>) {
        (this zip rowsToAppend).forEach { (row, rowToAppend) ->
            row.append(rowToAppend)
        }
    }

    private fun Array<FigureRowBuilder>.appendKerning(rowsToAppend: List<FigCharRow>) {
        val adjustment = getKerningAdjustment(rowsToAppend)

        (this zip rowsToAppend).forEach { (row, rowToAppend) ->
            row.append(rowToAppend, adjustment)
        }
    }

    private fun Array<FigureRowBuilder>.appendSmushing(rowsToAppend: List<FigCharRow>) {
        val (adjustment, smushResults) = getSmushingAdjustment(rowsToAppend)

        (this zip rowsToAppend zip smushResults).forEach { (rows, smushResult) ->
            val (row, rowToAppend) = rows
            val smushedRowToAppend = when (smushResult) {
                null -> rowToAppend
                else -> rowToAppend butStartsWith smushResult
            }

            row.append(smushedRowToAppend, adjustment)
        }
    }

    private fun Array<FigureRowBuilder>.getKerningAdjustment(rowsToAppend: List<FigCharRow>): Int {
        return (this zip rowsToAppend)
            .map { (row, rowToAppend) -> row.getKerningAdjustment(rowToAppend) }
            .minOrNull() ?: 0
    }

    private fun Array<FigureRowBuilder>.getSmushingAdjustment(rowsToAppend: List<FigCharRow>): Pair<Int, List<Int?>> {
        val adjustments = (this zip rowsToAppend)
            .map { (row, rowToAppend) -> row.getSmushingAdjustment(rowToAppend, font) }

        val smallestAdjustment = adjustments
            .minOf { (adjustment, _) -> adjustment }

        val replacements = adjustments
            .map { (adjustment, smushedChar) ->
                if (adjustment > smallestAdjustment) null else smushedChar
            }

        return Pair(smallestAdjustment, replacements)
    }
}
