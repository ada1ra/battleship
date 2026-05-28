package edu.battleship.data

import edu.battleship.engine.Game
import edu.battleship.model.Player

interface DataStorage {
    fun loadAllPlayers(): List<Player>

    fun saveAllPlayers(players: List<Player>)

    fun loadGameSummaries(): List<GameSummary>

    fun saveGameSummary(summary: GameSummary)

    fun loadGameDetail(id: Int): GameDetail?

    fun saveGameDetail(
        id: Int,
        detail: GameDetail,
    )

    fun completeGame(game: Game)
}

internal fun formatCoordinate(
    x: Int,
    y: Int,
) = "${'A' + x}${y + 1}"
