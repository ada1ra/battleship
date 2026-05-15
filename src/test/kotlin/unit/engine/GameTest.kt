package edu.battleship.unit.engine

import edu.battleship.engine.Game
import edu.battleship.engine.MoveResult
import edu.battleship.engine.ShotMove
import edu.battleship.model.CellState
import edu.battleship.model.GameMode
import edu.battleship.model.Player
import edu.battleship.model.Ship
import edu.battleship.model.ShipType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GameTest {
    @Test
    fun `game starts with correct player`() {
        val alice = Player(1, "Alice")
        val bob = Player(2, "Bob")
        val game = Game(1, alice, bob, GameMode.STANDARD)
        assertEquals(alice, game.currentPlayer)
    }

    @Test
    fun `cannot move after game over`() {
        val alice = Player(1, "Alice")
        val bob = Player(2, "Bob")
        val game = Game(1, alice, bob, GameMode.STANDARD)
        game.board1.placeShip(Ship(ShipType.SINGLE, mutableListOf(game.board1.getCell(0, 0))))
        game.board2.placeShip(Ship(ShipType.SINGLE, mutableListOf(game.board2.getCell(0, 0))))
        game.start()
        game.processMove(ShotMove(alice, game.board2.getCell(0, 0)))
        val extraMove = game.processMove(ShotMove(alice, game.board2.getCell(1, 1)))
        assertEquals(MoveResult.GameOver, extraMove)
    }

    @Test
    fun `shot hit changes cell state`() {
        val alice = Player(1, "Alice")
        val bob = Player(2, "Bob")
        val game = Game(1, alice, bob, GameMode.STANDARD)
        game.board1.placeShip(Ship(ShipType.SINGLE, mutableListOf(game.board1.getCell(0, 0))))
        game.board2.placeShip(Ship(ShipType.SINGLE, mutableListOf(game.board2.getCell(0, 0))))
        game.start()
        game.processMove(ShotMove(alice, game.board2.getCell(0, 0)))
        assertEquals(CellState.HIT, game.board2.getCell(0, 0).state)
    }

    @Test
    fun `shot miss changes cell state`() {
        val alice = Player(1, "Alice")
        val bob = Player(2, "Bob")
        val game = Game(1, alice, bob, GameMode.STANDARD)
        game.board1.placeShip(Ship(ShipType.SINGLE, mutableListOf(game.board1.getCell(0, 0))))
        game.board2.placeShip(Ship(ShipType.SINGLE, mutableListOf(game.board2.getCell(0, 0))))
        game.start()
        game.processMove(ShotMove(alice, game.board2.getCell(5, 5)))
        assertEquals(CellState.MISS, game.board2.getCell(5, 5).state)
    }

    @Test
    fun `game over when all ships sunk`() {
        val alice = Player(1, "Alice")
        val bob = Player(2, "Bob")
        val game = Game(1, alice, bob, GameMode.STANDARD)
        game.board1.placeShip(Ship(ShipType.SINGLE, mutableListOf(game.board1.getCell(0, 0))))
        game.board2.placeShip(Ship(ShipType.SINGLE, mutableListOf(game.board2.getCell(0, 0))))
        game.start()
        game.processMove(ShotMove(alice, game.board2.getCell(0, 0)))
        assertTrue(game.gameOver)
        assertEquals(alice, game.getWinner())
    }

    @Test
    fun `getOpponent returns correct player`() {
        val alice = Player(1, "Alice")
        val bob = Player(2, "Bob")
        val game = Game(1, alice, bob, GameMode.STANDARD)
        assertEquals(bob, game.getOpponent(alice))
        assertEquals(alice, game.getOpponent(bob))
    }

    @Test
    fun `getBoard returns correct board`() {
        val alice = Player(1, "Alice")
        val bob = Player(2, "Bob")
        val game = Game(1, alice, bob, GameMode.STANDARD)
        assertEquals(game.board1, game.getBoard(alice))
        assertEquals(game.board2, game.getBoard(bob))
    }
}
