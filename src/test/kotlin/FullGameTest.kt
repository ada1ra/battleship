package edu.battleship

import edu.battleship.engine.AircraftMove
import edu.battleship.engine.Game
import edu.battleship.engine.MoveResult
import edu.battleship.engine.PlaceMineMove
import edu.battleship.engine.ShotMove
import edu.battleship.model.Aircraft
import edu.battleship.model.AircraftType
import edu.battleship.model.Board
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

class FullGameTest {
    @Test
    fun `full standard game with hit continuation partly`() {
        val alice =
            Player(1, "Alice")
        val bob =
            Player(2, "Bob")
        val game =
            Game(
                id = 1,
                player1 = alice,
                player2 = bob,
                mode = GameMode.STANDARD,
            )

        val boardA = game.board1
        val boardB = game.board2

        assertTrue(
            boardA.placeShip(
                Ship(
                    ShipType.SINGLE,
                    mutableListOf(boardA.getCell(0, 0)),
                ),
            ),
        )
        assertTrue(
            boardB.placeShip(
                Ship(
                    ShipType.SINGLE,
                    mutableListOf(boardB.getCell(0, 0)),
                ),
            ),
        )

        game.start()

        val result = game.processMove(ShotMove(alice, boardB.getCell(0, 0)))
        assertEquals(
            MoveResult.GameOver,
            result,
        )

        assertTrue(game.gameOver)
        assertEquals(alice, game.getWinner())
    }

