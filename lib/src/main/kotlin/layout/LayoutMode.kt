package dev.junker.figby.layout

import dev.junker.figby.font.FigChar

/**
 * Defines the horizontal spacing between [FigChar]s.
 */
public enum class HorizontalLayoutMode {
    /**
     * [FigChar]s will occupy the full width of their sub-characters, as designed.
     */
    FullWidth,

    /**
     * [FigChar]s will move together and touch (but not overlap).
     */
    Kerning,

    /**
     * [FigChar]s will move together and attempt to overlap each other by one sub-character.
     * If any overlapping sub-characters cannot be smushed, [Kerning] will occur instead.
     * @see HorizontalSmushingRule
     */
    Smushing
}

/**
 * Defines the vertical spacing between [FigChar]s.
 */
public enum class VerticalLayoutMode {
    /**
     * [FigChar]s will occupy the full height of their sub-characters, as designed.
     */
    FullHeight,

    /**
     * [FigChar]s lines will move together and touch (but not overlap).
     */
    VerticalFitting,

    /**
     * [FigChar]s lines will move together and attempt to overlap each other by one sub-character.
     * If any overlapping sub-characters cannot be smushed, [VerticalFitting] will occur instead.
     * @see VerticalSmushingRule
     */
    Smushing
}
