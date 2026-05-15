package edu.battleship.data

import edu.battleship.engine.Game
import edu.battleship.model.Player
import edu.battleship.model.PlayerGameResult
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class JsonDataStorage : DataStorage {
    private val mainDir = File("history")
    private val playersSummaryFile = File("history/players.json")
    private val gamesSummaryFile = File("history/games.json")
    private val gamesDir = File("history/games")
    private val playersDir = File("history/players")
    private val json = Json { prettyPrint = true }

    init {
        mainDir.mkdirs()
        gamesDir.mkdirs()
        playersDir.mkdirs()
    }

    override fun loadAllPlayers(): List<Player> {
        if (!playersSummaryFile.exists()) return emptyList()
        val text = playersSummaryFile.readText()
        if (text.isBlank()) return emptyList()
        return json.decodeFromString(text)
    }

    override fun saveAllPlayers(players: List<Player>) {
        playersSummaryFile.writeText(json.encodeToString(players))
        for (player in players) {
            File(playersDir, "player${player.id}.json").writeText(json.encodeToString(player))
        }
    }

    override fun loadGameSummaries(): List<GameSummary> {
        if (!gamesSummaryFile.exists()) return emptyList()
        val text = gamesSummaryFile.readText()
        if (text.isBlank()) return emptyList()
        return json.decodeFromString(text)
    }

    override fun saveGameSummary(summary: GameSummary) {
        val summaries = loadGameSummaries().toMutableList()
        summaries.add(summary)
        gamesSummaryFile.writeText(json.encodeToString(summaries))
    }

    override fun loadGameDetail(id: Int): GameDetail? {
        val file = File(gamesDir, "game$id.json")
        if (!file.exists()) return null
        return json.decodeFromString(file.readText())
    }

    override fun saveGameDetail(
        id: Int,
        detail: GameDetail,
    ) {
        File(gamesDir, "game$id.json").writeText(json.encodeToString(detail))
    }

    override fun completeGame(game: Game) {
        val winner = game.getWinner()
        val loser = if (winner == game.player1) game.player2 else game.player1

        winner?.let { it.rating += 25 }
        loser.rating = (loser.rating - 25).coerceAtLeast(0)

        val shotsFired = game.moveHistory.size
        winner?.let { w ->
            w.gameHistory.add(
                PlayerGameResult(
                    game.id,
                    loser.name,
                    true,
                    shotsFired,
                    game.getBoard(loser).ships.count { it.isSunk() },
                ),
            )
            loser.gameHistory.add(
                PlayerGameResult(
                    game.id,
                    w.name,
                    false,
                    shotsFired,
                    game.getBoard(w).ships.count { it.isSunk() },
                ),
            )
        }

        val allPlayers = loadAllPlayers().toMutableList()

        fun updatePlayer(p: Player) {
            val idx = allPlayers.indexOfFirst { it.id == p.id }
            if (idx >= 0) allPlayers[idx] = p else allPlayers.add(p)
        }
        updatePlayer(game.player1)
        updatePlayer(game.player2)
        saveAllPlayers(allPlayers)

        saveGameSummary(
            GameSummary(
                game.id,
                game.player1.id,
                game.player2.id,
                game.mode.name,
                winner?.id ?: -1,
            ),
        )
        saveGameDetail(game.id, GameDetail.fromGame(game))
    }
}
