package dev.junker.figby.font

import dev.junker.figby.util.SPACE
import dev.junker.figby.util.leading
import dev.junker.figby.util.trailing

internal class FigCharRow {
    val leadingSpaces: Int
    val trailingSpaces: Int
    val trimmedCodePoints: List<Int>

    internal constructor(subChars: List<Int>) {
        leadingSpaces = subChars.leading(SPACE)
        trailingSpaces = subChars.trailing(SPACE)
        trimmedCodePoints = if (subChars.size == leadingSpaces) {
            emptyList()
        } else {
            subChars.subList(leadingSpaces, subChars.size - trailingSpaces)
        }
    }

    private constructor(other: FigCharRow, smushResult: Int) {
        leadingSpaces = other.leadingSpaces
        trailingSpaces = other.trailingSpaces
        trimmedCodePoints = other.trimmedCodePoints
            .toMutableList()
            .apply { if (this.isNotEmpty()) this[0] = smushResult }
    }

    infix fun butStartsWith(smushResult: Int): FigCharRow {
        return FigCharRow(this, smushResult)
    }

    val isEmpty: Boolean
        get() = trimmedCodePoints.isEmpty()

    val length: Int
        get() = when {
            isEmpty -> leadingSpaces
            else -> leadingSpaces + trailingSpaces + trimmedCodePoints.size
        }

    override fun toString(): String {
        return if (isEmpty) {
            " ".repeat(length)
        } else buildString {
            repeat(leadingSpaces) { appendCodePoint(SPACE) }
            trimmedCodePoints.forEach { appendCodePoint(it) }
            repeat(trailingSpaces) { appendCodePoint(SPACE) }
        }
    }
}