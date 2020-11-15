package helpers

fun <T, U, V> cartesianProduct(
    leftList: List<T>,
    rightList: List<U>,
    operation: (left: T, right: U) -> V?
): Sequence<V> = sequence {
    leftList.forEach { left ->
        rightList.forEach { right ->
            operation(left, right)?.apply {
                yield(this)
            }
        }
    }
}