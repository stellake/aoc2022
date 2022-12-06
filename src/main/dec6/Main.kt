package dec6

import common.readLines

fun main() {
    // 1640
    solvePart1()

    // 3613
    solvePart2()
}

fun solvePart1() {
    val subroutine = findElementNumberOfTheFirstUniqueWindow(4)
    println("Part 1 answer is $subroutine")
}

fun solvePart2() {
    val message = findElementNumberOfTheFirstUniqueWindow(14)
    println("Part 2 answer is $message")
}

private fun findElementNumberOfTheFirstUniqueWindow(windowSize: Int): Int {
    getData().windowed(windowSize).forEachIndexed { index, chars ->
        if (chars.toSet().size == windowSize) {
            return index + windowSize
        }
    }
    throw Error("No unique match found")
}

private fun getData(): List<Char> {
    val data = readLines(6)
    return data[0].toList()
}
