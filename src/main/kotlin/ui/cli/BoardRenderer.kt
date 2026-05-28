package edu.battleship.ui.cli

import edu.battleship.model.Board
import edu.battleship.model.CellState

object BoardRenderer {
    fun printBoard(
        board: Board,
        hideShips: Boolean,
    ) {
        print("  ")
        for (y in 1..10) print("$y ")
        println()
        for (x in 0..9) {
            print("${'A' + x} ")
            for (y in 0..9) {
                val cell = board.getCell(x, y)
                val ch =
                    when (cell.state) {
                        CellState.EMPTY -> '~'
                        CellState.SHIP -> if (hideShips) '~' else 'O'
                        CellState.HIT -> 'X'
                        CellState.MISS -> '*'
                        CellState.MINED -> if (hideShips) '~' else 'M'
                        CellState.EXPLODED_MINE -> '!'
                    }
                print("$ch ")
            }
            println()
        }
    }
}
