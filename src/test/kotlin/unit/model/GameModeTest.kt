package edu.battleship.unit.model

import edu.battleship.model.GameMode
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class GameModeTest {
    @Test
    fun `gameMode has three values`() {
        Assertions.assertEquals(3, GameMode.entries.size)
    }

    @Test
    fun `gameMode values are correct`() {
        Assertions.assertEquals(GameMode.STANDARD, GameMode.valueOf("STANDARD"))
        Assertions.assertEquals(GameMode.EXPERIENCED, GameMode.valueOf("EXPERIENCED"))
        Assertions.assertEquals(GameMode.EXTENDED, GameMode.valueOf("EXTENDED"))
    }
}
