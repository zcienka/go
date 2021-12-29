package GameEngine;

import Pair.Pair;
import Player.Player;

import java.util.*;

import static GeneralConstants.GeneralConstants.*;

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
//    private static int points;
//    private int

    // -1 - position not taken, 0 - position taken by white stone, 1 - position taken by black stone

    public GameEngine() {
        gameBoard = new ArrayList<>(NUMBER_OF_ROWS + 1);
        ArrayList<Integer> row = new ArrayList<>(NUMBER_OF_ROWS + 1);
//        this.lastPlayersMoves = null;
        this.HumanPlayer = new Player(0);
        this.ComputerPlayer = new Player(0);
        this.undefinedColor = -2;
        initializeZobristTable();
        pastMoves = new ArrayList<>();
        repaintBoard = false;
        lastMoveCoordinates = new Pair(null, null);
//        lastMoveCoordinates.first = null;
//        lastMoveCoordinates.second = null;
//        zobristTable = new ArrayList<>(NUMBER_OF_ROWS + 1);

        for (int j = 0; j <= NUMBER_OF_ROWS; j++) {
            row.add(-1);
        }
        for (int i = 0; i <= NUMBER_OF_ROWS; i++) {
            gameBoard.add(new ArrayList<>(row));
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
//        countTerritoryPoints(12, 12, 1);

        deadStones = new ArrayList<>(NUMBER_OF_ROWS + 1);

        for (int i = 0; i <= NUMBER_OF_ROWS; i++) {
            deadStones.add(new ArrayList<>(NUMBER_OF_ROWS + 1));
            for (int j = 0; j <= NUMBER_OF_ROWS; j++) {
                deadStones.get(i).add(j, false);
            }
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
    public Integer calculatePlayerOpponentPointRatio(int currentPlayerColor) {
        int currentPlayerPoints = 0;
        int opponentPoints = 0;
        Pair pair;
        ArrayList<List<Integer>> tempBoard = gameBoard;

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

        gameBoard = tempBoard;
        return currentPlayerPoints - opponentPoints;
    }

    public void makeMove(int i, int j, int currentPLayer) {
        gameBoard.get(i).set(j, currentPLayer);

        if (isSuperko()) {
            System.out.println("SUPERKO");
            undoMove(new Pair(i, j));
            setInvalidMove(true);
            return;
        }
        if (lastMoveCoordinates.first != null && lastMoveCoordinates.second != null) {
            if (isKo(i, j)) {
                undoMove(new Pair(i, j));
                System.out.println("KO");
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
//        ArrayList<List<Integer>> tempBoard = gameBoard;
//        tempBoard.get(move.first).set(move.second, currentPlayer);
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

    public boolean deleteCapturedStones() {
        boolean areStonesToCapture = false;
        int stoneColor;
        ArrayList<Pair> capturedStones;

        for (int i = 0; i <= NUMBER_OF_ROWS; i++) {
            for (int j = 0; j <= NUMBER_OF_ROWS; j++) {
                stoneColor = gameBoard.get(i).get(j);
                if (stoneColor != -1) {
                    capturedStones = checkForCapturedStones(new Pair(i, j), stoneColor);

                    if (capturedStones != null) {
                        for (Pair stone : capturedStones) {
                            gameBoard.get(stone.first).set(stone.second, -1);
                            areStonesToCapture = true;
                            CURRENT_PLAYER.addNumberOfCapturedStones(1);
                        }
                    }
//                    if (!checkForCapturedStones(new Pair(i, j), stoneColor)) {
//                        gameBoard.get(i).set(j, -1);
//                        CURRENT_PLAYER.addNumberOfCapturedStones(1);
//                        areStonesToCapture = true;
//                    }
                }
            }
        }
        return areStonesToCapture;
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

//    public boolean isGameOver() {
//        int stoneColor;
//
//        if (pastMoves.size() != 0) {
//            for (int i = 0; i <= NUMBER_OF_ROWS; i++) {
//                for (int j = 0; j <= NUMBER_OF_ROWS; j++) {
//                    stoneColor = gameBoard.get(i).get(j);
//                    if (checkForCapturedStones(new Pair(i, j), stoneColor) && !isSuperko(i, j)) {
//                        return false;
//                    }
//                }
//            }
//            return true;
//        }
//        return false;
//    }

    // liberty is defined as a valid move of a stone
//    public boolean checkForCapturedStones(Pair stone, int stoneColor) {
//        if (!checkIfInvalidIndex(stone.first + 1, stone.second)) {
//            if (isMoveValid(stone.first + 1, stone.second, stoneColor)) {
//                return true;
//            }
//        }
//        if (!checkIfInvalidIndex(stone.first - 1, stone.second)) {
//            if (isMoveValid(stone.first - 1, stone.second, stoneColor)) {
//                return true;
//            }
//        }
//        if (!checkIfInvalidIndex(stone.first, stone.second + 1)) {
//            if (isMoveValid(stone.first, stone.second + 1, stoneColor)) {
//                return true;
//            }
//        }
//        if (!checkIfInvalidIndex(stone.first, stone.second - 1)) {
//            if (isMoveValid(stone.first, stone.second - 1, stoneColor)) {
//                return true;
//            }
//        }
//        return false;
//    }

    public ArrayList<Pair> checkForCapturedStones(Pair stone, int stoneColor) {
        LinkedList<Pair> queue = new LinkedList<>();
        queue.add(stone);
        int color;
        ArrayList<Integer> colors = new ArrayList<>();
        int coloredStone = -2;
        ArrayList<Pair> coloredStonesIndexes = new ArrayList<>();

        while (!queue.isEmpty()) {
            Pair n = queue.pop();
            if (!checkIfInvalidIndex(n.first - 1, n.second)) {
                color = gameBoard.get(n.first - 1).get(n.second);
                if (color == stoneColor) {
                    gameBoard.get(n.first - 1).set(n.second, coloredStone);
                    queue.add(new Pair(n.first - 1, n.second));
                    coloredStonesIndexes.add(new Pair(n.first - 1, n.second));
                } else if (color != coloredStone) {
                    colors.add(color);
                }
            }
            if (!checkIfInvalidIndex(n.first + 1, n.second)) {
                color = gameBoard.get(n.first + 1).get(n.second);
                if (color == stoneColor) {
                    gameBoard.get(n.first + 1).set(n.second, coloredStone);
                    queue.add(new Pair(n.first + 1, n.second));
                    coloredStonesIndexes.add(new Pair(n.first + 1, n.second));
                } else if (color != coloredStone) {
                    colors.add(color);
                }
            }
            if (!checkIfInvalidIndex(n.first, n.second + 1)) {
                color = gameBoard.get(n.first).get(n.second + 1);
                if (color == stoneColor) {
                    gameBoard.get(n.first).set(n.second + 1, coloredStone);
                    queue.add(new Pair(n.first, n.second + 1));
                    coloredStonesIndexes.add(new Pair(n.first, n.second + 1));
                } else if (color != coloredStone) {
                    colors.add(color);
                }

            }
            if (!checkIfInvalidIndex(n.first, n.second - 1)) {
                color = gameBoard.get(n.first).get(n.second - 1);
                if (color == stoneColor) {
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
                    gameBoard.get(i).set(j, stoneColor);
                }
            }
        }
        int opponentColor = Math.abs(stoneColor - 1);

        for (Integer c : colors) {
            if (!c.equals(opponentColor)) {
                return null;
            }
        }
        if (coloredStonesIndexes.size() == 0) {
            coloredStonesIndexes.add(stone);
        }
        return coloredStonesIndexes;
    }

    public boolean isMoveValid(int i, int j, int stoneColor) {
        return (gameBoard.get(i).get(j) == -1 ||
                Objects.equals(gameBoard.get(i).get(j), stoneColor));
//                !isKo(i, j);
    }

    // counting number of valid
    public int countNumberOfLiberties(Pair stone) {
        int numberOfLiberties = 0;

        if (gameBoard.get(stone.first + 1).get(stone.second) == -1) {
            numberOfLiberties += 1;
        }
        if (gameBoard.get(stone.first - 1).get(stone.second) == -1) {
            numberOfLiberties += 1;
        }
        if (gameBoard.get(stone.first).get(stone.second + 1) != -1) {
            numberOfLiberties += 1;
        }
        if (gameBoard.get(stone.first).get(stone.second - 1) != -1) {
            numberOfLiberties += 1;
        }
        return numberOfLiberties;
    }

    //////// I assume that capturing the last liberty of the players own block is illegal

//    public ArrayList<Pair> findAllPossibleMoves() {
//        ArrayList<Pair> possibleMoves = new ArrayList<>();
//        Pair stone;
//        int stoneColor;
//
//        for (int i = 0; i <= NUMBER_OF_ROWS; i++) {
//            for (int j = 0; j <= NUMBER_OF_ROWS; j++) {
//                stone = new Pair(i, j);
//                stoneColor = gameBoard.get(i).get(j);
//                if (checkForCapturedStones(stone, stoneColor)) {
//                    possibleMoves.add(stone);
//                }
//            }
//        }
//        return possibleMoves;
//    }

    public void move(Pair stone) {
        gameBoard.get(stone.first).set(stone.second, CURRENT_PLAYER.getPlayerNumber());
    }

    public void undoMove(Pair stone) {
        gameBoard.get(stone.first).set(stone.second, -1);
    }

//    public void play(AI ai) {
//        if (!Objects.equals(CURRENT_PLAYER, HUMAN_PLAYER)) {
//            ai.findBestMove(this);
//            CURRENT_PLAYER = HUMAN_PLAYER;
//        } else {
//
//        }
//        System.out.println("hehehe");
//    }

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
}