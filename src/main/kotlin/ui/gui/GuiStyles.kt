package edu.battleship.ui.gui

import edu.battleship.model.CellState
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import javafx.stage.Stage

object GuiStyles {
    // ============================== COLORS ==============================
    val DARK_BG: Color? = Color.rgb(15, 20, 40)
    val PANEL_BG: Color? = Color.rgb(20, 25, 50)
    val TITLE_SHADOW: Color? = Color.rgb(30, 60, 120, 0.8)

    const val TEXT_PRIMARY = "#e0e6f0"
    const val TEXT_SECONDARY = "#c0d0f0"
    const val TEXT_STANDARD = "#a0c0f0"
    const val TEXT_WIN = "#80f0a0"

    const val SEMI_TRANSPARENT_WHITE = "rgba(255,255,255,0.05)"
    const val HOVER_TRANSPARENT_WHITE = "rgba(255,255,255,0.1)"

    const val SEMI_TRANSPARENT_STANDARD_TEXT = "rgba(160,192,240,0.5)"
    const val SEMI_TRANSPARENT_PANEL_BG = "rgba(20,25,50,0.5)"
    const val SEMI_TRANSPARENT_DARK_BG = "rgba(15,20,40,0.9)"
    const val ACCENT_BLUE = "rgba(30,80,180,0.8)"
    const val ACCENT_BLUE_HOVER = "rgba(40,100,220,0.9)"
    const val PLACEMENT_HIGHLIGHT_BORDER = "rgba(100,200,255,0.8)"
    const val DANGER_RED = "rgba(180,40,40,0.7)"

    // ============================== FONT SIZES ==============================
    const val FONT_SIZE_TITLE1 = "56px"
    const val FONT_SIZE_TITLE2 = "32px"
    const val FONT_SIZE_TITLE3 = "24px"
    const val FONT_SIZE_NORMAL = "16px"
    const val FONT_SIZE_SMALL = "8px"

    // ============================== FONT STYLES ==============================
    const val MAIN_TITLE_STYLE =
        "-fx-font-size: $FONT_SIZE_TITLE1; -fx-text-fill: $TEXT_PRIMARY; -fx-font-weight: bold;"
    const val DIALOG_TITLE_STYLE =
        "-fx-font-size: $FONT_SIZE_TITLE2; -fx-text-fill: $TEXT_PRIMARY; -fx-font-weight: bold;"
    const val SECOND_TITLE_STYLE =
        "-fx-font-size: $FONT_SIZE_TITLE3; -fx-text-fill: $TEXT_SECONDARY; -fx-font-weight: bold;"

    const val INFO_TEXT_STYLE = "-fx-font-size: $FONT_SIZE_NORMAL; -fx-text-fill: $TEXT_STANDARD;"
    const val BOARD_TEXT_STYLE = "-fx-font-size: $FONT_SIZE_NORMAL; -fx-text-fill: $TEXT_STANDARD; -fx-font-weight: bold;"
    const val WINNER_TEXT_STYLE = "-fx-font-size: $FONT_SIZE_NORMAL; -fx-text-fill: $TEXT_WIN;"
    const val HISTORY_COORD_TEXT_STYLE = "-fx-font-size: $FONT_SIZE_SMALL; -fx-text-fill: $TEXT_STANDARD;"

    // ============================== ITEMS STYLES ==============================
    const val MOVE_ITEM_STYLE = "-fx-font-size: $FONT_SIZE_NORMAL; -fx-text-fill: $TEXT_STANDARD; -fx-padding: 2 0 2 5;"
    const val TRANSPARENT_SCROLL_STYLE = "-fx-background: transparent; -fx-background-color: transparent;"
    const val HISTORY_BOARD_GRID_STYLE = "-fx-background-color: $SEMI_TRANSPARENT_WHITE; -fx-padding: 5;"
    const val INFO_BOX_STYLE = "-fx-background-color: $SEMI_TRANSPARENT_WHITE; -fx-background-radius: 8;"
    const val BOARD_GRID_STYLE = "-fx-background-color: $SEMI_TRANSPARENT_WHITE; -fx-padding: 8;"

    const val ACTIVE_BORDER = "-fx-border-color: $PLACEMENT_HIGHLIGHT_BORDER;"
    const val INACTIVE_BORDER = "-fx-border-color: $HOVER_TRANSPARENT_WHITE;"

    val CELL_BASE_STYLE =
        """
        -fx-background-radius: 5;
        -fx-border-color: $HOVER_TRANSPARENT_WHITE;
        -fx-border-radius: 5;
        -fx-border-width: 1;
        -fx-text-fill: $TEXT_STANDARD;
        -fx-font-size: $FONT_SIZE_SMALL;
        """.trimIndent()

    val INPUT_STYLE =
        """
        -fx-background-color: $HOVER_TRANSPARENT_WHITE; 
        -fx-text-fill: $TEXT_STANDARD; 
        -fx-font-size: $FONT_SIZE_NORMAL;
        -fx-prompt-text-fill: $SEMI_TRANSPARENT_STANDARD_TEXT;
        """.trimIndent()

    const val GAME_LIST_ITEM_STYLE = """
        -fx-text-fill: $TEXT_STANDARD;
        -fx-font-size: $FONT_SIZE_NORMAL;
        -fx-padding: 8;
        -fx-background-color: $SEMI_TRANSPARENT_WHITE;
        -fx-background-radius: 5;
        -fx-cursor: hand;
        """

