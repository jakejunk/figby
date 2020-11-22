package font.internal

import font.*

internal class FigFontLayout(
    val horizontalLayout: HorizontalLayoutMode,
    val verticalLayout: VerticalLayoutMode,
    val figFontSmusher: FigFontSmusher
)

internal fun parseOldLayout(oldLayout: Int): FigFontLayout {
    val fullLayout = when {
        oldLayout < 0 -> 0
        oldLayout > 0 -> oldLayout + 128
        else -> 64
    }

    return parseFullLayout(fullLayout)
}

internal fun parseFullLayout(fullLayout: Int): FigFontLayout {
    val hLayoutMode = parseHorizontalLayoutMode(fullLayout)
    val vLayoutMode = parseVerticalLayoutMode(fullLayout)
    val smusher = parseSmushingRules(fullLayout)

    return FigFontLayout(
        horizontalLayout = hLayoutMode,
        verticalLayout = vLayoutMode,
        figFontSmusher = smusher
    )
}
