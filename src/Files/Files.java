package Files;

import GameEngine.GameEngine;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static GeneralConstants.GeneralConstants.*;
import static java.lang.Integer.parseInt;

public class Files {
//    private final ArrayList<List<Integer>> gameBoard;
//
//    public Files(ArrayList<List<Integer>> gameBoard) {
//        this.gameBoard = gameBoard;
//    }

    public void saveGameToFile(ArrayList<List<Integer>> gameBoard) {
        File pastGameDirectory = new File("./pastGame");
        if (!pastGameDirectory.exists()) {
            pastGameDirectory.mkdirs();
        }

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(PAST_GAME_FILENAME), StandardCharsets.UTF_8))) {
            writer.write("Current player move: \n");

            if (CURRENT_PLAYER == PLAYER1) {
                writer.write("PLAYER1");
            } else {
                writer.write("PLAYER2");
            }

            writer.write("Number of stones captured by player 1: ");
            writer.write(PLAYER1.getNumberOfCapturedStones());
            writer.write("Number of stones captured by player 2: ");
            writer.write(PLAYER2.getNumberOfCapturedStones());


            for (int i = 0; i <= NUMBER_OF_ROWS; i++) {
                for (int j = 0; j <= NUMBER_OF_ROWS; j++) {
                    writer.write(gameBoard.get(i).get(j) + " ");
                }
                writer.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initializeBoardFromFile(GameEngine game) {
        try (BufferedReader br = new BufferedReader(new FileReader(PAST_GAME_FILENAME))) {
            String line;
            int indexCounter = 0;

            br.readLine();
            PLAYER1.setNumberOfCapturedStones(Integer.parseInt(br.readLine()));
            br.readLine();
            PLAYER1.setNumberOfCapturedStones(Integer.parseInt(br.readLine()));
            ArrayList<List<Integer>> gameBoard = new ArrayList<>(NUMBER_OF_ROWS + 1);

            while ((line = br.readLine()) != null) {
                String[] numbers = line.split(" ");
                gameBoard.add(new ArrayList<>(NUMBER_OF_ROWS + 1));

                for (String number : numbers) {
                    gameBoard.get(indexCounter).add(parseInt(number));
                }
                indexCounter++;
            }
            game.setGameBoard(gameBoard);
        } catch (IOException e) {
            System.exit(0);
        }
    }

    public boolean isPastFileAvailable() {
        File file = new File(PAST_GAME_FILENAME);
        File allPositionsFile = new File(ALL_POSITIONS_IN_GAME);
        return file.exists() && allPositionsFile.exists();
    }

    public void deleteBoardFiles() {
        if (isPastFileAvailable()) {
            File pastGameFile = new File(PAST_GAME_FILENAME);
            pastGameFile.delete();
            File allPositionsFile = new File(ALL_POSITIONS_IN_GAME);
            allPositionsFile.delete();
        }
    }


}
