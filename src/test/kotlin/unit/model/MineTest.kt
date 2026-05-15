package edu.battleship.unit.model

import edu.battleship.model.Cell
import edu.battleship.model.CellState
import edu.battleship.model.Mine
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MineTest {
    @Test
    fun `mine is armed by default`() {
        val mine = Mine(Cell(0, 0))
        assertTrue(mine.isArmed)
    }

    @Test
    fun `mine cell is unchanged in constructor`() {
        val cell = Cell(0, 0)
        val mine = Mine(cell)
        assertEquals(CellState.EMPTY, cell.state)
    }

    @Test
    fun `explode sets mine as unarmed`() {
        val mine = Mine(Cell(0, 0))
        mine.position.state = CellState.MINED
        mine.explode()
        assertTrue(!mine.isArmed)
    }
}
