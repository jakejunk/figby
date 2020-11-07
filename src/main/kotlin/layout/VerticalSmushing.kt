package layout

enum class VerticalSmushingRule(
    val bitMask: Int
) {
    EqualCharacter(256),
    Underscore(512),
    Hierarchy(1024),
    HorizontalLine(2048),
    VerticalLine(4096)
}