package edu.battleship.model

class Board {
    val cells: Array<Array<Cell>> = Array(10) { x -> Array(10) { y -> Cell(x, y) } }
    val ships = mutableListOf<Ship>()
    val mines = mutableListOf<Mine>()

    fun getCell(
        x: Int,
        y: Int,
    ): Cell = cells[x][y]

    fun isCellFree(
        x: Int,
        y: Int,
    ): Boolean {
        if (x !in 0..9 || y !in 0..9) return false
        if (cells[x][y].state != CellState.EMPTY) return false
        for (dx in -1..1) {
            for (dy in -1..1) {
                if (dx == 0 && dy == 0) continue
                val nx = x + dx
                val ny = y + dy
                if (nx in 0..9 && ny in 0..9 && cells[nx][ny].state == CellState.SHIP) {
                    return false
                }
            }
        }
        return true
    }

    fun placeShip(ship: Ship): Boolean {
        val shipCells = ship.cells
        if (shipCells.any { !isCellFree(it.x, it.y) }) return false

        val xs = shipCells.map { it.x }.distinct()
        val ys = shipCells.map { it.y }.distinct()
        if (xs.size != 1 && ys.size != 1) return false
        val sorted = if (xs.size == 1) shipCells.sortedBy { it.y } else shipCells.sortedBy { it.x }
        for (i in 0 until sorted.size - 1) {
            val diff = if (xs.size == 1) sorted[i + 1].y - sorted[i].y else sorted[i + 1].x - sorted[i].x
            if (diff != 1) return false
        }

        shipCells.forEach { cell ->
            getCell(cell.x, cell.y).state = CellState.SHIP
        }
        ships.add(ship)
        return true
    }

    fun placeMine(mine: Mine): Boolean {
        val cell = getCell(mine.position.x, mine.position.y)
        if (cell.state != CellState.EMPTY) return false
        cell.state = CellState.MINED
        mines.add(mine)
        return true
    }

    fun isAllShipsSunk(): Boolean = ships.all { it.isSunk() }

    fun getAliveShipsCount(): Int = ships.count { !it.isSunk() }
}
