package edu.battleship

import edu.battleship.ui.cli.ConsoleUI
import edu.battleship.ui.gui.MainWindow
import javafx.application.Application
import javafx.stage.Stage

class BattleshipApp : Application() {
    override fun start(primaryStage: Stage) {
        MainWindow().show(primaryStage)
    }
}

fun main() {
    println("Battleship game started!")
    ConsoleUI().start()
}
