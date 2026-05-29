package edu.battleship.data

import edu.battleship.engine.Game
import edu.battleship.model.Player
import edu.battleship.model.PlayerGameResult
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.sql.Connection
import java.sql.DriverManager

class SqliteDataStorage(
    private val dbPath: String = "history/history.sqlite",
) : DataStorage {
    init {
        val dbFile = java.io.File(dbPath)
        dbFile.parentFile?.mkdirs()
    }

    private val connection: Connection by lazy {
        DriverManager.getConnection("jdbc:sqlite:$dbPath").also { conn ->
            initDatabase(conn)
        }
    }

    private val json = Json { prettyPrint = true }

    private fun initDatabase(conn: Connection) {
        conn.createStatement().use { stmt ->
            stmt.executeUpdate(
                """
                CREATE TABLE IF NOT EXISTS players (
                    id INTEGER PRIMARY KEY,
                    name TEXT NOT NULL,
                    rating INTEGER NOT NULL DEFAULT 0
                )
                """.trimIndent(),
            )

            stmt.executeUpdate(
                """
                CREATE TABLE IF NOT EXISTS games_summary (
                    id INTEGER PRIMARY KEY,
                    player1_id INTEGER NOT NULL,
                    player2_id INTEGER NOT NULL,
                    mode TEXT NOT NULL,
                    winner_id INTEGER,
                    FOREIGN KEY (player1_id) REFERENCES players(id),
                    FOREIGN KEY (player2_id) REFERENCES players(id)
                )
                """.trimIndent(),
            )

            stmt.executeUpdate(
                """
                CREATE TABLE IF NOT EXISTS game_details (
                    id INTEGER PRIMARY KEY,
                    player1_name TEXT NOT NULL,
                    player2_name TEXT NOT NULL,
                    mode TEXT NOT NULL,
                    winner_name TEXT,
                    final_board_state1 TEXT NOT NULL,
                    final_board_state2 TEXT NOT NULL,
                    moves_json TEXT NOT NULL
                )
                """.trimIndent(),
            )

            stmt.executeUpdate(
                """
                CREATE TABLE IF NOT EXISTS player_game_results (
                    player_id INTEGER NOT NULL,
                    game_id INTEGER NOT NULL,
                    opponent_name TEXT NOT NULL,
                    won INTEGER NOT NULL,
                    shots_fired INTEGER NOT NULL,
                    ships_lost INTEGER NOT NULL,
                    PRIMARY KEY (player_id, game_id),
                    FOREIGN KEY (player_id) REFERENCES players(id),
                    FOREIGN KEY (game_id) REFERENCES games_summary(id)
                )
                """.trimIndent(),
            )
        }
    }

    override fun loadAllPlayers(): List<Player> {
        val players = mutableListOf<Player>()
        connection.prepareStatement("SELECT id, name, rating FROM players").use { ps ->
            ps.executeQuery().use { rs ->
                while (rs.next()) {
                    val player =
                        Player(
                            id = rs.getInt("id"),
                            name = rs.getString("name"),
                            rating = rs.getInt("rating"),
                        )
                    player.gameHistory.addAll(loadPlayerGameHistory(player.id))
                    players.add(player)
                }
            }
        }
        return players
    }

    private fun loadPlayerGameHistory(playerId: Int): List<PlayerGameResult> {
        val results = mutableListOf<PlayerGameResult>()
        connection
            .prepareStatement(
                "SELECT game_id, opponent_name, won, shots_fired, ships_lost FROM player_game_results WHERE player_id = ?",
            ).use { ps ->
                ps.setInt(1, playerId)
                ps.executeQuery().use { rs ->
                    while (rs.next()) {
                        results.add(
                            PlayerGameResult(
                                gameId = rs.getInt("game_id"),
                                opponentName = rs.getString("opponent_name"),
                                won = rs.getBoolean("won"),
                                shotsFired = rs.getInt("shots_fired"),
                                shipsLost = rs.getInt("ships_lost"),
                            ),
                        )
                    }
                }
            }
        return results
    }

    override fun saveAllPlayers(players: List<Player>) {
        connection.autoCommit = false
        try {
            connection.createStatement().use { stmt ->
                stmt.executeUpdate("DELETE FROM player_game_results")
                stmt.executeUpdate("DELETE FROM players")
            }
            for (player in players) {
                connection
                    .prepareStatement(
                        "INSERT OR REPLACE INTO players (id, name, rating) VALUES (?, ?, ?)",
                    ).use { ps ->
                        ps.setInt(1, player.id)
                        ps.setString(2, player.name)
                        ps.setInt(3, player.rating)
                        ps.executeUpdate()
                    }
                for (result in player.gameHistory) {
                    connection
                        .prepareStatement(
                            "INSERT OR REPLACE INTO player_game_results (player_id, game_id, opponent_name, won, shots_fired, ships_lost) VALUES (?, ?, ?, ?, ?, ?)",
                        ).use { ps ->
                            ps.setInt(1, player.id)
                            ps.setInt(2, result.gameId)
                            ps.setString(3, result.opponentName)
                            ps.setInt(4, if (result.won) 1 else 0)
                            ps.setInt(5, result.shotsFired)
                            ps.setInt(6, result.shipsLost)
                            ps.executeUpdate()
                        }
                }
            }
            connection.commit()
        } catch (e: Exception) {
            connection.rollback()
            throw e
        } finally {
            connection.autoCommit = true
        }
    }

    override fun loadGameSummaries(): List<GameSummary> {
        val summaries = mutableListOf<GameSummary>()
        connection.createStatement().use { stmt ->
            stmt.executeQuery("SELECT id, player1_id, player2_id, mode, winner_id FROM games_summary").use { rs ->
                while (rs.next()) {
                    summaries.add(
                        GameSummary(
                            id = rs.getInt("id"),
                            player1Id = rs.getInt("player1_id"),
                            player2Id = rs.getInt("player2_id"),
                            mode = rs.getString("mode"),
                            winnerId = rs.getInt("winner_id"),
                        ),
                    )
                }
            }
        }
        return summaries
    }

    override fun saveGameSummary(summary: GameSummary) {
        connection
            .prepareStatement(
                "INSERT OR REPLACE INTO games_summary (id, player1_id, player2_id, mode, winner_id) VALUES (?, ?, ?, ?, ?)",
            ).use { ps ->
                ps.setInt(1, summary.id)
                ps.setInt(2, summary.player1Id)
                ps.setInt(3, summary.player2Id)
                ps.setString(4, summary.mode)
                ps.setInt(5, summary.winnerId)
                ps.executeUpdate()
            }
    }

    override fun loadGameDetail(id: Int): GameDetail? {
        connection
            .prepareStatement(
                "SELECT id, player1_name, player2_name, mode, winner_name, final_board_state1, final_board_state2, moves_json FROM game_details WHERE id = ?",
            ).use { ps ->
                ps.setInt(1, id)
                ps.executeQuery().use { rs ->
                    if (rs.next()) {
                        return GameDetail(
                            id = rs.getInt("id"),
                            player1Name = rs.getString("player1_name"),
                            player2Name = rs.getString("player2_name"),
                            mode = rs.getString("mode"),
                            winnerName = rs.getString("winner_name"),
                            finalBoardState1 = rs.getString("final_board_state1"),
                            finalBoardState2 = rs.getString("final_board_state2"),
                            moves = json.decodeFromString(rs.getString("moves_json")),
                        )
                    }
                }
            }
        return null
    }

    override fun saveGameDetail(
        id: Int,
        detail: GameDetail,
    ) {
        connection
            .prepareStatement(
                "INSERT OR REPLACE INTO game_details (id, player1_name, player2_name, mode, winner_name, final_board_state1, final_board_state2, moves_json) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
            ).use { ps ->
                ps.setInt(1, id)
                ps.setString(2, detail.player1Name)
                ps.setString(3, detail.player2Name)
                ps.setString(4, detail.mode)
                ps.setString(5, detail.winnerName)
                ps.setString(6, detail.finalBoardState1)
                ps.setString(7, detail.finalBoardState2)
                ps.setString(8, json.encodeToString(detail.moves))
                ps.executeUpdate()
            }
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
                    gameId = game.id,
                    opponentName = loser.name,
                    won = true,
                    shotsFired = shotsFired,
                    shipsLost = game.getBoard(loser).ships.count { it.isSunk() },
                ),
            )
            loser.gameHistory.add(
                PlayerGameResult(
                    gameId = game.id,
                    opponentName = w.name,
                    won = false,
                    shotsFired = shotsFired,
                    shipsLost = game.getBoard(w).ships.count { it.isSunk() },
                ),
            )
        }

        saveAllPlayers(listOf(game.player1, game.player2))

        saveGameSummary(
            GameSummary(
                id = game.id,
                player1Id = game.player1.id,
                player2Id = game.player2.id,
                mode = game.mode.name,
                winnerId = winner?.id ?: -1,
            ),
        )
        saveGameDetail(game.id, GameDetail.fromGame(game))
    }
}
