package edu.battleship.integration

import edu.battleship.engine.Game
import edu.battleship.engine.MoveResult
import edu.battleship.engine.ShotMove
import edu.battleship.model.Board
import edu.battleship.model.GameMode
import edu.battleship.model.Player
import edu.battleship.model.Ship
import edu.battleship.model.ShipType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ExperiencedShotsTest {
    @Test
    fun `experienced mode shot count is correct`() {
        val alice = Player(1, "Alice")
        val bob = Player(2, "Bob")
        val game =
            Game(
                id = 2,
                player1 = alice,
                player2 = bob,
                mode = GameMode.EXPERIENCED,
            )

        // full fleet
        placeStandardFleet(game.board1)
        placeStandardFleet(game.board2)

        game.start()
        // Alice have ten shots (10 alive ships)
        assertEquals(10, game.shotsRemaining)

        // Alice missed ten times in a row
        for (i in 1..10) {
            val move = ShotMove(alice, game.board2.getCell(i % 10, (i * 3) % 10))
            val result = game.processMove(move)
            assertEquals(MoveResult.Success, result)
        }
        // move transited to Bob, shotsRemaining -> his alive ships (10)
        assertEquals(bob, game.currentPlayer)
        assertEquals(10, game.shotsRemaining)

        // Bob missed 9 times in a row and one hit
        val targetShipCell =
            game.board1.ships
                .first()
                .cells
                .first()
        for (i in 1..9) {
            game.processMove(ShotMove(bob, game.board1.getCell(i % 10, (i * 3) % 10)))
        }
        game.processMove(ShotMove(bob, targetShipCell))
        // ship alive (just hit) -> Alice has 10
        assertEquals(alice, game.currentPlayer)
        assertEquals(10, game.shotsRemaining)
    }

    private fun placeStandardFleet(board: Board) {
        val ships =
            listOf(
                Ship(
                    ShipType.QUADRUPLE,
                    mutableListOf(
                        board.getCell(0, 0),
                        board.getCell(0, 1),
                        board.getCell(0, 2),
                        board.getCell(0, 3),
                    ),
                ),
                Ship(
                    ShipType.TRIPLE,
                    mutableListOf(
                        board.getCell(2, 0),
                        board.getCell(2, 1),
                        board.getCell(2, 2),
                    ),
                ),
                Ship(
                    ShipType.TRIPLE,
                    mutableListOf(
                        board.getCell(4, 0),
                        board.getCell(4, 1),
                        board.getCell(4, 2),
                    ),
                ),
                Ship(
                    ShipType.DOUBLE,
                    mutableListOf(
                        board.getCell(6, 0),
                        board.getCell(6, 1),
                    ),
                ),
                Ship(
                    ShipType.DOUBLE,
                    mutableListOf(
                        board.getCell(8, 0),
                        board.getCell(9, 0),
                    ),
                ),
                Ship(
                    ShipType.DOUBLE,
                    mutableListOf(
                        board.getCell(8, 2),
                        board.getCell(9, 2),
                    ),
                ),
                Ship(ShipType.SINGLE, mutableListOf(board.getCell(0, 8))),
                Ship(ShipType.SINGLE, mutableListOf(board.getCell(2, 8))),
                Ship(ShipType.SINGLE, mutableListOf(board.getCell(2, 4))),
                Ship(ShipType.SINGLE, mutableListOf(board.getCell(9, 6))),
            )
        ships.forEach { assertTrue(board.placeShip(it)) }
    }
}
