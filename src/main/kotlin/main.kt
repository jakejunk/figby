import figure.FigureBuilder
import font.parseFigFont

internal fun main(args: Array<String>) {
    val fontFile = ClassLoader
        .getSystemClassLoader()
        .getResource("standard.flf")
        ?.openStream()!!

    val font = parseFigFont(fontFile)
    val builder = FigureBuilder(font)

    builder.append("Hello world")

    print(builder.buildFigure())
}