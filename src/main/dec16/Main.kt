package dec16

import common.readDraftLines
import common.readLines

const val DAY = 16

data class Valve(
    val id: String,
    val flowRate: Int,
    val leadingToValves: List<String>
)

data class LeadingToValve(
    val valveId: String,
    val minutesTaken: Int,
)

fun main() {
    val draft = readDraftLines(DAY)
    val input = readLines(DAY)

    // 2119
//    solvePart1(draft)
//    solvePart1(input)

    //
//    solvePart2(draft)
    solvePart2(input)
}

private fun solvePart1(input: List<String>) {
    val data = getData(input)
    val valveById = data.associateBy { it.id }
    val allFlowRates = data.sortedByDescending { it.flowRate }.map { it.flowRate }

    val numberOfMinutes = 30
    val numberOfMinutesToMove = 1
    val numberOfMinutesToOpen = 1

    val startValve = valveById["AA"]!!
    var maxPressure = 0
    var currentOptions = listOf(Option(emptyList(), startValve, 0, 0))
    while(currentOptions.isNotEmpty()) {
        val newOptionsToCheck = mutableSetOf<Option>()
        currentOptions.forEach {
            val newOptions = buildNextOptions(it, valveById)
            newOptions.forEach { newOption ->
                if (newOption.usedMinutes == 30 && newOption.flowRateReleased > maxPressure) {
                    maxPressure = newOption.flowRateReleased
                }
            }
            newOptionsToCheck.addAll(newOptions)
        }
        val usedMinutes = currentOptions[0].usedMinutes
        if (usedMinutes in 10..29) {
            val maxOption = newOptionsToCheck.map { it.flowRateReleased }.maxOf { it }
            val min = maxOption - allFlowRates.take(30 - usedMinutes).sumOf { it }
            val newO = newOptionsToCheck.toList().filter { it.flowRateReleased >= min }
            currentOptions = newO
        } else {
            currentOptions = newOptionsToCheck.toList()
        }
    }

    println("Part 1 answer: $maxPressure")
}

private fun solvePart2(input: List<String>) {
    val data = getData(input)
    val valveById = data.associateBy { it.id }
    val allFlowRates = data.sortedByDescending { it.flowRate }.map { it.flowRate }

    val startValve = valveById["AA"]!!
    var maxPressure = 0
    var currentOptions = listOf(OptionPart2(emptyList(), startValve, startValve, 0, 0))
    while(currentOptions.isNotEmpty()) {
        val newOptionsToCheck = mutableSetOf<OptionPart2>()
        currentOptions.forEach {
            val newOptions = buildNextOptionsPart2(it, valveById)
            newOptions.forEach { newOption ->
                if (newOption.usedMinutes == 26 && newOption.flowRateReleased > maxPressure) {
                    maxPressure = newOption.flowRateReleased
                }
            }
            newOptionsToCheck.addAll(newOptions)
        }
        val usedMinutes = currentOptions[0].usedMinutes
        currentOptions = if (usedMinutes in 10..25) {
            val maxOption = newOptionsToCheck.map { it.flowRateReleased }.maxOf { it }
            val min = maxOption - allFlowRates.take(30 - usedMinutes).sumOf { it }
            val newO = newOptionsToCheck.toList().filter { it.flowRateReleased >= min }
            newO
        } else {
            newOptionsToCheck.toList()
        }
    }

    println("Part 2 answer: $maxPressure")
}

data class Option(
    var openedValveIds: List<String>,
    var currentValve: Valve,
    var usedMinutes: Int,
    var flowRateReleased: Int,
)

data class OptionPart2(
    var openedValveIds: List<String>,
    var currentValveMe: Valve,
    var currentValveElephant: Valve,
    var usedMinutes: Int,
    var flowRateReleased: Int,
) {
    fun toMyOption(): Option {
        return Option(openedValveIds, currentValveMe, usedMinutes, flowRateReleased)
    }

    fun toElephantOption(): Option {
        return Option(openedValveIds, currentValveElephant, usedMinutes, flowRateReleased)
    }
}

private fun buildNextOptionsPart2(optionPart2: OptionPart2, valvesById: Map<String, Valve>): List<OptionPart2> {
    val myOptions = buildNextOptions(optionPart2.toMyOption(), valvesById)
    val flowRateThisRound = optionPart2.openedValveIds.sumOf { valvesById[it]!!.flowRate }
    val newFlowRate = optionPart2.flowRateReleased + flowRateThisRound
    val elephantOptions = buildNextOptions(optionPart2.toElephantOption(), valvesById)
    val newOptions = mutableListOf<OptionPart2>()
    myOptions.forEach { myOption ->
        elephantOptions.forEach { elOption ->
            if (optionPart2.usedMinutes == 0 && elOption.currentValve.id == myOption.currentValve.id) {
                // No op
            } else {
                newOptions.add(
                    OptionPart2(
                        myOption.openedValveIds.toSet().union(elOption.openedValveIds.toSet()).toList(),
                        myOption.currentValve,
                        elOption.currentValve,
                        myOption.usedMinutes,
                        newFlowRate
                    )
                )
            }
        }
    }
    return newOptions
}

private fun buildNextOptions(option: Option, valvesById: Map<String, Valve>): List<Option> {
    if (option.usedMinutes >= 30) {
        return emptyList()
    }

    val flowRateThisRound = option.openedValveIds.sumOf { valvesById[it]!!.flowRate }
    val newFlowRate = option.flowRateReleased + flowRateThisRound
    val newMinutes = option.usedMinutes + 1

    val nextOptions = mutableListOf<Option>()
    val valve = option.currentValve
    if (!option.openedValveIds.contains(valve.id) && valve.flowRate != 0) {
        nextOptions.add(Option(option.openedValveIds + valve.id, valve, newMinutes, newFlowRate))
    }
    val nextIds = valve.leadingToValves
    nextIds.forEach {
        nextOptions.add(
            Option(option.openedValveIds, valvesById[it]!!, newMinutes, newFlowRate)
        )
    }
    return nextOptions.toList()
}

private fun getData(input: List<String>): List<Valve> {
    return input.map { line ->
        val parts = line.split("alve ", " has flow rate=", "; tunnels lead to valves ", "; tunnel leads to valve ")
        Valve(parts[1], parts[2].toInt(), parts[3].split(", "))
    }
}
