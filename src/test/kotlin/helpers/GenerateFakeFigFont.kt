package helpers

import font.*
import font.internal.FigFontSmusher

fun fakeFontWithHorizontalRules(hardblank: Int, vararg rules: HorizontalSmushingRule): FigFont {
    val smusher = FigFontSmusher(
        horizontalRules = rules.asList()
    )

    return FigFont(
        hardblank = hardblank,
        height = 1,
        baseline = 1,
        maxLength = 1,
        horizontalLayout = HorizontalLayoutMode.Smushing,
        verticalLayout = VerticalLayoutMode.Smushing,
        printDirection = PrintDirection.LeftToRight,
        comments = "FAKE FONT",
        figFontSmusher = smusher,
        chars = emptyMap()
    )
}

//fun generateFakeFigFont(
//    horizontalRules: List<HorizontalSmushingRule>,
//    verticalRules: List<VerticalSmushingRule>
//): FigFont {
//    val smusher = FigFontSmusher(horizontalRules, verticalRules)
//
//    return FigFont(
//        hardblank = ,
//        height = ,
//        baseline = ,
//        maxLength = ,
//        horizontalLayout = ,
//        verticalLayout = ,
//        printDirection = PrintDirection.LeftToRight,
//        comments = "FAKE FONT",
//        figFontSmusher = smusher,
//        chars = emptyMap()
//    )
//}