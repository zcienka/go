package GameEngine;

import Pair.Pair;
import Player.Player;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static GeneralConstants.GeneralConstants.*;
import static java.lang.Integer.numberOfTrailingZeros;
import static java.lang.Integer.parseInt;

import java.util.concurrent.ThreadLocalRandom;

public class GameEngine {
    private ArrayList<List<Integer>> gameBoard;
    private Player HumanPlayer;
    private Player ComputerPlayer;
    private ArrayList<Long> pastMoves;
    private ArrayList<List<List<Long>>> zobristTable;
    private ArrayList<List<Boolean>> deadStones;
    private boolean repaintBoard;
    private boolean invalidMove;
    private Pair lastMoveCoordinates;
    private int undefinedColor;

    // -1 - position not taken, 0 - position taken by white stone, 1 - position taken by black stone

    public GameEngine() {
        gameBoard = new ArrayList<>(NUMBER_OF_ROWS + 1);

        this.HumanPlayer = new Player(0);
        this.ComputerPlayer = new Player(0);
        this.undefinedColor = -2;
        initializeZobristTable();
        pastMoves = new ArrayList<>();
        repaintBoard = false;
        lastMoveCoordinates = new Pair(null, null);

        if (isPastFileAvailable()) {
            initializeBoardFromFile();
        } else {
            initializeEmptyGameBoard();
        }
//        gameBoard = new ArrayList<Integer>();
//        gameBoard = new ArrayList<List<Integer>>();
//        gameBoard.add(Arrays.asList(-1, -1, -1, 0, 1, 1, -1, 1, 0, 0, -1, -1, -1));
//        gameBoard.add(Arrays.asList(-1, 0, -1, -1, 0, 1, -1, 1, 0, -1, 0, -1, -1));
//        gameBoard.add(Arrays.asList(0, -1, -1, 0, -1, 0, 1, 1, 1, 0, 0, 0, 0));
//        gameBoard.add(Arrays.asList(1, 0, 0, -1, 0, -1, 0, 1, -1, 1, 1, 0, 1));
//        gameBoard.add(Arrays.asList(1, 1, 0, -1, -1, 0, 0, 0, 1, -1, 1, 1, 1));
//        gameBoard.add(Arrays.asList(-1, -1, 1, 0, 0, 0, -1, 0, 0, 1, 1, -1, -1));
//        gameBoard.add(Arrays.asList(-1, 1, -1, 1, 1, 0, 0, 1, 0, 1, -1, -1, -1));
//        gameBoard.add(Arrays.asList(-1, -1, -1, -1, 1, -1, 0, 1, -1, 1, -1, -1, -1));
//        gameBoard.add(Arrays.asList(-1, -1, -1, -1, 1, 1, 1, 1, 1, 0, 1, -1, -1));
//        gameBoard.add(Arrays.asList(-1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, -1, -1));
//        gameBoard.add(Arrays.asList(1, 1, 0, 0, -1, -1, -1, 0, -1, 0, 1, -1, -1));
//        gameBoard.add(Arrays.asList(1, 0, -1, 0, -1, -1, -1, -1, 0, 1, 1, -1, -1));
//        gameBoard.add(Arrays.asList(-1, 0, -1, 0, -1, -1, -1, -1, 0, 0, 1, -1, -1));


//        gameBoard.add(Arrays.asList(-1, -1, -1, 0, 1, 1, -1, 1, 0, 0, -1, -1, -1));
//        gameBoard.add(Arrays.asList(-1, 0, -1, -1, 0, 1, -1, 1, 0, -1, 0, -1, -1));
//        gameBoard.add(Arrays.asList(0, -1, -1, 0, -1, 0, 1, 1, 1, 0, 0, 0, 0));
//        gameBoard.add(Arrays.asList(1, 0, 0, -1, 0, -1, 0, 1, -1, 1, 1, 0, 1));
//        gameBoard.add(Arrays.asList(1, 1, 0, -1, -1, 0, 0, 0, 1, -1, 1, 1, 1));
//        gameBoard.add(Arrays.asList(-1, -1, 1, 0, 0, 0, -1, 0, 0, 1, 1, -1, -1));
//        gameBoard.add(Arrays.asList(-1, 1, -1, 1, 1, 0, 0, 1, 0, 1, -1, 0, -1));
//        gameBoard.add(Arrays.asList(-1, -1, -1, -1, 1, -1, 0, 1, -1, 1, -1, -1, -1));
//        gameBoard.add(Arrays.asList(-1, -1, -1, -1, 1, 1, 1, 1, 1, 0, 1, -1, -1));
//        gameBoard.add(Arrays.asList(-1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, -1, -1));
//        gameBoard.add(Arrays.asList(1, 1, 0, 0, -1, -1, -1, 0, -1, 0, 1, -1, -1));
//        gameBoard.add(Arrays.asList(1, 0, -1, 0, -1, -1, -1, -1, 0, 1, 1, -1, -1));
//        gameBoard.add(Arrays.asList(-1, 0, -1, 0, -1, -1, -1, -1, 0, 0, 1, -1, -1));
        CURRENT_PLAYER = HUMAN_PLAYER;
        deadStones = new ArrayList<>(NUMBER_OF_ROWS + 1);

        for (int i = 0; i <= NUMBER_OF_ROWS; i++) {
            deadStones.add(new ArrayList<>(NUMBER_OF_ROWS + 1));
            for (int j = 0; j <= NUMBER_OF_ROWS; j++) {
                deadStones.get(i).add(j, false);
            }
        }

    }

