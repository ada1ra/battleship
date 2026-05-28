package edu.battleship.rules

import edu.battleship.model.Board

object ExtendedRules : RuleSet {
    override fun getShotsCount(board: Board) = 1

    override fun canPlaceMine(board: Board) = board.mines.size < 3
}
