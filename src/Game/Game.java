package Game;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import ErrorMessage.ErrorMessage;
import Files.Files;
import Pair.Pair;
import Player.Player;

import static GeneralConstants.GeneralConstants.*;

public class Game {
    private ArrayList<List<Integer>> gameBoard;
    private ArrayList<Long> pastMoves;
    private ArrayList<List<List<Long>>> zobristTable;
    private final ArrayList<List<Boolean>> deadStones;
    private final ErrorMessage errorMessage;
    private Files files;

    // -1 - position not taken, 0 - position taken by white stone, 1 - position taken by black stone
    public Game() {
        gameBoard = new ArrayList<>(NUMBER_OF_ROWS + 1);
        errorMessage = new ErrorMessage();
        pastMoves = new ArrayList<>();
        files = new Files();

        if (files.arePastFilesAvailable()) {
            files.initializeBoardFromFile(this);
            files.initializeZobristBoardFromFile(this);
            files.initializeAllPastPositionsFromFile(this);
        } else {
            initializeZobristTable();
            initializeEmptyGameBoard();
        }
        deadStones = new ArrayList<>(NUMBER_OF_ROWS + 1);

        for (int i = 0; i <= NUMBER_OF_ROWS; i++) {
            deadStones.add(new ArrayList<>(NUMBER_OF_ROWS + 1));
            for (int j = 0; j <= NUMBER_OF_ROWS; j++) {
                deadStones.get(i).add(j, false);
            }
        }

    }

    public void initializeEmptyGameBoard() {
        ArrayList<Integer> row = new ArrayList<>(NUMBER_OF_ROWS + 1);
        for (int j = 0; j <= NUMBER_OF_ROWS; j++) {
            row.add(PLACE_NOT_TAKEN);
        }
        for (int i = 0; i <= NUMBER_OF_ROWS; i++) {
            gameBoard.add(new ArrayList<>(row));
        }
    }

    public ArrayList<List<Integer>> getGameBoard() {
        return gameBoard;
    }

    public void setGameBoard(ArrayList<List<Integer>> gameBoard) {
        this.gameBoard = gameBoard;
    }

    // calculating points with flood fill algorithm
    // I assume that score is counted by the amount of empty places surrounded by given player minus number of captured
    // stones (so score can be negative)
    // I'm removing dead stones for calculating the final score
    public void calculateFinalPoints() {
        int player1Points = PLAYER1.getNumberOfCapturedStones();
        int player2Points = PLAYER2.getNumberOfCapturedStones();
        Pair<Integer, Integer> points;

        for (int i = 0; i <= NUMBER_OF_ROWS; i++) {
            for (int j = 0; j <= NUMBER_OF_ROWS; j++) {
                if (deadStones.get(i).get(j)) {
                    if (gameBoard.get(i).get(j) == PLAYER1.getPlayerNumber()) {
                        player1Points -= 1;
                    } else {
                        player2Points -= 1;
                    }
                    gameBoard.get(i).set(j, -1);
                }
                if (gameBoard.get(i).get(j) == PLACE_NOT_TAKEN) {
                    points = countTerritoryPoints(i, j);
                    if (points != null) {
                        if (points.first == PLAYER1.getPlayerNumber()) {
                            player1Points += points.second;
                        } else {
                            player2Points += points.second;
                        }
                    }
                }
            }
        }
        PLAYER1.setFinalScore(player1Points);
        PLAYER2.setFinalScore(player2Points);
    }

    // I assume that you can't commit suicide
    private boolean isSuicide(Pair<Integer, Integer> lastMove) {
        Integer opponentStone = Math.abs(CURRENT_PLAYER.getPlayerNumber() - 1);
        int counter = 0;
        int allAvailableMovesCounter = 0;

        if (!checkIfInvalidIndex(lastMove.first + 1, lastMove.second)) {
            allAvailableMovesCounter++;
            if (Objects.equals(gameBoard.get(lastMove.first + 1).get(lastMove.second), opponentStone)) {
                counter++;
            }
        }
        if (!checkIfInvalidIndex(lastMove.first - 1, lastMove.second)) {
            allAvailableMovesCounter++;
            if (Objects.equals(gameBoard.get(lastMove.first - 1).get(lastMove.second), opponentStone)) {
                counter++;
            }
        }
        if (!checkIfInvalidIndex(lastMove.first, lastMove.second + 1)) {
            allAvailableMovesCounter++;
            if (Objects.equals(gameBoard.get(lastMove.first).get(lastMove.second + 1), opponentStone)) {
                counter++;
            }
        }
        if (!checkIfInvalidIndex(lastMove.first, lastMove.second - 1)) {
            allAvailableMovesCounter++;
            if (Objects.equals(gameBoard.get(lastMove.first).get(lastMove.second - 1), opponentStone)) {
                counter++;
            }
        }

        return counter == allAvailableMovesCounter;
    }

