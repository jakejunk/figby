package layout

/**
 * Defines the horizontal spacing between [font.FigChar]s.
 */
public enum class HorizontalLayoutMode {
    /**
     * [font.FigChar]s will occupy the full width of their sub-characters, as designed.
     */
    FullWidth,

    /**
     * [font.FigChar]s will move together and touch (but not overlap).
     */
    Kerning,

    /**
     * [font.FigChar]s will move together and attempt to overlap each other by one sub-character.
     * If any overlapping sub-characters cannot be smushed, [Kerning] will occur instead.
     * @see HorizontalSmushingRule
     */
    Smushing
}

/**
 * Defines the vertical spacing between [font.FigChar]s.
 */
public enum class VerticalLayoutMode {
    /**
     * [font.FigChar]s will occupy the full height of their sub-characters, as designed.
     */
    FullHeight,

    /**
     * [font.FigChar] lines will move together and touch (but not overlap).
     */
    VerticalFitting,

    /**
     * [font.FigChar] lines will move together and attempt to overlap each other by one sub-character.
     * If any overlapping sub-characters cannot be smushed, [VerticalFitting] will occur instead.
     * @see VerticalSmushingRule
     */
    Smushing
}
