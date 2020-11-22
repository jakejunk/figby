package font

public enum class PrintDirection(public val value: Int) {
    LeftToRight(0),
    RightToLeft(1)
}

internal fun parsePrintDirection(printDirection: Int): PrintDirection {
    return when (printDirection) {
        0 -> PrintDirection.LeftToRight
        1 -> PrintDirection.RightToLeft
        else -> throw Exception("Print direction must be either 0 or 1")
    }
}
