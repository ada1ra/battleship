package edu.battleship.integration

import edu.battleship.engine.AircraftMove
import edu.battleship.engine.Game
import edu.battleship.engine.MoveResult
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
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ValidateMineAircraft {
    @Test
    fun `mine explosion damages symmetric cell`() {
        val alice = Player(1, "Alice")
        val bob = Player(2, "Bob")
        val game = Game(1, alice, bob, GameMode.EXTENDED)

        game.board1.placeShip(Ship(ShipType.SINGLE, mutableListOf(game.board1.getCell(0, 0))))
        game.board2.placeShip(Ship(ShipType.SINGLE, mutableListOf(game.board2.getCell(0, 0))))
        game.board2.placeMine(Mine(game.board2.getCell(7, 7)))

        game.start()

        val result = game.processMove(ShotMove(alice, game.board2.getCell(7, 7)))
        assertEquals(MoveResult.Success, result)
        assertEquals(CellState.EXPLODED_MINE, game.board2.getCell(7, 7).state)
        val symCell = game.board1.getCell(7, 7)
        assertTrue(symCell.state == CellState.MISS || symCell.state == CellState.HIT)
    }

    @Test
    fun `bomber damages multiple cells`() {
        val alice = Player(1, "Alice")
        val bob = Player(2, "Bob")
        val game = Game(1, alice, bob, GameMode.EXTENDED)

        game.board1.placeShip(
            Ship(
                ShipType.QUADRUPLE,
                mutableListOf(
                    game.board1.getCell(0, 0),
                    game.board1.getCell(0, 1),
                    game.board1.getCell(0, 2),
                    game.board1.getCell(0, 3),
                ),
            ),
        )
        game.board2.placeShip(
            Ship(
                ShipType.QUADRUPLE,
                mutableListOf(
                    game.board2.getCell(0, 0),
                    game.board2.getCell(0, 1),
                    game.board2.getCell(0, 2),
                    game.board2.getCell(0, 3),
                ),
            ),
        )

        val bomber = Aircraft(AircraftType.BOMBER, remainingUses = 1)
        alice.aircrafts.add(bomber)

        game.start()

        val pattern = bomber.getAttackPattern(0, 0)
        val targets = pattern.map { (x, y) -> game.board2.getCell(x, y) }
        val move = AircraftMove(alice, bomber, targets)
        val result = game.processMove(move)

        assertEquals(MoveResult.Success, result)
        // check all target cells (hit or miss)
        assertEquals(CellState.HIT, game.board2.getCell(0, 0).state)
        assertEquals(CellState.HIT, game.board2.getCell(0, 1).state)
        assertEquals(CellState.MISS, game.board2.getCell(1, 0).state)
        assertEquals(bob, game.currentPlayer)
    }
}
