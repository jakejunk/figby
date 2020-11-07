package layout

import java.util.*

class HorizontalSmushing(rules: List<Rule>) {
    private val ruleSet = when {
        rules.isEmpty() -> EnumSet.noneOf(Rule::class.java)
        else -> EnumSet.copyOf(rules)
    }

    fun trySmush(left: Int, right: Int, hardblank: Int): Int? {
        return ruleSet
            .asSequence()
            .map { rule -> rule.apply(left, right, hardblank) }
            .firstOrNull { it != null }
    }

    enum class Rule(val bitMask: Int) {
        EqualCharacter(1) {
            override fun apply(left: Int, right: Int, hardblank: Int): Int? {
                return when {
                    left == right && left != hardblank -> left
                    else -> null
                }
            }
        },
        Underscore(2) {
            private val underscoreCodePoint = '_'.toInt()
            private val underscoreReplacers = listOf(
                '|', '/', '\\', '[', ']', '{', '}', '(', ')', '<', '>'
            ).map { it.toInt() }

            override fun apply(left: Int, right: Int, hardblank: Int): Int? {
                return when {
                    left == underscoreCodePoint && right in underscoreReplacers -> right
                    right == underscoreCodePoint && left in underscoreReplacers -> left
                    else -> null
                }
            }
        },
        Hierarchy(4) {
            private val charClassMap = mapOf(
                '|' to 1,
                '/' to 2, '\\' to 2,
                '[' to 3, ']' to 3,
                '{' to 4, '}' to 4,
                '(' to 5, ')' to 5,
                '<' to 6, '>' to 6,
            ).mapKeys { it.key.toInt() }

            override fun apply(left: Int, right: Int, hardblank: Int): Int? {
                val leftClass = charClassMap.getOrElse(left) { 0 }
                val rightClass = charClassMap.getOrElse(right) { 0 }
                return when {
                    leftClass > rightClass -> left
                    leftClass < rightClass -> right
                    else -> null
                }
            }
        },
        OppositePair(8) {
            private val pairs = mapOf(
                '[' to ']',
                ']' to '[',
                '{' to '}',
                '}' to '{',
                '(' to ')',
                ')' to '(',
            ).mapKeys { it.key.toInt() }.mapValues { it.value.toInt() }

            override fun apply(left: Int, right: Int, hardblank: Int): Int? {
                return when (pairs[left]) {
                    right -> '|'.toInt()
                    else -> null
                }
            }
        },
        BigX(16) {
            private val xPairs = mapOf(
                Pair('/', '\\') to '|',
                Pair('\\', '/') to 'Y',
                Pair('>', '<') to 'X'
            ).mapKeys {
                val (left, right) = it.key
                left.toInt() to right.toInt()
            }.mapValues { it.value.toInt() }

            override fun apply(left: Int, right: Int, hardblank: Int): Int? {
                return xPairs[Pair(left, right)]
            }
        },
        Hardblank(32) {
            override fun apply(left: Int, right: Int, hardblank: Int): Int? {
                return when {
                    left == hardblank && right == hardblank -> hardblank
                    else -> null
                }
            }
        };

        abstract fun apply(left: Int, right: Int, hardblank: Int): Int?
    }
}

fun parseHorizontalSmushing(layoutMask: Int): HorizontalSmushing {
    val rules = HorizontalSmushing.Rule.values()
        .filter { layoutMask and it.bitMask == it.bitMask }

    return HorizontalSmushing(rules)
}
