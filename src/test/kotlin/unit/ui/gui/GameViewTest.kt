package edu.battleship.unit.ui.gui

import edu.battleship.engine.Game
import edu.battleship.model.GameMode
import edu.battleship.model.Player
import edu.battleship.ui.gui.GameView
import javafx.stage.Stage
import org.junit.jupiter.api.Test
import org.testfx.api.FxRobot
import org.testfx.assertions.api.Assertions
import org.testfx.framework.junit5.ApplicationTest

class GameViewTest : ApplicationTest() {
    override fun start(stage: Stage) {
        val game = Game(1, Player(1, "Alice"), Player(2, "Bob"), GameMode.STANDARD)
        GameView().show(game)
    }

    private val robot = FxRobot()

    @Test
    fun `should show placement label`() {
        sleep(500)
        Assertions
            .assertThat(
                robot.lookup("Player 1: Alice — place ship of size 4").queryLabeled(),
            ).isNotNull
    }
}
