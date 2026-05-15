package edu.battleship.model

import kotlinx.serialization.Serializable

@Serializable
data class Player(
    val id: Int,
    val name: String,
    var rating: Int = 0,
    val gameHistory: MutableList<PlayerGameResult> = mutableListOf(),
    val aircrafts: MutableList<Aircraft> = mutableListOf(),
)
