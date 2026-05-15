package edu.battleship.unit.model

import edu.battleship.model.Aircraft
import edu.battleship.model.AircraftType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class AircraftTest {
    @Test
    fun `scout returns 3x3 pattern`() {
        val aircraft = Aircraft(AircraftType.SCOUT, remainingUses = 1)
        val pattern = aircraft.getAttackPattern(5, 5)
        Assertions.assertEquals(9, pattern.size)
    }

    @Test
    fun `bomber returns cross pattern`() {
        val aircraft = Aircraft(AircraftType.BOMBER, remainingUses = 1)
        val pattern = aircraft.getAttackPattern(5, 5)
        Assertions.assertEquals(5, pattern.size)
    }

    @Test
    fun `torpedo returns full row pattern`() {
        val aircraft = Aircraft(AircraftType.TORPEDO, remainingUses = 1)
        val pattern = aircraft.getAttackPattern(5, 0)
        Assertions.assertEquals(10, pattern.size)
    }

    @Test
    fun `pattern is clamped to board boundaries`() {
        val aircraft = Aircraft(AircraftType.BOMBER, remainingUses = 1)
        val pattern = aircraft.getAttackPattern(0, 0)
        // cell outside the borders isn't included
        Assertions.assertEquals(3, pattern.size)
    }
}
