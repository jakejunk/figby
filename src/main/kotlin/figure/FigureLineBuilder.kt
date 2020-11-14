package figure

import font.FigCharLine
import layout.Smusher
import util.toInt

class FigureLineBuilder {
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
            trailingSpaces += line.leadingSpaces - overlap
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