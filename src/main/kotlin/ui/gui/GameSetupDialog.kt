package edu.battleship.ui.gui

import edu.battleship.data.DataStorage
import edu.battleship.data.JsonDataStorage
import edu.battleship.engine.Game
import edu.battleship.model.GameMode
import edu.battleship.ui.gui.GameView.DisplayMode
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.stage.Modality
import javafx.stage.Stage

class GameSetupDialog(
    private val storage: DataStorage = JsonDataStorage(),
) {
    private var nextGameId = 1

    fun show(parentStage: Stage) {
        val dialog =
            Stage().apply {
                initModality(Modality.APPLICATION_MODAL)
                initOwner(parentStage)
                title = "Battleship | New Game Setup"
                width = 700.0
                height = 700.0
            }

        val players = storage.loadAllPlayers()
        nextGameId = (storage.loadGameSummaries().maxOfOrNull { it.id } ?: 0) + 1

        val root =
            VBox().apply {
                spacing = 15.0
                padding = Insets(30.0)
                alignment = Pos.CENTER
                background =
                    Background(
                        BackgroundFill(Color.rgb(20, 25, 50), CornerRadii.EMPTY, Insets.EMPTY),
                    )
            }

        val titleLabel =
            Label("Setup New Game").apply {
                style = GuiStyles.DIALOG_TITLE_STYLE
            }

        val player1Combo =
            createComboBox(
                items = players,
                promptText = "Select Player 1",
                displayText = { "${it.name} (Rating: ${it.rating})" },
                longDisplayText = { "${it.name} (Rating: ${it.rating})" },
            )
        val player2Combo =
            createComboBox(
                items = players,
                promptText = "Select Player 2",
                displayText = { "${it.name} (Rating: ${it.rating})" },
                longDisplayText = { "${it.name} (Rating: ${it.rating})" },
            )

        val modeCombo =
            createComboBox(
                items = GameMode.entries.toList(),
                promptText = "Select Game Mode",
                displayText = { gameMode ->
                    when (gameMode) {
                        GameMode.STANDARD -> "Standard"
                        GameMode.EXPERIENCED -> "Experienced"
                        GameMode.EXTENDED -> "Extended"
                    }
                },
                longDisplayText = { gameMode ->
                    when (gameMode) {
                        GameMode.STANDARD -> "Standard"
                        GameMode.EXPERIENCED -> "Experienced (more moves)"
                        GameMode.EXTENDED -> "Extended (mines & aircraft)"
                    }
                },
            )

        val displayModeCombo =
            createComboBox(
                items = DisplayMode.entries.toList(),
                promptText = "Display Mode",
                displayText = { mode ->
                    when (mode) {
                        DisplayMode.TURN_BASED -> "Turn-based"
                        DisplayMode.SIMULTANEOUS -> "Shared screen"
                    }
                },
                longDisplayText = { mode ->
                    when (mode) {
                        DisplayMode.TURN_BASED -> "Turn-based (current player always left)"
                        DisplayMode.SIMULTANEOUS -> "Shared screen (boards are hidden always)"
                    }
                },
            )

        val startBtn =
            Button("Start Game").apply {
                style = GuiStyles.ACTION_BUTTON_STYLE
                setOnAction {
                    val p1 = player1Combo.value
                    val p2 = player2Combo.value
                    val mode = modeCombo.value
                    if (p1 != null && p2 != null && mode != null && p1 != p2) {
                        val game = Game(nextGameId, p1, p2, mode)
                        dialog.close()
                        GameView().apply { displayMode = displayModeCombo.value }.show(game, storage)
                    }
                }
                setOnMouseEntered {
                    style = style.replace(GuiStyles.ACCENT_BLUE, GuiStyles.ACCENT_BLUE_HOVER)
                }
                setOnMouseExited {
                    style = style.replace(GuiStyles.ACCENT_BLUE_HOVER, GuiStyles.ACCENT_BLUE)
                }
            }

        root.children.addAll(titleLabel, player1Combo, player2Combo, modeCombo, displayModeCombo, startBtn)
        dialog.scene = Scene(root)
        dialog.showAndWait()
    }

    private fun <T> createComboBox(
        items: List<T>,
        promptText: String,
        displayText: (T) -> String,
        longDisplayText: (T) -> String = displayText,
    ): ComboBox<T> =
        ComboBox<T>().apply {
            this.items.addAll(items)
            this.promptText = promptText
            style = GuiStyles.COMBO_BOX_STYLE
            setOnShowing {
                val listView = lookup(".list-view") as? ListView<*>
                listView?.style = GuiStyles.LIST_VIEW_STYLE
            }
            setCellFactory { _ ->
                ComboBoxCell(longDisplayText)
            }
            buttonCell = ComboBoxCell(displayText)
        }

    private class ComboBoxCell<T>(
        private val displayText: (T) -> String,
    ) : ListCell<T>() {
        override fun updateItem(
            item: T?,
            empty: Boolean,
        ) {
            super.updateItem(item, empty)
            if (empty || item == null) {
                text = null
                style = GuiStyles.COMBO_CELL_EMPTY_STYLE
            } else {
                text = displayText(item)
                style = GuiStyles.COMBO_CELL_FILLED_STYLE
                setOnMouseEntered { style = "$style-fx-background-color: ${GuiStyles.HOVER_TRANSPARENT_WHITE};" }
                setOnMouseExited {
                    style =
                        style.replace("-fx-background-color: ${GuiStyles.HOVER_TRANSPARENT_WHITE};", "-fx-background-color: transparent;")
                }
            }
        }
    }
}
