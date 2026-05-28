package edu.battleship.unit.engine

import edu.battleship.engine.AircraftMove
import edu.battleship.engine.Game
import edu.battleship.engine.PlaceMineMove
import edu.battleship.engine.ShotMove
import edu.battleship.model.Aircraft
import edu.battleship.model.AircraftType
import edu.battleship.model.CellState
import edu.battleship.model.GameMode
import edu.battleship.model.Mine
import edu.battleship.model.Player
import edu.battleship.model.Ship
import edu.battleship.model.ShipType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MoveTest {
    private val alice = Player(1, "Alice")
    private val bob = Player(2, "Bob")

    @Test
    fun `shotMove validates correctly for empty cell`() {
        val game = Game(1, alice, bob, GameMode.STANDARD)
        game.board1.placeShip(Ship(ShipType.SINGLE, mutableListOf(game.board1.getCell(0, 0))))
        game.board2.placeShip(Ship(ShipType.SINGLE, mutableListOf(game.board2.getCell(0, 0))))
        game.start()
        val move = ShotMove(alice, game.board2.getCell(5, 5))
        assertTrue(move.validate(game))
    }

    @Test
    fun `shotMove validates incorrectly for already hit cell`() {
        val game = Game(1, alice, bob, GameMode.STANDARD)
        game.board1.placeShip(Ship(ShipType.SINGLE, mutableListOf(game.board1.getCell(0, 0))))
        game.board2.placeShip(Ship(ShipType.SINGLE, mutableListOf(game.board2.getCell(0, 0))))
        game.start()
        game.board2.getCell(5, 5).state = CellState.HIT
        val move = ShotMove(alice, game.board2.getCell(5, 5))
        assertFalse(move.validate(game))
    }

    @Test
    fun `shotMove execute changes cell to hit when ship present`() {
        val game = Game(1, alice, bob, GameMode.STANDARD)
        game.board1.placeShip(Ship(ShipType.SINGLE, mutableListOf(game.board1.getCell(0, 0))))
        game.board2.placeShip(Ship(ShipType.SINGLE, mutableListOf(game.board2.getCell(0, 0))))
        game.start()
        val move = ShotMove(alice, game.board2.getCell(0, 0))
        move.execute(game)
        assertEquals(CellState.HIT, game.board2.getCell(0, 0).state)
    }

    @Test
    fun `shotMove execute changes cell to miss when empty`() {
        val game = Game(1, alice, bob, GameMode.STANDARD)
        game.board1.placeShip(Ship(ShipType.SINGLE, mutableListOf(game.board1.getCell(0, 0))))
        game.board2.placeShip(Ship(ShipType.SINGLE, mutableListOf(game.board2.getCell(0, 0))))
        game.start()
        val move = ShotMove(alice, game.board2.getCell(5, 5))
        move.execute(game)
        assertEquals(CellState.MISS, game.board2.getCell(5, 5).state)
    }

    @Test
    fun `aircraftMove validates remaining uses and pattern`() {
        val game = Game(1, alice, bob, GameMode.EXTENDED)
        game.board1.placeShip(Ship(ShipType.SINGLE, mutableListOf(game.board1.getCell(0, 0))))
        game.board2.placeShip(Ship(ShipType.SINGLE, mutableListOf(game.board2.getCell(0, 0))))

        // scout with remainingUses = 0 -> invalid
        val scoutNoUses = Aircraft(AircraftType.SCOUT, remainingUses = 0)
        val pattern = scoutNoUses.getAttackPattern(5, 5)
        val targets = pattern.map { (x, y) -> game.board1.getCell(x, y) }
        assertFalse(AircraftMove(alice, scoutNoUses, targets).validate(game))

        // scout with remainingUses > 0 -> valid
        val scoutOk = Aircraft(AircraftType.SCOUT, remainingUses = 1)
        assertTrue(AircraftMove(alice, scoutOk, targets).validate(game))

        // bomber with incomplete pattern -> invalid
        val bomber = Aircraft(AircraftType.BOMBER, remainingUses = 1)
        val wrongTargets = listOf(game.board1.getCell(5, 5))
        assertFalse(AircraftMove(alice, bomber, wrongTargets).validate(game))

        // bomber with complete pattern -> valid
        val bomberPattern = bomber.getAttackPattern(5, 5)
        val correctTargets = bomberPattern.map { (x, y) -> game.board1.getCell(x, y) }
        assertTrue(AircraftMove(alice, bomber, correctTargets).validate(game))
    }

    @Test
    fun `aircraftMove validates remaining uses`() {
        val game = Game(1, alice, bob, GameMode.EXTENDED)
        val aircraft = Aircraft(AircraftType.SCOUT, remainingUses = 0)
        val move = AircraftMove(alice, aircraft, emptyList())
        assertFalse(move.validate(game))
    }

    @Test
    fun `placeMineMove validates correctly for empty cell`() {
        val game = Game(1, alice, bob, GameMode.EXTENDED)
        val mine = Mine(game.board1.getCell(5, 5))
        val move = PlaceMineMove(alice, mine)
        assertTrue(move.validate(game))
    }

    @Test
    fun `placeMineMove validates incorrectly for occupied cell`() {
        val game = Game(1, alice, bob, GameMode.EXTENDED)
        game.board1.placeShip(Ship(ShipType.SINGLE, mutableListOf(game.board1.getCell(5, 5))))
        val mine = Mine(game.board1.getCell(5, 5))
        val move = PlaceMineMove(alice, mine)
        assertFalse(move.validate(game))
    }

    @Test
    fun `placeMineMove execute places mine on board`() {
        val game = Game(1, alice, bob, GameMode.EXTENDED)
        val mine = Mine(game.board1.getCell(5, 5))
        val move = PlaceMineMove(alice, mine)
        move.execute(game)
        assertEquals(CellState.MINED, game.board1.getCell(5, 5).state)
    }
}