    private void initializeEmptyGameBoard() {
        ArrayList<Integer> row = new ArrayList<>(NUMBER_OF_ROWS + 1);
        for (int j = 0; j <= NUMBER_OF_ROWS; j++) {
            row.add(-1);
        }
        for (int i = 0; i <= NUMBER_OF_ROWS; i++) {
            gameBoard.add(new ArrayList<>(row));
        }
    }

    public ArrayList<List<Integer>> getGameBoard() {
        return gameBoard;
    }

    // calculating points with flood fill algorithm
    // I assume that score is counted by the amount of empty places surrounded by given player plus number of captured
    // stones of opponent
    // I'm removing dead stones for calculating the final score
    // each dead stone increments opponent score by 1
    public void calculateFinalPoints(int currentPlayerColor) {
        int currentPlayerPoints = 0;
        int opponentPoints = 0;
        Pair pair;

        for (int i = 0; i <= NUMBER_OF_ROWS; i++) {
            for (int j = 0; j <= NUMBER_OF_ROWS; j++) {
                if (gameBoard.get(i).get(j) == -1) {
                    pair = countTerritoryPoints(i, j, currentPlayerColor);
                    if (pair.first != undefinedColor) {
                        if (pair.first == currentPlayerColor) {
                            currentPlayerPoints += pair.second;
                        } else {
                            opponentPoints += pair.second;
                        }
                    }
                }
            }
        }
    }

    // I assume that you can't commit suicide
    public void makeMove(int i, int j, int currentPLayer) {
        gameBoard.get(i).set(j, currentPLayer);

        if (isSuperko()) {
            undoMove(new Pair(i, j));
            setInvalidMove(true);
            return;
        }
        if (lastMoveCoordinates.first != null && lastMoveCoordinates.second != null) {
            if (isKo(i, j)) {
                undoMove(new Pair(i, j));
                setInvalidMove(true);
                return;
            }
        }

        if (deleteCapturedStones()) {
            setRepaintBoard(true);
        }
        setLastMoveCoordinates(i, j);
        addBoardToPastBoards();
    }

    public void addBoardToPastBoards() {
        Long hash = computeZobristHash(gameBoard);
        pastMoves.add(hash);
    }

    public void popLastMoveFromPastMoves() {
        pastMoves.remove(pastMoves.size() - 1);
    }

    private boolean checkIfInvalidIndex(float x, float y) {
        return x > NUMBER_OF_ROWS || y > NUMBER_OF_ROWS || y < 0 || x < 0;
    }

