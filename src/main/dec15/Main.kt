package dec15

import common.Coordinate
import common.readDraftLines
import common.readLines
import kotlin.math.abs

const val DAY = 15

data class SensorData(
    val sensorCoordinate: Coordinate,
    val nearestBeaconCoordinate: Coordinate // per Manhattan distance
) {
    val manhattanDistance = sensorCoordinate.manhattanDistance(nearestBeaconCoordinate)

    fun getPointsAroundPerimeter(): List<Coordinate> {
        val minX = sensorCoordinate.x - manhattanDistance - 1
        val maxX = sensorCoordinate.x + manhattanDistance + 1
        var deltaY = 0
        val pointsAroundPerimeter = mutableListOf<Coordinate>()
        for (x in minX..maxX) {
            pointsAroundPerimeter.add(Coordinate(x, sensorCoordinate.y + deltaY))
            pointsAroundPerimeter.add(Coordinate(x, sensorCoordinate.y - deltaY))
            if (x <= sensorCoordinate.x) deltaY++ else deltaY--
        }
        return pointsAroundPerimeter
    }
}

fun main() {
    val draft = readDraftLines(DAY)
    val input = readLines(DAY)

    // 5112034
    solvePart1(draft, 10)
    solvePart1(input, 2000000)

    // 13172087230812
    solvePart2(draft, 20)
    solvePart2(input, 4000000)
}

private fun solvePart1(input: List<String>, yCoordinate: Int) {
    val data = getData(input)
    val xCoordinates = data.map { listOf(it.sensorCoordinate.x, it.nearestBeaconCoordinate.x) }.flatten()
    val maxX = xCoordinates.maxOf { it }
    val minX = xCoordinates.minOf { it }
    val deltaX = abs(maxX - minX) + 1
    var cannotContainABeaconCount = 0

    for (x in minX - deltaX..maxX + deltaX) {
        val cannotContainABeacon = !canContainBeacon(Coordinate(x, yCoordinate), data)
        if (cannotContainABeacon) {
            cannotContainABeaconCount++
        }
    }

    println("Part 1 answer: $cannotContainABeaconCount")
}

private fun solvePart2(input: List<String>, spaceSize: Int) {
    val data = getData(input)
    data.forEach { d ->
        val perimeterCoordinates = d.getPointsAroundPerimeter().filter { it.x in 0..spaceSize && it.y in 0..spaceSize }
        perimeterCoordinates.forEach { edge ->
            val cannotContainABeacon = !canContainBeacon(edge, data)
            if (!cannotContainABeacon) {
                println("Part 2 answer: ${4000000L * edge.x + edge.y}")
                return
            }
        }
    }
}

private fun canContainBeacon(c: Coordinate, data: List<SensorData>): Boolean {
    data.forEach {
        if (it.sensorCoordinate == c || it.nearestBeaconCoordinate == c) {
            // Not counting other sensors or beacons!!!
            return true
        }
    }
    data.forEach {
        if (c.manhattanDistance(it.sensorCoordinate) <= it.manhattanDistance ) {
            return false
        }
    }
    return true
}

private fun getData(input: List<String>): List<SensorData> {
    return input.map { line ->
        val parts = line.split("ensor at ", ": closest beacon is at ")
        val sensorCoordinates = parts[1].split("=", ", ")
        val beaconCoordinates = parts[2].split("=", ", ")
        SensorData(
            Coordinate(sensorCoordinates[1].toInt(), sensorCoordinates[3].toInt()),
            Coordinate(beaconCoordinates[1].toInt(), beaconCoordinates[3].toInt())
        )
    }
}
