package layout

internal class Layout(
    val horizontalLayout: HorizontalLayoutMode,
    val verticalLayout: VerticalLayoutMode,
    val horizontalRules: List<HorizontalSmushingRule> = emptyList(),
    val verticalRules: List<VerticalSmushingRule> = emptyList()
) {
    fun tryHorizontalSmush(left: Int, right: Int, hardblank: Int): Int? = when {
        horizontalRules.isNotEmpty() -> applyHorizontalSmushing(left, right, hardblank)
        else -> applyUniversalSmushing(left, right, hardblank)
    }

    fun tryVerticalSmush(top: Int, bottom: Int, hardblank: Int): Int? = when {
        verticalRules.isNotEmpty() -> applyVerticalSmushing(top, bottom)
        else -> applyUniversalSmushing(top, bottom, hardblank)
    }

    private fun applyHorizontalSmushing(left: Int, right: Int, hardblank: Int): Int? {
        return horizontalRules
            .asSequence()
            .mapNotNull { rule -> rule.apply(left, right, hardblank) }
            .firstOrNull()
    }

    private fun applyVerticalSmushing(top: Int, bottom: Int): Int? {
        return verticalRules
            .asSequence()
            .mapNotNull { rule -> rule.apply(top, bottom) }
            .firstOrNull()
    }

    private fun applyUniversalSmushing(former: Int, latter: Int, hardblank: Int): Int = when {
        Character.isWhitespace(former) -> latter
        Character.isWhitespace(latter) -> former
        latter == hardblank -> former
        else -> latter
    }
}

