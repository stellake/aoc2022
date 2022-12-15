package dec14

import common.Coordinate
import common.Limits
import common.readLines

const val DAY = 14

// TODO: Refactor
fun main() {
    val data = getData(readLines(DAY))
    val rockCoordinates = getRockCoordinates(data).toSet()

    // 961
    solvePart1(rockCoordinates)

    // 26375
    solvePart2(rockCoordinates)
}

private fun solvePart1(rockCoordinates: Set<Coordinate>) {
    val sandCoordinates = simulateSandFall(rockCoordinates)
    println("Part 1 answer: ${sandCoordinates.size}")
}

private fun solvePart2(rockCoordinates: Set<Coordinate>) {
    val sandPieces = simulateSandFallPart2(rockCoordinates)
    println("Part 2 answer: ${sandPieces.size}")
}

private fun simulateSandFallPart2(rockCoordinates: Set<Coordinate>): Set<Coordinate> {
    val sandStartCoordinate = Coordinate(500, 0)
    val sandCoordinates = mutableSetOf<Coordinate>()
    val yLimits = Limits(0, rockCoordinates.maxOf { it.y })
    while (true) {
        val setCoordinate = getSandSetCoordinate(rockCoordinates, sandCoordinates, sandStartCoordinate, limitY = yLimits)
        sandCoordinates.add(setCoordinate)
        if (setCoordinate == sandStartCoordinate) {
            break
        }
    }
    return sandCoordinates
}

private fun simulateSandFall(rockCoordinates: Set<Coordinate>): Set<Coordinate> {
    val sandStartCoordinate = Coordinate(500, 0)
    val sandCoordinates = mutableSetOf<Coordinate>()
    val xLimits = Limits(rockCoordinates.minOf { it.x }, rockCoordinates.maxOf { it.x })
    val yLimits = Limits(0, rockCoordinates.maxOf { it.y })
    while (true) {
        val setCoordinate = getSandSetCoordinate(rockCoordinates, sandCoordinates, sandStartCoordinate, xLimits, yLimits)
        if (setCoordinate.x < xLimits.min!! || setCoordinate.x > xLimits.max!! || setCoordinate.y > yLimits.max!!) {
            // Falling into the void
            break
        }
        sandCoordinates.add(setCoordinate)
    }
    return sandCoordinates
}

private fun getSandSetCoordinate(
    rockCoordinates: Set<Coordinate>,
    sandCoordinates: Set<Coordinate>,
    sandStartCoordinate: Coordinate,
    limitX: Limits? = null,
    limitY: Limits? = null
): Coordinate {
    val allTakenCoordinates = rockCoordinates + sandCoordinates
    var x = sandStartCoordinate.x
    var y = sandStartCoordinate.y
    while (true) {
        if (limitX != null && (x < limitX.min!! || x > limitX.max!!)) {
            return Coordinate(x, y)
        }
        if (limitY != null && (y < limitY.min!! || y > limitY.max!!)) {
            return Coordinate(x, y)
        }
        val canKeepFallingStraight = !allTakenCoordinates.contains(Coordinate(x, y + 1))
        if (canKeepFallingStraight) {
            y++
            continue
        }
        val canFallLeftDiagonal = !allTakenCoordinates.contains(Coordinate(x - 1, y + 1))
        if (canFallLeftDiagonal) {
            x--
            y++
            continue
        }
        val canFallRightDiagonal = !allTakenCoordinates.contains(Coordinate(x + 1, y + 1))
        if (canFallRightDiagonal) {
            x++
            y++
            continue
        }
        return Coordinate(x, y)
    }
}

private fun getRockCoordinates(instructions: List<List<Coordinate>>): List<Coordinate> {
    return instructions.map { instruction ->
        val visitedCoordinates = mutableSetOf<Coordinate>()
        for (i in 1 until instruction.size) {
            val currentCoordinate = instruction[i]
            val prevCoordinate = instruction[i - 1]
            if (currentCoordinate.x == prevCoordinate.x) {
                val startY = if (currentCoordinate.y > prevCoordinate.y) prevCoordinate.y else currentCoordinate.y
                val endY = if (currentCoordinate.y > prevCoordinate.y) currentCoordinate.y else prevCoordinate.y
                for (y in startY..endY) {
                    visitedCoordinates.add(Coordinate(currentCoordinate.x, y))
                }
            } else if (currentCoordinate.y == prevCoordinate.y) {
                val startX = if (currentCoordinate.x > prevCoordinate.x) prevCoordinate.x else currentCoordinate.x
                val endX = if (currentCoordinate.x > prevCoordinate.x) currentCoordinate.x else prevCoordinate.x
                for (x in startX..endX) {
                    visitedCoordinates.add(Coordinate(x, currentCoordinate.y))
                }
            } else {
                throw Error("Expecting only straight lines for rocks!")
            }
        }
        visitedCoordinates
    }.flatten()
}

private fun getData(input: List<String>): List<List<Coordinate>> {
    return input.map { line ->
        val coordinatePairs = line.split(" -> ")
        coordinatePairs.map { pair ->
            val parts = pair.split(",")
            Coordinate(parts[0].toInt(), parts[1].toInt())
        }
    }
}

private fun printDrawing(rockCoordinates: List<Coordinate>, sandCoordinates: List<Coordinate>) {
    val rockXCoordinates: List<Int> = rockCoordinates.map { it.x }
    val minX = rockXCoordinates.minOrNull()!!
    val maxX = rockXCoordinates.maxOrNull()!!
    val minY = 0
    val maxY = rockCoordinates.maxOf { it.y }

    val drawing = Array(maxY - minY + 1) { Array(maxX - minX + 1) { '.' } }
    rockCoordinates.forEach { c -> drawing[c.y - minY][c.x - minX] = '#' }

    drawing[0][500 - minX] = '+'
    sandCoordinates.forEach { c -> drawing[c.y - minY][c.x - minX] = 'o' }

    drawing.forEach { line -> println(line.joinToString("")) }
}
