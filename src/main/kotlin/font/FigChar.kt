package font

/**
 * An organized collection of code points (aka sub-characters)
 * representing a single character in a [FigFont].
 */
public data class FigChar internal constructor(
    internal val rows: List<FigCharRow>
) {
    val width: Int
        get() = rows[0].length

    val height: Int
        get() = rows.size
}

