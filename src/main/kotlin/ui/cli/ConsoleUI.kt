package edu.battleship.ui.cli

import edu.battleship.data.DataStorage
import edu.battleship.data.JsonDataStorage
import edu.battleship.engine.AircraftMove
import edu.battleship.engine.Game
import edu.battleship.engine.Move
import edu.battleship.engine.MoveResult
import edu.battleship.engine.ShotMove
import edu.battleship.model.Aircraft
import edu.battleship.model.AircraftType
import edu.battleship.model.Cell
import edu.battleship.model.CellState
import edu.battleship.model.GameMode
import edu.battleship.model.Mine
import edu.battleship.model.Player
import edu.battleship.model.Ship
import edu.battleship.model.ShipType
import edu.battleship.ui.UserInterface

class ConsoleUI : UserInterface {
    private val storage: DataStorage = JsonDataStorage()
    private var players = mutableListOf<Player>()
    private var nextPlayerId = 1
    private var nextGameId = 1

    override fun showMessage(msg: String) = println(msg)

    override fun readCommand(): String {
        print("> ")
        return readlnOrNull() ?: ""
    }

    override fun start() {
        loadPlayers()
        while (true) {
            showMessage("\n=== Main Menu ===")
            showMessage("1. New Game")
            showMessage("2. Manage Players")
            showMessage("3. Show Game Log")
            showMessage("4. Exit")
            when (readCommand()) {
                "1" -> newGame()
                "2" -> managePlayers()
                "3" -> showGameLog()
                "4" -> return
                else -> showMessage("Invalid option")
            }
        }
    }

    private fun loadPlayers() {
        players = storage.loadAllPlayers().toMutableList()
        nextPlayerId = (players.maxOfOrNull { it.id } ?: 0) + 1
        val games = storage.loadGameSummaries()
        nextGameId = (games.maxOfOrNull { it.id } ?: 0) + 1
    }

    private fun newGame() {
        if (players.size < 2) {
            showMessage("Need at least two players. Create new ones.")
            return
        }
        showMessage("Select Player 1:")
        val p1 = selectPlayer() ?: return
        showMessage("Select Player 2:")
        val p2 = selectPlayer(p1) ?: return

        showMessage("Select mode: (1) Standard, (2) Experienced, (3) Extended")
        val mode =
            when (readCommand()) {
                "1" -> GameMode.STANDARD
                "2" -> GameMode.EXPERIENCED
                "3" -> GameMode.EXTENDED
                else -> return
            }

        val game = Game(nextGameId++, p1, p2, mode)
        preparePlayersForGame(game)

        placementPhase(game)
        game.start()
        gameLoop(game)
        finishGame(game)
    }

    private fun preparePlayersForGame(game: Game) {
        game.player1.aircrafts.clear()
        game.player2.aircrafts.clear()
        if (game.mode == GameMode.EXTENDED) {
            for (player in listOf(game.player1, game.player2)) {
                showMessage("${player.name}: choose up to 3 aircraft types (scout/bomber/torpedo) one by one, empty to end")
                while (player.aircrafts.size < 3) {
                    showMessage("Enter aircraft type (or press enter to stop):")
                    val input = readCommand().lowercase()
                    if (input.isEmpty()) break
                    val type =
                        when (input) {
                            "scout" -> AircraftType.SCOUT
                            "bomber" -> AircraftType.BOMBER
                            "torpedo" -> AircraftType.TORPEDO
                            else -> null
                        }
                    if (type != null) {
                        player.aircrafts.add(Aircraft(type))
                    } else {
                        showMessage("Unknown type")
                    }
                }
            }
        }
    }

