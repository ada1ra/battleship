package edu.battleship.ui.gui

import edu.battleship.data.DataStorage
import edu.battleship.data.JsonDataStorage
import edu.battleship.engine.AircraftMove
import edu.battleship.engine.Game
import edu.battleship.engine.MoveResult
import edu.battleship.engine.ShotMove
import edu.battleship.model.Aircraft
import edu.battleship.model.AircraftType
import edu.battleship.model.Board
import edu.battleship.model.Cell
import edu.battleship.model.CellState
import edu.battleship.model.GameMode
import edu.battleship.model.Mine
import edu.battleship.model.Player
import edu.battleship.model.Ship
import edu.battleship.model.ShipType
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.stage.Modality
import javafx.stage.Stage

class GameView {
    enum class DisplayMode { TURN_BASED, SIMULTANEOUS }

    var displayMode: DisplayMode = DisplayMode.TURN_BASED

    private val cellSize = 40.0
    private var game: Game? = null
    private var placementPhase = true
    private var placingMines = false
    private var selectingAircraft = false
    private var currentPlayerPlacing = 1
    private val shipSizes = listOf(4, 3, 3, 2, 2, 2, 1, 1, 1, 1)
    private var currentShipIndex = 0
    private var horizontal = true
    private var minesPlaced = 0
    private var aircraftSelected = 0
    private var waitingForAircraftAnchor: Aircraft? = null

    private var myBoardGrid: GridPane? = null
    private var opponentBoardGrid: GridPane? = null
    private var infoLabel: Label? = null
    private var rotateBtn: Button? = null
    private var doneBtn: Button? = null
    private var aircraftBtn: Button? = null
    private var boardsBox: HBox? = null
    private var stage: Stage? = null

    private var storage: DataStorage? = null

    // ============== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ==============

    /** Текущая доска и игрок, который сейчас действует */
    private fun currentBoardAndPlayer(): Pair<Board, Player> {
        val g = game ?: error("Game not started")
        return if (currentPlayerPlacing == 1) {
            Pair(g.board1, g.player1)
        } else {
            Pair(g.board2, g.player2)
        }
    }

    /** Возвращает GridPane для доски текущего игрока в режиме SIMULTANEOUS */
    private fun getSimultaneousGridForCurrentPlayer(): GridPane? {
        val idx = currentPlayerPlacing - 1
        val vbox = boardsBox?.children?.getOrNull(idx) as? VBox ?: return null
        return vbox.children.getOrNull(1) as? GridPane
    }

    /** Обновляет доску текущего игрока (или указанную) в зависимости от режима отображения */
    private fun updatePlayerBoard(
        board: Board,
        hideShips: Boolean = false,
    ) {
        if (displayMode == DisplayMode.SIMULTANEOUS) {
            val grid = getSimultaneousGridForCurrentPlayer()
            grid?.let { updateBoardDisplay(it, board, hideShips) }
        } else {
            myBoardGrid?.let { updateBoardDisplay(it, board, hideShips) }
        }
    }

    /** Перерисовывает все доски в соответствии с текущим состоянием */
    private fun refreshAllBoards() {
        if (game == null) return
        if (displayMode == DisplayMode.SIMULTANEOUS) {
            updateAllBoards()
            highlightActivePlayer()
        } else {
            updateAllBoards()
        }
    }

    /** Показывает сообщение в верхней панели */
    private fun showMessage(text: String) {
        infoLabel?.text = text
    }

    // ============== ФАЗА РАССТАНОВКИ КОРАБЛЕЙ ==============

    /** Обработчик клика для размещения корабля */
    private fun handlePlacementClick(
        x: Int,
        y: Int,
    ) {
        if (currentShipIndex >= shipSizes.size) return
        val (board, player) = currentBoardAndPlayer()
        val size = shipSizes[currentShipIndex]
        val cells = mutableListOf<Cell>()
        for (i in 0 until size) {
            val cx = if (horizontal) x else x + i
            val cy = if (horizontal) y + i else y
            if (cx !in 0..9 || cy !in 0..9) {
                showMessage("Ship out of bounds!")
                return
            }
            cells.add(board.getCell(cx, cy))
        }
        val ship = Ship(ShipType.entries.first { it.size == size }, cells)
        if (board.placeShip(ship)) {
            updatePlayerBoard(board, hideShips = false)
            currentShipIndex++
            if (currentShipIndex < shipSizes.size) {
                showMessage("Player $currentPlayerPlacing: ${player.name} — place ship of size ${shipSizes[currentShipIndex]}")
            } else {
                finishPlacementStage()
            }
        } else {
            showMessage("Can't place ship here")
        }
    }

