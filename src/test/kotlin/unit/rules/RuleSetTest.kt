package edu.battleship.unit.rules

import edu.battleship.model.Board
import edu.battleship.model.Ship
import edu.battleship.model.ShipType
import edu.battleship.rules.ExperiencedRules
import edu.battleship.rules.ExtendedRules
import edu.battleship.rules.StandardRules
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class RuleSetTest {
    @Test
    fun `standard rules always return 1 shot`() {
        val board = Board()
        Assertions.assertEquals(1, StandardRules.getShotsCount(board))
    }

    @Test
    fun `experienced rules return alive ships count`() {
        val board = Board()
        val ship1 = Ship(ShipType.SINGLE, mutableListOf(board.getCell(0, 0)))
        val ship2 = Ship(ShipType.SINGLE, mutableListOf(board.getCell(2, 2)))
        board.placeShip(ship1)
        board.placeShip(ship2)
        Assertions.assertEquals(2, ExperiencedRules.getShotsCount(board))
    }

    @Test
    fun `extended rules allow mine placement when less than 3`() {
        val board = Board()
        Assertions.assertTrue(ExtendedRules.canPlaceMine(board))
    }

    @Test
    fun `standard rules disallow mines`() {
        val board = Board()
        Assertions.assertFalse(StandardRules.canPlaceMine(board))
    }

    @Test
    fun `experienced rules disallow mines`() {
        val board = Board()
        Assertions.assertFalse(ExperiencedRules.canPlaceMine(board))
    }
}