    public void makeMove(Pair<Integer, Integer> move) {
        if (move.first >= 0 && move.second >= 0 && move.first <= NUMBER_OF_ROWS && move.second <= NUMBER_OF_ROWS) {
            if (gameBoard.get(move.first).get(move.second) == PLACE_NOT_TAKEN) {
                gameBoard.get(move.first).set(move.second, CURRENT_PLAYER.getPlayerNumber());
                if (pastMoves.size() != 0) {
                    if (isKo()) {
                        undoMove(move);
                        errorMessage.koRule();
                        return;
                    }
                }
                if (isSuperko()) {
                    undoMove(move);
                    errorMessage.superKoRule();
                    return;
                }
                if (isSuicide(move)) {
                    undoMove(move);
                    errorMessage.suicideMove();
                    return;
                }

                addBoardToPastBoards();
                deleteCapturedStones();
                changePlayers();
            } else {
                errorMessage.fieldAlreadyOccupied();
            }
        }
    }

    private void addBoardToPastBoards() {
        Long hash = computeZobristHash(gameBoard);
        pastMoves.add(hash);
    }

    private boolean checkIfInvalidIndex(float x, float y) {
        return x > NUMBER_OF_ROWS || y > NUMBER_OF_ROWS || y < 0 || x < 0;
    }

    // https://stackoverflow.com/questions/30462341/calculate-the-score-of-the-game-go
    // flood fill algorithm BFS
//    https://www.reddit.com/r/baduk/comments/13dt8t/help_writing_a_scoring_algorithm_for_a_go_game/
    private Pair<Integer, Integer> countTerritoryPoints(int x, int y) {
        LinkedList<Pair<Integer, Integer>> queue = new LinkedList<>();
        ArrayList<ArrayList<Integer>> colors = new ArrayList<>();
        int numberOfPoints = 0;
        ArrayList<Integer> c = new ArrayList<>();
        int coloredStone = -2;
        Integer color;

        queue.add(new Pair<>(x, y));

        for (int i = 0; i <= NUMBER_OF_ROWS; i++) {
            colors.add(new ArrayList<>());
            for (int j = 0; j <= NUMBER_OF_ROWS; j++) {
                colors.get(i).add(null);
            }
        }

        while (!queue.isEmpty()) {
            Pair<Integer, Integer> point = queue.pop();

            if (!checkIfInvalidIndex(point.first - 1, point.second)) {
                color = gameBoard.get(point.first - 1).get(point.second);

                if (color == PLACE_NOT_TAKEN) {
                    gameBoard.get(point.first - 1).set(point.second, coloredStone);
                    queue.add(new Pair<>(point.first - 1, point.second));
                    numberOfPoints++;
                } else if (color != coloredStone) {
                    if (!Objects.equals(colors.get(point.first - 1).get(point.second), color) &&
                            !isDeadStone(point.first - 1, point.second)) {
                        colors.get(point.first - 1).set(point.second, color);
                        c.add(color);
                    }
                }
            }

            if (!checkIfInvalidIndex(point.first + 1, point.second)) {
                color = gameBoard.get(point.first + 1).get(point.second);

                if (color == PLACE_NOT_TAKEN) {
                    gameBoard.get(point.first + 1).set(point.second, coloredStone);
                    queue.add(new Pair<>(point.first + 1, point.second));
                    numberOfPoints++;
                } else if (color != coloredStone) {
                    if (!Objects.equals(colors.get(point.first + 1).get(point.second), color) &&
                            !isDeadStone(point.first + 1, point.second)) {
                        colors.get(point.first + 1).set(point.second, color);
                        c.add(color);
                    }
                }
            }

            if (!checkIfInvalidIndex(point.first, point.second + 1)) {
                color = gameBoard.get(point.first).get(point.second + 1);

                if (color == PLACE_NOT_TAKEN) {
                    gameBoard.get(point.first).set(point.second + 1, coloredStone);
                    queue.add(new Pair<>(point.first, point.second + 1));
                    numberOfPoints++;
                } else if (color != coloredStone) {
                    if (!Objects.equals(colors.get(point.first).get(point.second + 1), color) &&
                            !isDeadStone(point.first, point.second + 1)) {
                        colors.get(point.first).set(point.second + 1, color);
                        c.add(color);
                    }
                }
            }

            if (!checkIfInvalidIndex(point.first, point.second - 1)) {
                color = gameBoard.get(point.first).get(point.second - 1);

                if (color == PLACE_NOT_TAKEN) {
                    gameBoard.get(point.first).set(point.second - 1, coloredStone);
                    queue.add(new Pair<>(point.first, point.second - 1));
                    numberOfPoints++;
                } else if (color != coloredStone) {
                    if (!Objects.equals(colors.get(point.first).get(point.second - 1), color) &&
                            !isDeadStone(point.first, point.second - 1)) {
                        colors.get(point.first).set(point.second - 1, color);
                        c.add(color);
                    }
                }
            }
        }
        if (c.size() == 0) {
            return null;
        }
        for (Integer s : c) {
            if (!s.equals(c.get(0))) {
                return null;
            }
        }

        Integer player = c.get(0);
        return new Pair<>(player, numberOfPoints);
    }

