package edu.battleship.engine

import edu.battleship.model.Board
import edu.battleship.model.Cell
import edu.battleship.model.CellState
import edu.battleship.model.Player

abstract class Move(
    val player: Player,
) {
    abstract fun validate(game: Game): Boolean

    abstract fun execute(game: Game)
}

internal fun explodeMineAndSymmetrical(
    mineCell: Cell,
    opponentBoard: Board,
    playerBoard: Board,
    symmetricalX: Int,
    symmetricalY: Int,
) {
    val mine = opponentBoard.mines.find { it.position == mineCell } ?: return
    mine.explode()
    val myCell = playerBoard.getCell(symmetricalX, symmetricalY)
    when (myCell.state) {
        CellState.EMPTY -> myCell.state = CellState.MISS
        CellState.SHIP -> myCell.state = CellState.HIT
        CellState.MINED -> playerBoard.mines.find { it.position == myCell }?.explode()
        else -> {}
    }
}
