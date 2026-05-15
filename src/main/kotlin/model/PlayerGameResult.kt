package edu.battleship.model

import kotlinx.serialization.Serializable

@Serializable
data class PlayerGameResult(
    val gameId: Int,
    val opponentName: String,
    val won: Boolean,
    val shotsFired: Int,
    val shipsLost: Int,
)