    // I'm checking only opponents of current stone are surrounded by current stone
    private void deleteCapturedStones() {
        int currentStonePlayer;
        ArrayList<Pair<Integer, Integer>> capturedStones;

        for (int i = 0; i <= NUMBER_OF_ROWS; i++) {
            for (int j = 0; j <= NUMBER_OF_ROWS; j++) {
                currentStonePlayer = gameBoard.get(i).get(j);

                if (currentStonePlayer == Math.abs(CURRENT_PLAYER.getPlayerNumber() - 1)) {
                    capturedStones = checkForCapturedStones(new Pair<>(i, j), CURRENT_PLAYER.getPlayerNumber());
                    if (capturedStones != null) {
                        for (Pair<Integer, Integer> stone : capturedStones) {
                            gameBoard.get(stone.first).set(stone.second, PLACE_NOT_TAKEN);

                            if (CURRENT_PLAYER.getPlayerNumber() == PLAYER1.getPlayerNumber()) {
                                PLAYER1.addNumberOfCapturedStones(1);
                            } else {
                                PLAYER2.addNumberOfCapturedStones(1);
                            }
                        }
                        return;
                    }
                }
            }
        }
    }

    // rule of ko means that the player can't move to the same position as before (last move stone color doesn't matter)
    // (it prevents infinite loop in a game)
    private boolean isKo() {
        Long zobristHash = computeZobristHash(gameBoard);
        return Objects.equals(zobristHash, pastMoves.get(pastMoves.size() - 1));
    }

