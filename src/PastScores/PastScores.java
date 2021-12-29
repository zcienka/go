package PastScores;

import Player.Player;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

public class PastScores {
    private int;

    public void readPastScores() {

    }

    public void saveScoreToPastScores(Player player1, Player player2) {
        File scoresDirectory = new File("/scores");
        if (!scoresDirectory.exists()) {
            scoresDirectory.mkdirs();
        }
        try {
            LocalDateTime now = LocalDateTime.now();
            File newScore = new File("/scores/" + now + ".txt");

            if (!newScore.createNewFile()) {
                System.out.println("File already exists.");
                System.exit(1);
            } else {
                
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