    /** Завершение этапа размещения кораблей: переход к минам или ко второму игроку */
    private fun finishPlacementStage() {
        val g = game ?: return
        if (currentPlayerPlacing == 1) {
            if (g.mode == GameMode.EXTENDED) {
                startMinePlacement()
            } else {
                switchToNextPlayer()
            }
        } else {
            if (g.mode == GameMode.EXTENDED) {
                startMinePlacement()
            } else {
                startPlaying()
            }
        }
    }

    private fun switchToNextPlayer() {
        currentPlayerPlacing = 2
        currentShipIndex = 0
        if (displayMode == DisplayMode.SIMULTANEOUS) {
            recreateBoardsForCurrentPlayer()
        } else {
            replaceMyBoardWith(game!!.board2)
        }
        rotateBtn?.isVisible = true
        doneBtn?.isVisible = false
        showMessage("Player 2: ${game!!.player2.name} — place ship of size ${shipSizes[0]}")
    }

    // ============== ФАЗА УСТАНОВКИ МИН ==============

    private fun startMinePlacement() {
        placingMines = true
        minesPlaced = 0
        rotateBtn?.isVisible = false
        doneBtn?.isVisible = true
        val player = currentBoardAndPlayer().second
        showMessage("${player.name}: place up to 3 mines (click your board). Press 'Done'")
        if (displayMode == DisplayMode.SIMULTANEOUS) {
            recreateBoardsForCurrentPlayer()
        }
    }

    private fun handleMinePlacement(
        x: Int,
        y: Int,
    ) {
        val (board, player) = currentBoardAndPlayer()
        if (minesPlaced >= 3) {
            showMessage("Maximum 3 mines already placed. Press 'Done'")
            return
        }
        val mine = Mine(board.getCell(x, y))
        if (board.placeMine(mine)) {
            minesPlaced++
            updatePlayerBoard(board, hideShips = false)
            showMessage("${player.name}: mine placed ($minesPlaced/3). Press 'Done' to finish")
        } else {
            showMessage("Can't place mine here")
        }
    }

    private fun finishMines() {
        placingMines = false
        val g = game ?: return
        if (g.mode == GameMode.EXTENDED) {
            startAircraftSelection()
        } else {
            if (currentPlayerPlacing == 1) {
                switchToNextPlayer()
            } else {
                startPlaying()
            }
        }
    }

    // ============== ФАЗА ВЫБОРА САМОЛЁТОВ ==============

    private fun toggleAircraftButtons(visible: Boolean) {
        val controlBox = (stage?.scene?.root as? VBox)?.children?.filterIsInstance<HBox>()?.lastOrNull() ?: return
        controlBox.children.removeIf { it is Button && it.text in listOf("Scout", "Bomber", "Torpedo") }
        if (visible) {
            val scoutBtn =
                Button("Scout").apply {
                    style = GuiStyles.ACTION_BUTTON_STYLE
                    setOnAction { selectAircraft(AircraftType.SCOUT) }
                }
            val bomberBtn =
                Button("Bomber").apply {
                    style = GuiStyles.ACTION_BUTTON_STYLE
                    setOnAction { selectAircraft(AircraftType.BOMBER) }
                }
            val torpedoBtn =
                Button("Torpedo").apply {
                    style = GuiStyles.ACTION_BUTTON_STYLE
                    setOnAction { selectAircraft(AircraftType.TORPEDO) }
                }
            controlBox.children.addAll(0, listOf(scoutBtn, bomberBtn, torpedoBtn))
        }
    }

    private fun startAircraftSelection() {
        selectingAircraft = true
        aircraftSelected = 0
        val player = currentBoardAndPlayer().second
        showMessage("${player.name}: select 3 aircraft")
        toggleAircraftButtons(true)
    }

