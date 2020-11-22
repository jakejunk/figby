import figure.FigureBuilder
import font.FigFont
import font.parse.parseFigFont

internal fun main(args: Array<String>) {
    val fontFile = ClassLoader
        .getSystemClassLoader()
        .getResource("standard.flf")
        ?.openStream()!!

    val font = FigFont.fromFile(fontFile)
    val builder = FigureBuilder(font)

    builder.append("Hello world")

    print(builder.buildFigure())
}