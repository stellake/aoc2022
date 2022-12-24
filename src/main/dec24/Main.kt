package dec24

import common.Coordinate
import common.Limits
import common.readDraftLines
import common.readLines

const val DAY = 24

private enum class Direction {
    UP, DOWN, LEFT, RIGHT
}

private data class Blizzard(
    val coordinate: Coordinate,
    val direction: Direction
)

private data class PuzzleInput(
    val map: List<List<Boolean>>,
    val blizzards: List<Blizzard>,
)

fun main() {
    val draft = readDraftLines(DAY)
    val input = readLines(DAY)

    solvePart1(draft) // 18
    solvePart1(input) // 286

    solvePart2(draft) // 54
    solvePart2(input) // 820
}

private data class CurrentState(
    val blizzards: List<Blizzard>,
    val nextBlizzards: List<Blizzard>,
    val minutesPassed: Int,
    val nextPossiblePositions: List<Coordinate>,
)

private fun solvePart1(input: List<String>) {
    val data = getData(input)

    val nextBlizzards = getNextBlizzardState(data.blizzards, data.map)
    val start = Coordinate(1, 0)
    val end = Coordinate(data.map[0].size - 2, data.map.size - 1)

    val statesToCheck = ArrayDeque<CurrentState>()
    statesToCheck.add(CurrentState(data.blizzards, nextBlizzards, 0, listOf(start)))
    
    while (statesToCheck.isNotEmpty()) {
        val stateToCheck = statesToCheck.removeFirst()
        if (stateToCheck.nextPossiblePositions.any { it == end }) {
            println("Part 1 answer: ${stateToCheck.minutesPassed + 1}")
            return
        }
        
        val nextStates = getNextState(stateToCheck, data.map)
        statesToCheck.add(nextStates)
    }

    throw Error("No solution found!!!")
}

private fun solvePart2(input: List<String>) {
    val data = getData(input)

    val nextBlizzards = getNextBlizzardState(data.blizzards, data.map)
    val start = Coordinate(1, 0)
    val end = Coordinate(data.map[0].size - 2, data.map.size - 1)

    val statesToCheck = ArrayDeque<CurrentState>()
    statesToCheck.add(CurrentState(data.blizzards, nextBlizzards, 0, listOf(start)))

    while (statesToCheck.isNotEmpty()) {
        val stateToCheck = statesToCheck.removeFirst()
        if (stateToCheck.nextPossiblePositions.any { it == end }) {
            println("First trip takes: ${stateToCheck.minutesPassed + 1}")
            statesToCheck.clear()
            val newStartState = CurrentState(stateToCheck.blizzards, stateToCheck.nextBlizzards, stateToCheck.minutesPassed, listOf(end))
            statesToCheck.add(newStartState)
            break
        }

        val nextStates = getNextState(stateToCheck, data.map)
        statesToCheck.add(nextStates)
    }

    while (statesToCheck.isNotEmpty()) {
        val stateToCheck = statesToCheck.removeFirst()
        if (stateToCheck.nextPossiblePositions.any { it == start }) {
            println("Trip back takes: ${stateToCheck.minutesPassed + 1}")
            statesToCheck.clear()
            val newStartState = CurrentState(stateToCheck.blizzards, stateToCheck.nextBlizzards, stateToCheck.minutesPassed, listOf(start))
            statesToCheck.add(newStartState)
            break
        }

        val nextStates = getNextState(stateToCheck, data.map)
        statesToCheck.add(nextStates)
    }

    while (statesToCheck.isNotEmpty()) {
        val stateToCheck = statesToCheck.removeFirst()
        if (stateToCheck.nextPossiblePositions.any { it == end }) {
            println("Total time taken: ${stateToCheck.minutesPassed + 1}")
            return
        }

        val nextStates = getNextState(stateToCheck, data.map)
        statesToCheck.add(nextStates)
    }

    throw Error("No solution found!!!")
}

private fun getNextState(state: CurrentState, map: List<List<Boolean>>): CurrentState {
    val newBlizzardState = state.nextBlizzards
    val blizzardStateAfterNext = getNextBlizzardState(newBlizzardState, map)
    val nextBlizzardCoordinates = blizzardStateAfterNext.map { it.coordinate }.toSet()
    val nextPossibleCoordinates = state.nextPossiblePositions.map { getNextPossibleCoordinates(it, map, nextBlizzardCoordinates) }.flatten().toSet().toList()
    return CurrentState(newBlizzardState, blizzardStateAfterNext, state.minutesPassed + 1, nextPossibleCoordinates )
}

private fun getNextPossibleCoordinates(coordinate: Coordinate, map: List<List<Boolean>>, nextBlizzardCoordinates: Set<Coordinate>): List<Coordinate> {
    val validSideCoordinates = coordinate.getNonDiagonalCoordinates(yLimits = Limits(0, map.size - 1)) + coordinate
    return validSideCoordinates.filter { map[it.y][it.x] && !nextBlizzardCoordinates.contains(it) }
}

private fun getNextBlizzardState(blizzards: List<Blizzard>, map: List<List<Boolean>>): List<Blizzard> {
    return blizzards.map {
        val nextCoordinate = when (it.direction) {
            Direction.UP -> {
                val y = if (it.coordinate.y - 1 < 1) map.size - 2 else it.coordinate.y - 1
                Coordinate(it.coordinate.x, y)
            }
            Direction.DOWN -> {
                val y = if (it.coordinate.y + 1 > map.size - 2) 1 else it.coordinate.y + 1
                Coordinate(it.coordinate.x, y)
            }
            Direction.LEFT -> {
                val x = if (it.coordinate.x - 1 < 1) map[0].size - 2 else it.coordinate.x - 1
                Coordinate(x, it.coordinate.y)
            }
            Direction.RIGHT -> {
                val x = if (it.coordinate.x + 1 > map[0].size - 2) 1 else it.coordinate.x + 1
                Coordinate(x, it.coordinate.y)
            }
        }
        Blizzard(nextCoordinate, it.direction)
    }
}

private fun getData(input: List<String>): PuzzleInput {
    val map = input.map { line ->
        line.toCharArray().toList().map { it != '#' }
    }
    val blizzards = mutableListOf<Blizzard>()
    input.forEachIndexed { y, line ->
        line.toCharArray().toList().forEachIndexed { x, char ->
            if (char != '#' && char != '.') {
                blizzards.add(Blizzard(
                    Coordinate(x, y), char.toDirection()
                ))
            }
        }
    }

    return PuzzleInput(map, blizzards)
}

private fun Char.toDirection(): Direction {
    return when (this) {
        '>' -> Direction.RIGHT
        '<' -> Direction.LEFT
        '^' -> Direction.UP
        'v' -> Direction.DOWN
        else -> throw Error("Invalid blizzard direction")
    }
}