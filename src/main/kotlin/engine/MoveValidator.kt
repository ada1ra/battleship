package edu.battleship.engine

import edu.battleship.rules.RuleSet

class MoveValidator(
    private val ruleSet: RuleSet,
) {
    fun validate(
        move: Move,
        game: Game,
    ): Boolean =
        when (move) {
            is ShotMove -> move.validate(game)
            is AircraftMove -> move.validate(game)
            is PlaceMineMove -> ruleSet.canPlaceMine(game.getBoard(move.player)) && move.validate(game)
            else -> false
        }
}
