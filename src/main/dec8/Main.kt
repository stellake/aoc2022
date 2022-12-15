package dec8

import common.*

data class Tree(
    val coordinate: Coordinate,
    val height: Int,
    var isVisible: Boolean = false,
    var scenicScore: Int? = null
)

data class LocationDetails(
    val isVisible: Boolean,
    val scenicScore: Int,
)

typealias Trees = List<List<Tree>>

fun main() {
    val trees = getData()

    // 1538
    solvePart1(trees)

    // 496125
    solvePart2(trees)
}

fun solvePart1(trees: Trees) {
    val noOfVisibleTrees = trees.flatten().map { isTreeVisible(it, trees) }.count { it }
    println("Part 1 answer $noOfVisibleTrees")
}

fun solvePart2(trees: Trees) {
    val maxScenicScore = trees.flatten().maxOfOrNull { getScenicScore(it, trees) }
    println("Part 2 answer $maxScenicScore")
}

private fun getScenicScore(tree: Tree, data: Trees): Int {
    var scenicScore = 1
    val coordinateChanges = getHorizontalCoordinateChanges() + getVerticalCoordinateChanges()
    coordinateChanges.forEach {
        val score = inspectSurroundings(tree,data, it).scenicScore
        scenicScore *= score
    }
    return scenicScore
}

private fun isTreeVisible(tree: Tree, data: Trees): Boolean {
    val coordinateChanges = getHorizontalCoordinateChanges() + getVerticalCoordinateChanges()
    coordinateChanges.forEach {
        if (inspectSurroundings(tree, data, it).isVisible) {
            return true
        }
    }
    return false
}

private fun inspectSurroundings(tree: Tree, trees: Trees, coordinateChange: CoordinateChange): LocationDetails {
    val startCoordinates = tree.coordinate

    var x = startCoordinates.x + coordinateChange.x
    var y = startCoordinates.y + coordinateChange.y

    var score = 0
    while (x < trees[0].size && y < trees.size && x >= 0 && y >= 0) {
        score ++
        val treeAtNextCoordinate = trees[y][x]
        if (treeAtNextCoordinate.height >= tree.height) {
            return LocationDetails(false, score)
        }

        x += coordinateChange.x
        y += coordinateChange.y
    }

    return LocationDetails(true, score)
}

private fun getData(): Trees {
    val input = readLines(8)
    return input.mapIndexed { col, line ->
        line.toCharArray().mapIndexed { row, treeHeight ->
            Tree(Coordinate(row, col), treeHeight.toString().toInt())
        }
    }
}
