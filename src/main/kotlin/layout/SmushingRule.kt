package layout

/**
 * Defines how to horizontally smush [font.FigChar] sub-characters.
 */
public enum class HorizontalSmushingRule {
    /**
     * Combines two sub-characters into one if they are equal. Does not smush hardblanks.
     */
    EqualCharacter {
        override fun apply(left: Int, right: Int, hardblank: Int): Int? = when {
            left == right && left != hardblank -> left
            else -> null
        }
    },

    /**
     * Underscore sub-characters will be replaced by any of the following:
     * `|`, `/`, `\`, `[`, `]`, `{`, `}`, `(`, `)`, `<` or `>`.
     */
    Underscore {
        override fun apply(left: Int, right: Int, hardblank: Int): Int? = when {
            left == UNDERSCORE && right in UNDERSCORE_REPLACERS -> right
            right == UNDERSCORE && left in UNDERSCORE_REPLACERS -> left
            else -> null
        }
    },

    /**
     * A hierarchy of six classes is used: `|`, `/\`, `[]`, `{}`, `()`, and `<>`.
     * When two sub-characters are from different classes, the one from the latter class will be used.
     */
    Hierarchy {
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

    /**
     * Returns a vertical bar (`|`) if the given inputs are
     * opposing brackets (`[]` or `][`), braces (`{}` or `}{`) or parentheses (`()` or `)(`).
     */
    OppositePair {
        override fun apply(left: Int, right: Int, hardblank: Int): Int? = when (PAIRS[left]) {
            right -> VERTICAL_BAR
            else -> null
        }
    },

    /**
     * Combines sub-characters as follows:
     * - `/ + \ = |`
     * - `\ + / = Y`
     * - `> + < = X`
     */
    BigX {
        override fun apply(left: Int, right: Int, hardblank: Int): Int? {
            return X_PAIRS[Pair(left, right)]
        }
    },

    /**
     * Combines two hardblanks into one.
     */
    Hardblank {
        override fun apply(left: Int, right: Int, hardblank: Int): Int? = when {
            left == hardblank && right == hardblank -> hardblank
            else -> null
        }
    };

    public abstract fun apply(left: Int, right: Int, hardblank: Int): Int?
}

/**
 * Defines how to vertically smush [font.FigChar] sub-characters.
 */
public enum class VerticalSmushingRule {
    /**
     * Combines two sub-characters into one if they are equal.
     */
    EqualCharacter {
        override fun apply(top: Int, bottom: Int): Int? = if (top == bottom) top else null
    },

    /**
     * Underscore sub-characters will be replaced by any of the following:
     * `|`, `/`, `\`, `[`, `]`, `{`, `}`, `(`, `)`, `<` or `>`.
     */
    Underscore {
        override fun apply(top: Int, bottom: Int): Int? = when {
            top == UNDERSCORE && bottom in UNDERSCORE_REPLACERS -> bottom
            bottom == UNDERSCORE && top in UNDERSCORE_REPLACERS -> top
            else -> null
        }
    },

    /**
     * A hierarchy of six classes is used: `|`, `/\`, `[]`, `{}`, `()`, and `<>`.
     * When two sub-characters are from different classes, the one from the latter class will be used.
     */
    Hierarchy {
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

    /**
     * Combines pairs of `-` and `_` into a single `=`.
     * Pairs of identical inputs (e.g. two `-` sub-characters) will not be smushed by this rule.
     */
    HorizontalLine {
        override fun apply(top: Int, bottom: Int): Int? = when {
            top == HYPHEN && bottom == UNDERSCORE -> EQUAL_SIGN
            top == UNDERSCORE && bottom == HYPHEN -> EQUAL_SIGN
            else -> null
        }
    },

    /**
     * Combines multiple `|` sub-characters into one.
     * Continues until any sub-characters other than `|` would have to be smushed.
     */
    VerticalLine {
        override fun apply(top: Int, bottom: Int): Int? = when {
            top == VERTICAL_BAR && bottom == VERTICAL_BAR -> VERTICAL_BAR
            else -> null
        }
    };

    public abstract fun apply(top: Int, bottom: Int): Int?
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
