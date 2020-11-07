package layout

enum class HorizontalLayoutMode(
    val bitMask: Int
) {
    FullWidth(0),
    Kerning(64),
    Smushing(128)
}

enum class VerticalLayoutMode(
    val bitMask: Int
) {
    FullHeight(0),
    VerticalFitting(8192),
    Smushing(16384)
}