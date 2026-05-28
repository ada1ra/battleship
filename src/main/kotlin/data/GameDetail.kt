package edu.battleship.data

import edu.battleship.engine.AircraftMove
import edu.battleship.engine.Game
import edu.battleship.engine.PlaceMineMove
import edu.battleship.engine.ShotMove
import edu.battleship.model.Board
import edu.battleship.model.CellState

@kotlinx.serialization.Serializable
data class GameDetail(
    val id: Int,
    val player1Name: String,
    val player2Name: String,
    val mode: String,
    val winnerName: String?,
    val finalBoardState1: String,
    val finalBoardState2: String,
    val moves: List<String>,
) {
    companion object {
        fun fromGame(game: Game): GameDetail {
            val p1 = game.player1
            val p2 = game.player2
            val finalBoard1 = boardToString(game.board1)
            val finalBoard2 = boardToString(game.board2)
            val moves =
                game.moveHistory.map { move ->
                    when (move) {
                        is ShotMove ->
                            "${move.player.name} shot ${
                                formatCoordinate(
                                    move.targetCell.x,
                                    move.targetCell.y,
                                )
                            }"

                        is AircraftMove ->
                            "${move.player.name} used ${move.aircraft.type.name} on ${
                                formatCoordinate(
                                    move.targetCells[0].x,
                                    move.targetCells[0].y,
                                )
                            }"

                        is PlaceMineMove ->
                            "${move.player.name} placed mine at ${
                                formatCoordinate(
                                    move.mine.position.x,
                                    move.mine.position.y,
                                )
                            }"

                        else -> "unknown"
                    }
                }
            return GameDetail(
                id = game.id,
                player1Name = p1.name,
                player2Name = p2.name,
                mode = game.mode.name,
                winnerName = game.getWinner()?.name,
                finalBoardState1 = finalBoard1,
                finalBoardState2 = finalBoard2,
                moves = moves,
            )
        }

        private fun boardToString(board: Board): String {
            val boardInString = StringBuilder()
            for (x in 0..9) {
                for (y in 0..9) {
                    val cell = board.getCell(x, y)
                    val ch =
                        when (cell.state) {
                            CellState.EMPTY -> '~'
                            CellState.SHIP -> 'O'
                            CellState.HIT -> 'X'
                            CellState.MISS -> '*'
                            CellState.MINED -> 'M'
                            CellState.EXPLODED_MINE -> '!'
                        }
                    boardInString.append(ch)
                }
                boardInString.append('\n')
            }
            return boardInString.toString()
        }
    }
}
