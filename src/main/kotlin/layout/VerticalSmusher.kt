package layout

class VerticalSmusher(
    private vararg val rules: Rule
) {
    fun trySmush(top: Int, bottom: Int, hardblank: Int): Int? {
        return when {
            rules.isNotEmpty() -> applyControlledSmushing(top, bottom)
            else -> applyUniversalSmushing(top, bottom, hardblank)
        }
    }

    private fun applyControlledSmushing(top: Int, bottom: Int): Int? {
        return rules
            .asSequence()
            .mapNotNull { rule -> rule.apply(top, bottom) }
            .firstOrNull()
    }

    private fun applyUniversalSmushing(top: Int, bottom: Int, hardblank: Int): Int {
        return when {
            Character.isWhitespace(top) -> bottom
            Character.isWhitespace(bottom) -> top
            bottom == hardblank -> top
            else -> bottom
        }
    }

    enum class Rule(val bitMask: Int) {
        EqualCharacter(256) {
            override fun apply(top: Int, bottom: Int): Int? {
                return if (top == bottom) top else null
            }
        },
        Underscore(512) {
            private val underscore = '_'.toInt()
            private val underscoreReplacers = listOf(
                '|', '/', '\\', '[', ']', '{', '}', '(', ')', '<', '>'
            ).map(Char::toInt)

            override fun apply(top: Int, bottom: Int): Int? {
                return when {
                    top == underscore && bottom in underscoreReplacers -> bottom
                    bottom == underscore && top in underscoreReplacers -> top
                    else -> null
                }
            }
        },
        Hierarchy(1024) {
            private val charClassMap = mapOf(
                '|' to 1,
                '/' to 2, '\\' to 2,
                '[' to 3, ']' to 3,
                '{' to 4, '}' to 4,
                '(' to 5, ')' to 5,
                '<' to 6, '>' to 6,
            ).mapKeys { it.key.toInt() }

            override fun apply(top: Int, bottom: Int): Int? {
                val topClass = charClassMap[top] ?: return null
                val bottomClass = charClassMap[bottom] ?: return null
                return when {
                    topClass > bottomClass -> top
                    topClass < bottomClass -> bottom
                    else -> null
                }
            }
        },
        HorizontalLine(2048) {
            private val hyphen = '-'.toInt()
            private val underscore = '_'.toInt()
            private val equalSign = '='.toInt()

            override fun apply(top: Int, bottom: Int): Int? {
                return when {
                    top == hyphen && bottom == underscore -> equalSign
                    top == underscore && bottom == hyphen -> equalSign
                    else -> null
                }
            }
        },
        VerticalLine(4096) {
            private val verticalBar = '|'.toInt()

            override fun apply(top: Int, bottom: Int): Int? {
                return when {
                    top == verticalBar && bottom == verticalBar -> verticalBar
                    else -> null
                }
            }
        };

        abstract fun apply(top: Int, bottom: Int): Int?
    }
}

fun parseVerticalSmushing(layoutMask: Int): VerticalSmusher {
    val rules = VerticalSmusher.Rule.values()
        .filter { layoutMask and it.bitMask == it.bitMask }

    return VerticalSmusher(*rules.toTypedArray())
}
