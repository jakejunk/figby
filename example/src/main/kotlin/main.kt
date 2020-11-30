package com.example

import dev.junker.figby.FigDriver
import dev.junker.figby.font.FigFont

fun main() {
    val fontFile = ClassLoader.getSystemClassLoader().getResource("standard.flf")?.openStream()!!
    val font = FigFont.fromFile(fontFile)
    val driver = FigDriver(font)

    println("=========== Font info ============")
    println("Hardblank: '${font.hardblank.toChar()}'")
    println("Height: ${font.height} characters")
    println("Baseline: ${font.baseline} characters")
    println("Max width: ${font.maxLength} characters")
    println("Print direction: ${font.printDirection}")
    println("Horizontal layout: ${font.horizontalLayout}")
    println("Vertical layout: ${font.verticalLayout}")
    println("Horizontal smushing rules: ${font.horizontalSmushingRules}")
    println("Vertical smushing rules: ${font.verticalSmushingRules}")
    println("Comments:\n${font.comments}")
    println("==================================")

    println("===== Font character samples =====")
    println("'A':\n${font['A'.toInt()]}")
    println("'9':\n${font['9'.toInt()]}")
    println("' ' (note the hardblanks):\n${font[' '.toInt()]}")
    println("'ß':\n${font[0x00df]}")
    println("==================================")

    println("======== Text conversion =========")
    println("\"Hello, Figby\":\n${driver.convert("Hello, Figby")}")
    println("==================================")
}
