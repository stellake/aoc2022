package dec5

import common.readLineGroups

data class Instruction(val quantity: Int, val fromStackId: Int, val toStackId: Int)

data class PuzzleInput(
    val stacks: Map<Int, ArrayDeque<String>>,
    val instructions: List<Instruction>
)

fun main() {
    // QMBMJDFTD
    solvePart1()

    // NBTVTJNFJ
    solvePart2()
}

fun solvePart1() {
    val data = getData()

    val stacks = data.stacks.toMutableMap()
    data.instructions.forEach {
        // last in first out
        val fromStackId = it.fromStackId
        val toStackId = it.toStackId
        repeat(it.quantity) {
            val elementToRemove = stacks[fromStackId]!!.removeLast()
            stacks[toStackId]!!.addLast(elementToRemove)
        }
    }

    val answer = getTopStackString(stacks)
    println("Answer: $answer")
}

fun solvePart2() {
    val data = getData()

    val stacks = data.stacks.toMutableMap()
    data.instructions.forEach {
        // last in first out
        val fromStackId = it.fromStackId
        val toStackId = it.toStackId

        val tempStack = ArrayDeque<String>()
        repeat(it.quantity) {
            val elementToRemove = stacks[fromStackId]!!.removeLast()
            tempStack.addLast(elementToRemove)
        }

        repeat(it.quantity) {
            stacks[toStackId]!!.addLast(tempStack.removeLast())
        }
    }

    val answer = getTopStackString(stacks)
    println("Answer: $answer")
}

private fun getTopStackString(stacks: Map<Int, ArrayDeque<String>>): String {
    return stacks.map { it.value.last().substring(1, 2) }.joinToString("")
}

private fun getData(): PuzzleInput {
    val data = readLineGroups(5)
    val stacks = data[0].toStacks()
    val instructions = data[1].split(System.lineSeparator()).map(String::toInstruction)
    return PuzzleInput(stacks, instructions)
}

private fun String.toStacks(): Map<Int, ArrayDeque<String>> {
    val lines = this.split(System.lineSeparator())
    val stackIds = lines.last().split(" ").map { it.trim() }.filter { it.isNotBlank() }.map { it.toInt() }
    val stacks = stackIds.associateWith { ArrayDeque<String>() }.toMutableMap()
    lines.forEachIndexed { lineIndex, line ->
        val isLineWithStackIds = lineIndex == lines.size - 1
        if (!isLineWithStackIds) {
            addElementsToStack(stackIds, line, stacks)
        }
    }
    return stacks
}

private fun addElementsToStack(
    stackIds: List<Int>,
    line: String,
    stacks: MutableMap<Int, ArrayDeque<String>>
) {
    stackIds.forEachIndexed { index, id ->
        val start = index * 4
        val end = start + 3
        if (line.length > start) {
            val stackItem = line.substring(start, end) // Elements noted as blank if missing
            if (stackItem.isNotBlank()) {
                // We are processing from last to first
                stacks[id]!!.addFirst(line.substring(start, end))
            }
        }
    }
}

private fun String.toInstruction(): Instruction {
    val values = this.split("move ", " from ", " to ")
    val quantity = values[1].toInt()
    val from = values[2].toInt()
    val to = values[3].toInt()
    return Instruction(quantity, from, to)
}
