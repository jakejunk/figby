package font

enum class HorizontalSmushingRule(val bitMask: Int) {
    EqualCharacter(1) {
        override fun apply(left: Int, right: Int, hardblank: Int): Int? = when {
            left == right && left != hardblank -> left
            else -> null
        }
    },
    Underscore(2) {
        override fun apply(left: Int, right: Int, hardblank: Int): Int? = when {
            left == UNDERSCORE && right in UNDERSCORE_REPLACERS -> right
            right == UNDERSCORE && left in UNDERSCORE_REPLACERS -> left
            else -> null
        }
    },
    Hierarchy(4) {
        override fun apply(left: Int, right: Int, hardblank: Int): Int? {
            val leftClass = CHAR_CLASS_MAP[left] ?: return null
            val rightClass = CHAR_CLASS_MAP[right] ?: return null
            return when {
                leftClass > rightClass -> left
                leftClass < rightClass -> right
                else -> null
            }
        }
    },
    OppositePair(8) {
        override fun apply(left: Int, right: Int, hardblank: Int): Int? = when (PAIRS[left]) {
            right -> VERTICAL_BAR
            else -> null
        }
    },
    BigX(16) {
        override fun apply(left: Int, right: Int, hardblank: Int): Int? {
            return X_PAIRS[Pair(left, right)]
        }
    },
    Hardblank(32) {
        override fun apply(left: Int, right: Int, hardblank: Int): Int? = when {
            left == hardblank && right == hardblank -> hardblank
            else -> null
        }
    };

    abstract fun apply(left: Int, right: Int, hardblank: Int): Int?
}

enum class VerticalSmushingRule(val bitMask: Int) {
    EqualCharacter(256) {
        override fun apply(top: Int, bottom: Int): Int? = if (top == bottom) top else null
    },
    Underscore(512) {
        override fun apply(top: Int, bottom: Int): Int? = when {
            top == UNDERSCORE && bottom in UNDERSCORE_REPLACERS -> bottom
            bottom == UNDERSCORE && top in UNDERSCORE_REPLACERS -> top
            else -> null
        }
    },
    Hierarchy(1024) {
        override fun apply(top: Int, bottom: Int): Int? {
            val topClass = CHAR_CLASS_MAP[top] ?: return null
            val bottomClass = CHAR_CLASS_MAP[bottom] ?: return null
            return when {
                topClass > bottomClass -> top
                topClass < bottomClass -> bottom
                else -> null
            }
        }
    },
    HorizontalLine(2048) {
        override fun apply(top: Int, bottom: Int): Int? = when {
            top == HYPHEN && bottom == UNDERSCORE -> EQUAL_SIGN
            top == UNDERSCORE && bottom == HYPHEN -> EQUAL_SIGN
            else -> null
        }
    },
    VerticalLine(4096) {
        override fun apply(top: Int, bottom: Int): Int? = when {
            top == VERTICAL_BAR && bottom == VERTICAL_BAR -> VERTICAL_BAR
            else -> null
        }
    };

    abstract fun apply(top: Int, bottom: Int): Int?
}

private const val HYPHEN = '-'.toInt()
private const val EQUAL_SIGN = '='.toInt()
private const val VERTICAL_BAR = '|'.toInt()
private const val UNDERSCORE = '_'.toInt()

private val UNDERSCORE_REPLACERS = listOf(
    '|', '/', '\\', '[', ']', '{', '}', '(', ')', '<', '>'
).map(Char::toInt)

private val CHAR_CLASS_MAP = mapOf(
    '|' to 1,
    '/' to 2, '\\' to 2,
    '[' to 3, ']' to 3,
    '{' to 4, '}' to 4,
    '(' to 5, ')' to 5,
    '<' to 6, '>' to 6,
).mapKeys { it.key.toInt() }

private val PAIRS = mapOf(
    '[' to ']',
    ']' to '[',
    '{' to '}',
    '}' to '{',
    '(' to ')',
    ')' to '(',
).entries.associate { (key, value) ->
    key.toInt() to value.toInt()
}

private val X_PAIRS = mapOf(
    Pair('/', '\\') to '|',
    Pair('\\', '/') to 'Y',
    Pair('>', '<') to 'X'
).entries.associate { (key, value) ->
    val (left, right) = key
    Pair(left.toInt(), right.toInt()) to value.toInt()
}
