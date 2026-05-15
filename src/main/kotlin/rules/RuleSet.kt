package edu.battleship.rules

import edu.battleship.model.Board

interface RuleSet {
    fun getShotsCount(board: Board): Int

    fun canPlaceMine(board: Board): Boolean
}
