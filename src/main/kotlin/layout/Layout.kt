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
    val hLayout = parseHorizontalLayout(layoutMask)
    val vLayout = parseVerticalLayout(layoutMask)
    val horizontalSmusher = parseHorizontalSmushing(layoutMask)
    val verticalSmusher = parseVerticalSmushing(layoutMask)

    return Layout(
        horizontalLayout = hLayout,
        verticalLayout = vLayout,
        horizontalSmusher = horizontalSmusher,
        verticalSmusher = verticalSmusher
    )
}

private fun parseHorizontalLayout(layoutMask: Int): HorizontalLayoutMode {
    val smushingMask = HorizontalLayoutMode.Smushing.bitMask
    val kerningMask = HorizontalLayoutMode.Kerning.bitMask

    return when {
        layoutMask and smushingMask == smushingMask -> HorizontalLayoutMode.Smushing
        layoutMask and kerningMask == kerningMask -> HorizontalLayoutMode.Kerning
        else -> HorizontalLayoutMode.FullWidth
    }
}

private fun parseVerticalLayout(layoutMask: Int): VerticalLayoutMode {
    val smushingMask = VerticalLayoutMode.Smushing.bitMask
    val fittingMask = VerticalLayoutMode.VerticalFitting.bitMask

    return when {
        layoutMask and smushingMask == smushingMask -> VerticalLayoutMode.Smushing
        layoutMask and fittingMask == fittingMask -> VerticalLayoutMode.VerticalFitting
        else -> VerticalLayoutMode.FullHeight
    }
}
