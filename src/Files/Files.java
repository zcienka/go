package Files;

import ErrorMessage.ErrorMessage;
import Game.Game;
import Pair.Pair;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static GeneralConstants.GeneralConstants.*;
import static java.lang.Integer.parseInt;

public class Files {
    private final ErrorMessage errorMessage = new ErrorMessage();
    private static final String pastGameBoardFilename = "./pastGame/pastGame.txt";
    private static final String allPositionsInGame = "./pastGame/allBoards.txt";
    private static final String zobristBoardFilename = "./pastGame/zobristBoard.txt";

    public void saveGameToFile(Game game) {
        File pastGameDirectory = new File("./pastGame");
        if (!pastGameDirectory.exists()) {
            pastGameDirectory.mkdirs();
        }

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(pastGameBoardFilename), StandardCharsets.UTF_8))) {
            writer.write("Last player move: \n");

            if (CURRENT_PLAYER.getPlayerNumber() == 1) {
                writer.write("PLAYER1\n");
            } else {
                writer.write("PLAYER2\n");
            }

            writer.write("Number of stones captured by player 1: \n");
            writer.write(PLAYER1.getNumberOfCapturedStones() + "\n");
            writer.write("Has player 1 passed his move: \n");
            writer.write(PLAYER1.isMovePassed() + "\n");

            writer.write("Number of stones captured by player 2: \n");
            writer.write(PLAYER2.getNumberOfCapturedStones() + "\n");
            writer.write("Has player 2 passed his move: \n");
            writer.write(Boolean.toString(PLAYER2.isMovePassed()) + "\n");

            ArrayList<List<Integer>> gameBoard = game.getGameBoard();

            for (int i = 0; i <= NUMBER_OF_ROWS; i++) {
                for (int j = 0; j <= NUMBER_OF_ROWS; j++) {
                    writer.write(gameBoard.get(i).get(j) + " ");
                }
                writer.write("\n");
            }
        } catch (IOException e) {
            errorMessage.brokenFile();
            deleteBoardFiles();
            System.exit(0);
        }
    }

    public void saveZobristBoardToFile(ArrayList<List<List<Long>>> zobristBoard) {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(zobristBoardFilename), StandardCharsets.UTF_8))) {

            for (int i = 0; i <= NUMBER_OF_ROWS; i++) {
                for (int j = 0; j <= NUMBER_OF_ROWS; j++) {
                    for (int k = 0; k < 2; k++) {
                        writer.write(zobristBoard.get(i).get(j).get(k) + " ");
                    }
                }
                writer.write("\n");
            }
        } catch (IOException e) {
            errorMessage.brokenFile();
            deleteBoardFiles();
            System.exit(0);
        }
    }

    public void savePastMovesToFile(ArrayList<Long> pastMoves) {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(allPositionsInGame), StandardCharsets.UTF_8))) {

            for (Long pastMove : pastMoves) {
                writer.write(pastMove + "\n");
            }

        } catch (IOException e) {
            errorMessage.brokenFile();
            deleteBoardFiles();
            System.exit(0);
        }
    }

    public void initializeBoardFromFile(Game game) {
        try (BufferedReader br = new BufferedReader(new FileReader(pastGameBoardFilename))) {
            String line;
            int indexCounter = 0;

            br.readLine(); // Last player move:
            String currentPlayer = br.readLine();
            if (Objects.equals(currentPlayer, "PLAYER1")) {
                game.setCurrentPlayerToPLayer1();
            } else {
                game.setCurrentPlayerToPlayer2();
            }

            br.readLine(); // Number of stones captured by player 1:
            int x = Integer.parseInt(br.readLine());
            PLAYER1.setNumberOfCapturedStones(x);
            br.readLine(); // Has player 1 passed his move:
            PLAYER1.setPassMove(Boolean.parseBoolean(br.readLine()));

            br.readLine(); // Number of stones captured by player 2:
            int y = Integer.parseInt(br.readLine());
            PLAYER2.setNumberOfCapturedStones(y);
            br.readLine(); // Has player 2 passed his move:
            PLAYER2.setPassMove(Boolean.parseBoolean(br.readLine()));

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
            errorMessage.brokenFile();
            deleteBoardFiles();
            System.exit(0);
        }
    }

    public void initializeZobristBoardFromFile(Game game) {
        try (BufferedReader br = new BufferedReader(new FileReader(zobristBoardFilename))) {
            String line;
            List<String> row;
            int l, i;
            ArrayList<List<List<Long>>> zobristTable = new ArrayList<>(NUMBER_OF_ROWS + 1);

            i = 0;
            while ((line = br.readLine()) != null) {
                l = 0;
                zobristTable.add(new ArrayList<>(NUMBER_OF_ROWS + 1));
                row = Arrays.asList(line.split(" "));

                for (int j = 0; j <= NUMBER_OF_ROWS; j++) {
                    zobristTable.get(i).add(new ArrayList<>(NUMBER_OF_ROWS + 1));
                    for (int k = 0; k < 2; k++) {
                        zobristTable.get(i).get(j).add(Long.parseLong(row.get(l)));
                        l++;
                    }
                }
                i++;
            }
            game.setZobristTable(zobristTable);
        } catch (IOException e) {
            errorMessage.brokenFile();
            deleteBoardFiles();
            System.exit(0);
        }
    }

    public void initializeAllPastPositionsFromFile(Game game) {
        try (BufferedReader br = new BufferedReader(new FileReader(allPositionsInGame))) {
            String line;
            ArrayList<Long> pastMoves = new ArrayList<>();

            while ((line = br.readLine()) != null) {
                pastMoves.add(Long.parseLong(line));
            }
            game.setPastMoves(pastMoves);
        } catch (IOException e) {
            errorMessage.brokenFile();
            deleteBoardFiles();
            System.exit(0);
        }
    }

    public void deleteBoardFiles() {
        File pastGameFile = new File(pastGameBoardFilename);
        if (pastGameFile.exists()) {
            pastGameFile.delete();
        }

        File allPositionsFile = new File(allPositionsInGame);
        if (allPositionsFile.exists()) {
            allPositionsFile.delete();
        }

        File zobristBoard = new File(zobristBoardFilename);
        if (zobristBoard.exists()) {
            zobristBoard.delete();
        }
    }

    public boolean arePastFilesAvailable() {
        File pastGameFile = new File(pastGameBoardFilename);
        File allPositionsFile = new File(allPositionsInGame);
        File zobristBoard = new File(zobristBoardFilename);

        return pastGameFile.exists() && allPositionsFile.exists() && zobristBoard.exists();
    }
}
