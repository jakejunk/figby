package dev.junker.figby.font

import dev.junker.figby.font.parse.parseFigFont
import dev.junker.figby.layout.*
import java.io.InputStream

/**
 * Describes the layout and graphical arrangement of sub-characters representing [FigChar]s.
 */
public data class FigFont internal constructor(
    /**
     * The code point representing the hardblank character for this font.
     */
    val hardblank: Int,
    /**
     * The height of every [FigChar] within this font, measured in sub-characters.
     */
    val height: Int,
    /**
     * The height of a [FigChar], ignoring any descenders.
     */
    val baseline: Int,
    /**
     * Greater than or equal to the width of the widest [FigChar], plus 2.
     */
    val maxLength: Int,
    /**
     * Specifies whether this font should be printed left-to-right or right-to-left.
     */
    val printDirection: PrintDirection,
    /**
     * The comments included as part of this font, which may include
     * anything from the font name to the author's contact info.
     */
    val comments: String,
    private val layout: Layout,
    private val figCharMap: Map<Int, FigChar>
) {
    /**
     * Defines the layout mode for the horizontal axis,
     * which specifies how much spacing to place between [FigChar]s.
     */
    val horizontalLayout: HorizontalLayoutMode
        get() = layout.horizontalLayout

    /**
     * Defines the layout mode for the vertical axis,
     * which specifies how much spacing to place between [FigChar]s.
     */
    val verticalLayout: VerticalLayoutMode
        get() = layout.verticalLayout

    /**
     * The horizontal smushing rules for this font.
     * @see HorizontalSmushingRule
     * @see tryHorizontalSmush
     */
    val horizontalSmushingRules: List<HorizontalSmushingRule>
        get() = layout.horizontalRules

    /**
     * The vertical smushing rules for this font.
     * @see VerticalSmushingRule
     * @see tryVerticalSmush
     */
    val verticalSmushingRules: List<VerticalSmushingRule>
        get() = layout.verticalRules

    /**
     * Returns the [FigChar] associated with the provided code point.
     * If [codePoint] is not represented in this font, the FigChar for
     * code point `0` will be returned, if present.
     */
    public operator fun get(codePoint: Int): FigChar? {
        return figCharMap[codePoint] ?: figCharMap[0]
    }

    /**
     * Attempts to smush [left] and [right] in accordance with this font's [horizontalSmushingRules],
     * returning the smushed code point if successful.
     * @see HorizontalSmushingRule
     */
    public fun tryHorizontalSmush(left: Int, right: Int): Int? {
        return layout.tryHorizontalSmush(left, right, hardblank)
    }

    /**
     * Attempts to smush [top] and [bottom] in accordance with this font's [verticalSmushingRules],
     * returning the smushed code point if successful.
     * @see VerticalSmushingRule
     */
    public fun tryVerticalSmush(top: Int, bottom: Int): Int? {
        return layout.tryVerticalSmush(top, bottom, hardblank)
    }

    public companion object {
        /**
         * Creates a [FigFont] from the given font file (`.flf`).
         */
        public fun fromFile(fontFile: InputStream): FigFont {
            return parseFigFont(fontFile)
        }
    }
}

