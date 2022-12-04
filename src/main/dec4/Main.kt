package dec4

import common.readLines

data class CleaningSections(
    val elf1: IntRange,
    val elf2: IntRange
) {
    fun hasFullOverlap(): Boolean {
        val i1 = elf1.toSet()
        val i2 = elf2.toSet()
        val commonElements = i1.intersect(i2)
        return commonElements.size == i1.size || commonElements.size == i2.size
    }

    fun hasAnyOverlap(): Boolean {
        val i1 = elf1.toSet()
        val i2 = elf2.toSet()
        val commonElements = i1.intersect(i2)
        return commonElements.isNotEmpty()
    }
}

fun main() {
    // 515
    solvePart1()

    // 883
    solvePart2()
}

fun solvePart1() {
    val answer = getData().count { it.hasFullOverlap() }
    println("Part 1 answer is: $answer")
}

fun solvePart2() {
    val answer = getData().count { it.hasAnyOverlap() }
    println("Part 2 answer is: $answer")
}

private fun getData(): List<CleaningSections> {
    return readLines(4).map { line ->
        val sections = line.split(",")
        CleaningSections(
            getCleaningSections(sections[0]),
            getCleaningSections(sections[1])
        )
    }
}

private fun getCleaningSections(sectionString: String): IntRange {
    val parts = sectionString.split('-')
    return IntRange(parts[0].toInt(), parts[1].toInt())
}
