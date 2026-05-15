package edu.battleship.model

class Mine(
    val position: Cell,
    var isArmed: Boolean = true,
) {
    fun explode() {
        if (!isArmed) return
        isArmed = false
        position.state = CellState.EXPLODED_MINE
    }
}