    // https://stackoverflow.com/questions/30462341/calculate-the-score-of-the-game-go
    // flood fill algorithm BFS
//    https://www.reddit.com/r/baduk/comments/13dt8t/help_writing_a_scoring_algorithm_for_a_go_game/
    public Pair countTerritoryPoints(int x, int y, int currentPlayerColor) {
        LinkedList<Pair> queue = new LinkedList<>();
        queue.add(new Pair(x, y));
        ArrayList<ArrayList<Integer>> colors = new ArrayList<>();
        int numberOfPoints = 0;
        ArrayList<Integer> c = new ArrayList<>();

        for (int i = 0; i <= NUMBER_OF_ROWS; i++) {
            colors.add(new ArrayList<>());
            for (int j = 0; j <= NUMBER_OF_ROWS; j++) {
                colors.get(i).add(null);
            }
        }
        Integer color;

        while (!queue.isEmpty()) {
            Pair n = queue.pop();
            if (!checkIfInvalidIndex(n.first - 1, n.second)) {
                color = gameBoard.get(n.first - 1).get(n.second);
                if (color == -1) {
                    gameBoard.get(n.first - 1).set(n.second, 4);
                    queue.add(new Pair(n.first - 1, n.second));
                    numberOfPoints++;
                } else if (color != 4) {
                    if (!Objects.equals(colors.get(n.first - 1).get(n.second), color) &&
                            !isDeadStone(n.first - 1, n.second)) {
                        colors.get(n.first - 1).set(n.second, color);
                        c.add(color);
                    }
                }
            }

            if (!checkIfInvalidIndex(n.first + 1, n.second)) {
                color = gameBoard.get(n.first + 1).get(n.second);
                if (color == -1) {
                    gameBoard.get(n.first + 1).set(n.second, 4);
                    queue.add(new Pair(n.first + 1, n.second));
                    numberOfPoints++;
                } else if (color != 4) {
                    if (!Objects.equals(colors.get(n.first + 1).get(n.second), color) &&
                            !isDeadStone(n.first + 1, n.second)) {
                        colors.get(n.first + 1).set(n.second, color);
                        c.add(color);
                    }
                }
            }

            if (!checkIfInvalidIndex(n.first, n.second + 1)) {
                color = gameBoard.get(n.first).get(n.second + 1);
                if (color == -1) {
                    gameBoard.get(n.first).set(n.second + 1, 4);
                    queue.add(new Pair(n.first, n.second + 1));
                    numberOfPoints++;
                } else if (color != 4) {
                    if (!Objects.equals(colors.get(n.first).get(n.second + 1), color) &&
                            !isDeadStone(n.first, n.second + 1)) {
                        colors.get(n.first).set(n.second + 1, color);
                        c.add(color);
                    }
                }
            }

            if (!checkIfInvalidIndex(n.first, n.second - 1)) {
                color = gameBoard.get(n.first).get(n.second - 1);
                if (color == -1) {
                    gameBoard.get(n.first).set(n.second - 1, 4);
                    queue.add(new Pair(n.first, n.second - 1));
                    numberOfPoints++;
                } else if (color != 4) {
                    if (!Objects.equals(colors.get(n.first).get(n.second - 1), color) &&
                            !isDeadStone(n.first, n.second - 1)) {
                        colors.get(n.first).set(n.second - 1, color);
                        c.add(color);
                    }
                }
            }
        }
        for (Integer s : c) {
            if (!s.equals(c.get(0))) {
                return new Pair(0, -1);
            }
        }

        if (c.get(0) == currentPlayerColor) {
            return new Pair(numberOfPoints, -1);
        } else {
            return new Pair(-numberOfPoints, -1);
        }
    }

