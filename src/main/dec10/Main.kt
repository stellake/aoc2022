package dec10

import common.*

const val DAY = 10

private data class Execution(
    val length: Int,
    val xToIncrease: Int?
)

fun main() {
    val input = getData()
    // 15680
    solvePart1(input)
    // ZFBFHGUP
    solvePart2(input)
}

// TODO: Refactor/commonise
private fun solvePart1(input: List<Execution>) {
    var currentCycle = 1
    var x = 1
    val signalStrengths = mutableListOf<Int>()

    input.forEach { execution ->
        repeat(execution.length) {
            if (currentCycle == 20) {
                val element = currentCycle * x
                signalStrengths.add(element)
            }
            if (currentCycle > 20) {
                val shouldRecordSS = (currentCycle - 20) % 40 == 0
                if (shouldRecordSS) {
                    signalStrengths.add(currentCycle * x)
                }
            }
            currentCycle += 1
        }
        x += execution.xToIncrease ?: 0
    }
    val ssSum = signalStrengths.sum()
    println("Part 1 answer $ssSum")
}

private fun solvePart2(input: List<Execution>) {
    var currentCycle = 1
    var x = 1
    val drawing = Array(6) { Array(40) { '.' } }
    input.forEach { execution ->
        repeat(execution.length) {
            val (row, col) = (currentCycle - 1) / 40 to (currentCycle - 1) % 40
            if (x in col - 1..col + 1) drawing[row][col] = '#'
            currentCycle += 1
        }
        x += execution.xToIncrease ?: 0
    }
    println("Part 2 answer:")
    drawing.forEach { line -> println(line.joinToString("")) }
}

private fun getData(): List<Execution> {
    return readLines(DAY).map { line ->
        if (line == "noop") {
            Execution(1, null)
        } else {
            val xToIncrease = line.split(" ")[1].toInt()
            Execution(2, xToIncrease)
        }
    }
}
