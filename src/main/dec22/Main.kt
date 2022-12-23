package dec22

import common.Coordinate
import common.readDraftGroups
import common.readLineGroups

const val DAY = 22

private enum class CubeSide {
    TOP, BOTTOM, MIDDLE, MIDDLE_LEFT, MIDDLE_RIGHT, BACK
}

private data class Instruction(
    val distance: Int,
    val rotateTo: Char?
)

private data class MapLine(
    val content: List<Char>,
    val lineWidth: Int,
    val startIndex: Int,
    val endIndex: Int,
)

private typealias PointMap = List<MapLine>

private data class Input(
    val map: PointMap,
    val instructions: List<Instruction>
)

fun main() {
    val draft = readDraftGroups(DAY)
    val input = readLineGroups(DAY)

    solvePart1(draft) // 6032
    solvePart1(input) // 57350

    solvePart2(input) // 104385
}

private fun solvePart1(input: List<String>) {
    val data = getData(input)
    val startX = data.map[0].startIndex
    var coordinate = Coordinate(startX, 0)
    var direction = 'R'
    val instructions = data.instructions
    instructions.forEach { instruction ->
        coordinate = getNewCoordinate(coordinate, direction, instruction.distance, data.map)
        if (instruction.rotateTo != null) {
            direction = getNewDirection(direction, instruction.rotateTo)
        }
    }
    val answer = getAnswer(coordinate, direction)
    println("Part 1 answer: $answer")
}

private fun solvePart2(input: List<String>) {
    val data = getData(input)
    val startX = data.map[0].startIndex
    var coordinate = Coordinate(startX, 0)
    var direction = 'R'
    val instructions = data.instructions
    instructions.forEach { instruction ->
        println("Coordinate $coordinate")
        println("Instruction $instruction")
        val (newCoordinate, newDirection) = getNewCoordinateAndDirection(coordinate, direction, instruction.distance, data.map)
        coordinate = newCoordinate
        direction = newDirection
        if (instruction.rotateTo != null) {
            direction = getNewDirection(direction, instruction.rotateTo)
        }
        println("Direction $direction")
    }
    val answer = getAnswer(coordinate, direction)
    println("Part 2 answer: $answer")
}

private fun getCubeSide(x: Int, y: Int, sideSize: Int): CubeSide {
    if (x in 0 until sideSize) {
        if (y in 2 * sideSize until 3 * sideSize) return CubeSide.MIDDLE_LEFT
        if (y >= 3 * sideSize) return CubeSide.BACK
    }
    if (x in sideSize until 2 * sideSize) {
        if (y in 0 until sideSize) return CubeSide.TOP
        if (y in sideSize until 2 * sideSize) return CubeSide.MIDDLE
        if (y in 2 * sideSize until 3 * sideSize) return CubeSide.BOTTOM
    }
    if (x >= 2 * sideSize) {
        if (y < sideSize) return CubeSide.MIDDLE_RIGHT
    }
    throw Error("Unknown cube side for x $x and y $y")
}

private fun getAnswer(coordinate: Coordinate, direction: Char): Int {
    val directionScore = when (direction) {
        'R' -> 0
        'L' -> 2
        'U' -> 3
        'D' -> 1
        else -> throw Error("Unexpected direction!")
    }
    return 1000 * (coordinate.y + 1) + 4 * (coordinate.x + 1) + directionScore
}

private fun getNewDirection(direction: Char, rotateTo: Char): Char {
    when (rotateTo) {
        'R' -> {
            return when (direction) {
                'R' -> 'D'
                'D' -> 'L'
                'L' -> 'U'
                'U' -> 'R'
                else -> throw Error("")
            }
        }
        'L' -> {
            return when (direction) {
                'R' -> 'U'
                'D' -> 'R'
                'L' -> 'D'
                'U' -> 'L'
                else -> throw Error("")
            }
        }
        else -> throw Error("Wrong rotation!")
    }
}

