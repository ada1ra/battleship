package edu.battleship.integration

import edu.battleship.engine.Game
import edu.battleship.engine.MoveResult
import edu.battleship.engine.ShotMove
import edu.battleship.model.CellState
import edu.battleship.model.GameMode
import edu.battleship.model.Mine
import edu.battleship.model.Player
import edu.battleship.model.Ship
import edu.battleship.model.ShipType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ExplodeMineTest {
    @Test
    fun `shooting a mine damages symmetric cell`() {
        val alice = Player(1, "Alice")
        val bob = Player(2, "Bob")
        val game = Game(1, alice, bob, GameMode.EXTENDED)

        // one ship to players to avoid 'game over'
        game.board1.placeShip(Ship(ShipType.SINGLE, mutableListOf(game.board1.getCell(0, 0))))
        game.board2.placeShip(Ship(ShipType.SINGLE, mutableListOf(game.board2.getCell(0, 0))))

        game.board2.placeMine(Mine(game.board2.getCell(5, 5)))
        game.board1.placeMine(Mine(game.board1.getCell(7, 7)))

        game.start()

        val result1 = game.processMove(ShotMove(alice, game.board2.getCell(5, 5)))
        assertEquals(MoveResult.Success, result1)
        // mine exploded
        assertEquals(CellState.EXPLODED_MINE, game.board2.getCell(5, 5).state)
        // Alice's symmetric cell also changed (5,5) EMPTY -> MISS
        assertEquals(CellState.MISS, game.board1.getCell(5, 5).state)

        assertEquals(bob, game.currentPlayer)
    }

    @Test
    fun `mine explosion hits own ship and triggers chain reaction`() {
        val alice = Player(1, "Alice")
        val bob = Player(2, "Bob")
        val game = Game(1, alice, bob, GameMode.EXTENDED)

        game.board1.placeShip(Ship(ShipType.SINGLE, mutableListOf(game.board1.getCell(3, 3))))
        game.board1.placeMine(Mine(game.board1.getCell(4, 4)))
        game.board2.placeShip(Ship(ShipType.DOUBLE, mutableListOf(game.board2.getCell(4, 3), game.board2.getCell(4, 4))))
        game.board2.placeMine(Mine(game.board2.getCell(3, 3)))

        game.start()

        game.processMove(ShotMove(alice, game.board2.getCell(1, 1)))
        game.processMove(ShotMove(bob, game.board1.getCell(4, 4)))
        // Alice's mine exploded
        assertEquals(CellState.EXPLODED_MINE, game.board1.getCell(4, 4).state)
        // Bob's symmetric cell (4,4) SHIP -> HIT
        assertEquals(CellState.HIT, game.board2.getCell(4, 4).state)
    }
}
