package dec17

import common.Coordinate
import common.readDraftLines
import common.readLines

const val DAY = 17

data class VisitedRockData(
    val rockIndex: Int,
    val rocksFallen: Long,
    val lastPatterns: String,
    val currentHeight: Int,
    val currentX: Int,
) {
    override fun equals(other: Any?): Boolean {
        return if (other is VisitedRockData) {
            other.rockIndex == this.rockIndex && other.lastPatterns == this.lastPatterns && other.currentX == this.currentX
        } else {
            false
        }
    }
}

private const val CAVE_WIDTH = 7

fun main() {
    val draft = readDraftLines(DAY)
    val input = readLines(DAY)

    solvePart1(draft, 2022) // 3068
    solvePart1(input, 2022) // 3188

    val rocksToStop = 1000000000000L
    solvePart2(draft, rocksToStop) // 1514285714288
    solvePart2(input, rocksToStop) // 1591977077342
}

// How tall is the tower after 2022 rocks
val ROCKS = listOf(
    listOf("####"),
    listOf(".#.", "###", ".#."),
    listOf("..#", "..#", "###"),
    listOf("#", "#", "#", "#"),
    listOf("##", "##")
)

private fun solvePart1(input: List<String>, rocksToStop: Long) {
    val indexLeftRockAppears = 2
    val indexFromTowerHeightRockAppears = 3

    val jetStream = getData(input)
    var currentRockIndex = 0
    var currentJetStreamIndex = 0
    val maxRockIndex = ROCKS.size - 1
    val maxJetStreamIndex = jetStream.size - 1

    val currentTower = mutableListOf<String>()
    var currentCoordinates = Coordinate(indexLeftRockAppears, 0 + indexFromTowerHeightRockAppears)
    var rocksStopped = 0

    while (rocksStopped < rocksToStop) {
        val currentRock = ROCKS[currentRockIndex]
        val jetImpact = jetStream[currentJetStreamIndex]

        currentCoordinates = getCoordinatesAfterJetImpact(jetImpact, currentRock, currentCoordinates, currentTower)
        currentJetStreamIndex = getNewIndex(currentJetStreamIndex, maxJetStreamIndex)

        val canGoDown = rockCanGoDown(currentRock, currentCoordinates, currentTower)
        if (canGoDown) {
            currentCoordinates = Coordinate(currentCoordinates.x, currentCoordinates.y - 1)
        } else {
            currentTower.addRock(currentRock, currentCoordinates)
            rocksStopped++
            currentRockIndex = getNewIndex(currentRockIndex, maxRockIndex)
            currentCoordinates = Coordinate(indexLeftRockAppears, currentTower.size + indexFromTowerHeightRockAppears + ROCKS[currentRockIndex].size - 1)
        }
    }

    println("Part 1 answer: ${currentTower.size}")
}

private fun solvePart2(input: List<String>, rocksToStop: Long) {
    val indexLeftRockAppears = 2
    val indexFromTowerHeightRockAppears = 3

    val jetStream = getData(input)
    var currentRockIndex = 0
    var currentJetStreamIndex = 0
    val maxRockIndex = ROCKS.size - 1
    val maxJetStreamIndex = jetStream.size - 1

    val currentTower = mutableListOf<String>()
    var currentCoordinates = Coordinate(indexLeftRockAppears, 0 + indexFromTowerHeightRockAppears)
    val visitedRockDataByJetStreamInstructionIndex = mutableMapOf<Int, MutableList<VisitedRockData>>()
    jetStream.forEachIndexed { index: Int, _: Char ->
        visitedRockDataByJetStreamInstructionIndex[index] = mutableListOf()
    }
    var rocksStopped = 0L
    var extraHeight: Long? = null

    while (rocksStopped < rocksToStop) {
        val currentRock = ROCKS[currentRockIndex]
        val jetImpact = jetStream[currentJetStreamIndex]
        currentCoordinates = getCoordinatesAfterJetImpact(jetImpact, currentRock, currentCoordinates, currentTower)
        currentJetStreamIndex = getNewIndex(currentJetStreamIndex, maxJetStreamIndex)

        val canGoDown = rockCanGoDown(currentRock, currentCoordinates, currentTower)
        if (canGoDown) {
            currentCoordinates = Coordinate(currentCoordinates.x, currentCoordinates.y - 1)
        } else {
            currentTower.addRock(currentRock, currentCoordinates)
            rocksStopped++
            val currentTowerPattern = currentTower.takeLast(30).joinToString { "" }
            currentRockIndex = getNewIndex(currentRockIndex, maxRockIndex)
            currentCoordinates = Coordinate(indexLeftRockAppears, currentTower.size + indexFromTowerHeightRockAppears + ROCKS[currentRockIndex].size - 1)
            val visitedRockData = VisitedRockData(currentRockIndex, rocksStopped, currentTowerPattern, currentTower.size, currentCoordinates.x)
            if (extraHeight == null && visitedRockDataByJetStreamInstructionIndex[currentJetStreamIndex - 1]!!.contains(visitedRockData)) {
                val cycleStartData = visitedRockDataByJetStreamInstructionIndex[currentJetStreamIndex - 1]!!.find { it == visitedRockData }!!
                val heightChangeInCycle = visitedRockData.currentHeight - cycleStartData.currentHeight
                val rocksFallenInCycle = visitedRockData.rocksFallen - cycleStartData.rocksFallen
                val numberOfCyclesLeft = (rocksToStop - rocksStopped) / rocksFallenInCycle
                val numberOfRocksToSkip = numberOfCyclesLeft * rocksFallenInCycle
                extraHeight = heightChangeInCycle * numberOfCyclesLeft
                rocksStopped += numberOfRocksToSkip
            }
            visitedRockDataByJetStreamInstructionIndex[currentJetStreamIndex - 1]!!.add(visitedRockData)
        }
    }

    val totalHeight = currentTower.size + extraHeight!!
    println("Part 2 answer: $totalHeight")
}

