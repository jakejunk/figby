package font

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

