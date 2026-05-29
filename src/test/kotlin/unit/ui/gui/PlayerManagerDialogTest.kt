package edu.battleship.unit.ui.gui

import edu.battleship.ui.gui.PlayerManagerDialog
import javafx.stage.Stage
import org.junit.jupiter.api.Test
import org.testfx.api.FxRobot
import org.testfx.assertions.api.Assertions
import org.testfx.framework.junit5.ApplicationTest

class PlayerManagerDialogTest : ApplicationTest() {
    override fun start(stage: Stage) {
        PlayerManagerDialog().show()
    }

    private val robot = FxRobot()

    @Test
    fun `should have add button`() {
        sleep(2000)
        Assertions.assertThat(robot.lookup("Add Player").queryButton()).isNotNull
    }
}