    const val COMBO_BOX_STYLE = """
        -fx-font-size: $FONT_SIZE_NORMAL;
        -fx-text-fill: $TEXT_SECONDARY;
        -fx-prompt-text-fill: $TEXT_STANDARD;
        -fx-background-color: $SEMI_TRANSPARENT_PANEL_BG;
        -fx-background-radius: 8;
        -fx-border-color: $HOVER_TRANSPARENT_WHITE;
        -fx-border-radius: 8;
        -fx-border-width: 1;
        -fx-padding: 8;
        -fx-mark-color: $TEXT_STANDARD;
        """

    const val LIST_VIEW_STYLE = """
        -fx-background-color: $SEMI_TRANSPARENT_DARK_BG;
        -fx-background-radius: 8;
        -fx-border-color: $HOVER_TRANSPARENT_WHITE;
        -fx-border-radius: 8;
        -fx-padding: 5;
        """

    const val COMBO_CELL_EMPTY_STYLE = """
        -fx-text-fill: $TEXT_STANDARD;
        -fx-background-color: transparent;
        -fx-alignment: center;
        """

    val COMBO_CELL_FILLED_STYLE =
        """
        -fx-text-fill: $TEXT_SECONDARY;
        -fx-background-color: transparent;
        -fx-font-size: $FONT_SIZE_NORMAL;
        -fx-padding: 8 15 8 15;
        -fx-alignment: center;
        """.trimIndent()

    const val BUTTON_BASE_STYLE = """
        -fx-font-size: $FONT_SIZE_NORMAL;
        -fx-font-weight: bold;
        -fx-text-fill: $TEXT_SECONDARY;
        -fx-background-radius: 12;
        -fx-padding: 10 20 10 20;
        -fx-cursor: hand;
        """

    const val GLASS_BUTTON_STYLE = """
        -fx-font-size: $FONT_SIZE_NORMAL;
        -fx-font-weight: bold;
        -fx-text-fill: $TEXT_SECONDARY;
        -fx-background-color: $SEMI_TRANSPARENT_WHITE;
        -fx-background-radius: 12;
        -fx-border-color: $HOVER_TRANSPARENT_WHITE;
        -fx-border-radius: 12;
        -fx-border-width: 1;
        -fx-padding: 12 40 12 40;
        -fx-cursor: hand;
        """

    const val ACTION_BUTTON_STYLE = "$BUTTON_BASE_STYLE -fx-background-color: $ACCENT_BLUE;"
    const val DANGER_BUTTON_STYLE = "$BUTTON_BASE_STYLE -fx-background-color: $DANGER_RED;"

    object CellColors {
        const val EMPTY = "#1a2540"
        const val SHIP = "#3050a0"
        const val HIT = "#cc3030"
        const val SUNK = "#801010"
        const val MISS = "#2a3050"
        const val MINED = "#5040a0"
        const val EXPLODED_MINE = "#804040"
    }

    fun titleShadowEffect() = DropShadow(10.0, TITLE_SHADOW)

    fun cellColorFromChar(ch: Char): String =
        when (ch) {
            '~' -> "#1a2540"
            'O' -> "#3050a0"
            'X' -> "#cc3030"
            '*' -> "#2a3050"
            'M' -> "#5040a0"
            '!' -> "#804040"
            '#' -> "#404040"
            else -> "#1a2540"
        }

    fun cellColor(
        state: CellState,
        hideShips: Boolean,
    ) = when (state) {
        CellState.EMPTY -> CellColors.EMPTY
        CellState.SHIP -> if (hideShips) CellColors.EMPTY else CellColors.SHIP
        CellState.HIT -> CellColors.HIT
        CellState.MISS -> CellColors.MISS
        CellState.MINED -> if (hideShips) CellColors.EMPTY else CellColors.MINED
        CellState.EXPLODED_MINE -> CellColors.EXPLODED_MINE
    }

    fun historyCellStyle(color: String) =
        """
        -fx-background-color: $color;
        -fx-background-radius: 2;
        -fx-border-color: $SEMI_TRANSPARENT_WHITE;
        -fx-border-radius: 2;
        -fx-border-width: 1;
        """.trimIndent()

    fun fixScrollStyles(stage: Stage) {
        for (scroll in stage.scene.root.lookupAll(".scroll-bar")) {
            scroll.style =
                """
                -fx-background-color: transparent;
                -fx-background: transparent;
                """.trimIndent()
        }
        for (track in stage.scene.root.lookupAll(".scroll-bar .track")) {
            track.style =
                """
                -fx-background-color: transparent;
                -fx-background-radius: 0;
                """.trimIndent()
        }
        for (thumb in stage.scene.root.lookupAll(".scroll-bar .thumb")) {
            thumb.style =
                """
                -fx-background-color: $HOVER_TRANSPARENT_WHITE;
                -fx-background-radius: 4;
                """.trimIndent()
        }
        for (btn in stage.scene.root.lookupAll(".scroll-bar .increment-button, .scroll-bar .decrement-button")) {
            btn.style =
                """
                -fx-background-color: transparent;
                -fx-padding: 0;
                """.trimIndent()
        }
    }
}
