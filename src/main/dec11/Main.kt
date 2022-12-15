package dec11

import common.*

const val DAY = 11

private data class Operation(
    val toApply: String, // int or "old"
    val operator: String
)

private data class DivisionTest(
    val divider: Long,
    val throwIfTrue: Int,
    val throwIfFalse: Int,
)

private data class Monkey(
    val id: Int,
    val startingItems: List<Long>,
    val operation: Operation,
    val divisionTest: DivisionTest
)

fun main() {
    val monkeys = getData()

    // 61005
    solvePart1(monkeys)

    // 20567144694
    solvePart2(monkeys)
}

private fun solvePart1(monkeys: List<Monkey>) {
    val monkeyBusiness = getMonkeyBusiness(monkeys, 20, 3)
    println("Part 1 answer: $monkeyBusiness")
}

private fun solvePart2(monkeys: List<Monkey>) {
    val monkeyBusiness = getMonkeyBusiness(monkeys, 10000)
    println("Part 1 answer: $monkeyBusiness")
}

private fun getMonkeyBusiness(monkeys: List<Monkey>, numberOfRounds: Int, worryDivider: Int? = null): Long {
    val currentItems = monkeys.associate { it.id to it.startingItems.toMutableList() }
    val numberOfItemsInspected = monkeys.associate { it.id to 0L }.toMutableMap()
    val primeMultiplier = monkeys.map { it.divisionTest.divider }.reduce { acc, num -> acc * num }

    repeat(numberOfRounds) {
        monkeys.forEach { m ->
            val items = currentItems[m.id]
            items!!.forEach { item ->
                val newWorry = if (worryDivider != null) {
                    calculateNewWorry(item, m.operation) / worryDivider
                } else {
                    calculateNewWorry(item, m.operation) % primeMultiplier
                }
                numberOfItemsInspected[m.id] = numberOfItemsInspected[m.id]!! + 1
                val monkeyToThrow = if (newWorry % m.divisionTest.divider == 0L) m.divisionTest.throwIfTrue else m.divisionTest.throwIfFalse
                currentItems[monkeyToThrow]!!.add(newWorry)
            }
            currentItems[m.id]!!.clear()
        }
    }

    val monkeysByActivity = numberOfItemsInspected.toList().sortedByDescending { it.second }
    return monkeysByActivity[0].second * monkeysByActivity[1].second
}

private fun calculateNewWorry(old: Long, operation: Operation): Long {
    return when (operation.operator) {
        "+" -> {
            val numToAdd = if (operation.toApply == "old") old else operation.toApply.toLong()
            old + numToAdd
        }
        "*" -> {
            val numToMultiply = if (operation.toApply == "old") old else operation.toApply.toLong()
            old * numToMultiply
        }
        else -> throw Error("Unknown op")
    }
}

private fun getData(): List<Monkey> {
    val input = readLineGroups(DAY)
    return input.map {
        val lines = it.split(System.lineSeparator())

        val id = lines[0].substring(7, 8).toInt()
        val startingItems = lines[1].split("items: ")[1]
            .split(", ")
            .map { i -> i.toLong() }

        val operationLine = lines[2].split("new = old ")[1].split(" ")
        val operation = Operation(operationLine[1], operationLine[0])

        val testDivisionInt = lines[3].split("divisible by ")[1].toLong()
        val ifTrueValue = lines[4].split("throw to monkey ")[1].toInt()
        val ifFalseValue = lines[5].split("throw to monkey ")[1].toInt()
        val divisionTest = DivisionTest(testDivisionInt, ifTrueValue, ifFalseValue)

        Monkey(id, startingItems, operation, divisionTest)
    }
}