    @Test
    fun `full standard game with hit continuation until win`() {
        val alice =
            Player(1, "Alice")
        val bob =
            Player(2, "Bob")
        val game =
            Game(
                id = 1,
                player1 = alice,
                player2 = bob,
                mode = GameMode.STANDARD,
            )

        val boardA = game.board1
        val boardB = game.board2

        // helper function for place ships with list of pairs (x,y)
        fun placeShipOnBoard(
            board: Board,
            type: ShipType,
            vararg positions: Pair<Int, Int>,
        ): Boolean {
            val cells = positions.map { board.getCell(it.first, it.second) }.toMutableList()
            return board.placeShip(
                Ship(type, cells),
            )
        }

        assertTrue(
            placeShipOnBoard(
                boardA,
                ShipType.QUADRUPLE,
                0 to 0,
                0 to 1,
                0 to 2,
                0 to 3,
            ),
        )
        assertTrue(
            placeShipOnBoard(
                boardA,
                ShipType.TRIPLE,
                2 to 0,
                2 to 1,
                2 to 2,
            ),
        )
        assertTrue(
            placeShipOnBoard(
                boardA,
                ShipType.TRIPLE,
                4 to 0,
                4 to 1,
                4 to 2,
            ),
        )
        assertTrue(
            placeShipOnBoard(
                boardA,
                ShipType.DOUBLE,
                6 to 0,
                6 to 1,
            ),
        )
        assertTrue(
            placeShipOnBoard(
                boardA,
                ShipType.DOUBLE,
                8 to 0,
                9 to 0,
            ),
        )
        assertTrue(
            placeShipOnBoard(
                boardA,
                ShipType.DOUBLE,
                8 to 2,
                9 to 2,
            ),
        )
        assertTrue(
            placeShipOnBoard(
                boardA,
                ShipType.SINGLE,
                0 to 8,
            ),
        )
        assertTrue(
            placeShipOnBoard(
                boardA,
                ShipType.SINGLE,
                2 to 8,
            ),
        )
        assertTrue(
            placeShipOnBoard(
                boardA,
                ShipType.SINGLE,
                2 to 4,
            ),
        )
        assertTrue(
            placeShipOnBoard(
                boardA,
                ShipType.SINGLE,
                9 to 6,
            ),
        )

        assertTrue(
            placeShipOnBoard(
                boardB,
                ShipType.QUADRUPLE,
                0 to 0,
                0 to 1,
                0 to 2,
                0 to 3,
            ),
        )
        assertTrue(
            placeShipOnBoard(
                boardB,
                ShipType.TRIPLE,
                2 to 0,
                2 to 1,
                2 to 2,
            ),
        )
        assertTrue(
            placeShipOnBoard(
                boardB,
                ShipType.TRIPLE,
                4 to 0,
                4 to 1,
                4 to 2,
            ),
        )
        assertTrue(
            placeShipOnBoard(
                boardB,
                ShipType.DOUBLE,
                6 to 0,
                6 to 1,
            ),
        )
        assertTrue(
            placeShipOnBoard(
                boardB,
                ShipType.DOUBLE,
                8 to 0,
                9 to 0,
            ),
        )
        assertTrue(
            placeShipOnBoard(
                boardB,
                ShipType.DOUBLE,
                8 to 2,
                9 to 2,
            ),
        )
        assertTrue(
            placeShipOnBoard(
                boardB,
                ShipType.SINGLE,
                0 to 8,
            ),
        )
        assertTrue(
            placeShipOnBoard(
                boardB,
                ShipType.SINGLE,
                2 to 8,
            ),
        )
        assertTrue(
            placeShipOnBoard(
                boardB,
                ShipType.SINGLE,
                2 to 4,
            ),
        )
        assertTrue(
            placeShipOnBoard(
                boardB,
                ShipType.SINGLE,
                9 to 6,
            ),
        )

        game.start()

        // helper function for shoot with checks
        fun shoot(
            shooter: Player,
            targetX: Int,
            targetY: Int,
            expectedResult: MoveResult,
            expectedCellState: CellState,
            expectedNextPlayer: Player,
        ) {
            val targetCell = game.getBoard(game.getOpponent(shooter)).getCell(targetX, targetY)
            val move =
                ShotMove(shooter, targetCell)
            val result = game.processMove(move)
            assertEquals(
                expectedResult,
                result,
                "Unexpected result for shot at ($targetX,$targetY)",
            )
            assertEquals(
                expectedCellState,
                targetCell.state,
                "Cell state mismatch",
            )
            assertEquals(
                expectedNextPlayer,
                game.currentPlayer,
                "Next player mismatch",
            )
            if (expectedResult == MoveResult.GameOver) {
                assertTrue(game.gameOver, "Game should be over")
            }
        }

        // Alice begins and Alice is current player after hit
        shoot(alice, 0, 0, MoveResult.Success, CellState.HIT, alice)
        shoot(alice, 1, 0, MoveResult.Success, CellState.MISS, bob)

        shoot(bob, 0, 0, MoveResult.Success, CellState.HIT, bob)
        shoot(bob, 1, 0, MoveResult.Success, CellState.MISS, alice)

        shoot(alice, 0, 1, MoveResult.Success, CellState.HIT, alice)
        shoot(alice, 0, 2, MoveResult.Success, CellState.HIT, alice)
        shoot(alice, 0, 3, MoveResult.Success, CellState.HIT, alice)
        shoot(alice, 0, 4, MoveResult.Success, CellState.MISS, bob)

        shoot(bob, 0, 4, MoveResult.Success, CellState.MISS, alice)

        shoot(alice, 2, 0, MoveResult.Success, CellState.HIT, alice)
        shoot(alice, 2, 1, MoveResult.Success, CellState.HIT, alice)
        shoot(alice, 2, 2, MoveResult.Success, CellState.HIT, alice)
        shoot(alice, 3, 0, MoveResult.Success, CellState.MISS, bob)

        shoot(bob, 0, 5, MoveResult.Success, CellState.MISS, alice)

        shoot(alice, 4, 0, MoveResult.Success, CellState.HIT, alice)
        shoot(alice, 4, 1, MoveResult.Success, CellState.HIT, alice)
        shoot(alice, 4, 2, MoveResult.Success, CellState.HIT, alice)
        shoot(alice, 5, 0, MoveResult.Success, CellState.MISS, bob)

        shoot(bob, 0, 6, MoveResult.Success, CellState.MISS, alice)

        shoot(alice, 6, 0, MoveResult.Success, CellState.HIT, alice)
        shoot(alice, 6, 1, MoveResult.Success, CellState.HIT, alice)
        shoot(alice, 7, 0, MoveResult.Success, CellState.MISS, bob)

        shoot(bob, 0, 7, MoveResult.Success, CellState.MISS, alice)

        shoot(alice, 8, 0, MoveResult.Success, CellState.HIT, alice)
        shoot(alice, 9, 0, MoveResult.Success, CellState.HIT, alice)
        shoot(alice, 8, 1, MoveResult.Success, CellState.MISS, bob)

        shoot(bob, 1, 8, MoveResult.Success, CellState.MISS, alice)

        shoot(alice, 8, 2, MoveResult.Success, CellState.HIT, alice)
        shoot(alice, 9, 2, MoveResult.Success, CellState.HIT, alice)
        shoot(alice, 8, 3, MoveResult.Success, CellState.MISS, bob)

        shoot(bob, 1, 9, MoveResult.Success, CellState.MISS, alice)

        shoot(alice, 0, 8, MoveResult.Success, CellState.HIT, alice)
        shoot(alice, 0, 9, MoveResult.Success, CellState.MISS, bob)

        shoot(bob, 2, 9, MoveResult.Success, CellState.MISS, alice)

        shoot(alice, 2, 8, MoveResult.Success, CellState.HIT, alice)
        shoot(alice, 3, 8, MoveResult.Success, CellState.MISS, bob)

        shoot(bob, 3, 9, MoveResult.Success, CellState.MISS, alice)

        shoot(alice, 2, 4, MoveResult.Success, CellState.HIT, alice)
        shoot(alice, 2, 5, MoveResult.Success, CellState.MISS, bob)

        shoot(bob, 4, 9, MoveResult.Success, CellState.MISS, alice)

        val lastTarget = boardB.getCell(9, 6)
        val lastMove = ShotMove(alice, lastTarget)
        val lastResult = game.processMove(lastMove)
        assertEquals(MoveResult.GameOver, lastResult, "Game should be over after sinking last ship")
        assertEquals(CellState.HIT, lastTarget.state)
        assertTrue(game.gameOver)
        assertEquals(alice, game.getWinner())

        // check if next moves is impossible
        val extraMove = ShotMove(alice, boardB.getCell(5, 5))
        assertEquals(MoveResult.GameOver, game.processMove(extraMove))
    }