    // rule of superko means that the board can't be in the same state as before
    private boolean isSuperko() {
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

    // I define game over as a situation when two players have passed or one player has resigned or there are no
    // available moves
    public boolean isGameOver() {
        if (PLAYER1.hasResigned() || PLAYER2.hasResigned()) {
            return true;
        }

        if (PLAYER1.isMovePassed() && PLAYER2.isMovePassed()) {
            return true;
        }

        if (pastMoves.size() != 0) {
            for (int i = 0; i <= NUMBER_OF_ROWS; i++) {
                for (int j = 0; j <= NUMBER_OF_ROWS; j++) {
                    if (areLibertiesLeft(new Pair<>(i, j))) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    private ArrayList<Pair<Integer, Integer>> checkForCapturedStones(Pair<Integer, Integer> stone, int playerColor) {
        LinkedList<Pair<Integer, Integer>> queue = new LinkedList<>();
        ArrayList<Integer> colors = new ArrayList<>();
        ArrayList<Pair<Integer, Integer>> coloredStonesIndexes = new ArrayList<>();
        Pair<Integer, Integer> neighbour;

        int opponent = Math.abs(playerColor - 1);
        int color;
        int coloredStone = -2;
        queue.add(stone);

        while (!queue.isEmpty()) {
            Pair<Integer, Integer> point = queue.pop();

            if (!checkIfInvalidIndex(point.first - 1, point.second)) {
                color = gameBoard.get(point.first - 1).get(point.second);

                if (color == opponent) {
                    gameBoard.get(point.first - 1).set(point.second, coloredStone);
                    neighbour = new Pair<>(point.first - 1, point.second);
                    queue.add(neighbour);
                    coloredStonesIndexes.add(neighbour);
                } else if (color != coloredStone) {
                    colors.add(color);
                }
            }
            if (!checkIfInvalidIndex(point.first + 1, point.second)) {
                color = gameBoard.get(point.first + 1).get(point.second);

                if (color == opponent) {
                    gameBoard.get(point.first + 1).set(point.second, coloredStone);
                    neighbour = new Pair<>(point.first + 1, point.second);
                    queue.add(neighbour);
                    coloredStonesIndexes.add(neighbour);
                } else if (color != coloredStone) {
                    colors.add(color);
                }
            }
            if (!checkIfInvalidIndex(point.first, point.second + 1)) {
                color = gameBoard.get(point.first).get(point.second + 1);

                if (color == opponent) {
                    gameBoard.get(point.first).set(point.second + 1, coloredStone);
                    neighbour = new Pair<>(point.first, point.second + 1);
                    queue.add(neighbour);
                    coloredStonesIndexes.add(neighbour);
                } else if (color != coloredStone) {
                    colors.add(color);
                }

            }
            if (!checkIfInvalidIndex(point.first, point.second - 1)) {
                color = gameBoard.get(point.first).get(point.second - 1);

                if (color == opponent) {
                    gameBoard.get(point.first).set(point.second - 1, coloredStone);
                    neighbour = new Pair<>(point.first, point.second - 1);
                    queue.add(neighbour);
                    coloredStonesIndexes.add(neighbour);
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

    // checking if there are any valid moves left
    private boolean areLibertiesLeft(Pair<Integer, Integer> stone) {
        if (!checkIfInvalidIndex(stone.first + 1, stone.second)) {
            if (gameBoard.get(stone.first + 1).get(stone.second) == PLACE_NOT_TAKEN) {
                return true;
            }
        }
        if (!checkIfInvalidIndex(stone.first - 1, stone.second)) {
            if (gameBoard.get(stone.first - 1).get(stone.second) == PLACE_NOT_TAKEN) {
                return true;
            }
        }
        if (!checkIfInvalidIndex(stone.first, stone.second + 1)) {
            if (gameBoard.get(stone.first).get(stone.second + 1) == PLACE_NOT_TAKEN) {
                return true;
            }
        }
        if (!checkIfInvalidIndex(stone.first, stone.second - 1)) {
            if (gameBoard.get(stone.first).get(stone.second - 1) == PLACE_NOT_TAKEN) {
                return true;
            }
        }
        return false;
    }

    private void undoMove(Pair<Integer, Integer> stone) {
        gameBoard.get(stone.first).set(stone.second, PLACE_NOT_TAKEN);
    }

    public void initializeZobristTable() {
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
    }

    // https://www.geeksforgeeks.org/minimax-algorithm-in-game-theory-set-5-zobrist-hashing/?ref=lbp
    private Long computeZobristHash(ArrayList<List<Integer>> board) {
        Long hash = 0L;
        int stoneNumber;

        for (int i = 0; i <= NUMBER_OF_ROWS; i++) {
            for (int j = 0; j <= NUMBER_OF_ROWS; j++) {
                stoneNumber = board.get(i).get(j);
                if (stoneNumber != PLACE_NOT_TAKEN) {
                    hash ^= zobristTable.get(i).get(j).get(stoneNumber);
                }
            }
        }
        return hash;
    }

    public void setDeadStone(int i, int j) {
        deadStones.get(i).set(j, true);
    }

    private boolean isDeadStone(int i, int j) {
        return deadStones.get(i).get(j);
    }

    public void resign() {
        if (CURRENT_PLAYER.getPlayerNumber() == PLAYER1.getPlayerNumber()) {
            PLAYER1.resign();
        } else {
            PLAYER2.resign();
        }
    }

    public void passMove() {
        if (CURRENT_PLAYER.getPlayerNumber() == PLAYER1.getPlayerNumber()) {
            PLAYER1.setPassMove(true);
        } else {
            PLAYER2.setPassMove(true);
        }
    }

    public void changePlayers() {
        // idk how to make a deepcopy in java
        if (CURRENT_PLAYER.getPlayerNumber() == PLAYER2.getPlayerNumber()) {
            setCurrentPlayerToPLayer1();
        } else {
            setCurrentPlayerToPlayer2();
        }
    }

    public void setCurrentPlayerToPLayer1() {
        int numberOfCapturedStones = PLAYER1.getNumberOfCapturedStones();
        int playerNumber = PLAYER1.getPlayerNumber();
        int finalScore = PLAYER1.getFinalScore();
        boolean hasPassedMove = PLAYER1.isMovePassed();
        boolean resigned = PLAYER1.hasResigned();
        CURRENT_PLAYER = new Player(numberOfCapturedStones, playerNumber, finalScore, hasPassedMove, resigned);
    }

    public void setCurrentPlayerToPlayer2() {
        int numberOfCapturedStones = PLAYER2.getNumberOfCapturedStones();
        int playerNumber = PLAYER2.getPlayerNumber();
        int finalScore = PLAYER2.getFinalScore();
        boolean hasPassedMove = PLAYER2.isMovePassed();
        boolean resigned = PLAYER2.hasResigned();
        CURRENT_PLAYER = new Player(numberOfCapturedStones, playerNumber, finalScore, hasPassedMove, resigned);
    }

    public void setZobristTable(ArrayList<List<List<Long>>> zobristTable) {
        this.zobristTable = zobristTable;
    }

    public void setPastMoves(ArrayList<Long> pastMoves) {
        this.pastMoves = pastMoves;
    }

    public ArrayList<List<List<Long>>> getZobristTable() {
        return zobristTable;
    }

    public ArrayList<Long> getPastMoves() {
        return pastMoves;
    }

    public void clearPastMoves(){
        pastMoves.clear();
    }
}