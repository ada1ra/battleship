package edu.battleship.unit

import edu.battleship.model.Player
import edu.battleship.model.PlayerGameResult
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PlayerTest {
    @Test
    fun `player initial rating is zero`() {
        val player = Player(1, "Test")
        assertEquals(0, player.rating)
    }

    @Test
    fun `player game history is initially empty`() {
        val player = Player(1, "Test")
        assertTrue(player.gameHistory.isEmpty())
    }

    @Test
    fun `player aircrafts list is initially empty`() {
        val player = Player(1, "Test")
        assertTrue(player.aircrafts.isEmpty())
    }

    @Test
    fun `player can have game results added`() {
        val player = Player(1, "Test")
        val result = PlayerGameResult(1, "Opponent", true, 10, 5)
        player.gameHistory.add(result)
        assertEquals(1, player.gameHistory.size)
    }
}
