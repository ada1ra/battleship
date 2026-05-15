package edu.battleship.unit.model

import edu.battleship.model.Cell
import edu.battleship.model.CellState
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CellTest {
    @Test
    fun `cell initial state is EMPTY`() {
        val cell = Cell(0, 0)
        assertEquals(CellState.EMPTY, cell.state)
    }

    @Test
    fun `cell coordinates are correct`() {
        val cell = Cell(5, 7)
        assertEquals(5, cell.x)
        assertEquals(7, cell.y)
    }

    @Test
    fun `cell state can be changed`() {
        val cell = Cell(0, 0)
        cell.state = CellState.HIT
        assertEquals(CellState.HIT, cell.state)
    }
}
