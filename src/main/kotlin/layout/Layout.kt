package layout

data class Layout(
    val horizontalLayout: HorizontalLayoutMode,
    val verticalLayout: VerticalLayoutMode,
    val smusher: Smusher,
    val printDirection: PrintDirection
)

fun parseOldLayout(oldLayout: Int, printDirection: Int = 0): Layout {
    val fullLayout = when {
        oldLayout < 0 -> 0
        oldLayout > 0 -> oldLayout + 128
        else -> 64
    }

    return parseFullLayout(fullLayout, printDirection)
}

fun parseFullLayout(fullLayout: Int, printDirection: Int = 0): Layout {
    val hLayoutMode = parseHorizontalLayoutMode(fullLayout)
    val vLayoutMode = parseVerticalLayoutMode(fullLayout)
    val smusher = parseSmushingRules(fullLayout)
    val printDir = parsePrintDirection(printDirection)

    return Layout(
        horizontalLayout = hLayoutMode,
        verticalLayout = vLayoutMode,
        smusher = smusher,
        printDirection = printDir
    )
}
