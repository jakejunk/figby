package dev.junker.figby.font.parse

import dev.junker.figby.layout.*

internal fun parseOldLayout(oldLayout: Int): Layout {
    val fullLayout = when {
        oldLayout < 0 -> 0
        oldLayout > 0 -> oldLayout + 128
        else -> 64
    }

    return parseFullLayout(fullLayout)
}

internal fun parseFullLayout(fullLayout: Int): Layout {
    val hLayoutMode = parseHorizontalLayoutMode(fullLayout)
    val vLayoutMode = parseVerticalLayoutMode(fullLayout)
    val horizontalRules = parseHorizontalSmushingRules(fullLayout)
    val verticalRules = parseVerticalSmushingRules(fullLayout)

    return Layout(
        horizontalLayout = hLayoutMode,
        verticalLayout = vLayoutMode,
        horizontalRules = horizontalRules,
        verticalRules = verticalRules
    )
}

private fun parseHorizontalLayoutMode(layoutMask: Int): HorizontalLayoutMode = when {
    layoutMask and 128 == 128 -> HorizontalLayoutMode.Smushing
    layoutMask and 64 == 64 -> HorizontalLayoutMode.Kerning
    else -> HorizontalLayoutMode.FullWidth
}

private fun parseVerticalLayoutMode(layoutMask: Int): VerticalLayoutMode = when {
    layoutMask and 16384 == 16384 -> VerticalLayoutMode.Smushing
    layoutMask and 8192 == 8192 -> VerticalLayoutMode.VerticalFitting
    else -> VerticalLayoutMode.FullHeight
}

private fun parseHorizontalSmushingRules(layoutMask: Int): List<HorizontalSmushingRule> {
    return mapOf(
        1 to HorizontalSmushingRule.EqualCharacter,
        2 to HorizontalSmushingRule.Underscore,
        4 to HorizontalSmushingRule.Hierarchy,
        8 to HorizontalSmushingRule.OppositePair,
        16 to HorizontalSmushingRule.BigX,
        32 to HorizontalSmushingRule.Hardblank
    ).filterKeys { ruleMask ->
        layoutMask and ruleMask == ruleMask
    }.values.toList()
}

private fun parseVerticalSmushingRules(layoutMask: Int): List<VerticalSmushingRule> {
    return mapOf(
        256 to VerticalSmushingRule.EqualCharacter,
        512 to VerticalSmushingRule.Underscore,
        1024 to VerticalSmushingRule.Hierarchy,
        2048 to VerticalSmushingRule.HorizontalLine,
        4096 to VerticalSmushingRule.VerticalLine,
    ).filterKeys { ruleMask ->
        layoutMask and ruleMask == ruleMask
    }.values.toList()
}
