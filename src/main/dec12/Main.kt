package dec12

import common.*

const val DAY = 12

data class PointInMap(
    val height: Int,
    val coordinate: Coordinate,
    val isStart: Boolean,
    val isEnd: Boolean,
    var shortestPathToStart: Int? = null
)

data class Data(
    val map: List<List<PointInMap>>,
    val start: Coordinate,
    val end: Coordinate
)

// TODO: Part 2 is basically copy-past of Part 1 - refactor!
fun main() {
    // 380
    solvePart1(readLines(DAY))

    // 375
    solvePart2(readLines(DAY))
}

private fun solvePart1(input: List<String>) {
    val data = getData(input)
    val start = data.start
    var turn = 0
    var shortestPath: Int? = null
    var coordinatesToCheck = listOf(start)
    while (shortestPath == null) {
        turn++
        val newCoordinates = mutableListOf<Coordinate>()
        coordinatesToCheck.forEach { coordinate ->
            val currentHeight = data.map[coordinate.y][coordinate.x].height
            val nextCoordinates = getNextCoordinates(coordinate, data.map).filter { data.map[it.y][it.x].height - currentHeight <= 1 }
            nextCoordinates.forEach { c -> data.map[c.y][c.x].shortestPathToStart = turn }
            val coordinateWithShortestPath = nextCoordinates.find { data.map[it.y][it.x].isEnd }
            if (coordinateWithShortestPath != null) {
                shortestPath = turn
            } else {
                newCoordinates.addAll(nextCoordinates)
            }
        }
        coordinatesToCheck = newCoordinates
    }
    println("Part 1 answer: $shortestPath")
}

private fun solvePart2(input: List<String>) {
    val data = getData(input)
    val start = data.end
    var turn = 0
    var shortestPath: Int? = null
    var coordinatesToCheck = listOf(start)
    val visitedCoordinates = mutableListOf(start)
    while (shortestPath == null) {
        turn++
        val newCoordinates = mutableListOf<Coordinate>()
        coordinatesToCheck.forEach { coordinate ->
            val currentHeight = data.map[coordinate.y][coordinate.x].height
            val nextCoordinates = getNextCoordinates(coordinate, data.map).filter { currentHeight - data.map[it.y][it.x].height <= 1 && !visitedCoordinates.contains(it) }
            nextCoordinates.forEach { c -> data.map[c.y][c.x].shortestPathToStart = turn }
            visitedCoordinates.addAll(nextCoordinates)
            val coordinateWithShortestPath = nextCoordinates.find { data.map[it.y][it.x].height == 1 }
            if (coordinateWithShortestPath != null) {
                shortestPath = turn
            } else {
                newCoordinates.addAll(nextCoordinates)
            }
        }
        if (newCoordinates.size == 0) {
            println("NO NEW COORDINATES")
            break
        }
        coordinatesToCheck = newCoordinates
    }
    println("Part 2 answer: $shortestPath")
}

private fun getNextCoordinates(
    coordinate: Coordinate,
    map: List<List<PointInMap>>
): List<Coordinate> {
    val maxY = map.size - 1
    val maxX = map[0].size - 1

    val nonDiagonalCoordinates = coordinate.getNonDiagonalCoordinates(
        Limits(0, maxX),
        Limits(0, maxY)
    )

    return nonDiagonalCoordinates.filter { map[it.y][it.x].shortestPathToStart == null }
}

private fun getData(input: List<String>): Data {
    val heightMap = getHeightMap()
    var startCoordinate: Coordinate? = null
    var endCoordinate: Coordinate? = null
    val map = input.mapIndexed { y, line ->
        line.split("").filter { it != "" }.mapIndexed { x, letter ->
            if (letter == "S") {
                startCoordinate = Coordinate(x, y)
            }
            if (letter == "E") {
                endCoordinate = Coordinate(x, y)
            }
            PointInMap(
                heightMap[letter[0]]!!,
                Coordinate(x, y),
                letter == "S",
                letter == "E"
            )
        }
    }
    return Data(map, startCoordinate!!, endCoordinate!!)
}

 private fun getHeightMap(): Map<Char, Int> {
    val heightMap = mutableMapOf<Char, Int>()
     heightMap['S'] = 1

    var letter = 'a'
    var score = 1
    while (letter <= 'z') {
        heightMap[letter] = score
        ++letter
        ++score
    }
     heightMap['E'] = score

    return heightMap
}