    private fun selectAircraft(type: AircraftType) {
        val (_, player) = currentBoardAndPlayer()
        if (aircraftSelected >= 3) {
            showMessage("Already selected 3 aircraft. Press 'Done'")
            return
        }
        player.aircrafts.add(Aircraft(type))
        aircraftSelected++
        showMessage("${player.name}: selected $type ($aircraftSelected/3)")
        if (aircraftSelected == 3) {
            showMessage("${player.name}: 3 aircraft selected. Press 'Done'")
        }
    }

    private fun finishAircraftSelection() {
        selectingAircraft = false
        toggleAircraftButtons(false)
        if (currentPlayerPlacing == 1) {
            switchToNextPlayer()
        } else {
            startPlaying()
        }
    }

    // ============== ФАЗА ИГРЫ ==============

    private fun startPlaying() {
        placementPhase = false
        placingMines = false
        selectingAircraft = false
        rotateBtn?.isVisible = false
        doneBtn?.isVisible = false
        game?.start()
        showMessage("Game started! ${game?.currentPlayer?.name}'s turn")
        toggleAircraftButtons(false)
        updateAircraftButton()
        if (displayMode == DisplayMode.SIMULTANEOUS) {
            setupSimultaneousMode()
        } else {
            updateAllBoards()
        }
    }

    private fun handleShotClick(
        x: Int,
        y: Int,
    ) {
        val g = game ?: return
        if (g.gameOver) return
        val target = g.getBoard(g.getOpponent(g.currentPlayer)).getCell(x, y)
        val move = ShotMove(g.currentPlayer, target)
        when (val result = g.processMove(move)) {
            is MoveResult.Success -> {
                val state = target.state
                val text =
                    when (state) {
                        CellState.HIT -> "Hit!"
                        CellState.MISS -> "Miss!"
                        CellState.EXPLODED_MINE -> "Mine exploded!"
                        else -> "Shot fired"
                    }
                val sunk =
                    g.getBoard(g.getOpponent(g.currentPlayer)).ships.find {
                        it.cells.contains(target) && it.isSunk()
                    }
                showMessage("$text${if (sunk != null) " Ship sunk!" else ""} ${g.currentPlayer.name}'s turn")
                refreshAllBoards()
                updateAircraftButton()
            }
            is MoveResult.Invalid -> showMessage("Invalid move: ${result.reason}")
            is MoveResult.GameOver -> {
                showMessage("Game Over! Winner: ${g.getWinner()?.name}")
                refreshAllBoards()
                saveGameAndUpdateRatings(g)
            }
        }
    }

    private fun handleSimultaneousShot(
        x: Int,
        y: Int,
        targetBoard: Board,
    ) {
        val g = game ?: return
        if (g.gameOver) return
        if (waitingForAircraftAnchor != null) {
            executeAircraftMove(x, y)
            return
        }
        val targetCell = targetBoard.getCell(x, y)
        val move = ShotMove(g.currentPlayer, targetCell)
        when (val result = g.processMove(move)) {
            is MoveResult.Success -> {
                val text =
                    when (targetCell.state) {
                        CellState.HIT -> "Hit!"
                        CellState.MISS -> "Miss!"
                        CellState.EXPLODED_MINE -> "Mine exploded!"
                        else -> "Shot fired"
                    }
                showMessage("$text ${g.currentPlayer.name}'s turn")
                refreshAllBoards()
                highlightActivePlayer()
            }
            is MoveResult.Invalid -> showMessage("Invalid move: ${result.reason}")
            is MoveResult.GameOver -> {
                showMessage("Game Over! Winner: ${g.getWinner()?.name}")
                refreshAllBoards()
                saveGameAndUpdateRatings(g)
            }
        }
        updateAircraftButton()
    }

    // ============== КЛИКИ ПО ДОСКЕ (объединённая логика) ==============

