package common

import java.io.File

fun readLines(day: Int): List<String> {
    return File("C:/Coding/aoc/aoc2023/src/main/dec$day/input.txt").readLines()
}

fun readDraftLines(day: Int): List<String> {
    return File("C:/Coding/aoc/aoc2023/src/main/dec$day/draft.txt").readLines()
}

fun readLineGroups(day: Int): List<String> {
    return File("C:/Coding/aoc/aoc2023/src/main/dec$day/input.txt")
        .readText()
        .split(System.lineSeparator().repeat(2))
}

fun readDraftGroups(day: Int): List<String> {
    return File("C:/Coding/aoc/aoc2023/src/main/dec$day/draft.txt")
        .readText()
        .split(System.lineSeparator().repeat(2))
}
