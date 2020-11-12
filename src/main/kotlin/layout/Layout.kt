package layout

data class Layout(
    val horizontalLayout: HorizontalLayoutMode,
    val verticalLayout: VerticalLayoutMode,
    val horizontalSmusher: HorizontalSmusher,
    val verticalSmusher: VerticalSmusher,
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
    val horizontalSmusher = parseHorizontalSmushing(fullLayout)
    val verticalSmusher = parseVerticalSmushing(fullLayout)
    val printDir = parsePrintDirection(printDirection)

    return Layout(
        horizontalLayout = hLayoutMode,
        verticalLayout = vLayoutMode,
        horizontalSmusher = horizontalSmusher,
        verticalSmusher = verticalSmusher,
        printDirection = printDir
    )
}
