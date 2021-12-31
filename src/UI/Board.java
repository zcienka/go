package UI;


import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import ErrorMessage.ErrorMessage;
import Files.Files;
import Game.Game;
import Pair.Pair;

import static GeneralConstants.GeneralConstants.*;
import static UI.Constants.*;

public class Board extends JPanel {
    private PointInfo point;
    private final Game game;
    private BufferedImage whiteStonePicture;
    private BufferedImage blackStonePicture;

    public Board(Game game) {
        this.game = game;
        setBackground(Color.BLACK);
        drawButtons();
        ErrorMessage errorMessage = new ErrorMessage();

        try {
            whiteStonePicture = ImageIO.read(new File("imgs/white.png"));
            blackStonePicture = ImageIO.read(new File("imgs/black.png"));
        } catch (IOException e) {
            errorMessage.missingPicture();
            System.exit(0);
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                point = new PointInfo(e.getX(), e.getY(), CURRENT_PLAYER.getPlayerNumber());
                repaint();
            }
        });
    }

    @Override public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setColor(Color.darkGray);

        if (point != null) {
            Pair<Integer, Integer> closestPoints = findClosestPoint(point.x, point.y);
            game.makeMove(closestPoints);
        }

        // I couldn't find a way to update text in java with JLabel, so I decided to display current move by picture
        drawGrid(g2, g);

        if (CURRENT_PLAYER.getPlayerNumber() == 0) {
            g2.drawImage(whiteStonePicture, WINDOW_PADDING + WINDOW_WIDTH / 2 + 80, WINDOW_PADDING + 14, 40, 40, null);
        } else {
            g2.drawImage(blackStonePicture, WINDOW_PADDING + WINDOW_WIDTH / 2 + 80, WINDOW_PADDING + 14, 40, 40, null);
        }

        try {
            drawAllStonesOnBoard(g2);
        } catch (IOException e) {
            e.printStackTrace();
        }

        checkIfGameOver();
        if (CURRENT_PLAYER.getPlayerNumber() == PLAYER1.getPlayerNumber()) {
            PLAYER1.setPassMove(false);
        } else {
            PLAYER2.setPassMove(false);
        }
        point = null;
    }

    private void checkIfGameOver() {
        if (game.isGameOver()) {
            insertDeadStoneCoordinates();
            game.calculateFinalPoints();
            showGameOverPopUp();
            Files files = new Files();
            files.deleteBoardFiles();
            System.exit(0);
        }
    }

    private Pair<Integer, Integer> findClosestPoint(int x, int y) {
        float pointX = (float) x - GRID_SHIFT_X;
        float pointY = (float) y - GRID_SHIFT_X;

        int closestColumn;
        int closestRow;
        if (Math.abs(Math.ceil(pointX / SIZE_OF_CELL) - pointX / SIZE_OF_CELL) <
                Math.abs(Math.floor(pointX / SIZE_OF_CELL) - pointX / SIZE_OF_CELL)) {
            closestColumn = (int) Math.ceil(pointX / SIZE_OF_CELL);
        } else {
            closestColumn = (int) Math.floor(pointX / SIZE_OF_CELL);
        }

        if (Math.abs(Math.ceil(pointY / SIZE_OF_CELL) - pointY / SIZE_OF_CELL) <
                Math.abs(Math.floor(pointY / SIZE_OF_CELL) - pointY / SIZE_OF_CELL)) {
            closestRow = (int) Math.ceil(pointY / SIZE_OF_CELL);
        } else {
            closestRow = (int) Math.floor(pointY / SIZE_OF_CELL);
        }

        return new Pair<>(closestColumn, closestRow);
    }

    private void drawGrid(Graphics2D g2, Graphics g) {
        super.paint(g);
        g2.setColor(BOARD_COLOR);
        RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(BOARD_SHIFT_X, BOARD_SHIFT_Y,
                BOARD_SIZE, BOARD_SIZE, 16, 16);
        g2.fill(roundedRectangle);
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(3));

        for (int i = GRID_SHIFT_X; i < GRID_SHIFT_X + SIZE_OF_CELL * NUMBER_OF_ROWS; i += SIZE_OF_CELL) {
            for (int j = GRID_SHIFT_X; j < GRID_SHIFT_X + SIZE_OF_CELL * NUMBER_OF_ROWS; j += SIZE_OF_CELL) {
                g2.drawRect(i, j, SIZE_OF_CELL, SIZE_OF_CELL);
            }
        }
        drawLabelCoordinates(g2);
    }

    // There's always no "I" in label coordinates in Go
    private void drawLabelCoordinates(Graphics2D g2) {
        ArrayList<String> letters = new ArrayList<>(Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "J", "K",
                "L", "M", "N", "O", "P", "Q", "R", "S", "T"));
        g2.setFont(new Font(FONT_STYLE, Font.BOLD, SMALL_FONT_SIZE));
        String number;
        int counter = 0;

        for (int i = GRID_SHIFT_X; i <= GRID_SHIFT_X + SIZE_OF_CELL * NUMBER_OF_ROWS; i += SIZE_OF_CELL) {
            g2.drawString(letters.get(counter), i, GRID_SHIFT_X - BOARD_LABEL_SHIFT);
            g2.drawString(letters.get(counter), i,
                    GRID_SHIFT_X + BOARD_LABEL_SHIFT + NUMBER_OF_ROWS * SIZE_OF_CELL + 6);
            counter++;
        }
        counter = NUMBER_OF_ROWS + 1;
        for (int j = GRID_SHIFT_X; j <= GRID_SHIFT_X + SIZE_OF_CELL * NUMBER_OF_ROWS; j += SIZE_OF_CELL) {
            number = Integer.toString(counter);
            if (counter <= 9) {
                g2.drawString(number, GRID_SHIFT_X - BOARD_LABEL_SHIFT, j);
                g2.drawString(number, GRID_SHIFT_X + BOARD_LABEL_SHIFT + NUMBER_OF_ROWS * SIZE_OF_CELL - 6, j);
            } else {
                g2.drawString(number, GRID_SHIFT_X - BOARD_LABEL_SHIFT - 4, j);
                g2.drawString(number, GRID_SHIFT_X + BOARD_LABEL_SHIFT + NUMBER_OF_ROWS * SIZE_OF_CELL - 10, j);
            }
            counter--;
        }
        addOrientationPoints(g2);
    }

    // draws small circle orientation points
    private void addOrientationPoints(Graphics2D g2) {
        if (NUMBER_OF_ROWS == 18) {
            int l = 0, k;
            for (int i = GRID_SHIFT_X; i <= GRID_SHIFT_X + SIZE_OF_CELL * NUMBER_OF_ROWS; i += SIZE_OF_CELL) {
                k = 0;
                for (int j = GRID_SHIFT_X; j <= GRID_SHIFT_X + SIZE_OF_CELL * NUMBER_OF_ROWS; j += SIZE_OF_CELL) {
                    if ((k == 9 || k == 3 || k == 15) && (l == 9 || l == 3 || l == 15)) {
                        g2.fillOval(i - ORIENTATION_POINTS_RADIUS / 2, j - ORIENTATION_POINTS_RADIUS / 2,
                                ORIENTATION_POINTS_RADIUS, ORIENTATION_POINTS_RADIUS);
                    }
                    k++;
                }
                l++;
            }
        }
    }

    private void drawAllStonesOnBoard(Graphics2D g2) throws IOException {
        int stoneColor;

        for (int i = 0; i <= NUMBER_OF_ROWS; i++) {
            for (int j = 0; j <= NUMBER_OF_ROWS; j++) {
                stoneColor = game.getGameBoard().get(i).get(j);
                if (stoneColor != PLACE_NOT_TAKEN) {
                    drawSingleStone(g2, i, j, stoneColor);
                }
            }
        }
    }

    private void drawSingleStone(Graphics2D g2, int x, int y, int stoneColor) {
        if (stoneColor == 0) {
            g2.drawImage(whiteStonePicture, x * SIZE_OF_CELL + GRID_SHIFT_X - STONE_DIAMETER / 2,
                    y * SIZE_OF_CELL + GRID_SHIFT_X - STONE_DIAMETER / 2,
                    STONE_DIAMETER, STONE_DIAMETER, null);
        } else {
            g2.drawImage(blackStonePicture, x * SIZE_OF_CELL + GRID_SHIFT_X - STONE_DIAMETER / 2,
                    y * SIZE_OF_CELL + GRID_SHIFT_X - STONE_DIAMETER / 2,
                    STONE_DIAMETER, STONE_DIAMETER, null);
        }
    }

    private JLabel getCurrentPlayer() {
        JLabel currentPlayer = new JLabel("Current move: ");
        currentPlayer.setFont(new Font(FONT_STYLE, Font.BOLD, BIG_FONT_SIZE));
        currentPlayer.setForeground(Color.LIGHT_GRAY);
        return currentPlayer;
    }

    private void showGameOverPopUp() {
        String message = "Game Over!\nPlayer 1 (black stone) score: " + PLAYER1.getFinalScore() + "\n" +
                "Player 2 (white stone) score: " + PLAYER2.getFinalScore();
        JOptionPane.showMessageDialog(this, message);
    }

    private void drawButtons() {
        setBorder(new EmptyBorder(WINDOW_PADDING, WINDOW_PADDING, WINDOW_PADDING, WINDOW_PADDING));
        JPanel containerPanel = new JPanel(new BorderLayout());
        JPanel currentPlayerContainer = new JPanel();
        currentPlayerContainer.setPreferredSize(new Dimension(WINDOW_WIDTH - WINDOW_PADDING * 3,
                WINDOW_WIDTH - WINDOW_PADDING * 3));

        currentPlayerContainer.setBackground(Color.BLACK);

        JLabel currentPlayer = getCurrentPlayer();

        currentPlayerContainer.add(currentPlayer);

        JPanel buttonContainer = new JPanel(new BorderLayout());
        buttonContainer.setBackground(Color.BLACK);
        buttonContainer.setPreferredSize(new Dimension(WINDOW_WIDTH - 72 - 32, 50));

        JButton passButton = new Button("Pass");
        JButton resignButton = new Button("Resign");


        buttonContainer.setBorder(BorderFactory.createEmptyBorder(0, BOARD_SIZE / 2 - BUTTON_WIDTH,
                0, BOARD_SIZE / 2 - BUTTON_WIDTH));

        passButton.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                game.passMove();
                checkIfGameOver();
                game.changePlayers();
                point = null;
                repaint();
            }
        });
        resignButton.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                game.resign();
                checkIfGameOver();
            }
        });

        buttonContainer.add(passButton, BorderLayout.WEST);
        buttonContainer.add(resignButton, BorderLayout.EAST);

        containerPanel.add(currentPlayerContainer, BorderLayout.CENTER);
        containerPanel.add(buttonContainer, BorderLayout.SOUTH);
        add(containerPanel);

    }

    private void insertDeadStoneCoordinates() {
        ArrayList<String> letters = new ArrayList<>(Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "J", "K",
                "L", "M", "N", "O", "P", "Q", "R", "S", "T"));
        ArrayList<String> numbers = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                "11", "12", "13", "14", "15", "16", "17", "18", "19"));

        String message = "Insert dead stones with comma and space (e.g A1, B2)";
        String deadStones = JOptionPane.showInputDialog(this, message);
        String[] deadStonesList;
        ArrayList<ArrayList<Integer>> stoneIndexes;

        if (deadStones != null) {
            deadStonesList = deadStones.split(", ");
            stoneIndexes = checkIfInputIsCorrect(deadStonesList, letters, numbers);

            while (stoneIndexes == null) {
                deadStones = JOptionPane.showInputDialog(this, message);
                if (deadStones != null) {
                    deadStonesList = deadStones.split(", ");
                    stoneIndexes = checkIfInputIsCorrect(deadStonesList, letters, numbers);
                }
            }

            for (ArrayList<Integer> indexes : stoneIndexes) {
                game.setDeadStone(indexes.get(0), indexes.get(1));
            }
        }
    }

    private ArrayList<ArrayList<Integer>> checkIfInputIsCorrect(String[] deadStonesList, ArrayList<String> letters,
            ArrayList<String> numbers) {
        Integer i, j;
        ArrayList<ArrayList<Integer>> stones = new ArrayList<>();

        for (String stone : deadStonesList) {
            if (stone.length() != 2 && stone.length() != 3) {
                return null;
            }

            i = getStoneIndex(Character.toString(stone.charAt(0)), letters);
            if (i != null) {
                if (stone.length() == 2) {
                    j = getStoneIndex(Character.toString(stone.charAt(1)), numbers);
                } else {
                    String s = "" + stone.charAt(1) + stone.charAt(2);
                    j = getStoneIndex(s, numbers);
                }
                if (j != null) {
                    stones.add(new ArrayList<>(Arrays.asList(i, NUMBER_OF_ROWS - j)));
                }
            }
        }

        if (stones.size() == 0) {
            return null;
        } else {
            return stones;
        }
    }

    private Integer getStoneIndex(String letter, ArrayList<String> list) {
        int i = 0;

        for (String l : list) {
            if (Objects.equals(l, letter)) {
                return i;
            }
            if (i > NUMBER_OF_ROWS) {
                return null;
            }
            i++;
        }
        return null;
    }
}
