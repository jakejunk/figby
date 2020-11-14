package font.internal

internal class FigFontHeader(
    val hardblank: Int,
    val height: Int,
    val baseline: Int,
    val maxLength: Int,
    val oldLayout: Int,
    val commentLines: Int,
    val printDirection: Int,
    val fullLayout: Int?
)

internal fun parseFigFontHeader(headerLine: String): FigFontHeader {
    val params = headerLine.split(" ")
    if (params.size < 6) {
        throw Exception("Malformed header, TODO")
    }

    // Required values [0, 5]
    val hardblank = parseSignature(params[0])
    val height = parseNumericParam(params[1], "height")
    val baseline = parseNumericParam(params[2], "baseline")
    val maxLength = parseNumericParam(params[3], "max length")
    val oldLayout = parseNumericParam(params[4], "old layout")
    val commentLines = parseNumericParam(params[5], "comment lines")

    // Optional values [6, 7]
    val printDirection = params.getOrNull(6)?.let { parseNumericParam(it, "print direction") } ?: 0
    val fullLayout = params.getOrNull(7)?.let { parseNumericParam(it, "full layout") }

    // There's also a "Codetag_Count" parameter, but it doesn't seem useful

    return FigFontHeader(
        hardblank = hardblank,
        height = height,
        baseline = baseline,
        maxLength = maxLength,
        oldLayout = oldLayout,
        commentLines = commentLines,
        printDirection = printDirection,
        fullLayout = fullLayout
    )
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
