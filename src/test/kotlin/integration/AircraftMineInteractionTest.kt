package edu.battleship.integration

import edu.battleship.engine.AircraftMove
import edu.battleship.engine.Game
import edu.battleship.engine.MoveResult
import edu.battleship.model.Aircraft
import edu.battleship.model.AircraftType
import edu.battleship.model.CellState
import edu.battleship.model.GameMode
import edu.battleship.model.Mine
import edu.battleship.model.Player
import edu.battleship.model.Ship
import edu.battleship.model.ShipType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AircraftMineInteractionTest {
    @Test
    fun `bomber triggers mine and damages own board`() {
        val alice = Player(1, "Alice")
        val bob = Player(2, "Bob")
        val game = Game(1, alice, bob, GameMode.EXTENDED)

        // one ship to players to avoid 'game over'
        game.board1.placeShip(Ship(ShipType.SINGLE, mutableListOf(game.board1.getCell(9, 9))))
        game.board2.placeShip(Ship(ShipType.SINGLE, mutableListOf(game.board2.getCell(9, 9))))

        game.board2.placeMine(Mine(game.board2.getCell(5, 5)))
        game.board1.placeMine(Mine(game.board1.getCell(5, 5)))

        val bomber = Aircraft(AircraftType.BOMBER, remainingUses = 1)
        alice.aircrafts.add(bomber)

        game.start()

        val pattern = bomber.getAttackPattern(5, 5)
        val targets = pattern.map { (x, y) -> game.board2.getCell(x, y) }
        val move = AircraftMove(alice, bomber, targets)
        val result = game.processMove(move)

        assertEquals(MoveResult.Success, result)
        // Bob's mine exploded
        assertEquals(CellState.EXPLODED_MINE, game.board2.getCell(5, 5).state)
        // Alice's symmetric cell (5,5, mine) -> also exploded
        assertEquals(CellState.EXPLODED_MINE, game.board1.getCell(5, 5).state)
        // other cells -> misses
        assertEquals(CellState.MISS, game.board2.getCell(4, 5).state)
        assertEquals(CellState.MISS, game.board2.getCell(6, 5).state)
        assertEquals(CellState.MISS, game.board2.getCell(5, 4).state)
        assertEquals(CellState.MISS, game.board2.getCell(5, 6).state)
    }

    @Test
    fun `scout does not trigger mines`() {
        val alice = Player(1, "Alice")
        val bob = Player(2, "Bob")
        val game = Game(1, alice, bob, GameMode.EXTENDED)

        game.board1.placeShip(Ship(ShipType.SINGLE, mutableListOf(game.board1.getCell(9, 9))))
        game.board2.placeShip(Ship(ShipType.SINGLE, mutableListOf(game.board2.getCell(9, 9))))

        game.board2.placeMine(Mine(game.board2.getCell(5, 5)))

        val scout = Aircraft(AircraftType.SCOUT, remainingUses = 1)
        alice.aircrafts.add(scout)

        game.start()

        val pattern = scout.getAttackPattern(5, 5)
        val targets = pattern.map { (x, y) -> game.board2.getCell(x, y) }
        val move = AircraftMove(alice, scout, targets)
        val result = game.processMove(move)

        assertEquals(MoveResult.Success, result)
        // mine didn't explode, state didn't change
        assertEquals(CellState.MINED, game.board2.getCell(5, 5).state)
        assertEquals(0, scout.remainingUses)
    }
}
