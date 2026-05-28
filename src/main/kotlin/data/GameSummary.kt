package edu.battleship.data

@kotlinx.serialization.Serializable
data class GameSummary(
    val id: Int,
    val player1Id: Int,
    val player2Id: Int,
    val mode: String,
    val winnerId: Int,
)
