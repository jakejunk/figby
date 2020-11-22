package font

/**
 * Defines the horizontal spacing between [FigChar]s.
 */
public enum class HorizontalLayoutMode(public val bitMask: Int) {
    /**
     * [FigChar]s will occupy the full width of their sub-characters, as designed.
     */
    FullWidth(0),

    /**
     * [FigChar]s will move together and touch (but not overlap).
     */
    Kerning(64),

    /**
     * [FigChar]s will move together and attempt to overlap each other by one sub-character.
     * If any overlapping sub-characters cannot be smushed, [Kerning] will occur instead.
     * @see HorizontalSmushingRule
     */
    Smushing(128)
}

/**
 * Defines the vertical spacing between [FigChar]s.
 */
public enum class VerticalLayoutMode(public val bitMask: Int) {
    /**
     * [FigChar]s will occupy the full height of their sub-characters, as designed.
     */
    FullHeight(0),

    /**
     * [FigChar] lines will move together and touch (but not overlap).
     */
    VerticalFitting(8192),

    /**
     * [FigChar] lines will move together and attempt to overlap each other by one sub-character.
     * If any overlapping sub-characters cannot be smushed, [VerticalFitting] will occur instead.
     * @see VerticalSmushingRule
     */
    Smushing(16384)
}

internal fun parseHorizontalLayoutMode(layoutMask: Int): HorizontalLayoutMode {
    val smushingMask = HorizontalLayoutMode.Smushing.bitMask
    val kerningMask = HorizontalLayoutMode.Kerning.bitMask

    return when {
        layoutMask and smushingMask == smushingMask -> HorizontalLayoutMode.Smushing
        layoutMask and kerningMask == kerningMask -> HorizontalLayoutMode.Kerning
        else -> HorizontalLayoutMode.FullWidth
    }
}

internal fun parseVerticalLayoutMode(layoutMask: Int): VerticalLayoutMode {
    val smushingMask = VerticalLayoutMode.Smushing.bitMask
    val fittingMask = VerticalLayoutMode.VerticalFitting.bitMask

    return when {
        layoutMask and smushingMask == smushingMask -> VerticalLayoutMode.Smushing
        layoutMask and fittingMask == fittingMask -> VerticalLayoutMode.VerticalFitting
        else -> VerticalLayoutMode.FullHeight
    }
}
