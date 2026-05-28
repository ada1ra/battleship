package edu.battleship.ui.gui

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.input.KeyCode
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.VBox
import javafx.stage.Stage

class MainWindow {
    fun show(stage: Stage) {
        stage.title = "Battleship Admin"

        val root =
            VBox(20.0).apply {
                alignment = Pos.CENTER
                padding = Insets(40.0)
                background =
                    Background(
                        BackgroundFill(
                            GuiStyles.DARK_BG,
                            CornerRadii.EMPTY,
                            Insets.EMPTY,
                        ),
                    )
            }

        val title =
            javafx.scene.control.Label("BATTLESHIP").apply {
                style = GuiStyles.MAIN_TITLE_STYLE
                effect = GuiStyles.titleShadowEffect()
            }

        val newGameBtn =
            createGlassButton("New Game", onAction = {
                GameSetupDialog().show(stage)
            })

        val playersBtn =
            createGlassButton("Player Manager", onAction = {
                PlayerManagerDialog().show()
            })

        val historyBtn =
            createGlassButton("Game History", onAction = {
                HistoryDialog().show()
            })

        val exitBtn =
            createGlassButton("Exit", onAction = {
                stage.close()
            })

        root.children.addAll(title, newGameBtn, playersBtn, historyBtn, exitBtn)

        stage.scene = Scene(root, 700.0, 700.0)
        stage.isMaximized = true
        stage.show()

        stage.scene.setOnKeyPressed { event ->
            if (event.code == KeyCode.F11) {
                stage.isFullScreen = !stage.isFullScreen
                event.consume()
            }
        }
    }

    private fun createGlassButton(
        text: String,
        onAction: () -> Unit,
    ): Button =
        Button(text).apply {
            style = GuiStyles.GLASS_BUTTON_STYLE
            setOnAction { onAction() }
            setOnMouseEntered {
                style = style.replace(GuiStyles.SEMI_TRANSPARENT_WHITE, GuiStyles.HOVER_TRANSPARENT_WHITE)
            }
            setOnMouseExited {
                style = style.replace(GuiStyles.HOVER_TRANSPARENT_WHITE, GuiStyles.SEMI_TRANSPARENT_WHITE)
            }
        }
}
