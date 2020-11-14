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

    abstract fun apply(left: Int, right: Int, hardblank: Int): Int?
}

enum class VerticalSmushingRule(val bitMask: Int) {
    EqualCharacter(256) {
        override fun apply(top: Int, bottom: Int): Int? = if (top == bottom) top else null
    },
    Underscore(512) {
        override fun apply(top: Int, bottom: Int): Int? = when {
            top == underscore && bottom in underscoreReplacers -> bottom
            bottom == underscore && top in underscoreReplacers -> top
            else -> null
        }
    },
    Hierarchy(1024) {
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
        override fun apply(top: Int, bottom: Int): Int? = when {
            top == hyphen && bottom == underscore -> equalSign
            top == underscore && bottom == hyphen -> equalSign
            else -> null
        }
    },
    VerticalLine(4096) {
        override fun apply(top: Int, bottom: Int): Int? = when {
            top == verticalBar && bottom == verticalBar -> verticalBar
            else -> null
        }
    };

    abstract fun apply(top: Int, bottom: Int): Int?
}


private const val hyphen = '-'.toInt()
private const val equalSign = '='.toInt()
private const val verticalBar = '|'.toInt()
private const val underscore = '_'.toInt()

private val underscoreReplacers = listOf(
    '|', '/', '\\', '[', ']', '{', '}', '(', ')', '<', '>'
).map(Char::toInt)

private val charClassMap = mapOf(
    '|' to 1,
    '/' to 2, '\\' to 2,
    '[' to 3, ']' to 3,
    '{' to 4, '}' to 4,
    '(' to 5, ')' to 5,
    '<' to 6, '>' to 6,
).mapKeys { it.key.toInt() }

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

private val xPairs = mapOf(
    Pair('/', '\\') to '|',
    Pair('\\', '/') to 'Y',
    Pair('>', '<') to 'X'
).entries.associate { (key, value) ->
    val (left, right) = key
    Pair(left.toInt(), right.toInt()) to value.toInt()
}