    private fun placementPhase(game: Game) {
        val shipSizes = listOf(4, 3, 3, 2, 2, 2, 1, 1, 1, 1)
        for (player in listOf(game.player1, game.player2)) {
            showMessage("\n${player.name}, place your ships.")
            val board = game.getBoard(player)
            for (size in shipSizes) {
                while (true) {
                    BoardRenderer.printBoard(board, false)
                    showMessage("Place ship of size $size (format: A1 H):")
                    val input = readCommand().split(" ")
                    if (input.size != 2) {
                        showMessage("Invalid format. Use: A1 H")
                        continue
                    }

                    val coord = input[0]
                    if (coord.length < 2) continue
                    val letter = coord[0].uppercaseChar()
                    val numberStr = coord.substring(1)
                    val number = numberStr.toIntOrNull()
                    if (letter !in 'A'..'J' || number == null || number !in 1..10) continue
                    val x = letter - 'A'
                    val y = number - 1

                    // orientation: v(vertical)/h(horizontal)
                    val orientation = input[1].uppercase()
                    if (orientation != "H" && orientation != "V") continue
                    val horizontal = orientation == "H"

                    val cells = mutableListOf<Cell>()
                    for (i in 0 until size) {
                        val cx = if (horizontal) x else x + i
                        val cy = if (horizontal) y + i else y
                        if (cx !in 0..9 || cy !in 0..9) break
                        cells.add(board.getCell(cx, cy))
                    }
                    if (cells.size != size) {
                        showMessage("Ship does not fit. Try again.")
                        continue
                    }

                    val ship = Ship(ShipType.entries.first { it.size == size }, cells)
                    if (board.placeShip(ship)) {
                        break
                    } else {
                        showMessage("Can't place here (overlap or invalid). Try again.")
                    }
                }
            }

            if (game.mode == GameMode.EXTENDED) {
                showMessage("Place up to 3 mines (enter coordinate or 'done'):")
                while (board.mines.size < 3) {
                    val input = readCommand()
                    if (input == "done") break
                    if (input.length < 2) continue
                    val x = input[0].uppercaseChar() - 'A'
                    val y = input.substring(1).toIntOrNull()?.minus(1) ?: continue
                    if (x in 0..9 && y in 0..9) {
                        val cell = board.getCell(x, y)
                        if (board.placeMine(Mine(cell))) {
                            showMessage("Mine placed.")
                        } else {
                            showMessage("Cannot place mine there.")
                        }
                    }
                }
            }
        }
    }

    private fun gameLoop(game: Game) {
        while (!game.gameOver) {
            val current = game.currentPlayer
            showMessage("\n${current.name}'s turn (${game.shotsRemaining} shots)")
            BoardRenderer.printBoard(game.getBoard(game.getOpponent(current)), true)
            showMessage("Your board:")
            BoardRenderer.printBoard(game.getBoard(current), false)

            if (game.mode == GameMode.EXTENDED && current.aircrafts.isNotEmpty()) {
                showMessage("Choose action: shot or aircraft type (scout/bomber/torpedo)")
            } else {
                showMessage("Enter shot coordinate (e.g., shot B5):")
            }
            val cmd = readCommand()
            val move = parseMove(cmd, current, game)
            if (move == null) {
                showMessage("Invalid command")
                continue
            }
            when (val result = game.processMove(move)) {
                is MoveResult.Success -> {
                    if (move is AircraftMove && move.aircraft.type == AircraftType.SCOUT) {
                        showMessage("Scout report (${move.targetCells.size} cells):")
                        for (cell in move.targetCells) {
                            val status =
                                when (cell.state) {
                                    CellState.SHIP -> "Ship"
                                    CellState.MINED -> "Mine"
                                    CellState.EMPTY -> "Empty"
                                    else -> "Unknown"
                                }
                            showMessage("  ${('A' + cell.x)}${cell.y + 1}: $status")
                        }
                    }
                    showMessage("Move accepted")
                }
                is MoveResult.Invalid -> showMessage("Error: ${result.reason}")
                is MoveResult.GameOver -> break
            }
        }
    }

