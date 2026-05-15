package edu.battleship.model

import kotlinx.serialization.Serializable

@Serializable
enum class AircraftType {
    SCOUT,
    BOMBER,
    TORPEDO,
}
