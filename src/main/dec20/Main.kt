package dec20

import common.readDraftLines
import common.readLines
import kotlin.math.abs

const val DAY = 20

data class ValueToMix(
    val id: Int,
    val number: Long,
    var prevValueId: Int,
    var nextValueId: Int,
)

fun main() {
    val draft = readDraftLines(DAY)
    val input = readLines(DAY)

    solvePart1(draft) // 3
    solvePart1(input) // 2827

    solvePart2(draft) // 1623178306
    solvePart2(input) // 7834270093909
}


private fun solvePart1(input: List<String>) {
    val mixValues = getData(input)
    executeRoundOfMixin(mixValues)
    println("Part 1 answer: ${findSumOfGroveCoordinates(mixValues)}")
}

private fun solvePart2(input: List<String>) {
    val decryptionKey = 811589153L
    val mixValues = getData(input).map { ValueToMix(it.id, it.number * decryptionKey, it.prevValueId, it.nextValueId) }
    repeat(10) {
        executeRoundOfMixin(mixValues)
    }
    println("Part 2 answer: ${findSumOfGroveCoordinates(mixValues)}")
}

private fun executeRoundOfMixin(mixValues: List<ValueToMix>) {
    val numberOfMixValues = mixValues.size
    for (i in 0..mixValues.lastIndex) {
        val element = mixValues[i]
        val elementValue = element.number
        val timesToRepeat = abs(elementValue) % (numberOfMixValues - 1)
        repeat(timesToRepeat.toInt()) {
            if (elementValue > 0) {
                shiftElementRight(mixValues, element)
            } else {
                shiftElementLeft(mixValues, element)
            }
        }
    }
}

private fun shiftElementLeft(mixValues: List<ValueToMix>, element: ValueToMix) {
    val nextElement = mixValues[element.nextValueId]
    val prevElement = mixValues[element.prevValueId]
    val elementAfterPrev = mixValues[prevElement.prevValueId]
    prevElement.nextValueId = nextElement.id
    prevElement.prevValueId = element.id
    nextElement.prevValueId = prevElement.id
    elementAfterPrev.nextValueId = element.id
    element.nextValueId = prevElement.id
    element.prevValueId = elementAfterPrev.id
}

private fun shiftElementRight(mixValues: List<ValueToMix>, element: ValueToMix) {
    val nextElement = mixValues[element.nextValueId]
    val elementAfterNext = mixValues[nextElement.nextValueId]
    val prevElement = mixValues[element.prevValueId]

    prevElement.nextValueId = nextElement.id
    nextElement.prevValueId = prevElement.id
    nextElement.nextValueId = element.id
    element.prevValueId = nextElement.id
    element.nextValueId = elementAfterNext.id
    elementAfterNext.prevValueId = element.id
}

private fun findSumOfGroveCoordinates(mixValues: List<ValueToMix>): Long {
    val elementToStartFrom = mixValues.find { it.number == 0L } ?: throw Error("No element with value 0")
    val numberOfElements = mixValues.size

    val firstIndex = 1000 % numberOfElements
    val item1 = findNthNextElement(firstIndex, elementToStartFrom, mixValues)

    val secondIndex = 2000 % numberOfElements
    val item2 = findNthNextElement(secondIndex, elementToStartFrom, mixValues)

    val thirdIndex = 3000 % numberOfElements
    val item3 = findNthNextElement(thirdIndex, elementToStartFrom, mixValues)

    return item1 + item2 + item3
}

private fun findNthNextElement(n: Int, startElement: ValueToMix, mixValues: List<ValueToMix>): Long {
    var currentElement = startElement
    repeat(n) {
        currentElement = mixValues[currentElement.nextValueId]
    }
    return currentElement.number
}

private fun getData(input: List<String>): List<ValueToMix> {
    val lastIndex = input.lastIndex
    return input.mapIndexed { index, mixValueString ->
        ValueToMix(
            index,
            mixValueString.toLong(),
            if (index == 0) lastIndex else index - 1,
            if (index == lastIndex) 0 else index + 1
        )
    }
}
