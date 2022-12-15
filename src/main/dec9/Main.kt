package dec9

import common.*
import kotlin.math.abs

enum class Direction {
    U, D, R, L
}

data class Instruction(
    val number: Int,
    val direction: Direction
)

const val DAY = 9

fun main() {
    val input = readLines(DAY)
    // 6175
    solvePart1(input)
    // 2578
    solvePart2(input)
}

private fun solvePart1(input: List<String>) {
    val data = getData(input)
    val startCoordinate = Coordinate(0, 0)

    val coordinatesVisited = mutableSetOf(startCoordinate)

    var currentHeadCoordinate = startCoordinate
    var currentTailCoordinate = startCoordinate

    data.forEach { instruction ->
        repeat(instruction.number) {
            currentHeadCoordinate = getNewHeadCoordinate(instruction, currentHeadCoordinate)
            currentTailCoordinate = getNewTailCoordinate(currentTailCoordinate, currentHeadCoordinate)
            coordinatesVisited.add(currentTailCoordinate)
        }
    }

    println("Number of coordinates visited: ${coordinatesVisited.size}")
}

private fun solvePart2(input: List<String>) {
    val data = getData(input)
    val startCoordinate = Coordinate(0, 0)
    val coordinatesVisited = mutableSetOf(startCoordinate)

    // TODO: Yikes! (refactor)
    var currentHeadCoordinate = startCoordinate
    var currentT1 = startCoordinate
    var currentT2 = startCoordinate
    var currentT3 = startCoordinate
    var currentT4 = startCoordinate
    var currentT5 = startCoordinate
    var currentT6 = startCoordinate
    var currentT7 = startCoordinate
    var currentT8 = startCoordinate
    var currentT9 = startCoordinate

    data.forEach { instruction ->
        repeat(instruction.number) {
            currentHeadCoordinate = getNewHeadCoordinate(instruction, currentHeadCoordinate)

            currentT1 = getNewTailCoordinate(currentT1, currentHeadCoordinate)
            currentT2 = getNewTailCoordinate(currentT2, currentT1)
            currentT3 = getNewTailCoordinate(currentT3, currentT2)
            currentT4 = getNewTailCoordinate(currentT4, currentT3)
            currentT5 = getNewTailCoordinate(currentT5, currentT4)
            currentT6 = getNewTailCoordinate(currentT6, currentT5)
            currentT7 = getNewTailCoordinate(currentT7, currentT6)
            currentT8 = getNewTailCoordinate(currentT8, currentT7)
            currentT9 = getNewTailCoordinate(currentT9, currentT8)

            coordinatesVisited.add(currentT9)
        }
    }

    println("Part 2 coordinates visited: ${coordinatesVisited.size}")
}

private fun getNewHeadCoordinate(instruction: Instruction, currentCoordinateHead: Coordinate) =
    when (instruction.direction) {
        Direction.D -> currentCoordinateHead.getBottomCoordinate(1)
        Direction.U -> currentCoordinateHead.getTopCoordinate(1)
        Direction.R -> currentCoordinateHead.getRightCoordinate(1)
        Direction.L -> currentCoordinateHead.getLeftCoordinate(1)
    }

private fun getNewTailCoordinate(current: Coordinate, head: Coordinate): Coordinate {
    val deltaX = head.x - current.x
    val deltaY = head.y - current.y
    return if (abs(deltaX) > 1 || abs(deltaY) > 1) {
        val moveX = getDeltaMove(deltaX)
        val moveY = getDeltaMove(deltaY)
        Coordinate(current.x + moveX, current.y + moveY)
    } else {
        current
    }
}

private fun getDeltaMove(delta: Int): Int {
    if (delta == 0) return 0
    return if (delta > 0) 1 else -1
}

private fun getData(input: List<String>): List<Instruction> {
    return input.map { line ->
        val parts = line.split(' ')
        val direction = Direction.valueOf(parts[0])
        Instruction(parts[1].toInt(), direction)
    }
}

private fun Coordinate.getRightCoordinate(number: Int): Coordinate {
    return Coordinate(this.x + number, this.y)
}

private fun Coordinate.getLeftCoordinate(number: Int): Coordinate {
    return Coordinate(this.x - number, this.y)
}

private fun Coordinate.getTopCoordinate(number: Int): Coordinate {
    return Coordinate(this.x, this.y - number)
}

private fun Coordinate.getBottomCoordinate(number: Int): Coordinate {
    return Coordinate(this.x, this.y + number)
}
