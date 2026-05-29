package edu.battleship.unit.ui.gui

import edu.battleship.ui.gui.MainWindow
import javafx.stage.Stage
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.testfx.api.FxRobot
import org.testfx.assertions.api.Assertions
import org.testfx.framework.junit5.ApplicationTest
import org.testfx.framework.junit5.Start

class MainWindowTest : ApplicationTest() {
    @Mock
    private val robot = FxRobot()

    @Start
    override fun start(stage: Stage) {
        MainWindow().show(stage)
    }

    @Test
    fun `should display four main buttons`() {
        Assertions.assertThat(this.robot.lookup("New Game").queryButton()).isNotNull
        Assertions.assertThat(this.robot.lookup("Player Manager").queryButton()).isNotNull
        Assertions.assertThat(this.robot.lookup("Game History").queryButton()).isNotNull
        Assertions.assertThat(this.robot.lookup("Exit").queryButton()).isNotNull
    }

    @Test
    fun `click New Game should open setup dialog`() {
        this.robot.clickOn("New Game")
        Assertions.assertThat(this.robot.lookup("Start Game").queryButton()).isNotNull
    }

    @Test
    fun `click Exit should close the window`() {
        this.robot.clickOn("Exit")
        Assertions.assertThat(this.robot.listWindows()).isEmpty()
    }
}
