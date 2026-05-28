package edu.battleship.model

import kotlinx.serialization.Serializable

@Serializable
data class Aircraft(
    val type: AircraftType,
    var remainingUses: Int = 1,
) {
    fun getAttackPattern(
        anchorX: Int,
        anchorY: Int,
    ): List<Pair<Int, Int>> =
        when (type) {
            AircraftType.SCOUT -> {
                val cells = mutableListOf<Pair<Int, Int>>()
                for (dx in -1..1) {
                    for (dy in -1..1) {
                        cells.add(anchorX + dx to anchorY + dy)
                    }
                }
                cells
            }

            AircraftType.BOMBER -> {
                listOf(
                    anchorX to anchorY,
                    anchorX - 1 to anchorY,
                    anchorX + 1 to anchorY,
                    anchorX to anchorY - 1,
                    anchorX to anchorY + 1,
                )
            }

            AircraftType.TORPEDO -> {
                (0..9).map { y -> anchorX to y }
            }
        }.filter { (x, y) -> x in 0..9 && y in 0..9 }
}
