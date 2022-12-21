package dec21

import common.readDraftLines
import common.readLines

const val DAY = 21

private data class OperationData(
    val firstMonkey: String,
    val secondMonkey: String,
    val operation: String
)

private data class Monkey(
    val name: String,
    val numberToYell: Long? = null,
    val operationData: OperationData? = null
)

fun main() {
    val draft = readDraftLines(DAY)
    val input = readLines(DAY)

    solvePart1(draft) // 152
    solvePart1(input) // 82225382988628

    solvePart2(draft) // 301
    solvePart2(input) // 3429411069028
}


private fun solvePart1(input: List<String>) {
    val data = getData(input)
    val monkeys = data.associateBy { it.name }
    val start = data.find { it.name == "root" }!!
    val answer = getYelledNumber(start, monkeys)
    println("Part 1 answer: $answer")
}

private fun solvePart2(input: List<String>) {
    val monkeyNameToExclude = "humn"
    val data = getData(input)
    val monkeys = data.associateBy { it.name }
    val start = data.find { it.name == "root" }!!
    val firstValue = monkeys[start.operationData!!.firstMonkey]!!
    val secondValue = monkeys[start.operationData.secondMonkey]!!

    val val1 = getYelledNumber(firstValue, monkeys, monkeyNameToExclude)
    val val2 = getYelledNumber(secondValue, monkeys, monkeyNameToExclude)

    if (val1 == null) {
        val answer = getValueYelledByMonkey(val2!!, firstValue, monkeys, monkeyNameToExclude)
        println("Part 2 answer: $answer")
    } else {
        val answer = getValueYelledByMonkey(val1, secondValue, monkeys, monkeyNameToExclude)
        println("Part 2 answer: $answer")
    }
}

private fun getValueYelledByMonkey(total: Long, monkey: Monkey, monkeys: Map<String, Monkey>, monkeyNameToLook: String): Long {
    if (monkey.name == "humn") return total

    val firstValue = monkeys[monkey.operationData!!.firstMonkey]!!
    val secondValue = monkeys[monkey.operationData.secondMonkey]!!
    val val1 = getYelledNumber(firstValue, monkeys, monkeyNameToLook)
    val val2 = getYelledNumber(secondValue, monkeys, monkeyNameToLook)

    val newTotal = getNewTotal(total, monkey.operationData.operation, val1, val2)
    return if (val1 == null) {
        getValueYelledByMonkey(newTotal, firstValue, monkeys, monkeyNameToLook)
    } else {
        getValueYelledByMonkey(newTotal, secondValue, monkeys, monkeyNameToLook)
    }
}

private fun getNewTotal(currentTotal: Long, operation: String, num1: Long?, num2: Long?): Long {
    return when (operation) {
        "+" -> return currentTotal - (num1 ?: num2 ?: throw Error("Both nums null"))
        "-" -> return if (num1 == null) currentTotal + num2!! else num1 - currentTotal
        "*" -> return currentTotal / (num1 ?: num2 ?: throw Error("Both nums null"))
        "/" -> return if (num1 == null) currentTotal * num2!! else num1 / currentTotal
        else -> throw Error("New total!!")
    }
}

private fun getYelledNumber(monkey: Monkey, monkeys: Map<String, Monkey>, monkeyNameToExclude: String? = null): Long? {
    if (monkey.name == monkeyNameToExclude) return null
    if (monkey.numberToYell != null) return monkey.numberToYell

    val monkey1 = monkeys[monkey.operationData!!.firstMonkey]!!
    val number1 = getYelledNumber(monkey1, monkeys, monkeyNameToExclude) ?: return null
    val monkey2 = monkeys[monkey.operationData.secondMonkey]!!
    val number2 = getYelledNumber(monkey2, monkeys, monkeyNameToExclude) ?: return null

    when (monkey.operationData.operation) {
        "+" -> return number1 + number2
        "-" -> return number1 - number2
        "*" -> return number1 * number2
        "/" -> return number1 / number2
    }

    throw Error("Unexpected case!")
}

private fun getData(input: List<String>): List<Monkey> {
    return input.map {
        val (name, actionString) = it.split(": ")
        if (actionString.toLongOrNull() == null) {
            val (firstMonkey, operator, secondMonkey) = actionString.split(" ")
            Monkey(name, operationData = OperationData(firstMonkey, secondMonkey, operator))
        } else {
            Monkey(name, numberToYell = actionString.toLong())
        }
    }
}
