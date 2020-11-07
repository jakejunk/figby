package util

fun Boolean.toInt(): Int {
    return when {
        this -> 1
        else -> 0
    }
}