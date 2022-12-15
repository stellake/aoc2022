package dec7

import common.readLines

data class PuzzleFile(val size: Int, val name: String)
data class Directory(
    val id: Int,
    val name: String,
    val files: MutableList<PuzzleFile> = mutableListOf(),
    val previousDirectoryId: Int? = null,
    var nextDirectories: MutableList<Directory> = mutableListOf(),
) {
    fun getTotalFileSize(): Int {
        return files.sumOf { it.size }
    }
}

fun main() {
    // 1428881
    solvePart1()

    // 10475598
    solvePart2()
}

fun solvePart1() {
    val allDirectories = buildDirectories()
    val allDirSizes = getDirSizeMap(allDirectories)
    val answer = allDirSizes.values.filter { it < 100000  }.sum()
    println("Part 1 answer: $answer")
}

fun solvePart2() {
    val allDirectories = buildDirectories()
    val sizes = getDirSizeMap(allDirectories)

    val totalSpace = 70000000
    val spaceNeeded = 30000000
    val spaceUsed = sizes[0]!!

    val currentUnusedSpace = totalSpace - spaceUsed
    val moreSpaceNeeded = spaceNeeded - currentUnusedSpace

    val smallestDirectoryToDelete = sizes.values.filter { it >= moreSpaceNeeded }.min()
    println("Part two answer: $smallestDirectoryToDelete")
}

private fun getFileSizeOfAllSubDirs(dir: Directory, allDirs: Map<Int, Directory>): Int {
    return dir.nextDirectories.sumOf { it.getTotalFileSize() + getFileSizeOfAllSubDirs(it, allDirs) }
}

private fun getDirSizeMap(allDirs: Map<Int, Directory>): Map<Int, Int> {
    return buildMap {
        allDirs.values.forEach { d ->
            val size = d.getTotalFileSize() + getFileSizeOfAllSubDirs(d, allDirs)
            put(d.id, size)
        }
    }
}

private fun buildDirectories(): Map<Int, Directory> {
    val input = readLines(7)
    var currentDirectory = Directory(0, "root")
    val allDirectories = mutableMapOf(0 to currentDirectory)
    var newDirId = 1

    fun buildNewDirectory(newDirName: String): Directory {
        val dirToCreate = Directory(newDirId, newDirName, previousDirectoryId = currentDirectory.id)
        allDirectories[newDirId] = dirToCreate
        allDirectories[currentDirectory.id]!!.nextDirectories.add(dirToCreate)
        newDirId++
        return dirToCreate
    }

    input.forEach { line ->
        when {
            line == "$ ls" -> { } // No op
            line == "$ cd .." -> currentDirectory = allDirectories[currentDirectory.previousDirectoryId]!!
            line == "$ cd /" -> currentDirectory = allDirectories[0]!!
            line.startsWith("$ cd ") -> {
                val secondPart = line.split("$ cd ")[1]
                val nextDirectory = currentDirectory.nextDirectories.filter { it.name == secondPart }
                currentDirectory = if (nextDirectory.isNotEmpty()) {
                    allDirectories[nextDirectory[0].id]!!
                } else {
                    buildNewDirectory(secondPart)
                }
            }
            line.startsWith("dir ") -> {
                val newDirName = line.split("dir ")[1]
                buildNewDirectory(newDirName)
            }
            else -> {
                val parts = line.split(" ")
                val newFile = PuzzleFile(parts[0].toInt(), parts[1])
                currentDirectory.files.add(newFile)
            }
        }
    }

    return allDirectories
}