    // I'm checking only opponents of current stone are surrounded by current stone
    public boolean deleteCapturedStones() {
        int currentStonePlayer;
        ArrayList<Pair> capturedStones;

        for (int i = 0; i <= NUMBER_OF_ROWS; i++) {
            for (int j = 0; j <= NUMBER_OF_ROWS; j++) {
                currentStonePlayer = gameBoard.get(i).get(j);

                if (currentStonePlayer == Math.abs(CURRENT_PLAYER.getPlayerNumber() - 1)) {
                    capturedStones = checkForCapturedStones(new Pair(i, j));
                    if (capturedStones != null) {
                        for (Pair stone : capturedStones) {
                            gameBoard.get(stone.first).set(stone.second, -1);
                            CURRENT_PLAYER.addNumberOfCapturedStones(1);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // rule of ko means that the player can't move to the same position as before (last move stone color doesn't matter)
    // (it prevents infinite loop in a game)
    public boolean isKo(int i, int j) {
        return lastMoveCoordinates.first == i && lastMoveCoordinates.second == j;
    }

    // rule of superko means that the board can't be in the same state as before
    public boolean isSuperko() {
//        ArrayList<List<Integer>> xd = (ArrayList<List<Integer>) tempGameBoard.clone();
//        gameBoard.get(x).set(y, CURRENT_PLAYER.getPlayerNumber());
        Long zobristHash = computeZobristHash(gameBoard);

        if (!pastMoves.isEmpty()) {
            for (Long move : pastMoves) {
                if (Objects.equals(zobristHash, move)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isGameOver() {
        if (pastMoves.size() != 0) {
            for (int i = 0; i <= NUMBER_OF_ROWS; i++) {
                for (int j = 0; j <= NUMBER_OF_ROWS; j++) {
                    if (areLibertiesLeft(new Pair(i, j))) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    public ArrayList<Pair> checkForCapturedStones(Pair stone) {
        LinkedList<Pair> queue = new LinkedList<>();
        ArrayList<Integer> colors = new ArrayList<>();
        ArrayList<Pair> coloredStonesIndexes = new ArrayList<>();

        int opponent = Math.abs(CURRENT_PLAYER.getPlayerNumber() - 1);
        int color;
        int coloredStone = -2;
        queue.add(stone);

        while (!queue.isEmpty()) {
            Pair n = queue.pop();
            if (!checkIfInvalidIndex(n.first - 1, n.second)) {
                color = gameBoard.get(n.first - 1).get(n.second);
                if (color == opponent) {
                    gameBoard.get(n.first - 1).set(n.second, coloredStone);
                    queue.add(new Pair(n.first - 1, n.second));
                    coloredStonesIndexes.add(new Pair(n.first - 1, n.second));
                } else if (color != coloredStone) {
                    colors.add(color);
                }
            }
            if (!checkIfInvalidIndex(n.first + 1, n.second)) {
                color = gameBoard.get(n.first + 1).get(n.second);
                if (color == opponent) {
                    gameBoard.get(n.first + 1).set(n.second, coloredStone);
                    queue.add(new Pair(n.first + 1, n.second));
                    coloredStonesIndexes.add(new Pair(n.first + 1, n.second));
                } else if (color != coloredStone) {
                    colors.add(color);
                }
            }
            if (!checkIfInvalidIndex(n.first, n.second + 1)) {
                color = gameBoard.get(n.first).get(n.second + 1);
                if (color == opponent) {
                    gameBoard.get(n.first).set(n.second + 1, coloredStone);
                    queue.add(new Pair(n.first, n.second + 1));
                    coloredStonesIndexes.add(new Pair(n.first, n.second + 1));
                } else if (color != coloredStone) {
                    colors.add(color);
                }

            }
            if (!checkIfInvalidIndex(n.first, n.second - 1)) {
                color = gameBoard.get(n.first).get(n.second - 1);
                if (color == opponent) {
                    gameBoard.get(n.first).set(n.second - 1, coloredStone);
                    queue.add(new Pair(n.first, n.second - 1));
                    coloredStonesIndexes.add(new Pair(n.first, n.second - 1));
                } else if (color != coloredStone) {
                    colors.add(color);
                }
            }
        }

        for (int i = 0; i <= NUMBER_OF_ROWS; i++) {
            for (int j = 0; j <= NUMBER_OF_ROWS; j++) {
                if (gameBoard.get(i).get(j) == coloredStone) {
                    gameBoard.get(i).set(j, opponent);
                }
            }
        }
        int currentPlayer = Math.abs(opponent - 1);

        for (Integer c : colors) {
            if (!c.equals(currentPlayer)) {
                return null;
            }
        }
        if (coloredStonesIndexes.size() == 0) {
            coloredStonesIndexes.add(stone);
        }
        return coloredStonesIndexes;
    }

//    public boolean isMoveValid(int i, int j, int currentStonePlayer) {
//        return (gameBoard.get(i).get(j) == -1 ||
//                Objects.equals(gameBoard.get(i).get(j), currentStonePlayer));
//    }

    // checking if there are any valid moves left
    public boolean areLibertiesLeft(Pair stone) {
        if (gameBoard.get(stone.first + 1).get(stone.second) == -1) {
            return true;
        }
        if (gameBoard.get(stone.first - 1).get(stone.second) == -1) {
            return true;
        }
        if (gameBoard.get(stone.first).get(stone.second + 1) == -1) {
            return true;
        }
        if (gameBoard.get(stone.first).get(stone.second - 1) == -1) {
            return true;
        }
        return false;
    }

    public void undoMove(Pair stone) {
        gameBoard.get(stone.first).set(stone.second, -1);
    }

    private void initializeZobristTable() {
        zobristTable = new ArrayList<>(NUMBER_OF_ROWS + 1);
        long randomNumber;

        for (int i = 0; i <= NUMBER_OF_ROWS; i++) {
            zobristTable.add(new ArrayList<>(NUMBER_OF_ROWS + 1));
            for (int j = 0; j <= NUMBER_OF_ROWS; j++) {
                zobristTable.get(i).add(new ArrayList<>(NUMBER_OF_ROWS + 1));
                for (int k = 0; k < 2; k++) {
                    randomNumber = ThreadLocalRandom.current().nextLong(0, Long.MAX_VALUE);
                    zobristTable.get(i).get(j).add(randomNumber);
                }
            }
        }
        System.out.println("xd");
    }

    // https://www.geeksforgeeks.org/minimax-algorithm-in-game-theory-set-5-zobrist-hashing/?ref=lbp
    public Long computeZobristHash(ArrayList<List<Integer>> board) {
        Long hash = 0L;
        int stoneNumber;

        for (int i = 0; i <= NUMBER_OF_ROWS; i++) {
            for (int j = 0; j <= NUMBER_OF_ROWS; j++) {
                stoneNumber = board.get(i).get(j);
                if (stoneNumber != -1) {
                    hash ^= zobristTable.get(i).get(j).get(stoneNumber);
                }
            }
        }
        return hash;
    }

    public void setDeadStone(int i, int j) {
        deadStones.get(i).set(j, true);
    }

    public boolean isDeadStone(int i, int j) {
        return deadStones.get(i).get(j);
    }

    public boolean isRepaintBoard() {
        return repaintBoard;
    }

    public void setRepaintBoard(boolean repaintBoard) {
        this.repaintBoard = repaintBoard;
    }

    public boolean isInvalidMove() {
        return invalidMove;
    }

    public void setInvalidMove(boolean invalidMove) {
        this.invalidMove = invalidMove;
    }

    public void setLastMoveCoordinates(int i, int j) {
        lastMoveCoordinates.first = i;
        lastMoveCoordinates.second = j;
    }

    public void resign() {

    }

    public void passMove() {

    }

    public int getPlayerOneScore() {
        return HumanPlayer.getNumberOfCapturedStones();
    }

    public int getPlayerTwoScore() {
        return ComputerPlayer.getNumberOfCapturedStones();
    }

    public void saveGameToFile() {
        File pastGameDirectory = new File("/pastGame");
        if (!pastGameDirectory.exists()) {
            pastGameDirectory.mkdirs();
        }

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("/pastGame/pastGame.txt"), StandardCharsets.UTF_8))) {

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

    public void initializeBoardFromFile() {
        String file = "/pastGame/pastGame.txt";
        ArrayList<ArrayList<Integer>> gameBoard = new ArrayList<>(NUMBER_OF_ROWS + 1);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int indexCounter = 0;

            while ((line = br.readLine()) != null) {
                String[] numbers = line.split(" ");
                gameBoard.add(new ArrayList<>(NUMBER_OF_ROWS + 1));

                for (String number : numbers) {
                    gameBoard.get(indexCounter).add(parseInt(number));
                }

                indexCounter++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isPastFileAvailable() {
        File file = new File("/pastGame/pastGame1.txt");
        return file.exists();
    }
}