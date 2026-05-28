package edu.battleship.unit.model

import edu.battleship.model.Board
import edu.battleship.model.CellState
import edu.battleship.model.Mine
import edu.battleship.model.Player
import edu.battleship.model.Ship
import edu.battleship.model.ShipType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class BoardTest {
    private val player = Player(1, "Test")
    private val board = Board()

    @Test
    fun `board initialization creates empty cells`() {
        val cell = board.getCell(0, 0)
        assertEquals(CellState.EMPTY, cell.state)
    }

    @Test
    fun `place ship horizontally`() {
        val ship =
            Ship(
                ShipType.QUADRUPLE,
                mutableListOf(
                    board.getCell(0, 0),
                    board.getCell(0, 1),
                    board.getCell(0, 2),
                    board.getCell(0, 3),
                ),
            )
        assertTrue(board.placeShip(ship))
        assertEquals(CellState.SHIP, board.getCell(0, 0).state)
        assertEquals(1, board.ships.size)
    }

    @Test
    fun `place ship vertically`() {
        val ship =
            Ship(
                ShipType.DOUBLE,
                mutableListOf(
                    board.getCell(0, 0),
                    board.getCell(1, 0),
                ),
            )
        assertTrue(board.placeShip(ship))
        assertEquals(CellState.SHIP, board.getCell(0, 0).state)
    }

    @Test
    fun `cannot place overlapping ships`() {
        val ship1 =
            Ship(
                ShipType.QUADRUPLE,
                mutableListOf(
                    board.getCell(0, 0),
                    board.getCell(0, 1),
                    board.getCell(0, 2),
                    board.getCell(0, 3),
                ),
            )
        val ship2 = Ship(ShipType.SINGLE, mutableListOf(board.getCell(0, 0)))
        board.placeShip(ship1)
        assertFalse(board.placeShip(ship2))
    }

    @Test
    fun `cannot place adjacent ships`() {
        val ship1 =
            Ship(
                ShipType.QUADRUPLE,
                mutableListOf(
                    board.getCell(0, 0),
                    board.getCell(0, 1),
                    board.getCell(0, 2),
                    board.getCell(0, 3),
                ),
            )
        board.placeShip(ship1)
        val ship2 = Ship(ShipType.SINGLE, mutableListOf(board.getCell(1, 0)))
        assertFalse(board.placeShip(ship2))
    }

    @Test
    fun `place mine on empty cell`() {
        val mine = Mine(board.getCell(5, 5))
        assertTrue(board.placeMine(mine))
        assertEquals(CellState.MINED, board.getCell(5, 5).state)
    }

    @Test
    fun `cannot place mine on occupied cell`() {
        val ship = Ship(ShipType.SINGLE, mutableListOf(board.getCell(5, 5)))
        board.placeShip(ship)
        val mine = Mine(board.getCell(5, 5))
        assertFalse(board.placeMine(mine))
    }

    @Test
    fun `isAllShipsSunk returns true when all sunk`() {
        val ship = Ship(ShipType.SINGLE, mutableListOf(board.getCell(0, 0)))
        board.placeShip(ship)
        board.getCell(0, 0).state = CellState.HIT
        assertTrue(board.isAllShipsSunk())
    }

    @Test
    fun `getAliveShipsCount returns correct count`() {
        val ship1 = Ship(ShipType.SINGLE, mutableListOf(board.getCell(0, 0)))
        val ship2 = Ship(ShipType.SINGLE, mutableListOf(board.getCell(2, 2)))
        board.placeShip(ship1)
        board.placeShip(ship2)
        assertEquals(2, board.getAliveShipsCount())
        board.getCell(0, 0).state = CellState.HIT
        assertEquals(1, board.getAliveShipsCount())
    }
}
