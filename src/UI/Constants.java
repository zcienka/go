package UI;

import java.awt.*;

import static GeneralConstants.GeneralConstants.NUMBER_OF_ROWS;

public class Constants {
    public static int WINDOW_WIDTH = 900;
    public static int WINDOW_HEIGHT = WINDOW_WIDTH + 100;
    public static int SIZE_OF_CELL = 50;
    public static int GRID_SHIFT_X = WINDOW_WIDTH / 2 - (SIZE_OF_CELL * NUMBER_OF_ROWS) / 2;
    public static int STONE_DIAMETER = SIZE_OF_CELL;
    public static int BOARD_SIZE = SIZE_OF_CELL * NUMBER_OF_ROWS + 96;
    public static int BOARD_SHIFT_X = GRID_SHIFT_X - 48;
    public static int BOARD_SHIFT_Y = BOARD_SHIFT_X;
    public static int BUTTON_WIDTH = 200;
    public static int BUTTON_HEIGHT = 100;
    public static String FONT_STYLE = "Helvetica";
    public static int BIG_FONT_SIZE = 36;
    public static int MEDIUM_FONT_SIZE = 24;
    public static int WINDOW_PADDING = 36;
    public static Color BOARD_COLOR = new Color(255, 211, 154);
    public static int PAST_SCORES_ICON_SIZE = 56;
}
