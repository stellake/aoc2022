package dec13

import com.google.gson.GsonBuilder
import common.readLineGroups


const val DAY = 13

private enum class CASES {
    LEFT_LIST_SHORTER,
    LEFT_LIST_LONGER,
    LEFT_VALUE_HIGHER,
    LEFT_VALUE_LOWER
}

private fun getIsValid(case: CASES): Boolean {
    return when(case) {
        CASES.LEFT_VALUE_HIGHER -> false
        CASES.LEFT_LIST_SHORTER -> true
        CASES.LEFT_LIST_LONGER -> false
        CASES.LEFT_VALUE_LOWER -> true
    }
}

fun main() {
    val input = readLineGroups(DAY)

    // 4734
    solvePart1(input)

    // 21836
    solvePart2(input)
}

private fun solvePart1(input: List<String>) {
    val data = getData(input)
    val valid = data
        .mapIndexed { index, packets -> Pair(index + 1, isPacketValid(packets)) }
        .filter { it.second }
    val answer = valid.sumOf { it.first }
    println("Part 1 answer: $answer")
}

private fun solvePart2(input: List<String>) {
    val firstExtraElement = listOf(listOf(2.0))
    val secondExtraElement = listOf(listOf(6.0))
    val packagesToAdd = listOf(firstExtraElement, secondExtraElement)

    val orderedPackets = getData(input)
        .map { listOf(it.first, it.second) }
        .flatten()
        .plus(packagesToAdd)
        .sortedWith(compareTwoPackets)

    val decodeKey = (orderedPackets.indexOf(firstExtraElement) + 1) * (orderedPackets.indexOf(secondExtraElement) + 1)
    println("Part 2 answer: $decodeKey")
}

private val compareTwoPackets =  Comparator<Any> { a, b ->
    when (compareValues(a, b)) {
        null -> 0
        false -> 1
        else -> -1
    }
}

private fun isPacketValid(packets: Packets): Boolean {
    return compareValues(packets.first, packets.second) ?: false
}

private fun compareValues(item1: Any, item2: Any): Boolean? {
    if (item1 is Double && item2 is Double) {
        return compareDigits(item1.toInt(), item2.toInt())
    }
    val list1: List<*> = if (item1 is Double) listOf(item1) else item1 as List<*>
    val list2: List<*> = if (item2 is Double) listOf(item2) else item2 as List<*>

    for (i in list1.indices) {
        val i1 = list1[i]!!
        val i2 = (if (list2.size > i) list2[i] else null) ?: return getIsValid(CASES.LEFT_LIST_LONGER)
        val res = compareValues(i1, i2)
        if (res != null) return res
    }

    // Gone through the first list comparison at this point
    if (list2.size > list1.size) {
        return getIsValid(CASES.LEFT_LIST_SHORTER)
    }

    return null
}

private fun compareDigits(value1: Int, value2: Int): Boolean? {
    return if (value1 < value2) {
        getIsValid(CASES.LEFT_VALUE_LOWER)
    } else if (value1 > value2) {
        getIsValid(CASES.LEFT_VALUE_HIGHER)
    } else null
}

private data class Packets(val first: Any, val second: Any)

private fun getData(input: List<String>): List<Packets> {
    return input.map { linePair ->
        val lines = linePair.split(System.lineSeparator())
        val builder = GsonBuilder()
        val first = builder.create().fromJson(lines[0], Any::class.java) as ArrayList<*>
        val second = builder.create().fromJson(lines[1], Any::class.java) as ArrayList<*>
        Packets(first, second)
    }
}
