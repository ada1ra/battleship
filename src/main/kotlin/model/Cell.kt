package edu.battleship.model

data class Cell(
    val x: Int,
    val y: Int,
    var state: CellState = CellState.EMPTY,
)
