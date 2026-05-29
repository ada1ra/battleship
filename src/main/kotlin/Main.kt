package edu.battleship

import edu.battleship.data.DataStorage
import edu.battleship.data.JsonDataStorage
import edu.battleship.data.SqliteDataStorage
import edu.battleship.ui.cli.ConsoleUI
import edu.battleship.ui.gui.MainWindow
import javafx.application.Application
import javafx.stage.Stage

class BattleshipApp : Application() {
    companion object {
        var storage: DataStorage = JsonDataStorage()
    }

    override fun start(primaryStage: Stage) {
        MainWindow(storage).show(primaryStage)
    }
}

fun main() {
    val storage : DataStorage = SqliteDataStorage()
    while (true) {
        println("Launch mode: (1) Console or (2) GUI: ")
        when (readlnOrNull()) {
            "1" -> {
                println("Launched mode: ConsoleUI. Game started!")
                ConsoleUI(storage).start()
                break
            }
            "2" -> {
                println("Launched mode: GUI. Game started!")
                BattleshipApp.storage = storage
                Application.launch(BattleshipApp::class.java)
                break
            }
            else -> println("Invalid mode. Please enter 1 or 2.")
        }
    }
}
