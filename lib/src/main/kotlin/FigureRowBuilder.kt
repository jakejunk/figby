package dev.junker.figby

import dev.junker.figby.font.FigCharRow
import dev.junker.figby.font.FigFont

internal class FigureRowBuilder {
    private val subChars = mutableListOf<Int>()
    private var trailingSpaces = 0

    fun getKerningAdjustment(figCharRow: FigCharRow): Int {
        return trailingSpaces + figCharRow.leadingSpaces
    }

    fun getSmushingAdjustment(figCharRow: FigCharRow, font: FigFont): Pair<Int, Int?> {
        val kerningAdjustment = getKerningAdjustment(figCharRow)
        val smushedSubChar = getSmushedSubCharacter(figCharRow, font)
        val smushAdjustment = when (smushedSubChar) {
            null -> kerningAdjustment
            else -> kerningAdjustment + 1
        }

        return Pair(smushAdjustment, smushedSubChar)
    }

    private fun getSmushedSubCharacter(figCharRow: FigCharRow, font: FigFont): Int? {
        val left = subChars.lastOrNull() ?: return null
        val right = figCharRow.trimmedCodePoints.firstOrNull() ?: return null

        return font.tryHorizontalSmush(left, right)
    }

    fun append(row: FigCharRow, adjustment: Int = 0) {
        if (row.isEmpty) {
            trailingSpaces += row.length - adjustment
        } else {
            val spacesToAdd = trailingSpaces + row.leadingSpaces - adjustment

            if (spacesToAdd < 0) {
                trimEnd(-spacesToAdd)
            } else {
                appendSpaces(spacesToAdd)
            }

            appendCodePoints(row.trimmedCodePoints)
            trailingSpaces = row.trailingSpaces
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
        val rowBuilder = StringBuilder().apply {
            subChars.forEach {
                val codePoint = if (it == hardblank) spaceCodePoint else it
                appendCodePoint(codePoint)
            }
        }

        return "${rowBuilder}${System.lineSeparator()}"
    }
}