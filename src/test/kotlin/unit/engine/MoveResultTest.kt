package edu.battleship.unit.engine

import edu.battleship.engine.MoveResult
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MoveResultTest {
    @Test
    fun `success is singleton`() {
        val s1 = MoveResult.Success
        val s2 = MoveResult.Success
        assertEquals(s1, s2)
    }

    @Test
    fun `invalid holds reason`() {
        val reason = "Test reason"
        val result = MoveResult.Invalid(reason)
        assertEquals(reason, result.reason)
    }

    @Test
    fun `gameOver is singleton`() {
        val g1 = MoveResult.GameOver
        val g2 = MoveResult.GameOver
        assertEquals(g1, g2)
    }
}