    private fun parseMove(
        input: String,
        player: Player,
        game: Game,
    ): Move? {
        val parts = input.split(" ")
        if (parts.isEmpty()) return null
        return when (parts[0].lowercase()) {
            "shot" -> {
                if (parts.size < 2) return null
                val coord = parts[1]
                val x = coord[0] - 'A'
                val y = coord.drop(1).toIntOrNull()?.minus(1) ?: return null
                if (x !in 0..9 || y !in 0..9) return null
                val targetCell = game.getBoard(game.getOpponent(player)).getCell(x, y)
                ShotMove(player, targetCell)
            }

            "scout", "bomber", "torpedo" -> {
                if (game.mode != GameMode.EXTENDED) return null
                val typeName = parts[0]
                val type =
                    when (typeName.lowercase()) {
                        "scout" -> AircraftType.SCOUT
                        "bomber" -> AircraftType.BOMBER
                        "torpedo" -> AircraftType.TORPEDO
                        else -> return null
                    }
                val aircraft = player.aircrafts.find { it.type == type && it.remainingUses > 0 } ?: return null
                val anchorIndex = if (parts[0] == "a") 2 else 1
                val anchorCoord = parts.getOrNull(anchorIndex) ?: return null
                val ax = anchorCoord[0] - 'A'
                val ay = anchorCoord.drop(1).toIntOrNull()?.minus(1) ?: return null
                if (ax !in 0..9 || ay !in 0..9) return null
                val pattern = aircraft.getAttackPattern(ax, ay)
                val targetCells = pattern.map { (x, y) -> game.getBoard(game.getOpponent(player)).getCell(x, y) }
                AircraftMove(player, aircraft, targetCells)
            }

            else -> null
        }
    }

    private fun finishGame(game: Game) {
        showMessage("\nGame over! Winner: ${game.getWinner()?.name ?: "Draw"}")
        storage.completeGame(game)
    }

    private fun selectPlayer(exclude: Player? = null): Player? {
        val available = players.filter { it != exclude }
        if (available.isEmpty()) {
            showMessage("No other players available.")
            return null
        }
        available.forEachIndexed { i, p ->
            showMessage("${i + 1}. ${p.name} (ID: ${p.id}, Rating: ${p.rating})")
        }
        showMessage("0. Create new player")
        val choice = readCommand().toIntOrNull() ?: return null
        return if (choice == 0) {
            showMessage("Enter name:")
            val name = readCommand()
            val p = Player(nextPlayerId++, name)
            players.add(p)
            storage.saveAllPlayers(players)
            p
        } else {
            val selected = available.getOrNull(choice - 1)
            if (selected == exclude) {
                showMessage("You can't choose the same player twice.")
                return null
            }
            selected
        }
    }

    private fun managePlayers() {
        while (true) {
            showMessage("\n=== Players ===")
            players.forEach { showMessage("${it.id}: ${it.name} (${it.rating})") }
            showMessage("A. Add player")
            showMessage("B. Back")
            when (readCommand().lowercase()) {
                "a" -> {
                    showMessage("Name:")
                    val name = readCommand()
                    players.add(Player(nextPlayerId++, name))
                    storage.saveAllPlayers(players)
                }

                "b" -> return
            }
        }
    }

    private fun showGameLog() {
        val summaries = storage.loadGameSummaries()
        if (summaries.isEmpty()) {
            showMessage("No completed games found.")
            return
        }
        showMessage("\n=== Game History ===")
        summaries.forEach { summary ->
            val detail = storage.loadGameDetail(summary.id)
            val player1Name = detail?.player1Name ?: "Unknown"
            val player2Name = detail?.player2Name ?: "Unknown"
            val winnerName = detail?.winnerName ?: "Draw"
            showMessage("Game ${summary.id}: $player1Name vs $player2Name | Mode: ${summary.mode} | Winner: $winnerName")
        }
        showMessage("\nEnter game ID for details (or press Enter to return):")
        val id = readCommand().toIntOrNull() ?: return
        val detail = storage.loadGameDetail(id)
        if (detail != null) {
            showMessage(detail.toString())
        } else {
            showMessage("Game not found")
        }
    }
}
