package dev.junker.figby

import dev.junker.figby.figure.FigureBuilder
import dev.junker.figby.font.FigFont

/**
 * TODO
 */
public class FigDriver(
    font: FigFont
) {
    private val builder = FigureBuilder(font)

    public fun convert(text: String): String {
        return builder.buildFigure(text)
    }
}