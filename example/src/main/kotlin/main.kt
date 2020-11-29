package com.example

import dev.junker.figby.FigDriver
import dev.junker.figby.font.FigFont

fun main() {
    val fontFile = ClassLoader.getSystemClassLoader().getResource("standard.flf")?.openStream()!!
    val font = FigFont.fromFile(fontFile)
    val driver = FigDriver(font)

    print(driver.convert("Figby Test 123"))
}
