package font

import layout.*

data class FigFontInfo(
    /**
     * The code point representing the hardblank character for this font.
     */
    val hardblank: Int,
    /**
     * The height of every [FigChar] within this font, measured in sub-characters.
     */
    val height: Int,
    /**
     * The height of a [FigChar], ignoring any descenders.
     */
    val baseline: Int,
    /**
     * Greater than or equal to the width of the widest [FigChar], plus 2.
     */
    val maxLength: Int,
    val layout: Layout,
)

fun parseFigFontHeader(headerLine: String): Pair<FigFontInfo, Int> {
    val parts = headerLine.split(" ")
    if (parts.size < 6) {
        throw Exception("Malformed header, TODO")
    }

    // Required values [0, 5]
    val hardblank = parseSignature(parts[0])
    val height = parseNumericParam(parts[1], "height")
    val baseline = parseNumericParam(parts[2], "baseline")
    val maxLength = parseNumericParam(parts[3], "max length")
    val oldLayout = parseNumericParam(parts[4], "old layout")
    val commentLines = parseNumericParam(parts[5], "comment lines")

    // Optional values [6, 7]
    val printDirection = parts.getOrNull(6)?.toIntOrNull() ?: 0
    val layout = when (val fullLayout = parts.getOrNull(7)?.toIntOrNull()) {
        null -> parseOldLayout(oldLayout, printDirection)
        else -> parseFullLayout(fullLayout, printDirection)
    }

    // There's also a "Codetag_Count" parameter, but it doesn't seem useful

    return Pair(FigFontInfo(
        hardblank = hardblank,
        height = height,
        baseline = baseline,
        maxLength = maxLength,
        layout = layout
    ), commentLines)
}

private fun parseSignature(signatureAndHardblank: String): Int {
    val signature = "flf2a"
    val signatureParts = signatureAndHardblank.split(signature)

    if (signatureParts.size != 2 || signatureParts[0] != "") {
        throw Exception("Malformed signature")
    }

    return signatureParts[1].codePointAt(0)
}

private fun parseNumericParam(param: String, paramName: String): Int {
    return param.toIntOrNull() ?: throw Exception("Could not parse $paramName")
}
