package helpers

import font.*
import font.internal.FigFontSmusher

fun fakeFontWithHorizontalRules(hardblank: Int, vararg rules: HorizontalSmushingRule): FigFont {
    return generateFakeFigFont(
        hardblank = hardblank,
        figFontSmusher = FigFontSmusher(
            horizontalRules = rules.asList()
        )
    )
}

fun fakeFontWithVerticalRules(hardblank: Int, vararg rules: VerticalSmushingRule): FigFont {
    return generateFakeFigFont(
        hardblank = hardblank,
        figFontSmusher = FigFontSmusher(
            verticalRules = rules.asList()
        )
    )
}

private fun generateFakeFigFont(
    hardblank: Int = 0,
    height: Int = 1,
    baseline: Int = 1,
    maxLength: Int = 1,
    horizontalLayout: HorizontalLayoutMode = HorizontalLayoutMode.FullWidth,
    verticalLayout: VerticalLayoutMode = VerticalLayoutMode.FullHeight,
    printDirection: PrintDirection = PrintDirection.LeftToRight,
    comments: String = "FAKE FONT",
    figFontSmusher: FigFontSmusher = FigFontSmusher(),
    chars: Map<Int, FigChar> = emptyMap()
): FigFont = FigFont(
    hardblank = hardblank,
    height = height,
    baseline = baseline,
    maxLength = maxLength,
    horizontalLayout = horizontalLayout,
    verticalLayout = verticalLayout,
    printDirection = printDirection,
    comments = comments,
    figFontSmusher = figFontSmusher,
    chars = chars
)