package helpers

import font.*
import layout.*

fun fakeFontWithHorizontalRules(hardblank: Int, vararg rules: HorizontalSmushingRule): FigFont {
    val layout = Layout(
        horizontalLayout = HorizontalLayoutMode.FullWidth,
        verticalLayout = VerticalLayoutMode.FullHeight,
        horizontalRules = rules.asList()
    )

    return generateFakeFigFont(
        hardblank = hardblank,
        layout = layout
    )
}

fun fakeFontWithVerticalRules(hardblank: Int, vararg rules: VerticalSmushingRule): FigFont {
    val layout = Layout(
        horizontalLayout = HorizontalLayoutMode.FullWidth,
        verticalLayout = VerticalLayoutMode.FullHeight,
        verticalRules = rules.asList()
    )

    return generateFakeFigFont(
        hardblank = hardblank,
        layout = layout
    )
}

private fun generateFakeFigFont(
    hardblank: Int = 0,
    height: Int = 1,
    baseline: Int = 1,
    maxLength: Int = 1,
    printDirection: PrintDirection = PrintDirection.LeftToRight,
    comments: String = "FAKE FONT",
    layout: Layout = Layout(
        horizontalLayout = HorizontalLayoutMode.FullWidth,
        verticalLayout = VerticalLayoutMode.FullHeight,
    ),
    chars: Map<Int, FigChar> = emptyMap()
): FigFont = FigFont(
    hardblank = hardblank,
    height = height,
    baseline = baseline,
    maxLength = maxLength,
    printDirection = printDirection,
    comments = comments,
    layout = layout,
    figCharMap = chars
)
