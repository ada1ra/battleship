package edu.battleship.engine

import edu.battleship.model.Board
import edu.battleship.model.CellState
import edu.battleship.model.GameMode
import edu.battleship.model.Player
import edu.battleship.rules.ExperiencedRules
import edu.battleship.rules.ExtendedRules
import edu.battleship.rules.RuleSet
import edu.battleship.rules.StandardRules

class Game(
    val id: Int,
    val player1: Player,
    val player2: Player,
    val mode: GameMode,
    val board1: Board = Board(),
    val board2: Board = Board(),
    var currentPlayer: Player = player1,
) {
    val moveHistory = mutableListOf<Move>()
    private val ruleSet: RuleSet =
        when (mode) {
            GameMode.STANDARD -> StandardRules
            GameMode.EXPERIENCED -> ExperiencedRules
            GameMode.EXTENDED -> ExtendedRules
        }
    private val validator = MoveValidator(ruleSet)
    var shotsRemaining: Int = 0
        private set
    var gameOver = false
        private set

    fun start() {
        shotsRemaining = ruleSet.getShotsCount(getBoard(currentPlayer))
    }

    fun processMove(move: Move): MoveResult {
        if (gameOver) return MoveResult.GameOver
        if (move.player != currentPlayer) {
            return MoveResult.Invalid("Not your turn")
        }
        if (!validator.validate(move, this)) {
            return MoveResult.Invalid("Invalid move")
        }

        move.execute(this)
        moveHistory.add(move)

        if (board1.isAllShipsSunk() || board2.isAllShipsSunk()) {
            gameOver = true
            return MoveResult.GameOver
        }

        // move transition logic
        when (mode) {
            GameMode.STANDARD, GameMode.EXTENDED -> {
                if (move is ShotMove) {
                    // check: hit or miss
                    val targetBoard = getBoard(getOpponent(currentPlayer))
                    val cell = targetBoard.getCell(move.targetCell.x, move.targetCell.y)
                    if (cell.state != CellState.HIT) { // промах
                        currentPlayer = getOpponent(currentPlayer)
                        shotsRemaining = ruleSet.getShotsCount(getBoard(currentPlayer))
                    }
                } else {
                    // if not shot (mine/aircraft) -> to next player
                    currentPlayer = getOpponent(currentPlayer)
                    shotsRemaining = ruleSet.getShotsCount(getBoard(currentPlayer))
                }
            }
            GameMode.EXPERIENCED -> {
                shotsRemaining--
                if (shotsRemaining <= 0) {
                    currentPlayer = getOpponent(currentPlayer)
                    shotsRemaining = ruleSet.getShotsCount(getBoard(currentPlayer))
                }
            }
        }
        return MoveResult.Success
    }

    fun getOpponent(player: Player): Player = if (player == player1) player2 else player1

    fun getBoard(player: Player): Board = if (player == player1) board1 else board2

    fun getWinner(): Player? =
        when {
            board1.isAllShipsSunk() -> player2
            board2.isAllShipsSunk() -> player1
            else -> null
        }
}
