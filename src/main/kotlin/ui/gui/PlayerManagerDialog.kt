package edu.battleship.ui.gui

import edu.battleship.data.DataStorage
import edu.battleship.data.JsonDataStorage
import edu.battleship.model.Player
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.VBox
import javafx.stage.Stage

class PlayerManagerDialog(
    private val storage: DataStorage = JsonDataStorage(),
) {
    fun show() {
        val stage =
            Stage().apply {
                title = "Battleship | Player Manager"
                width = 700.0
                height = 700.0
            }

        val root =
            VBox().apply {
                spacing = 12.0
                padding = Insets(25.0)
                alignment = Pos.TOP_CENTER
                background =
                    Background(
                        BackgroundFill(GuiStyles.PANEL_BG, CornerRadii.EMPTY, Insets.EMPTY),
                    )
            }

        val title =
            Label("Players").apply {
                style = GuiStyles.DIALOG_TITLE_STYLE
            }

        val playersList =
            VBox().apply {
                spacing = 8.0
            }
        refreshPlayerList(playersList)

        val nameInput =
            TextField().apply {
                promptText = "Player name"
                style = GuiStyles.INPUT_STYLE
            }

        val addBtn =
            Button("Add Player").apply {
                style = GuiStyles.ACTION_BUTTON_STYLE

                setOnAction {
                    val name = nameInput.text.trim()
                    if (name.isNotBlank()) {
                        val players = storage.loadAllPlayers().toMutableList()
                        val nextId = (players.maxOfOrNull { it.id } ?: 0) + 1
                        val newPlayer = Player(nextId, name)
                        players.add(newPlayer)
                        storage.saveAllPlayers(players)
                        nameInput.clear()
                        refreshPlayerList(playersList)
                    }
                }
            }

        root.children.addAll(title, playersList, nameInput, addBtn)
        stage.scene = Scene(root)
        stage.show()
    }

    private fun refreshPlayerList(container: VBox) {
        container.children.clear()
        val players = storage.loadAllPlayers()
        players.forEach { player ->
            container.children.add(
                Label("${player.name} (Rating: ${player.rating})").apply {
                    style = GuiStyles.INFO_TEXT_STYLE
                },
            )
        }
    }
}