private fun getNewCoordinateAndDirection(coordinate: Coordinate, direction: Char, numOfMoves: Int, map: List<MapLine>): Pair<Coordinate, Char> {
    val sizeOfSide = map.size / 4 // TODO!! 3 in draft!!
    when (direction) {
        'R' -> {
            val currentLine = map[coordinate.y]
            var movesMade = 0
            var currentX = coordinate.x
            while (movesMade < numOfMoves) {
                // 49, 99, 149
                val isOnCubeEdge = (currentX + 1) % sizeOfSide == 0
                if (isOnCubeEdge) {
                    val cubeSide = getCubeSide(currentX, coordinate.y, sizeOfSide)
                    val y = coordinate.y % sizeOfSide
                    when (cubeSide) {
                        CubeSide.TOP, CubeSide.MIDDLE_LEFT -> {} // No op, increase X
                        CubeSide.BOTTOM -> {
                            val startCoordinate = Coordinate(sizeOfSide * 3 - 1, sizeOfSide - 1 - y)
                            if (map.isValueOccupied(startCoordinate)) return Pair(Coordinate(currentX, coordinate.y), direction)
                            return getNewCoordinateAndDirection(startCoordinate, 'L', numOfMoves - 1 - movesMade, map)
                        }
                        CubeSide.MIDDLE -> {
                            val startCoordinate = Coordinate(2 * sizeOfSide + y, sizeOfSide - 1)
                            if (map.isValueOccupied(startCoordinate)) return Pair(Coordinate(currentX, coordinate.y), direction)
                            return getNewCoordinateAndDirection(startCoordinate, 'U', numOfMoves - 1 - movesMade, map)
                        }
                        CubeSide.MIDDLE_RIGHT -> {
                            val startCoordinate = Coordinate(sizeOfSide * 2 - 1, 3 * sizeOfSide - 1 - y)
                            if (map.isValueOccupied(startCoordinate)) return Pair(Coordinate(currentX, coordinate.y), direction)
                            return getNewCoordinateAndDirection(startCoordinate, 'L', numOfMoves - 1 - movesMade, map)
                        }
                        CubeSide.BACK -> {
                            val startCoordinate = Coordinate(sizeOfSide + y, 3 * sizeOfSide - 1)
                            if (map.isValueOccupied(startCoordinate)) return Pair(Coordinate(currentX, coordinate.y), direction)
                            return getNewCoordinateAndDirection(startCoordinate, 'U', numOfMoves - 1 - movesMade, map)
                        }
                    }
                }
                val nextX = currentX + 1
                if (currentLine.content[nextX] == '#') return Pair(Coordinate(currentX, coordinate.y), direction)
                currentX = nextX
                movesMade++
            }
            return Pair(Coordinate(currentX, coordinate.y), direction)
        }
        'L' -> {
            val currentLine = map[coordinate.y]
            var movesMade = 0
            var currentX = coordinate.x
            while (movesMade < numOfMoves) {
                val isOnCubeEdge = currentX % sizeOfSide == 0
                if (isOnCubeEdge) {
                    val cubeSide = getCubeSide(currentX, coordinate.y, sizeOfSide)
                    val y = coordinate.y % sizeOfSide
                    when (cubeSide) {
                        CubeSide.BOTTOM, CubeSide.MIDDLE_RIGHT -> {} // No op, increase X
                        CubeSide.TOP -> {
                            val startCoordinate = Coordinate(0, 3 * sizeOfSide - 1 - y)
                            if (map.isValueOccupied(startCoordinate)) return Pair(Coordinate(currentX, coordinate.y), direction)
                            return getNewCoordinateAndDirection(startCoordinate, 'R', numOfMoves - 1 - movesMade, map)
                        }
                        CubeSide.MIDDLE -> {
                            val startCoordinate = Coordinate(y, 2 * sizeOfSide)
                            if (map.isValueOccupied(startCoordinate)) return Pair(Coordinate(currentX, coordinate.y), direction)
                            return getNewCoordinateAndDirection(startCoordinate, 'D', numOfMoves - 1 - movesMade, map)
                        }
                        CubeSide.MIDDLE_LEFT -> {
                            val startCoordinate = Coordinate(sizeOfSide, sizeOfSide - 1 - y)
                            if (map.isValueOccupied(startCoordinate)) return Pair(Coordinate(currentX, coordinate.y), direction)
                            return getNewCoordinateAndDirection(startCoordinate, 'R', numOfMoves - 1 - movesMade, map)
                        }
                        CubeSide.BACK -> {
                            val startCoordinate = Coordinate(sizeOfSide + y, 0)
                            if (map.isValueOccupied(startCoordinate)) return Pair(Coordinate(currentX, coordinate.y), direction)
                            return getNewCoordinateAndDirection(startCoordinate, 'D', numOfMoves - 1 - movesMade, map)
                        }
                    }
                }
                val nextX = currentX - 1
                if (currentLine.content[nextX] == '#') return Pair(Coordinate(currentX, coordinate.y), direction)
                currentX = nextX
                movesMade++
            }
            return Pair(Coordinate(currentX, coordinate.y), direction)
        }
        'U' -> {
            var movesMade = 0
            var currentY = coordinate.y
            while (movesMade < numOfMoves) {
                val isOnCubeEdge = currentY % sizeOfSide == 0
                if (isOnCubeEdge) {
                    val cubeSide = getCubeSide(coordinate.x, currentY, sizeOfSide)
                    val x = coordinate.x % sizeOfSide
                    when (cubeSide) {
                        CubeSide.BOTTOM, CubeSide.MIDDLE, CubeSide.BACK -> {} // No op, continue
                        CubeSide.TOP -> {
                            val startCoordinate = Coordinate(0, 3 * sizeOfSide + x)
                            if (map.isValueOccupied(startCoordinate)) return Pair(Coordinate(coordinate.x, currentY), direction)
                            return getNewCoordinateAndDirection(startCoordinate, 'R', numOfMoves - 1 - movesMade, map)
                        }
                        CubeSide.MIDDLE_RIGHT -> {
                            val startCoordinate = Coordinate(x, 4 * sizeOfSide - 1)
                            if (map.isValueOccupied(startCoordinate)) return Pair(Coordinate(coordinate.x, currentY), direction)
                            return getNewCoordinateAndDirection(startCoordinate, 'U', numOfMoves - 1 - movesMade, map)
                        }
                        CubeSide.MIDDLE_LEFT -> {
                            val startCoordinate = Coordinate(sizeOfSide, sizeOfSide + x)
                            if (map.isValueOccupied(startCoordinate)) return Pair(Coordinate(coordinate.x, currentY), direction)
                            return getNewCoordinateAndDirection(startCoordinate, 'R', numOfMoves - 1 - movesMade, map)
                        }
                    }
                }
                val nextY = currentY - 1
                val nextValue = map[nextY].content[coordinate.x]
                currentY = when (nextValue) {
                    '#' -> return Pair(Coordinate(coordinate.x, currentY), direction)
                    '.' -> nextY
                    else -> throw Error("Unexpected value!")
                }
                movesMade++
            }
            return Pair(Coordinate(coordinate.x, currentY), direction)
        }
        'D' -> {
            var movesMade = 0
            var currentY = coordinate.y
            while (movesMade < numOfMoves) {
                val isOnCubeEdge = (currentY + 1) % sizeOfSide == 0
                if (isOnCubeEdge) {
                    val cubeSide = getCubeSide(coordinate.x, currentY, sizeOfSide)
                    val x = coordinate.x % sizeOfSide
                    when (cubeSide) {
                        CubeSide.TOP, CubeSide.MIDDLE, CubeSide.MIDDLE_LEFT -> {} // No op, continue
                        CubeSide.MIDDLE_RIGHT -> {
                            val startCoordinate = Coordinate(2 * sizeOfSide - 1, sizeOfSide + x)
                            if (map.isValueOccupied(startCoordinate)) return Pair(Coordinate(coordinate.x, currentY), direction)
                            return getNewCoordinateAndDirection(startCoordinate, 'L', numOfMoves - 1 - movesMade, map)
                        }
                        CubeSide.BOTTOM -> {
                            val startCoordinate = Coordinate(sizeOfSide - 1, 3 * sizeOfSide + x)
                            if (map.isValueOccupied(startCoordinate)) return Pair(Coordinate(coordinate.x, currentY), direction)
                            return getNewCoordinateAndDirection(startCoordinate, 'L', numOfMoves - 1 - movesMade, map)
                        }
                        CubeSide.BACK -> {
                            val startCoordinate = Coordinate(2 * sizeOfSide + x, 0)
                            if (map.isValueOccupied(startCoordinate)) return Pair(Coordinate(coordinate.x, currentY), direction)
                            return getNewCoordinateAndDirection(startCoordinate, 'D', numOfMoves - 1 - movesMade, map)
                        }
                    }
                }
                val nextY = currentY + 1
                val nextValue = map[nextY].content[coordinate.x]
                currentY = when (nextValue) {
                    '#' -> return Pair(Coordinate(coordinate.x, currentY), direction)
                    '.' -> nextY
                    else -> throw Error("Unexpected value: $nextValue, at coordinate: $currentY")
                }
                movesMade++
            }
            return Pair(Coordinate(coordinate.x, currentY), direction)
        }
        else -> throw Error("Unexpected direction: $direction")
    }
}

