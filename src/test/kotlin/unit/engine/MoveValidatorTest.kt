package edu.battleship.unit.engine

import edu.battleship.engine.Game
import edu.battleship.engine.MoveValidator
import edu.battleship.engine.ShotMove
import edu.battleship.model.GameMode
import edu.battleship.model.Player
import edu.battleship.model.Ship
import edu.battleship.model.ShipType
import edu.battleship.rules.StandardRules
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MoveValidatorTest {
    @Test
    fun `valid shot move passes validation`() {
        val alice = Player(1, "Alice")
        val bob = Player(2, "Bob")
        val game = Game(1, alice, bob, GameMode.STANDARD)
        game.board1.placeShip(Ship(ShipType.SINGLE, mutableListOf(game.board1.getCell(0, 0))))
        game.board2.placeShip(Ship(ShipType.SINGLE, mutableListOf(game.board2.getCell(0, 0))))
        game.start()
        val validator = MoveValidator(StandardRules)
        val move = ShotMove(alice, game.board2.getCell(0, 0))
        assertTrue(validator.validate(move, game))
    }

    @Test
    fun `shot to already hit cell fails validation`() {
        val alice = Player(1, "Alice")
        val bob = Player(2, "Bob")
        val game = Game(1, alice, bob, GameMode.STANDARD)
        game.board2.placeShip(Ship(ShipType.SINGLE, mutableListOf(game.board2.getCell(0, 0))))
        game.start()
        game.processMove(ShotMove(alice, game.board2.getCell(0, 0)))
        val validator = MoveValidator(StandardRules)
        val secondMove = ShotMove(alice, game.board2.getCell(0, 0))
        assertFalse(validator.validate(secondMove, game))
    }
}