    @Test
    fun `full experienced game with limited shots`() {
        val alice = Player(1, "Alice")
        val bob = Player(2, "Bob")
        val game =
            Game(
                id = 2,
                player1 = alice,
                player2 = bob,
                mode = GameMode.EXPERIENCED,
            )

        val boardA = game.board1
        val boardB = game.board2

        // helper function for place ships with list of pairs (x,y)
        fun placeShipOnBoard(
            board: Board,
            type: ShipType,
            vararg positions: Pair<Int, Int>,
        ): Boolean {
            val cells = positions.map { board.getCell(it.first, it.second) }.toMutableList()
            return board.placeShip(Ship(type, cells))
        }

        assertTrue(placeShipOnBoard(boardA, ShipType.QUADRUPLE, 9 to 0, 9 to 1, 9 to 2, 9 to 3))
        assertTrue(placeShipOnBoard(boardA, ShipType.TRIPLE, 9 to 7, 9 to 8, 9 to 9))
        assertTrue(placeShipOnBoard(boardA, ShipType.TRIPLE, 7 to 0, 7 to 1, 7 to 2))
        assertTrue(placeShipOnBoard(boardA, ShipType.DOUBLE, 7 to 4, 7 to 5))
        assertTrue(placeShipOnBoard(boardA, ShipType.DOUBLE, 7 to 7, 6 to 7))
        assertTrue(placeShipOnBoard(boardA, ShipType.DOUBLE, 5 to 0, 5 to 1))
        assertTrue(placeShipOnBoard(boardA, ShipType.SINGLE, 9 to 5))
        assertTrue(placeShipOnBoard(boardA, ShipType.SINGLE, 5 to 3))
        assertTrue(placeShipOnBoard(boardA, ShipType.SINGLE, 5 to 5))
        assertTrue(placeShipOnBoard(boardA, ShipType.SINGLE, 7 to 9))

        assertTrue(placeShipOnBoard(boardB, ShipType.QUADRUPLE, 9 to 0, 9 to 1, 9 to 2, 9 to 3))
        assertTrue(placeShipOnBoard(boardB, ShipType.TRIPLE, 9 to 7, 9 to 8, 9 to 9))
        assertTrue(placeShipOnBoard(boardB, ShipType.TRIPLE, 7 to 0, 7 to 1, 7 to 2))
        assertTrue(placeShipOnBoard(boardB, ShipType.DOUBLE, 7 to 4, 7 to 5))
        assertTrue(placeShipOnBoard(boardB, ShipType.DOUBLE, 7 to 7, 6 to 7))
        assertTrue(placeShipOnBoard(boardB, ShipType.DOUBLE, 5 to 0, 5 to 1))
        assertTrue(placeShipOnBoard(boardB, ShipType.SINGLE, 9 to 5))
        assertTrue(placeShipOnBoard(boardB, ShipType.SINGLE, 5 to 3))
        assertTrue(placeShipOnBoard(boardB, ShipType.SINGLE, 5 to 5))
        assertTrue(placeShipOnBoard(boardB, ShipType.SINGLE, 7 to 9))

        game.start()

        // Alice has 10 alive ships -> 10 shoot
        assertEquals(10, game.shotsRemaining)

        fun shoot(
            shooter: Player,
            targetY: Int,
            targetX: Int,
            expectedShotsAfter: Int,
            expectedNextPlayer: Player,
        ) {
            val targetCell = game.getBoard(game.getOpponent(shooter)).getCell(targetY, targetX)
            val result = game.processMove(ShotMove(shooter, targetCell))
            assertEquals(MoveResult.Success, result)
            assertEquals(expectedShotsAfter, game.shotsRemaining, "Shots remaining mismatch")
            assertEquals(expectedNextPlayer, game.currentPlayer, "Next player mismatch")
        }

        // Alice shoot 10 times, first 9 -> Alice is current player
        for (i in 1..9) {
            shoot(alice, 0, i, 10 - i, alice)
        }
        // last shot, shotsRemaining = 10, because Bob is current player (all ships are alive)
        shoot(alice, 0, 0, 10, bob)

        assertEquals(10, game.shotsRemaining)

        shoot(bob, 9, 0, 9, bob)
        shoot(bob, 9, 1, 8, bob)
        shoot(bob, 9, 2, 7, bob)
        shoot(bob, 9, 3, 6, bob)
        // 4 hit, -1 ship
        shoot(bob, 9, 5, 5, bob)
        // 1 hit, -1 ship
        shoot(bob, 9, 7, 4, bob)
        shoot(bob, 9, 8, 3, bob)
        shoot(bob, 9, 9, 2, bob)
        // 3 hit, -1 ship
        shoot(bob, 6, 7, 1, bob)
        shoot(bob, 7, 7, 6, alice)
        // 2 hit, -1 ship
        // now alice, remain 6 ships

        for (i in 1..5) {
            shoot(alice, 1, i, 6 - i, alice)
        }

        shoot(alice, 1, 0, 10, bob)
        // game is not over
        assertFalse(game.gameOver)

        // now bob
        shoot(bob, 7, 0, 9, bob)
        shoot(bob, 7, 1, 8, bob)
        shoot(bob, 7, 2, 7, bob)
        // 3 hit, -1 ship
        shoot(bob, 7, 4, 6, bob)
        shoot(bob, 7, 5, 5, bob)
        // 2 hit, -1 ship
        shoot(bob, 7, 9, 4, bob)
        // 1 hit, -1 ship
        shoot(bob, 5, 0, 3, bob)
        shoot(bob, 5, 1, 2, bob)
        // 1 hit, -1 ship
        shoot(bob, 5, 3, 1, bob)
        // 1 hit, -1 ship
        // last Bob's shot
        val lastTarget = game.getBoard(game.getOpponent(bob)).getCell(5, 5)
        val lastResult = game.processMove(ShotMove(bob, lastTarget))
        assertEquals(MoveResult.GameOver, lastResult)
        assertTrue(game.gameOver)
        assertEquals(bob, game.getWinner())
    }

