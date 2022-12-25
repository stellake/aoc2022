package dec25

import common.readDraftLines
import common.readLines
import kotlin.math.pow

const val DAY = 25

fun main() {
    val draft = readDraftLines(DAY)
    val input = readLines(DAY)

    solvePart1(draft) // 2=-1=0
    solvePart1(input) // 2-0-0=1-0=2====20=-2
}

private fun solvePart1(input: List<String>) {
    val data = getData(input)
    val sum = data.sum()
    val snafu = getSnafuNumber(sum)
    println("Part 1 answer: $snafu")
}

private fun getSnafuNumber(number: Long): String {
    val originalBase5 = number.toString(radix = 5).toCharArray().toList()
    val result = mutableListOf<Char>()
    val addOneToIndexes = mutableSetOf<Int>()
    originalBase5.reversed().forEachIndexed { index, c ->
        val char = if (addOneToIndexes.contains(index)) {
            val new = c.digitToInt() + 1
            if (new < 5) {
                new.toString()[0]
            } else {
                addOneToIndexes.add(index + 1)
                '0'
            }
        } else c
        when(char) {
            '0', '1', '2' -> result.add(char)
            '3' -> {
                addOneToIndexes.add(index + 1)
                result.add('=')
            }
            '4' -> {
                addOneToIndexes.add(index + 1)
                result.add('-')
            }
            else -> throw Error("Unexpected character to get snafu")
        }
    }
    if (addOneToIndexes.contains(originalBase5.size)) {
        result.add('1')
    }
    return result.reversed().joinToString("")
}

private fun getData(input: List<String>): List<Long> {
    return input.map { it.toBase10() }
}

private fun String.toBase10(): Long {
    val chars = this.toCharArray().toList()
    var total = 0.0
    chars.reversed().forEachIndexed { index, char ->
        val multiplier = 5.0.pow(index.toDouble())
        if (char.isDigit()) {
            total += char.digitToInt() * multiplier
        } else if (char == '-') {
            total -= multiplier
        } else if (char == '=') {
            total -= 2 * multiplier
        } else throw Error("Unknown symbol")
    }
    return total.toLong()
}