private fun getNewCoordinate(coordinate: Coordinate, direction: Char, numOfMoves: Int, map: List<MapLine>): Coordinate {
    when (direction) {
        'R' -> {
            val currentLine = map[coordinate.y]
            var movesMade = 0
            var currentX = coordinate.x
            while (movesMade < numOfMoves) {
                val nextX = if (currentX + 1 > currentLine.endIndex) currentLine.startIndex else currentX + 1
                if (currentLine.content[nextX] == '#') {
                    return Coordinate(currentX, coordinate.y)
                }
                currentX = nextX
                movesMade++
            }
            return Coordinate(currentX, coordinate.y)
        }
        'L' -> {
            val currentLine = map[coordinate.y]
            var movesMade = 0
            var currentX = coordinate.x
            while (movesMade < numOfMoves) {
                val nextX = if (currentX - 1 < currentLine.startIndex) currentLine.endIndex else currentX - 1
                if (currentLine.content[nextX] == '#') return Coordinate(currentX, coordinate.y)
                currentX = nextX
                movesMade++
            }
            return Coordinate(currentX, coordinate.y)
        }
        'U' -> {
            var movesMade = 0
            var currentY = coordinate.y
            while (movesMade < numOfMoves) {
                val nextY = if (currentY - 1 < 0) getNewCoordinateIfEndOfMapAtTop(
                    map,
                    Coordinate(coordinate.x, currentY)
                ).y else currentY - 1
                val nextValue = map.getValueAt(coordinate.x, nextY)
                currentY = when (nextValue) {
                    '#' -> return Coordinate(coordinate.x, currentY)
                    ' ' -> getNewCoordinateIfEndOfMapAtTop(map, Coordinate(coordinate.x, currentY)).y
                    '.' -> nextY
                    else -> throw Error("Unexpected value!")
                }
                movesMade++
            }
            return Coordinate(coordinate.x, currentY)
        }
        'D' -> {
            var movesMade = 0
            var currentY = coordinate.y
            while (movesMade < numOfMoves) {
                val nextY = if (currentY + 1 >= map.size) getNewCoordinateIfEndOfMapAtBottom(
                    map,
                    Coordinate(coordinate.x, currentY)
                ).y else currentY + 1
                val nextValue = map.getValueAt(coordinate.x, nextY)
                currentY = when (nextValue) {
                    '#' -> return Coordinate(coordinate.x, currentY)
                    ' ' -> getNewCoordinateIfEndOfMapAtBottom(map, Coordinate(coordinate.x, currentY)).y
                    '.' -> nextY
                    else -> throw Error("Unexpected value!")
                }
                movesMade++
            }
            return Coordinate(coordinate.x, currentY)
        }
        else -> throw Error("Unexpected direction: $direction")
    }
}

