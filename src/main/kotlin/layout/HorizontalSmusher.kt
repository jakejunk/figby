package layout

class HorizontalSmusher(
    private vararg val rules: Rule
) {
    fun trySmush(left: Int, right: Int, hardblank: Int): Int? = when {
        rules.isNotEmpty() -> applyControlledSmushing(left, right, hardblank)
        else -> applyUniversalSmushing(left, right, hardblank)
    }

    private fun applyControlledSmushing(left: Int, right: Int, hardblank: Int): Int? = rules
        .asSequence()
        .mapNotNull { rule -> rule.apply(left, right, hardblank) }
        .firstOrNull()

    private fun applyUniversalSmushing(left: Int, right: Int, hardblank: Int): Int = when {
        Character.isWhitespace(left) -> right
        Character.isWhitespace(right) -> left
        right == hardblank -> left
        else -> right
    }

    enum class Rule(val bitMask: Int) {
        EqualCharacter(1) {
            override fun apply(left: Int, right: Int, hardblank: Int): Int? = when {
                left == right && left != hardblank -> left
                else -> null
            }
        },
        Underscore(2) {
            override fun apply(left: Int, right: Int, hardblank: Int): Int? = when {
                left == underscore && right in underscoreReplacers -> right
                right == underscore && left in underscoreReplacers -> left
                else -> null
            }
        },
        Hierarchy(4) {
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
            override fun apply(left: Int, right: Int, hardblank: Int): Int? = when (pairs[left]) {
                right -> verticalBar
                else -> null
            }
        },
        BigX(16) {
            override fun apply(left: Int, right: Int, hardblank: Int): Int? {
                return xPairs[Pair(left, right)]
            }
        },
        Hardblank(32) {
            override fun apply(left: Int, right: Int, hardblank: Int): Int? = when {
                left == hardblank && right == hardblank -> hardblank
                else -> null
            }
        };

        private companion object {
            const val verticalBar = '|'.toInt()
            const val underscore = '_'.toInt()

            val underscoreReplacers = listOf(
                '|', '/', '\\', '[', ']', '{', '}', '(', ')', '<', '>'
            ).map(Char::toInt)

            val charClassMap = mapOf(
                '|' to 1,
                '/' to 2, '\\' to 2,
                '[' to 3, ']' to 3,
                '{' to 4, '}' to 4,
                '(' to 5, ')' to 5,
                '<' to 6, '>' to 6,
            ).mapKeys { it.key.toInt() }

            val pairs = mapOf(
                '[' to ']',
                ']' to '[',
                '{' to '}',
                '}' to '{',
                '(' to ')',
                ')' to '(',
            ).entries.associate { (key, value) ->
                key.toInt() to value.toInt()
            }

            val xPairs = mapOf(
                Pair('/', '\\') to '|',
                Pair('\\', '/') to 'Y',
                Pair('>', '<') to 'X'
            ).entries.associate { (key, value) ->
                val (left, right) = key
                Pair(left.toInt(), right.toInt()) to value.toInt()
            }
        }

        abstract fun apply(left: Int, right: Int, hardblank: Int): Int?
    }
}

fun parseHorizontalSmushing(layoutMask: Int): HorizontalSmusher {
    val rules = HorizontalSmusher.Rule.values()
        .filter { layoutMask and it.bitMask == it.bitMask }

    return HorizontalSmusher(*rules.toTypedArray())
}
