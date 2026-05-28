package edu.battleship.engine

import edu.battleship.model.Cell
import edu.battleship.model.CellState
import edu.battleship.model.Player

class ShotMove(
    player: Player,
    val targetCell: Cell,
) : Move(player) {
    override fun validate(game: Game): Boolean {
        val opponentBoard = game.getBoard(game.getOpponent(player))
        val cell = opponentBoard.getCell(targetCell.x, targetCell.y)
        return cell.state != CellState.HIT && cell.state != CellState.MISS && cell.state != CellState.EXPLODED_MINE
    }

    override fun execute(game: Game) {
        val opponentBoard = game.getBoard(game.getOpponent(player))
        val cell = opponentBoard.getCell(targetCell.x, targetCell.y)
        when (cell.state) {
            CellState.EMPTY -> cell.state = CellState.MISS
            CellState.SHIP -> cell.state = CellState.HIT
            CellState.MINED -> {
                explodeMineAndSymmetrical(cell, opponentBoard, game.getBoard(player), targetCell.x, targetCell.y)
            }

            else -> {}
        }
    }
}
