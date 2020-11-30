package helpers.generators

import io.kotest.property.Arb
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.int

fun fullLayoutsWithPattern(enabledMask: Short, disabledMask: Short = 0): Arb<Int> {
    if (enabledMask.toInt() and disabledMask.toInt() != 0) {
        throw Exception("$enabledMask and $disabledMask contain overlapping masking bits")
    }

    val expectedMask = enabledMask.toInt()
    val checkMask = expectedMask or disabledMask.toInt()

    return Arb.int(1, 32767).filter { it and checkMask == expectedMask }
}

fun oldLayoutsWithPattern(enabledMask: Byte): Arb<Int> {
    val checkMask = enabledMask.toInt()

    return Arb.int(1, 63).filter { it and checkMask == checkMask }
}