    private fun onCellClick(
        x: Int,
        y: Int,
        board: Board,
        belongsToPlayer: Int,
        isOpponent: Boolean,
    ) {
        if (displayMode == DisplayMode.SIMULTANEOUS) {
            if (placementPhase && !placingMines && !selectingAircraft) {
                if (belongsToPlayer == currentPlayerPlacing) handlePlacementClick(x, y)
            } else if (placingMines) {
                if (belongsToPlayer == currentPlayerPlacing) handleMinePlacement(x, y)
            } else if (!placementPhase) {
                val g = game ?: return
                if (belongsToPlayer == 1 && g.currentPlayer == g.player2) {
                    handleSimultaneousShot(x, y, board)
                } else if (belongsToPlayer == 2 && g.currentPlayer == g.player1) {
                    handleSimultaneousShot(x, y, board)
                }
            }
        } else {
            if (isOpponent) {
                handleOpponentClick(x, y)
            } else {
                handleMyBoardClick(x, y)
            }
        }
    }

    private fun handleMyBoardClick(
        x: Int,
        y: Int,
    ) {
        when {
            placementPhase && !placingMines && !selectingAircraft -> handlePlacementClick(x, y)
            placingMines -> handleMinePlacement(x, y)
            else -> showMessage("Click opponent's board to shoot")
        }
    }

    private fun handleOpponentClick(
        x: Int,
        y: Int,
    ) {
        if (waitingForAircraftAnchor != null) {
            executeAircraftMove(x, y)
        } else if (!placementPhase && !placingMines && !selectingAircraft) {
            handleShotClick(x, y)
        } else {
            showMessage("You can only shoot after placement is complete")
        }
    }

    // ============== ОБРАБОТЧИК "DONE" ==============

    private fun handleDone() {
        if (placingMines) {
            finishMines()
        } else if (selectingAircraft) {
            if (aircraftSelected < 3) {
                showMessage("Please select exactly 3 aircraft")
            } else {
                finishAircraftSelection()
            }
        }
    }

    // ============== ВИЗУАЛИЗАЦИЯ И ДОСКИ ==============

    private fun createBoard(
        board: Board,
        hideShips: Boolean,
        isOpponent: Boolean,
        belongsToPlayer: Int = 0,
    ): Pair<GridPane, Array<Array<Label?>>> {
        val grid =
            GridPane().apply {
                hgap = 2.0
                vgap = 2.0
                style = GuiStyles.BOARD_GRID_STYLE
            }
        val labels = Array(10) { arrayOfNulls<Label>(10) }

        for (x in 0..9) {
            for (y in 0..9) {
                val cell = board.getCell(x, y)
                val lbl =
                    Label().apply {
                        prefWidth = cellSize
                        prefHeight = cellSize
                        alignment = Pos.CENTER
                        style = "-fx-background-color: ${GuiStyles.cellColor(cell.state, hideShips)}; ${GuiStyles.CELL_BASE_STYLE}"

                        setOnMouseClicked { onCellClick(x, y, board, belongsToPlayer, isOpponent) }

                        val needHighlight =
                            when {
                                displayMode == DisplayMode.SIMULTANEOUS ->
                                    (placementPhase || placingMines) && belongsToPlayer == currentPlayerPlacing
                                else -> !isOpponent && (placementPhase || placingMines)
                            }
                        if (needHighlight) {
                            setOnMouseEntered { highlightPlacement(x, y, grid) }
                            setOnMouseExited { updateBoardDisplay(grid, board, hideShips) }
                        }
                    }
                grid.add(lbl, y + 1, x + 1)
                labels[x][y] = lbl
            }
        }

        for (i in 0..9) {
            grid.add(
                Label("${'A' + i}").apply {
                    prefWidth = cellSize
                    prefHeight = cellSize
                    alignment = Pos.CENTER
                    style = GuiStyles.BOARD_TEXT_STYLE
                },
                0,
                i + 1,
            )
            grid.add(
                Label("${i + 1}").apply {
                    prefWidth = cellSize
                    prefHeight = cellSize
                    alignment = Pos.CENTER
                    style = GuiStyles.BOARD_TEXT_STYLE
                },
                i + 1,
                0,
            )
        }
        return Pair(grid, labels)
    }

    private fun recreateBoardsForCurrentPlayer() {
        val g = game ?: return
        boardsBox?.children?.clear()
        val leftHide = currentPlayerPlacing != 1
        val rightHide = currentPlayerPlacing != 2
        val (leftGrid, _) = createBoard(g.board1, leftHide, false, 1)
        val (rightGrid, _) = createBoard(g.board2, rightHide, false, 2)
        boardsBox?.children?.addAll(
            labeledBoard("Player 1 (${g.player1.name})", leftGrid),
            labeledBoard("Player 2 (${g.player2.name})", rightGrid),
        )
    }

