package com.example

import dev.junker.figby.font.FigFont

fun main() {
    val fontFile = ClassLoader
        .getSystemClassLoader()
        .getResource("standard.flf")
        ?.openStream()!!

    val font = FigFont.fromFile(fontFile)
//    val builder = FigureBuilder(font)
//
//    builder.append("Figby")
//
//    print(builder.buildFigure())

    print("Working!")
}