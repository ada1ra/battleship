package edu.battleship.ui

interface UserInterface {
    fun start()

    fun showMessage(msg: String)

    fun readCommand(): String
}