    private fun setupSimultaneousMode() {
        recreateBoardsForCurrentPlayer()
        refreshAllBoards()
    }

    private fun highlightActivePlayer() {
        val g = game ?: return
        val leftGrid = (boardsBox?.children?.get(0) as? VBox)?.children?.get(1) as? GridPane
        val rightGrid = (boardsBox?.children?.get(1) as? VBox)?.children?.get(1) as? GridPane
        if (g.currentPlayer == g.player1) {
            leftGrid?.style += GuiStyles.INACTIVE_BORDER
            rightGrid?.style += GuiStyles.ACTIVE_BORDER
        } else {
            rightGrid?.style += GuiStyles.INACTIVE_BORDER
            leftGrid?.style += GuiStyles.ACTIVE_BORDER
        }
    }

    private fun highlightPlacement(
        startX: Int,
        startY: Int,
        grid: GridPane,
    ) {
        if (currentShipIndex >= shipSizes.size) return
        val size = shipSizes[currentShipIndex]
        for (i in 0 until size) {
            val cx = if (horizontal) startX else startX + i
            val cy = if (horizontal) startY + i else startY
            if (cx in 0..9 && cy in 0..9) {
                val node =
                    grid.children.find {
                        GridPane.getRowIndex(it) == cx + 1 && GridPane.getColumnIndex(it) == cy + 1
                    } as? Label
                node?.style = node?.style?.replace("-fx-border-color: ${GuiStyles.HOVER_TRANSPARENT_WHITE};", "") +
                    "-fx-border-color: ${GuiStyles.PLACEMENT_HIGHLIGHT_BORDER};"
            }
        }
    }

    private fun updateBoardDisplay(
        grid: GridPane,
        board: Board,
        hideShips: Boolean,
    ) {
        for (x in 0..9) {
            for (y in 0..9) {
                val cell = board.getCell(x, y)
                val node =
                    grid.children.find {
                        GridPane.getRowIndex(it) == x + 1 && GridPane.getColumnIndex(it) == y + 1
                    } as? Label ?: continue
                val color =
                    when (cell.state) {
                        CellState.EMPTY -> GuiStyles.CellColors.EMPTY
                        CellState.SHIP -> if (hideShips) GuiStyles.CellColors.EMPTY else GuiStyles.CellColors.SHIP
                        CellState.HIT -> {
                            val ship = board.ships.find { it.cells.contains(cell) }
                            if (ship?.isSunk() == true) GuiStyles.CellColors.SUNK else GuiStyles.CellColors.HIT
                        }
                        CellState.MISS -> GuiStyles.CellColors.MISS
                        CellState.MINED -> if (hideShips) GuiStyles.CellColors.EMPTY else GuiStyles.CellColors.MINED
                        CellState.EXPLODED_MINE -> GuiStyles.CellColors.EXPLODED_MINE
                    }
                node.style = "-fx-background-color: $color; ${GuiStyles.CELL_BASE_STYLE}"
            }
        }
    }

    private fun updateAllBoards() {
        val g = game ?: return
        if (displayMode == DisplayMode.SIMULTANEOUS) {
            val leftGrid = (boardsBox?.children?.get(0) as? VBox)?.children?.get(1) as? GridPane
            val rightGrid = (boardsBox?.children?.get(1) as? VBox)?.children?.get(1) as? GridPane
            leftGrid?.let { updateBoardDisplay(it, g.board1, hideShips = true) }
            rightGrid?.let { updateBoardDisplay(it, g.board2, hideShips = true) }
        } else {
            myBoardGrid?.let { updateBoardDisplay(it, g.getBoard(g.currentPlayer), false) }
            opponentBoardGrid?.let { updateBoardDisplay(it, g.getBoard(g.getOpponent(g.currentPlayer)), true) }
        }
    }

    private fun labeledBoard(
        title: String,
        grid: GridPane,
    ) = VBox(5.0).apply {
        alignment = Pos.CENTER
        children.addAll(Label(title).apply { style = GuiStyles.SECOND_TITLE_STYLE }, grid)
    }

