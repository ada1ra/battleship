package edu.battleship.engine

sealed class MoveResult {
    data object Success : MoveResult()

    data class Invalid(
        val reason: String,
    ) : MoveResult()

    data object GameOver : MoveResult()
}
