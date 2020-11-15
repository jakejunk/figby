package helpers

import java.io.InputStream

fun fakeFigFontFile(
    headerLine: String,
    comments: String,
    charWidth: Int,
    charHeight: Int
): InputStream {
    val figChars = ((32..126) + listOf(196, 214, 220, 223, 228, 246, 252))
        .joinToString(separator = "") {
            fakeFigFontChar(it, charWidth, charHeight)
        }

    val fakeFileContents = StringBuilder().apply {
        append(headerLine)
        append('\n')
        append(comments)
        append('\n')
        append(figChars)
    }.toString()

    return fakeFileContents.byteInputStream()
}

fun fakeFigFontChar(codePoint: Int, width: Int, height: Int): String {
    val char = codePoint.toChar()

    return StringBuilder().apply {
        repeat(height) {
            repeat(width) {
                append(char)
            }

            append('@')
            append(System.lineSeparator())
        }
    }.toString()
}