private fun MutableList<String>.addRock(rock: List<String>, coordinate: Coordinate) {
    val emptyLine = "......."
    val lastYIndexInTower = this.size - 1
    val lastY = coordinate.y - (rock.size - 1)

    rock.reversed().forEachIndexed { i1, line ->
        val yForLine = lastY + i1
        if (lastYIndexInTower >= yForLine) {
            var towerLine = this[yForLine]
            line.forEachIndexed { i2, char ->
                val xToCheck = coordinate.x + i2
                if (char == '#') {
                    val newLine = towerLine.substring(
                        0,
                        xToCheck
                    ) + char + towerLine.substring(xToCheck + 1, CAVE_WIDTH)
                    towerLine = newLine
                }
            }
            this[yForLine] = towerLine
        } else {
            val newLine = emptyLine.substring(0, coordinate.x) + line + emptyLine.substring(coordinate.x + line.length, CAVE_WIDTH)
            this.add(newLine)
        }
    }
}

private fun getNewIndex(currentIndex: Int, maxIndex: Int): Int {
    return if (currentIndex < maxIndex) currentIndex + 1 else 0
}

private fun getCoordinatesAfterJetImpact(
    jetImpact: Char,
    currentRock: List<String>,
    currentCoordinates: Coordinate,
    currentTower: MutableList<String>
): Coordinate {
    return if (jetImpact == '<') {
        if (canRockGoLeft(currentRock, currentCoordinates, currentTower)) {
            Coordinate((currentCoordinates.x - 1), currentCoordinates.y)
        } else {
            currentCoordinates
        }
    } else if (jetImpact == '>') {
        if (canRockGoRight(currentRock, currentCoordinates, currentTower)) {
            Coordinate((currentCoordinates.x + 1), currentCoordinates.y)
        } else {
            currentCoordinates
        }
    } else throw Error("Unknown jet stream value")
}

private fun canRockGoLeft(rock: List<String>, currentCoordinate: Coordinate, currentTower: List<String>): Boolean {
    if (currentCoordinate.x == 0) return false
    return rock.canBePlacedInCoordinate(Coordinate(currentCoordinate.x - 1, currentCoordinate.y), currentTower)
}

private fun canRockGoRight(rock: List<String>, currentCoordinate: Coordinate, currentTower: List<String>): Boolean {
    if (currentCoordinate.x + rock[0].length - 1 >= 6) return false
    return rock.canBePlacedInCoordinate(Coordinate(currentCoordinate.x + 1, currentCoordinate.y), currentTower)
}

private fun rockCanGoDown(rock: List<String>, currentCoordinate: Coordinate, currentTower: List<String>): Boolean {
    val bottomY = currentCoordinate.y - (rock.size - 1)
    if (currentTower.size < bottomY) return true

    val noLinesBelowRock = bottomY < 1
    if (noLinesBelowRock) return false

    return rock.canBePlacedInCoordinate(Coordinate(currentCoordinate.x, currentCoordinate.y - 1), currentTower)
}

private fun List<String>.canBePlacedInCoordinate(coordinate: Coordinate, tower: List<String>): Boolean {
    val lastYIndexInTower = tower.size - 1
    this.forEachIndexed { i1, line ->
        val yForLine = coordinate.y - i1
        if (lastYIndexInTower >= yForLine) {
            val towerLine = tower[yForLine]
            line.forEachIndexed { i2, char ->
                val xToCheck = coordinate.x + i2
                if (towerLine[xToCheck] == '#' && char == '#') {
                    return false
                }
            }
        }
    }
    return true
}

private fun getData(input: List<String>): List<Char> {
    return input[0].toCharArray().toList()
}
