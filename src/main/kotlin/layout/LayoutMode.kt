package layout

/**
 * Defines the horizontal spacing between [font.FigChar]s.
 */
public enum class HorizontalLayoutMode(public val bitMask: Int) {
    /**
     * [font.FigChar]s will occupy the full width of their sub-characters, as designed.
     */
    FullWidth(0),

    /**
     * [font.FigChar]s will move together and touch (but not overlap).
     */
    Kerning(64),

    /**
     * [font.FigChar]s will move together and attempt to overlap each other by one sub-character.
     * If any overlapping sub-characters cannot be smushed, [Kerning] will occur instead.
     * @see HorizontalSmushingRule
     */
    Smushing(128)
}

/**
 * Defines the vertical spacing between [font.FigChar]s.
 */
public enum class VerticalLayoutMode(public val bitMask: Int) {
    /**
     * [font.FigChar]s will occupy the full height of their sub-characters, as designed.
     */
    FullHeight(0),

    /**
     * [font.FigChar] lines will move together and touch (but not overlap).
     */
    VerticalFitting(8192),

    /**
     * [font.FigChar] lines will move together and attempt to overlap each other by one sub-character.
     * If any overlapping sub-characters cannot be smushed, [VerticalFitting] will occur instead.
     * @see VerticalSmushingRule
     */
    Smushing(16384)
}
