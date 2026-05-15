package edu.battleship.engine

import edu.battleship.model.Aircraft
import edu.battleship.model.AircraftType
import edu.battleship.model.Cell
import edu.battleship.model.CellState
import edu.battleship.model.Player
import kotlin.collections.forEach

class AircraftMove(
    player: Player,
    val aircraft: Aircraft,
    val targetCells: List<Cell>,
) : Move(player) {
    override fun validate(game: Game): Boolean {
        // check if there's aircraft
        if (aircraft.remainingUses <= 0) return false
        // check pattern (for scout it's not need)
        if (aircraft.type == AircraftType.SCOUT) return true
        val pattern = aircraft.getAttackPattern(targetCells.first().x, targetCells.first().y)
        val targetSet = targetCells.map { it.x to it.y }.toSet()
        if (targetSet != pattern.toSet()) return false
        val opponentBoard = game.getBoard(game.getOpponent(player))
        // find minimum 1 free cell
        return targetCells.any { cell ->
            val state = opponentBoard.getCell(cell.x, cell.y).state
            state != CellState.HIT && state != CellState.MISS && state != CellState.EXPLODED_MINE
        }
    }

    override fun execute(game: Game) {
        val opponentBoard = game.getBoard(game.getOpponent(player))
        aircraft.remainingUses--
        if (aircraft.type == AircraftType.SCOUT) {
            // everything happen in UI
            return
        }
        targetCells.forEach { (x, y) ->
            val cell = opponentBoard.getCell(x, y)
            when (cell.state) {
                CellState.EMPTY -> cell.state = CellState.MISS
                CellState.SHIP -> cell.state = CellState.HIT
                CellState.MINED -> {
                    explodeMineAndSymmetrical(cell, opponentBoard, game.getBoard(player), x, y)
                }

                else -> {}
            }
        }
    }
}
