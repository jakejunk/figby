package font

import util.leading
import util.trailing

/**
 * An organized collection of code points (aka sub-characters)
 * representing a single character in a [FigFont].
 */
public data class FigChar internal constructor(
    internal val lines: List<FigCharLine>
) {
    val width: Int
        get() = lines[0].length

    val height: Int
        get() = lines.size
}

internal class FigCharLine {
    val leadingSpaces: Int
    val trailingSpaces: Int
    val trimmedCodePoints: List<Int>

    internal constructor(subChars: List<Int>) {
        leadingSpaces = subChars.leading(' '.toInt())
        trailingSpaces = subChars.trailing(' '.toInt())
        trimmedCodePoints = if (subChars.size == leadingSpaces) {
            emptyList()
        } else {
            subChars.subList(leadingSpaces, subChars.size - trailingSpaces)
        }
    }

    private constructor(other: FigCharLine, smushResult: Int) {
        leadingSpaces = other.leadingSpaces
        trailingSpaces = other.trailingSpaces
        trimmedCodePoints = other.trimmedCodePoints
            .toMutableList()
            .apply { if (this.isNotEmpty()) this[0] = smushResult }
    }

    val isEmpty: Boolean
        get() = trimmedCodePoints.isEmpty()

    val length: Int
        get() = when {
            isEmpty -> leadingSpaces
            else -> leadingSpaces + trailingSpaces + trimmedCodePoints.size
        }

    infix fun butStartsWith(smushResult: Int): FigCharLine {
        return FigCharLine(this, smushResult)
    }
}
