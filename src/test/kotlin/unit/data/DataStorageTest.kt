package edu.battleship.unit.data

import edu.battleship.data.GameDetail
import edu.battleship.data.GameSummary
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class DataStorageTest {
    @Test
    fun `gameSummary stores correct data`() {
        val summary = GameSummary(1, 1, 2, "STANDARD", 1)
        Assertions.assertEquals(1, summary.id)
        Assertions.assertEquals("STANDARD", summary.mode)
        Assertions.assertEquals(1, summary.winnerId)
    }

    @Test
    fun `gameDetail stores correct data`() {
        val detail =
            GameDetail(
                id = 1,
                player1Name = "Alice",
                player2Name = "Bob",
                mode = "STANDARD",
                winnerName = "Alice",
                finalBoardState1 = "test1",
                finalBoardState2 = "test2",
                moves = listOf("move1", "move2"),
            )
        Assertions.assertEquals(1, detail.id)
        Assertions.assertEquals(2, detail.moves.size)
    }
}
