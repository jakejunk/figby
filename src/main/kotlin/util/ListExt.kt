package util

public fun <T> List<T>.leading(element: T): Int {
    var leading = 0
    val iterator = this.listIterator(0)
    while (iterator.hasNext()) {
        if (iterator.next() != element) {
            break
        }
        ++leading
    }
    return leading
}

public fun <T> List<T>.trailing(element: T): Int {
    var trailing = 0
    val iterator = this.listIterator(size)
    while (iterator.hasPrevious()) {
        if (iterator.previous() != element) {
            break
        }
        ++trailing
    }
    return trailing
}
