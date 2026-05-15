package edu.battleship.rules

import edu.battleship.model.Board

object ExperiencedRules : RuleSet {
    override fun getShotsCount(board: Board): Int = board.getAliveShipsCount()

    override fun canPlaceMine(board: Board): Boolean = false
}
