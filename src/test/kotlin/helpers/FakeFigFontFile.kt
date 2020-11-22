package helpers

import java.io.InputStream

fun fakeFigFontFileWithLayout(
    oldLayout: Int = -1,
    fullLayout: Int? = null
): InputStream {
    return fakeFigFontFile("flf2a\$ 1 1 2 $oldLayout 1 0 ${fullLayout ?: ""}", "A comment", 1, 1)
}

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

private fun fakeFigFontChar(codePoint: Int, width: Int, height: Int): String {
    return StringBuilder().apply {
        repeat(height) {
            repeat(width) {
                appendCodePoint(codePoint)
            }

            append('@')
            append(System.lineSeparator())
        }
    }.toString()
}
