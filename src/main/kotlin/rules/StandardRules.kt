package edu.battleship.rules

import edu.battleship.model.Board

object StandardRules : RuleSet {
    override fun getShotsCount(board: Board) = 1

    override fun canPlaceMine(board: Board) = false
}
