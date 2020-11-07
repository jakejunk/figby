package font

data class FigChar internal constructor(
    val lines: List<FigCharLine>
) {
    val width: Int
        get() = lines[0].length

    val height: Int
        get() = lines.size
}
