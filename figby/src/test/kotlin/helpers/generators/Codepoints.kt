package helpers.generators

import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.exhaustive.exhaustive

private val underscoreReplacerList = listOf(
    '|', '/', '\\', '[', ']', '{', '}', '(', ')', '<', '>'
).map { it.toInt() }
val underscoreReplacers = underscoreReplacerList.exhaustive()
val notUnderscoreReplacers = Arb.codepoints()
    .filterNot { it.value in underscoreReplacerList }
    .map { it.value }

private val charClassMap = listOf(
    '|' to 1,
    '/' to 2, '\\' to 2,
    '[' to 3, ']' to 3,
    '{' to 4, '}' to 4,
    '(' to 5, ')' to 5,
    '<' to 6, '>' to 6,
).map { (key, value) -> key.toInt() to value }
val charClassMembers = charClassMap.exhaustive()
val notCharClassMembers = Arb.codepoints()
    .filterNot { it.value in charClassMap.map { (key, _) -> key } }
    .map { it.value }

private val oppositePairList = listOf(
    '[' to ']',
    ']' to '[',
    '{' to '}',
    '}' to '{',
    '(' to ')',
    ')' to '(',
).map { (key, value) -> key.toInt() to value.toInt() }
val oppositePairs = oppositePairList.exhaustive()
val notOppositePairs = arbitrary { source ->
    val left = Arb.codepoints().sample(source)
    val right = Arb.codepoints().sample(source)

    left.value.value to right.value.value // Ew
}.filterNot { it in oppositePairList }
