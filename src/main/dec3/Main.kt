package dec3

import common.readLines

data class RuckSack(val allItems: String) {
    private val firstCompartmentItems: Set<Char> get() {
        val halfLength = allItems.length / 2
        val item = allItems.substring(0, halfLength)
        return item.toCharArray().toSet()
    }

    private val secondCompartmentItems: Set<Char> get() {
        val halfLength = allItems.length / 2
        val item = allItems.substring(halfLength, allItems.length)
        return item.toCharArray().toSet()
    }

    fun getItemInBothCompartments(): Char {
        val common = firstCompartmentItems.intersect(secondCompartmentItems)
        if (common.size != 1) {
            throw Error("Invalid number of common items: ${common.size}")
        }
        return common.elementAt(0)
    }
}

fun main() {
    // 7997
    solvePart1()

    // 2545
    solvePart2()
}

fun solvePart1() {
    val scoreMap = getScoreMap()
    val data = getData().map { it.getItemInBothCompartments() }
    val score = data.sumOf { scoreMap[it]!! }
    println("Part 1 answer is: $score")
}

fun solvePart2() {
    val data = getData().map { it.allItems.toCharArray().toSet() }
    val scoreMap = getScoreMap()

    var score = 0
    for (i in 0 until data.size / 3) {
        val startIndex = i * 3;
        val commonChar = data[startIndex].intersect(data[startIndex + 1]).intersect(data[startIndex + 2]).elementAt(0)
        score += scoreMap[commonChar] ?: throw Error("Invalid char found: $commonChar")
    }
    println("Part 2 answer is: $score")
}

private fun getScoreMap(): Map<Char, Int> {
    val extraPointsForCapitalLetter = 26
    val scoreMap = mutableMapOf<Char, Int>()

    var letter = 'a'
    var score = 1
    while (letter <= 'z') {
        scoreMap[letter] = score
        scoreMap[letter.uppercaseChar()] = score + extraPointsForCapitalLetter
        ++letter
        ++score
    }

    return scoreMap
}

private fun getData(): List<RuckSack> {
    return readLines(3).map { RuckSack(it) }
}
