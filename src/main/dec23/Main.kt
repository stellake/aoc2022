package dec23

import common.Coordinate
import common.Limits
import common.readDraftLines
import common.readLines

const val DAY = 23

fun main() {
    val draft = readDraftLines(DAY)
    val input = readLines(DAY)

    solvePart1(draft) // 110
    solvePart1(input) // 3990

    solvePart2(draft) // 20
    solvePart2(input) // 1057
}

private val DIRECTIONS = listOf("N", "S", "W", "E")

private fun solvePart1(input: List<String>) {
    val data = getData(input)

    var currentMap = data
    var round = 0
    var dirIndex = 0
    repeat(10) {
        round++
//        println("Round $round")
        val coordinateToNextCoordinate = findMoves(currentMap, dirIndex)
        dirIndex = if (dirIndex == DIRECTIONS.size - 1) 0 else dirIndex + 1
        currentMap = getNewMap(data, coordinateToNextCoordinate)
//        currentMap.print()
    }

    val answer = countEmptyTilesInSmallestRectangle(currentMap)
    println("Part 1 answer: $answer")
}

private fun solvePart2(input: List<String>) {
    val data = getData(input)

    var currentMap = data
    var round = 0
    var dirIndex = 0
    while(true) {
        round++
//        println("Round $round")
        val coordinateToNextCoordinate = findMovesPart2(currentMap, dirIndex) ?: break
        dirIndex = if (dirIndex == DIRECTIONS.size - 1) 0 else dirIndex + 1
        currentMap = getNewMap(data, coordinateToNextCoordinate)
//        currentMap.print()
    }

    println("Part 2 answer: $round")
}

private fun findMovesPart2(
    currentMap: List<List<Boolean>>,
    dirIndex: Int,
): Map<Coordinate, Coordinate>? {
    val coordinateToNextCoordinate = mutableMapOf<Coordinate, Coordinate>()
    var hasGotNextMoves = false
    currentMap.forEachIndexed { y, xList ->
        xList.forEachIndexed { x, hasElf ->
            if (hasElf) {
                val currentCoordinate = Coordinate(x, y)
                val nextCoordinate = getNextCoordinate(currentCoordinate, currentMap, dirIndex)
                if (nextCoordinate != null) hasGotNextMoves = true
                coordinateToNextCoordinate[currentCoordinate] = nextCoordinate ?: currentCoordinate
            }
        }
    }
    return if (hasGotNextMoves) coordinateToNextCoordinate else null
}

private fun findMoves(
    currentMap: List<List<Boolean>>,
    dirIndex: Int,
): Map<Coordinate, Coordinate> {
    val coordinateToNextCoordinate = mutableMapOf<Coordinate, Coordinate>()
    currentMap.forEachIndexed { y, xList ->
        xList.forEachIndexed { x, hasElf ->
            if (hasElf) {
                val currentCoordinate = Coordinate(x, y)
                val nextCoordinate = getNextCoordinate(currentCoordinate, currentMap, dirIndex)
                coordinateToNextCoordinate[currentCoordinate] = nextCoordinate ?: currentCoordinate
            }
        }
    }
    return coordinateToNextCoordinate
}

private fun List<List<Boolean>>.print() {
    this.forEach { line ->
        val formatted = line.map { value -> if (value) '#' else '.' }.toList().joinToString("")
        println(formatted)
    }
    println("          ")
    println("  -----   ")
    println("          ")
}

private fun countEmptyTilesInSmallestRectangle(map: List<List<Boolean>>): Int {
    val startY = map.indexOfFirst { it.any { value -> value } }
    val endY = map.indexOfLast { it.any { value -> value } }
    var startX: Int? = null
    var currentIndex = 0
    while (startX == null) {
        val values = map.map { it[currentIndex] }
        if (values.any { it == true }) {
            startX = currentIndex
        }
        currentIndex++
    }

    var endX: Int? = null
    var currentIndex2 = map[0].size - 1
    while (endX == null) {
        val values = map.map { it[currentIndex2] }
        if (values.any { it == true }) {
            endX = currentIndex2
        }
        currentIndex2--
    }

    val rectangle = map.subList(startY, endY + 1).map { it.subList(startX, endX + 1) }
    return rectangle.sumOf { it.count { value -> !value } }
}

private fun getNewMap(currentMap: List<List<Boolean>>, coordinateToNextCoordinate: Map<Coordinate, Coordinate>): List<List<Boolean>> {
    val allProposedCoordinates = coordinateToNextCoordinate.values.toList()
    val newMap = Array(currentMap.size) { Array(currentMap[0].size) { false }.toMutableList() }.toMutableList()
    coordinateToNextCoordinate.forEach {
        val currentCoordinate = it.key
        val nextCoordinate = it.value
        if (allProposedCoordinates.filter { it == nextCoordinate }.size < 2) {
            newMap[nextCoordinate.y][nextCoordinate.x] = true
        } else {
            newMap[currentCoordinate.y][currentCoordinate.x] = true
        }
    }
    return newMap
}

private fun getNextCoordinate(currentCoordinate: Coordinate, map: List<List<Boolean>>, dirIndex: Int): Coordinate? {
    val adjacentCoordinates = currentCoordinate.getAllSurroundingCoordinates(Limits(0, map[0].size - 1), Limits(0, map.size - 1))
    if (adjacentCoordinates.all { !map[it.y][it.x] }) return null

    val directions = if (dirIndex == 0) DIRECTIONS else DIRECTIONS.subList(dirIndex, DIRECTIONS.size) + DIRECTIONS.subList(0, dirIndex)
    directions.forEach { direction ->
        if (direction == "N") {
            val northCoordinates = adjacentCoordinates.filter { it.y == currentCoordinate.y - 1 }
            if (northCoordinates.all { !map[it.y][it.x] }) return Coordinate(currentCoordinate.x, currentCoordinate.y - 1)
        } else if (direction == "S") {
            val southCoordinates = adjacentCoordinates.filter { it.y == currentCoordinate.y + 1 }
            if (southCoordinates.all { !map[it.y][it.x] }) return Coordinate(currentCoordinate.x, currentCoordinate.y + 1)
        } else if (direction == "W") {
            val westCoordinates = adjacentCoordinates.filter { it.x == currentCoordinate.x - 1 }
            if (westCoordinates.all { !map[it.y][it.x] }) return Coordinate(currentCoordinate.x - 1, currentCoordinate.y)
        } else if (direction == "E") {
            val eastCoordinates = adjacentCoordinates.filter { it.x == currentCoordinate.x + 1 }
            if (eastCoordinates.all { !map[it.y][it.x] }) return Coordinate(currentCoordinate.x + 1, currentCoordinate.y)
        } else throw Error(" Unknown direction")
    }

    return null
}

private fun getData(input: List<String>): List<List<Boolean>> {
    val initialRectangle = input.map {
        val elements = it.toCharArray().toMutableList()
        elements.map { char ->
            when (char) {
                '#' -> true
                '.' -> false
                else -> throw Error("Unknown character!")
            }
        }.toMutableList()
    }
    val padding = initialRectangle.size
    val totalLineSize = initialRectangle[0].size + 2 * padding
    val linesToAdd = Array(padding) { Array(totalLineSize) { false }.toMutableList() }
    val newList = linesToAdd + initialRectangle.map {
        val newList = Array(padding) { false } + it + Array(padding) { false }
        newList.toMutableList()
    } + linesToAdd
    return newList.toList()
}
