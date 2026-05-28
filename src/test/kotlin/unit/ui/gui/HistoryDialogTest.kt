package edu.battleship.unit.ui.gui

import edu.battleship.ui.gui.HistoryDialog
import javafx.stage.Stage
import org.junit.jupiter.api.Test
import org.testfx.api.FxRobot
import org.testfx.assertions.api.Assertions
import org.testfx.framework.junit5.ApplicationTest

class HistoryDialogTest : ApplicationTest() {
    override fun start(stage: Stage) {
        HistoryDialog().show()
    }

    private val robot = FxRobot()

    @Test
    fun `should display completed games header`() {
        Assertions.assertThat(robot.lookup("Completed Games").queryLabeled()).isNotNull
    }
}
