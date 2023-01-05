package dec18

import common.Limits
import common.readDraftLines
import common.readLines

const val DAY = 18

data class DimensionLimits(
    val x: Limits,
    val y: Limits,
    val z: Limits
)

data class Cube(
    val x: Int,
    val y: Int,
    val z: Int
) {
    fun getNumberOfSidesNotInSet(set: Set<Cube>): Int {
        val sideCubes = getSurroundingCubes()
        return sideCubes.size - sideCubes.intersect(set).size
    }

    fun getNumberOfSidesInSet(cubes: Set<Cube>): Int {
        return getSurroundingCubes().count { cubes.contains(it) }
    }

    fun getSurroundingCubes(): Set<Cube> {
        return setOf(
            Cube(x + 1, y, z),
            Cube(x - 1, y, z),
            Cube(x, y + 1, z),
            Cube(x, y - 1, z),
            Cube(x, y, z + 1),
            Cube(x, y, z - 1),
        )
    }
}

fun main() {
    val draft = readDraftLines(DAY)
    val input = readLines(DAY)

    solvePart1(draft) // 64
    solvePart1(input) // 3390

    solvePart2(draft) // 58
    solvePart2(input) // 2058
}

private fun solvePart1(input: List<String>) {
    val cubes = getData(input)
    val surfaceArea = cubes.sumOf { it.getNumberOfSidesNotInSet(cubes.toSet()) }
    println("Part 1 answer: $surfaceArea")
}

private fun solvePart2(input: List<String>) {
    val cubes = getData(input)
    val dimensionLimits = getDimensionLimits(cubes)
    val waterCubes = getWaterCubes(cubes.toSet(), dimensionLimits)
    val numberOfCubesTouchingWater = cubes.sumOf { it.getNumberOfSidesInSet(waterCubes) }
    println("Part 2 answer: $numberOfCubesTouchingWater")
}

private fun getDimensionLimits(cubes: List<Cube>): DimensionLimits {
    val xCoordinates = cubes.map { it.x }
    val yCoordinates = cubes.map { it.y }
    val zCoordinates = cubes.map { it.z }
    return DimensionLimits(
        Limits(xCoordinates.minOf { it } - 1, xCoordinates.maxOf { it } + 1),
        Limits(yCoordinates.minOf { it } - 1, yCoordinates.maxOf { it } + 1),
        Limits(zCoordinates.minOf { it } - 1, zCoordinates.maxOf { it } + 1),
    )
}

private fun getWaterCubes(cubes: Set<Cube>, dimensionLimits: DimensionLimits): Set<Cube> {
    val allWaterCubes = mutableSetOf<Cube>()

    val minX = dimensionLimits.x.min!!
    val minY = dimensionLimits.y.min!!
    val minZ = dimensionLimits.z.min!!

    val startCube = Cube(minX, minY, minZ)
    val cubesToCheck = ArrayDeque<Cube>()
    cubesToCheck.add(startCube)

    while (cubesToCheck.isNotEmpty()) {
        val currentCube = cubesToCheck.removeFirst()
        val waterCubes = getAllSurroundingWaterCubes(cubes + allWaterCubes, dimensionLimits, currentCube)
        allWaterCubes.addAll(waterCubes)
        cubesToCheck.addAll(waterCubes)
    }

    return allWaterCubes
}

private fun getAllSurroundingWaterCubes(knownCubes: Set<Cube>, dimensionLimits: DimensionLimits, waterCube: Cube): List<Cube> {
    val (minX, maxX) = dimensionLimits.x
    val (minY, maxY) = dimensionLimits.y
    val (minZ, maxZ) = dimensionLimits.z

    val surroundingCubesInLimits = waterCube.getSurroundingCubes().toList().filter { it.x in minX!!..maxX!! && it.y in minY!!..maxY!! && it.z in minZ!!..maxZ!! }
    return surroundingCubesInLimits.filter { !knownCubes.contains(it) }
}

private fun getData(input: List<String>): List<Cube> {
    return input.map {
        val (x, y, z) = it.split(",")
        Cube(x.toInt(), y.toInt(), z.toInt())
    }
}
