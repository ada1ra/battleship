package edu.battleship.unit.model

import edu.battleship.model.Cell
import edu.battleship.model.CellState
import edu.battleship.model.Ship
import edu.battleship.model.ShipType
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ShipTest {
    @Test
    fun `ship with correct size is created`() {
        val cells = mutableListOf(Cell(0, 0), Cell(0, 1), Cell(0, 2), Cell(0, 3))
        val ship = Ship(ShipType.QUADRUPLE, cells)
        assertFalse(ship.isSunk())
    }

    @Test
    fun `ship with wrong size throws exception`() {
        val cells = mutableListOf(Cell(0, 0), Cell(0, 1))
        assertThrows<IllegalArgumentException> {
            Ship(ShipType.QUADRUPLE, cells)
        }
    }

    @Test
    fun `ship is sunk when all cells are hit`() {
        val cells = mutableListOf(Cell(0, 0), Cell(0, 1))
        val ship = Ship(ShipType.DOUBLE, cells)
        cells.forEach { it.state = CellState.HIT }
        assertTrue(ship.isSunk())
    }

    @Test
    fun `ship is not sunk when partially hit`() {
        val cells = mutableListOf(Cell(0, 0), Cell(0, 1))
        val ship = Ship(ShipType.DOUBLE, cells)
        cells[0].state = CellState.HIT
        assertFalse(ship.isSunk())
    }
}
