package layout

class VerticalSmusher(vararg rules: Rule) {
    private val ruleSet = rules.asSequence()

    fun trySmush(top: Int, bottom: Int, hardblank: Int): Int? {
        return ruleSet
            .map { rule -> rule.apply(top, bottom, hardblank) }
            .firstOrNull { it != null }
    }

    enum class Rule(val bitMask: Int) {
        EqualCharacter(256) {
            override fun apply(top: Int, bottom: Int, hardblank: Int): Int? {
                return when {
                    top == bottom && top != hardblank -> top
                    else -> null
                }
            }
        },
        Underscore(512) {
            private val underscore = '_'.toInt()
            private val underscoreReplacers = listOf(
                '|', '/', '\\', '[', ']', '{', '}', '(', ')', '<', '>'
            ).map(Char::toInt)

            override fun apply(top: Int, bottom: Int, hardblank: Int): Int? {
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

            override fun apply(top: Int, bottom: Int, hardblank: Int): Int? {
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
            override fun apply(top: Int, bottom: Int, hardblank: Int): Int? {
                TODO("Not yet implemented")
            }
        },
        VerticalLine(4096) {
            override fun apply(top: Int, bottom: Int, hardblank: Int): Int? {
                TODO("Not yet implemented")
            }
        };

        abstract fun apply(top: Int, bottom: Int, hardblank: Int): Int?
    }
}

fun parseVerticalSmushing(layoutMask: Int): VerticalSmusher {
    val rules = VerticalSmusher.Rule.values()
        .filter { layoutMask and it.bitMask == it.bitMask }

    return VerticalSmusher(*rules.toTypedArray())
}
