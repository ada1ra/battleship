package edu.battleship.engine

import edu.battleship.model.CellState
import edu.battleship.model.Mine
import edu.battleship.model.Player

class PlaceMineMove(
    player: Player,
    val mine: Mine,
) : Move(player) {
    override fun validate(game: Game): Boolean {
        val cell = game.getBoard(player).getCell(mine.position.x, mine.position.y)
        return cell.state == CellState.EMPTY
    }

    override fun execute(game: Game) {
        game.getBoard(player).placeMine(mine)
    }
}
