package dev.junker.figby

import dev.junker.figby.figure.FigureBuilder
import dev.junker.figby.font.FigFont

/**
 * TODO
 */
public class FigDriver(
    private val font: FigFont
) {
    public fun convert(text: String): String {
        // TODO: This would probably be better as a factory
        val builder = FigureBuilder(font)

        builder.append(text)

        return builder.buildFigure()
    }
}