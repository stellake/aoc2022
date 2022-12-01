package dec1

import common.readLineGroups

// 69206
fun main() {
    val data = getDec1Data()
    println("Max num of cals: ${data.max()}")
}

fun getDec1Data(): List<Int> {
    return readLineGroups(1).map { lineGroup ->
        lineGroup.split(System.lineSeparator()).sumOf { it.toInt() }
    }
}
