package GeneralConstants;

import Player.Player;

public class GeneralConstants {
    public static int NUMBER_OF_ROWS = 18;
    public static Player PLAYER1 = new Player(1);
    public static Player PLAYER2 = new Player(0);
    public static Player CURRENT_PLAYER;
    public static int PLACE_NOT_TAKEN = -1;
    public static String PAST_GAME_FILENAME = "./pastGame/pastGame.txt";
    public static String ALL_POSITIONS_IN_GAME = "./pastGame/allBoards.txt";
}
