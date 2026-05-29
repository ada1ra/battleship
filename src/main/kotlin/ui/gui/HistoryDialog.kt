package edu.battleship.ui.gui

import edu.battleship.data.DataStorage
import edu.battleship.data.JsonDataStorage
import edu.battleship.ui.gui.GuiStyles.fixScrollStyles
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Stage

class HistoryDialog(
    private val storage: DataStorage = JsonDataStorage(),
) {
    fun show() {
        val stage =
            Stage().apply {
                title = "Battleship | Game History"
                width = 700.0
                height = 700.0
            }
        val root =
            VBox().apply {
                spacing = 12.0
                padding = Insets(25.0)
                alignment = Pos.TOP_CENTER
                background = Background(BackgroundFill(GuiStyles.PANEL_BG, CornerRadii.EMPTY, Insets.EMPTY))
            }
        val title =
            Label("Completed Games").apply {
                style = GuiStyles.DIALOG_TITLE_STYLE
            }
        val gamesContainer = VBox().apply { spacing = 8.0 }
        val summaries = storage.loadGameSummaries()
        if (summaries.isEmpty()) {
            gamesContainer.children.add(Label("No games played yet.").apply { style = GuiStyles.INFO_TEXT_STYLE })
        } else {
            summaries.forEach { summary ->
                val detail = storage.loadGameDetail(summary.id)
                val gameLabel =
                    Label().apply {
                        text =
                            if (detail !=
                                null
                            ) {
                                "${detail.player1Name} vs ${detail.player2Name} | ${detail.mode} | Winner: ${detail.winnerName ?: "Draw"}"
                            } else {
                                "Game ${summary.id}: ${summary.mode}"
                            }
                        style = GuiStyles.GAME_LIST_ITEM_STYLE
                        setOnMouseClicked { showGameDetail(summary.id) }
                        setOnMouseEntered {
                            style = style.replace(GuiStyles.SEMI_TRANSPARENT_WHITE, GuiStyles.HOVER_TRANSPARENT_WHITE)
                        }
                        setOnMouseExited {
                            style = style.replace(GuiStyles.HOVER_TRANSPARENT_WHITE, GuiStyles.SEMI_TRANSPARENT_WHITE)
                        }
                    }
                gamesContainer.children.add(gameLabel)
            }
        }
        val scrollPane =
            ScrollPane(gamesContainer).apply {
                isFitToWidth = true
                style = GuiStyles.TRANSPARENT_SCROLL_STYLE
            }
        root.children.addAll(title, scrollPane)
        stage.scene = Scene(root)
        stage.show()
        fixScrollStyles(stage)
    }

    private fun showGameDetail(gameId: Int) {
        val detail = storage.loadGameDetail(gameId) ?: return
        val stage =
            Stage().apply {
                title = "Battleship | Game $gameId | Details"
                width = 700.0
                height = 700.0
            }
        val root =
            VBox().apply {
                spacing = 15.0
                padding = Insets(25.0)
                background = Background(BackgroundFill(GuiStyles.PANEL_BG, CornerRadii.EMPTY, Insets.EMPTY))
            }

        val header =
            Label("${detail.player1Name} vs ${detail.player2Name}").apply {
                style = GuiStyles.DIALOG_TITLE_STYLE
            }
        val infoBox =
            VBox().apply {
                spacing = 5.0
                padding = Insets(10.0)
                style = GuiStyles.INFO_BOX_STYLE
            }
        infoBox.children.addAll(
            Label("Mode: ${detail.mode}").apply { style = GuiStyles.INFO_TEXT_STYLE },
            Label("Winner: ${detail.winnerName ?: "Draw"}").apply { style = GuiStyles.WINNER_TEXT_STYLE },
        )

        val boardsTitle =
            Label("Final Boards:").apply {
                style = GuiStyles.SECOND_TITLE_STYLE
            }
        val boardsHBox =
            HBox(20.0).apply {
                children.addAll(
                    createColoredBoard(detail.player1Name, detail.finalBoardState1),
                    createColoredBoard(detail.player2Name, detail.finalBoardState2),
                )
            }

        val movesTitle =
            Label("Move History:").apply {
                style = GuiStyles.SECOND_TITLE_STYLE
            }
        val movesList = VBox().apply { spacing = 3.0 }
        detail.moves.forEachIndexed { index, move ->
            movesList.children.add(
                Label("${index + 1}. $move").apply {
                    style = GuiStyles.MOVE_ITEM_STYLE
                },
            )
        }
        val moveScrollPane =
            ScrollPane(movesList).apply {
                isFitToWidth = true
                style = GuiStyles.TRANSPARENT_SCROLL_STYLE
            }
        root.children.addAll(header, infoBox, boardsTitle, boardsHBox, movesTitle, moveScrollPane)
        stage.scene = Scene(root)
        stage.show()
        fixScrollStyles(stage)
    }

    private fun createColoredBoard(
        playerName: String,
        boardString: String,
    ): VBox {
        val container = VBox(5.0).apply { alignment = Pos.CENTER }
        container.children.add(
            Label(playerName).apply { style = GuiStyles.BOARD_TEXT_STYLE },
        )

        val grid =
            GridPane().apply {
                hgap = 1.0
                vgap = 1.0
                style = GuiStyles.HISTORY_BOARD_GRID_STYLE
            }
        val rows = boardString.trim().split("\n")
        for ((x, row) in rows.withIndex()) {
            if (x >= 10) break
            for ((y, ch) in row.withIndex()) {
                if (y >= 10) break
                val color = GuiStyles.cellColorFromChar(ch)
                val cell =
                    Label().apply {
                        prefWidth = 22.0
                        prefHeight = 22.0
                        alignment = Pos.CENTER
                        style = GuiStyles.historyCellStyle(color)
                    }
                grid.add(cell, y + 1, x + 1)
            }
        }

        for (i in 0..9) {
            grid.add(
                Label("${'A' + i}").apply {
                    prefWidth = 22.0
                    prefHeight = 22.0
                    alignment = Pos.CENTER
                    style = GuiStyles.HISTORY_COORD_TEXT_STYLE
                },
                0,
                i + 1,
            )
            grid.add(
                Label("${i + 1}").apply {
                    prefWidth = 22.0
                    prefHeight = 22.0
                    alignment = Pos.CENTER
                    style = GuiStyles.HISTORY_COORD_TEXT_STYLE
                },
                i + 1,
                0,
            )
        }

        container.children.add(grid)
        return container
    }
}
