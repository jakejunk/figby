package layout

data class Layout(
    val horizontalLayout: HorizontalLayoutMode,
    val verticalLayout: VerticalLayoutMode,
    val horizontalSmusher: HorizontalSmusher,
    val verticalSmusher: VerticalSmusher
)

fun parseOldLayout(oldLayout: String): Layout {
    val layout = oldLayout.toIntOrNull() ?: throw Exception("Could not parse old layout")
    val fullLayout = when {
        layout < 0 -> 0
        layout > 0 -> layout + 128
        else -> 64
    }

    return parseLayoutMask(fullLayout)
}

fun parseFullLayout(fullLayout: String): Layout {
    val layout = fullLayout.toIntOrNull() ?: throw Exception("Could not parse full layout")

    return parseLayoutMask(layout)
}

private fun parseLayoutMask(layoutMask: Int): Layout {
    val hLayoutMode = parseHorizontalLayoutMode(layoutMask)
    val vLayoutMode = parseVerticalLayoutMode(layoutMask)
    val horizontalSmusher = parseHorizontalSmushing(layoutMask)
    val verticalSmusher = parseVerticalSmushing(layoutMask)

    return Layout(
        horizontalLayout = hLayoutMode,
        verticalLayout = vLayoutMode,
        horizontalSmusher = horizontalSmusher,
        verticalSmusher = verticalSmusher
    )
}
