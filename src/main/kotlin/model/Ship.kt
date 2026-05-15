package edu.battleship.model

class Ship(
    val type: ShipType,
    val cells: MutableList<Cell>,
) {
    init {
        require(cells.size == type.size) { "Ship must have exactly ${type.size} cells" }
    }

    fun isSunk(): Boolean = cells.all { it.state == CellState.HIT }
}