private fun PointMap.getValueAt(x: Int, y: Int): Char {
    val maxXForY = this[y].content.size - 1
    if (maxXForY < x) return ' '
    return this[y].content[x]
}

private fun PointMap.isValueOccupied(coordinate: Coordinate): Boolean {
    return this[coordinate.y].content[coordinate.x] == '#'
}

private fun getNewCoordinateIfEndOfMapAtBottom(
    map: List<MapLine>,
    coordinate: Coordinate,
): Coordinate {
    var currentIndex = coordinate.y
    var nextChar = map[currentIndex - 1].content[coordinate.x]
    while (nextChar != ' ' && currentIndex > 0) {
        currentIndex--
        if (currentIndex == 0) {
            break
        }
        nextChar = map.getValueAt(coordinate.x, currentIndex - 1)
    }
    if (map[currentIndex].content[coordinate.x] == '#') {
        return coordinate // don;t move
    }
    return Coordinate(coordinate.x, currentIndex)
}

private fun getNewCoordinateIfEndOfMapAtTop(
    map: List<MapLine>,
    coordinate: Coordinate,
): Coordinate {
    var currentIndex = coordinate.y
    val maxIndex = map.size - 1
    var nextChar = map[currentIndex + 1].content[coordinate.x]
    while (nextChar != ' ' && currentIndex < maxIndex) {
        currentIndex++
        if (currentIndex == map.size - 1) return Coordinate(coordinate.x, currentIndex) // if reaching the end, return this
        nextChar = map.getValueAt(coordinate.x, currentIndex + 1)
    }
    if (map[currentIndex].content[coordinate.x] == '#') {
        return coordinate // don;t move
    }
    return Coordinate(coordinate.x, currentIndex)
}

private fun getData(input: List<String>): Input {
    val mapContent = input[0].split(System.lineSeparator()).map {
        val content = it.toCharArray().toList()
        val firstEl = content.indexOfFirst { c -> c != ' ' }
        val lastEl = content.indexOfLast { c -> c != ' ' }
        MapLine(content, lastEl - firstEl + 1, firstEl, lastEl)
    }
    return Input(mapContent, input[1].toInstructions())
}

private fun String.toInstructions(): List<Instruction> {
    val instructions = mutableListOf<Instruction>()
    var stringLeftToProcess = this
    while(stringLeftToProcess.isNotEmpty()) {
        val nonDigitIndex = stringLeftToProcess.indexOfFirst { !it.isDigit() }
        if (nonDigitIndex == 0) {
            throw Error("Unexpected case when parsing")
        } else if (nonDigitIndex > 0) {
            val number = stringLeftToProcess.substring(0, nonDigitIndex).toInt()
            instructions.add(Instruction(number, stringLeftToProcess[nonDigitIndex]))
            stringLeftToProcess = stringLeftToProcess.substring(nonDigitIndex + 1)
        } else {
            instructions.add(Instruction(stringLeftToProcess.toInt(), null))
            stringLeftToProcess = ""
        }
    }
    return instructions
}
