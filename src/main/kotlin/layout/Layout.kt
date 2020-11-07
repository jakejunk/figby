package layout

import java.util.*

data class Layout(
    val horizontalLayout: HorizontalLayoutMode,
    val verticalLayout: VerticalLayoutMode,
    val horizontalSmushing: HorizontalSmushing,
    private val verticalSmushingRules: EnumSet<VerticalSmushingRule>
)

enum class PrintDirection(
    val value: Int
) {
    LeftToRight(0),
    RightToLeft(1)
}

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
    val hSmushingRules = parseHorizontalSmushing(layoutMask)
    val vSmushingRules = parseVerticalSmushingRules(layoutMask)

    return Layout(
        horizontalLayout = hLayout,
        verticalLayout = vLayout,
        horizontalSmushing = hSmushingRules,
        verticalSmushingRules = vSmushingRules
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

private fun parseVerticalSmushingRules(layoutMask: Int): EnumSet<VerticalSmushingRule> {
    val rules = VerticalSmushingRule.values()
        .filter { layoutMask and it.bitMask == it.bitMask }

    return if (rules.isEmpty()) {
        EnumSet.noneOf(VerticalSmushingRule::class.java)
    } else {
        EnumSet.copyOf(rules)
    }
}
