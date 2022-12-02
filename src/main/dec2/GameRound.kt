package dec2

enum class Move {
    ROCK, PAPER, SCISSORS
}

data class GameRound(
    val opponentMove: Move,
    val myMove: Move,
) {
    fun getRoundScore(): Int {
        return getScoreForMove(this.myMove) + getScoreForResult(this)
    }

    private fun getScoreForMove(move: Move): Int {
        return when(move) {
            Move.ROCK -> 1
            Move.PAPER -> 2
            Move.SCISSORS -> 3
        }
    }

    private fun getScoreForResult(round: GameRound): Int {
        when (round.myMove) {
            Move.ROCK -> {
                if (round.opponentMove == Move.ROCK) return 3
                if (round.opponentMove == Move.PAPER) return 0
                return 6
            }

            Move.PAPER -> {
                if (round.opponentMove == Move.PAPER) return 3
                if (round.opponentMove == Move.SCISSORS) return 0
                return 6
            }

            Move.SCISSORS -> {
                if (round.opponentMove == Move.SCISSORS) return 3
                if (round.opponentMove == Move.ROCK) return 0
                return 6
            }
        }
    }
}