    @Test
    fun `full extended game with mines and aircraft`() {
        val alice = Player(1, "Alice")
        val bob = Player(2, "Bob")
        alice.aircrafts.addAll(
            listOf(
                Aircraft(AircraftType.SCOUT, remainingUses = 1),
                Aircraft(AircraftType.BOMBER, remainingUses = 1),
                Aircraft(AircraftType.TORPEDO, remainingUses = 1),
            ),
        )
        bob.aircrafts.addAll(
            listOf(
                Aircraft(AircraftType.SCOUT, remainingUses = 1),
                Aircraft(AircraftType.BOMBER, remainingUses = 1),
                Aircraft(AircraftType.TORPEDO, remainingUses = 1),
            ),
        )

        val game =
            Game(
                id = 3,
                player1 = alice,
                player2 = bob,
                mode = GameMode.EXTENDED,
            )

        val boardA = game.board1
        val boardB = game.board2

        fun placeShipOnBoard(
            board: Board,
            type: ShipType,
            vararg positions: Pair<Int, Int>,
        ): Boolean {
            val cells = positions.map { board.getCell(it.first, it.second) }.toMutableList()
            return board.placeShip(Ship(type, cells))
        }

        assertTrue(placeShipOnBoard(boardA, ShipType.QUADRUPLE, 0 to 0, 0 to 1, 0 to 2, 0 to 3))
        assertTrue(placeShipOnBoard(boardB, ShipType.QUADRUPLE, 0 to 0, 0 to 1, 0 to 2, 0 to 3))

        val mine1 = Mine(boardA.getCell(5, 5))
        assertTrue(boardA.placeMine(mine1))
        val mine2 = Mine(boardA.getCell(5, 6))
        assertTrue(boardA.placeMine(mine2))
        val mine3 = Mine(boardA.getCell(5, 7))
        assertTrue(boardA.placeMine(mine3))
        // check mine limit
        val mine4 = Mine(boardA.getCell(6, 6))
        val move4 = PlaceMineMove(alice, mine4)
        val result4 = game.processMove(move4)
        assertTrue(result4 is MoveResult.Invalid)

        val mineBob = Mine(boardB.getCell(7, 7))
        assertTrue(boardB.placeMine(mineBob))

        game.start()

        val shotMine = ShotMove(alice, boardB.getCell(7, 7))
        val resultMine = game.processMove(shotMine)
        assertEquals(MoveResult.Success, resultMine)
        assertEquals(CellState.EXPLODED_MINE, boardB.getCell(7, 7).state)
        // Alice's symmetric cell (7,7) EMPTY/SHIP -> MISS/HIT
        assertTrue(
            boardA.getCell(7, 7).state == CellState.MISS || boardA.getCell(7, 7).state == CellState.HIT,
            "Expected MISS or HIT but was ${boardA.getCell(7, 7).state}",
        )
        assertEquals(bob, game.currentPlayer)

        val scout = bob.aircrafts.find { it.type == AircraftType.SCOUT }!!
        val patternScout = scout.getAttackPattern(0, 0) // центр A1
        val targetCells = patternScout.map { (x, y) -> boardA.getCell(x, y) }
        val scoutMove = AircraftMove(bob, scout, targetCells)
        val resultScout = game.processMove(scoutMove)
        assertEquals(MoveResult.Success, resultScout)
        // remainingUses = 1 -> 0
        assertEquals(0, scout.remainingUses)
        // after aircraft next player always != current player
        assertEquals(alice, game.currentPlayer)

        val bomber = alice.aircrafts.find { it.type == AircraftType.BOMBER }!!
        val patternBomber = bomber.getAttackPattern(0, 0)
        val bomberTargets = patternBomber.map { (x, y) -> boardB.getCell(x, y) }
        val bomberMove = AircraftMove(alice, bomber, bomberTargets)
        val resultBomber = game.processMove(bomberMove)
        assertEquals(MoveResult.Success, resultBomber)
        assertEquals(CellState.HIT, boardB.getCell(0, 0).state)
        assertEquals(CellState.MISS, boardB.getCell(1, 0).state)
        assertEquals(bob, game.currentPlayer)

        val torpedo = bob.aircrafts.find { it.type == AircraftType.TORPEDO }!!
        val torpedoTargets = torpedo.getAttackPattern(0, 0).map { (x, y) -> boardA.getCell(x, y) }
        val torpedoMove = AircraftMove(bob, torpedo, torpedoTargets)
        val resultTorpedo = game.processMove(torpedoMove)
        assertEquals(MoveResult.GameOver, resultTorpedo)
        assertEquals(CellState.HIT, boardA.getCell(0, 0).state)
        assertTrue(
            boardA.getCell(7, 7).state == CellState.MISS || boardA.getCell(0, 1).state == CellState.HIT,
            "Expected MISS or HIT but was ${boardA.getCell(0, 1).state}",
        )

        assertTrue(game.gameOver)
        assertEquals(bob, game.getWinner())
    }
}
