package common

import java.io.File

fun readLines(day: Int): List<String> {
    return File("C:/Coding/aoc/aoc2023/src/main/dec$day/input.txt").readLines()
}

fun readLineGroups(day: Int): List<String> {
    return File("C:/Coding/aoc/aoc2023/src/main/dec$day/input.txt")
        .readText()
        .split(System.lineSeparator().repeat(2))
}
