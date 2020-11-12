package layout

enum class HorizontalLayoutMode(val bitMask: Int) {
    FullWidth(0),
    Kerning(64),
    Smushing(128)
}

enum class VerticalLayoutMode(val bitMask: Int) {
    FullHeight(0),
    VerticalFitting(8192),
    Smushing(16384)
}

fun parseHorizontalLayoutMode(layoutMask: Int): HorizontalLayoutMode {
    val smushingMask = HorizontalLayoutMode.Smushing.bitMask
    val kerningMask = HorizontalLayoutMode.Kerning.bitMask

    return when {
        layoutMask and smushingMask == smushingMask -> HorizontalLayoutMode.Smushing
        layoutMask and kerningMask == kerningMask -> HorizontalLayoutMode.Kerning
        else -> HorizontalLayoutMode.FullWidth
    }
}

fun parseVerticalLayoutMode(layoutMask: Int): VerticalLayoutMode {
    val smushingMask = VerticalLayoutMode.Smushing.bitMask
    val fittingMask = VerticalLayoutMode.VerticalFitting.bitMask

    return when {
        layoutMask and smushingMask == smushingMask -> VerticalLayoutMode.Smushing
        layoutMask and fittingMask == fittingMask -> VerticalLayoutMode.VerticalFitting
        else -> VerticalLayoutMode.FullHeight
    }
}
