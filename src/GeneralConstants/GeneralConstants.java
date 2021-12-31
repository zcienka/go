package GeneralConstants;

import Player.Player;

// PLAYER 1 is a black stone and PLAYER 2 is a white stone
public class GeneralConstants {
    public static int NUMBER_OF_ROWS = 18;
    public static int PLACE_NOT_TAKEN = -1;
    public static Player PLAYER1 = new Player(1);
    public static Player PLAYER2 = new Player(0);
    public static Player CURRENT_PLAYER = new Player(1);
}