    // ============== САМОЛЁТЫ И ДИАЛОГИ ==============

    private fun updateAircraftButton() {
        val g = game ?: return
        val hasAircraft = g.mode == GameMode.EXTENDED && g.currentPlayer.aircrafts.any { it.remainingUses > 0 }
        aircraftBtn?.isVisible = hasAircraft
    }

    private fun openAircraftDialog() {
        val g = game ?: return
        val player = g.currentPlayer
        val available = player.aircrafts.filter { it.remainingUses > 0 }
        if (available.isEmpty()) {
            showMessage("No available aircraft")
            return
        }

        val dialog =
            Stage().apply {
                initModality(Modality.APPLICATION_MODAL)
                initOwner(stage)
                title = "Battleship | Select Aircraft"
                width = 500.0
                height = 500.0
            }
        val root =
            VBox(10.0).apply {
                padding = Insets(15.0)
                alignment = Pos.CENTER
                background = Background(BackgroundFill(Color.rgb(20, 25, 50), CornerRadii.EMPTY, Insets.EMPTY))
            }
        root.children.add(Label("Choose aircraft:").apply { style = GuiStyles.DIALOG_TITLE_STYLE })
        available.forEach { aircraft ->
            val btn =
                Button("${aircraft.type.name} (uses left: ${aircraft.remainingUses})").apply {
                    style = GuiStyles.ACTION_BUTTON_STYLE
                    setOnAction {
                        waitingForAircraftAnchor = aircraft
                        showMessage("Click on opponent's board to place ${aircraft.type.name} anchor")
                        dialog.close()
                    }
                }
            root.children.add(btn)
        }
        dialog.scene = Scene(root)
        dialog.showAndWait()
    }

    private fun executeAircraftMove(
        x: Int,
        y: Int,
    ) {
        val aircraft = waitingForAircraftAnchor ?: return
        val g = game ?: return

        val opponentBoard = g.getBoard(g.getOpponent(g.currentPlayer)) // запомнили ДО хода
        val pattern = aircraft.getAttackPattern(x, y)
        val targets = pattern.map { (px, py) -> opponentBoard.getCell(px, py) }

        val move = AircraftMove(g.currentPlayer, aircraft, targets)
        when (val result = g.processMove(move)) {
            is MoveResult.Success -> {
                showMessage("Aircraft used! ${g.currentPlayer.name}'s turn")
                refreshAllBoards()
                if (aircraft.type == AircraftType.SCOUT) {
                    revealOpponentShipsTemporarily(opponentBoard, pattern) // передаём паттерн
                }
            }
            is MoveResult.Invalid -> showMessage("Invalid aircraft move: ${result.reason}")
            is MoveResult.GameOver -> {
                showMessage("Game Over! Winner: ${g.getWinner()?.name}")
                refreshAllBoards()
                saveGameAndUpdateRatings(g)
            }
        }

        waitingForAircraftAnchor = null
        updateAircraftButton()
    }

    private fun revealOpponentShipsTemporarily(
        opponentBoard: Board,
        cellsToReveal: List<Pair<Int, Int>>,
    ) {
        val g = game ?: return

        val gridToUpdate =
            when {
                displayMode == DisplayMode.SIMULTANEOUS -> {
                    if (opponentBoard === g.board1) {
                        (boardsBox?.children?.get(0) as? VBox)?.children?.get(1) as? GridPane
                    } else {
                        (boardsBox?.children?.get(1) as? VBox)?.children?.get(1) as? GridPane
                    }
                }
                else -> {
                    if (opponentBoard === g.board1) {
                        if (g.currentPlayer == g.player1) opponentBoardGrid else myBoardGrid
                    } else {
                        if (g.currentPlayer == g.player2) opponentBoardGrid else myBoardGrid
                    }
                }
            }

        gridToUpdate?.let { grid ->
            for ((px, py) in cellsToReveal) {
                val cell = opponentBoard.getCell(px, py)
                val label =
                    grid.children.find {
                        GridPane.getRowIndex(it) == px + 1 && GridPane.getColumnIndex(it) == py + 1
                    } as? Label
                label?.style = "-fx-background-color: ${GuiStyles.cellColor(cell.state, false)}; ${GuiStyles.CELL_BASE_STYLE}"
            }

            val pause = javafx.animation.PauseTransition(javafx.util.Duration.seconds(5.0))
            pause.setOnFinished {
                for ((px, py) in cellsToReveal) {
                    val cell = opponentBoard.getCell(px, py)
                    val label =
                        grid.children.find {
                            GridPane.getRowIndex(it) == px + 1 && GridPane.getColumnIndex(it) == py + 1
                        } as? Label
                    label?.style = "-fx-background-color: ${GuiStyles.cellColor(cell.state, false)}; ${GuiStyles.CELL_BASE_STYLE}"
                }
            }
            pause.play()
        }
    }

