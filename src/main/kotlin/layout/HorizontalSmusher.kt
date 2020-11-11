package layout

class HorizontalSmusher(
    private vararg val rules: Rule
) {
    fun trySmush(left: Int, right: Int, hardblank: Int): Int? {
        return when {
            rules.isNotEmpty() -> applyControlledSmushing(left, right, hardblank)
            else -> applyUniversalSmushing(left, right, hardblank)
        }
    }

    private fun applyControlledSmushing(left: Int, right: Int, hardblank: Int): Int? {
        return rules
            .asSequence()
            .map { rule -> rule.apply(left, right, hardblank) }
            .firstOrNull { it != null }
    }

    private fun applyUniversalSmushing(left: Int, right: Int, hardblank: Int): Int {
        return when {
            Character.isWhitespace(left) -> right
            Character.isWhitespace(right) -> left
            right == hardblank -> left
            else -> right
        }
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
            private val underscore = '_'.toInt()
            private val underscoreReplacers = listOf(
                '|', '/', '\\', '[', ']', '{', '}', '(', ')', '<', '>'
            ).map(Char::toInt)

            override fun apply(left: Int, right: Int, hardblank: Int): Int? {
                return when {
                    left == underscore && right in underscoreReplacers -> right
                    right == underscore && left in underscoreReplacers -> left
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
                val leftClass = charClassMap[left] ?: return null
                val rightClass = charClassMap[right] ?: return null
                return when {
                    leftClass > rightClass -> left
                    leftClass < rightClass -> right
                    else -> null
                }
            }
        },
        OppositePair(8) {
            private val verticalBar = '|'.toInt()
            private val pairs = mapOf(
                '[' to ']',
                ']' to '[',
                '{' to '}',
                '}' to '{',
                '(' to ')',
                ')' to '(',
            ).entries.associate { (key, value) ->
                key.toInt() to value.toInt()
            }

            override fun apply(left: Int, right: Int, hardblank: Int): Int? {
                return when (pairs[left]) {
                    right -> verticalBar
                    else -> null
                }
            }
        },
        BigX(16) {
            private val xPairs = mapOf(
                Pair('/', '\\') to '|',
                Pair('\\', '/') to 'Y',
                Pair('>', '<') to 'X'
            ).entries.associate { (key, value) ->
                val (left, right) = key
                Pair(left.toInt(), right.toInt()) to value.toInt()
            }

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

fun parseHorizontalSmushing(layoutMask: Int): HorizontalSmusher {
    val rules = HorizontalSmusher.Rule.values()
        .filter { layoutMask and it.bitMask == it.bitMask }

    return HorizontalSmusher(*rules.toTypedArray())
}
