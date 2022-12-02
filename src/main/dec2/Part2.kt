package dec2

// 14859
fun main() {
    val data = getData()
    val score = data.sumOf { it.getRoundScore() }
    println("Score is $score")
}

private fun getData(): List<GameRound> {
    return getDec2Data().map { line ->
        val instructions = line.split(' ')
        val opponentMove = getOpponentMove(instructions[0])
        val myMove = getMyMove(opponentMove, instructions[1])
        GameRound(opponentMove, myMove)
    }
}

fun getMyMove(opMove: Move, instruction: String): Move {
    when (instruction) {
        "X" -> {
            // Lose the round
            if (opMove == Move.ROCK) return Move.SCISSORS
            if (opMove == Move.PAPER) return Move.ROCK
            return Move.PAPER
        }
        "Y" -> return opMove // Get a draw
        "Z" -> {
            // Win the round
            if (opMove == Move.ROCK) return Move.PAPER
            if (opMove == Move.SCISSORS) return Move.ROCK
            return Move.SCISSORS
        }
    }
    throw Error("Invalid instruction")
}
