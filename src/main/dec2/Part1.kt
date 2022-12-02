package dec2

import common.readLines

// 10310
fun main() {
    val data = getData()
    val score = data.sumOf { it.getRoundScore() }
    println("Score is $score")
}

fun getDec2Data(): List<String> {
    return readLines(2)
}

fun getOpponentMove(instruction: String): Move {
    when(instruction) {
        "A" -> return Move.ROCK
        "B" -> return Move.PAPER
        "C" -> return Move.SCISSORS
    }
    throw Error("Invalid instruction")
}

private fun getData(): List<GameRound> {
    return getDec2Data().map { line ->
        val instructions = line.split(' ')
        val opponentMove = getOpponentMove(instructions[0])
        val myMove = getMyMove(instructions[1])
        GameRound(opponentMove, myMove)
    }
}

private fun getMyMove(instruction: String): Move {
    when(instruction) {
        "X" -> return Move.ROCK
        "Y" -> return Move.PAPER
        "Z" -> return Move.SCISSORS
    }
    throw Error("Invalid instruction")
}