    // ============== СОХРАНЕНИЕ ==============
    private fun saveGameAndUpdateRatings(game: Game) {
        storage?.completeGame(game)
    }

    // ============== ЗАМЕНА ДОСКИ (TURN_BASED) ==============

    private fun replaceMyBoardWith(board: Board) {
        val oldVBox = myBoardGrid?.parent as? VBox ?: return
        val title = (oldVBox.children[0] as? Label)?.text ?: "Your Board"
        val (newGrid, _) = createBoard(board, hideShips = false, isOpponent = false)
        myBoardGrid = newGrid
        val idx = boardsBox?.children?.indexOf(oldVBox) ?: -1
        if (idx >= 0) boardsBox?.children?.set(idx, labeledBoard(title, newGrid))
    }

    // ============== ЗАПУСК ОКНА ==============

    fun show(
        game: Game,
        storage: DataStorage = JsonDataStorage(),
    ) {
        this.game = game
        this.storage = storage
        val newStage =
            Stage().apply {
                title = "Battleship | ${game.player1.name} vs ${game.player2.name}"
                width = 1050.0
                height = 650.0
            }
        stage = newStage

        val root =
            VBox().apply {
                spacing = 15.0
                padding = Insets(20.0)
                alignment = Pos.CENTER
                background = Background(BackgroundFill(GuiStyles.DARK_BG, CornerRadii.EMPTY, Insets.EMPTY))
            }

        infoLabel =
            Label("Player 1: ${game.player1.name} — place ship of size 4").apply {
                style = GuiStyles.INFO_TEXT_STYLE
            }

        val box = HBox(30.0).apply { alignment = Pos.CENTER }
        boardsBox = box
        val isSimultaneous = displayMode == DisplayMode.SIMULTANEOUS
        val (myGrid, _) = createBoard(game.board1, hideShips = false, isOpponent = false, belongsToPlayer = 1)
        val (oppGrid, _) = createBoard(game.board2, hideShips = true, isOpponent = !isSimultaneous, belongsToPlayer = 2)
        myBoardGrid = myGrid
        opponentBoardGrid = oppGrid

        if (isSimultaneous) {
            box.children.addAll(
                labeledBoard("Player 1 (${game.player1.name})", myGrid),
                labeledBoard("Player 2 (${game.player2.name})", oppGrid),
            )
        } else {
            box.children.addAll(
                labeledBoard("Your Board", myGrid),
                labeledBoard("Opponent's Board", oppGrid),
            )
        }

        val controlBox = HBox(15.0).apply { alignment = Pos.CENTER }

        rotateBtn =
            Button("Horizontal").apply {
                style = GuiStyles.ACTION_BUTTON_STYLE
                setOnAction {
                    horizontal = !horizontal
                    text = if (horizontal) "Horizontal" else "Vertical"
                }
            }

        doneBtn =
            Button("Done").apply {
                style = GuiStyles.ACTION_BUTTON_STYLE
                isVisible = false
                setOnAction { handleDone() }
            }

        aircraftBtn =
            Button("Use Aircraft").apply {
                style = GuiStyles.ACTION_BUTTON_STYLE
                isVisible = false
                setOnAction { openAircraftDialog() }
            }

        val backBtn =
            Button("Back to Menu").apply {
                style = GuiStyles.DANGER_BUTTON_STYLE
                setOnAction { newStage.close() }
            }

        controlBox.children.addAll(rotateBtn!!, doneBtn!!, aircraftBtn!!, backBtn)

        root.children.addAll(infoLabel, box, controlBox)
        newStage.scene = Scene(root)
        newStage.show()
    }
}
