package UI;

import GameEngine.GameEngine;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static GeneralConstants.GeneralConstants.*;
import static UI.Constants.*;

public class UI {
    public record PointInfo(int x, int y, int player) {
    }

    public static class Window {
        public Window() {
            JFrame frame = new JFrame("Go");

            frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new Board());
            frame.setVisible(true);
        }

    }

    public static class Board extends JPanel {
        private final ArrayList<PointInfo> points;
        public GameEngine game;
        boolean firstMove;

        Board() {
            points = new ArrayList<>();
            firstMove = true;
            game = new GameEngine();
            this.setBackground(Color.black);
            setBorder(new EmptyBorder(WINDOW_PADDING, WINDOW_PADDING, WINDOW_PADDING, WINDOW_PADDING));
            JPanel containerPanel = new JPanel(new BorderLayout());
            JPanel boxPanel = new JPanel();
            boxPanel.setPreferredSize(new Dimension(WINDOW_WIDTH - WINDOW_PADDING * 3,
                    WINDOW_WIDTH - WINDOW_PADDING * 3));

            boxPanel.setBackground(Color.BLACK);
//            boxPanel.setBorder(new LineBorder(Color.red, 2));

            JLabel playerInfo = getPlayerInfo();

            boxPanel.add(playerInfo);

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
//                    repaint();
                }
            });
//            showGameOverPopUp();
            resignButton.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {

//                    repaint();
                }
            });

            buttonContainer.add(passButton, BorderLayout.WEST);
            buttonContainer.add(resignButton, BorderLayout.EAST);

            containerPanel.add(boxPanel, BorderLayout.CENTER);
            containerPanel.add(buttonContainer, BorderLayout.SOUTH);
            add(containerPanel);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    points.add(new PointInfo(e.getX(), e.getY(), CURRENT_PLAYER.getPlayerNumber()));
                    repaint();
                }
            });
        }

        @Override public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setColor(Color.darkGray);


            int x, y;
            boolean popLastElement = false;
            ArrayList<Integer> closestPoints;
            boolean repaintBoard = false;

            for (PointInfo point : points) {
                closestPoints = findClosestPoint(point.x, point.y);
                x = closestPoints.get(0);
                y = closestPoints.get(1);

                if (x >= 0 && y >= 0 && x <= NUMBER_OF_ROWS && y <= NUMBER_OF_ROWS
                        && game.getGameBoard().get(x).get(y) == -1) {
                    game.makeMove(x, y, CURRENT_PLAYER.getPlayerNumber());

                    if (!game.isInvalidMove()) {
                        if (CURRENT_PLAYER == HUMAN_PLAYER) {
                            CURRENT_PLAYER = COMPUTER;
                        } else {
                            CURRENT_PLAYER = HUMAN_PLAYER;
                        }
                    } else {
                        popLastElement = true;
                        break;
                    }
                    if (game.isRepaintBoard()) {
                        repaintBoard = true;
                        break;
                    } else {
                        try {
                            drawSingleStone(g2, x, y, point.player);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    popLastElement = true;
                }
            }
            if (popLastElement) {
                PointInfo pointToRemove = points.get(points.size() - 1);
                points.remove(pointToRemove);
            }
            if (repaintBoard) {
                points.clear();
            }
            drawGrid(g2, g);
            try {
                drawAllStonesOnBoard(g2);
            } catch (IOException e) {
                e.printStackTrace();
            }
            drawPastScoresIcon(g2);
            game.setInvalidMove(false);


//            FOR TESTING PURPOSES!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//            int x = 0;
//            for (List<Integer> row : getGameBoard()) {
//                int j = 0;
//                for (Integer number : row) {
//                    if (number == 1) {
//                        g2.setColor(Color.darkGray);
//                        g2.fillOval(j * SIZE_OF_CELL + GRID_SHIFT_X - STONE_DIAMETER / 2,
//                                x * SIZE_OF_CELL + GRID_SHIFT_X - STONE_DIAMETER / 2,
//                                STONE_DIAMETER, STONE_DIAMETER);
//                    } else if (number == 0) {
//                        g2.setColor(Color.lightGray);
//                        g2.fillOval(j * SIZE_OF_CELL + GRID_SHIFT_X - STONE_DIAMETER / 2,
//                                x * SIZE_OF_CELL + GRID_SHIFT_X - STONE_DIAMETER / 2,
//                                STONE_DIAMETER, STONE_DIAMETER);
//                    } else if (number == 4) {
//                        g2.setColor(Color.red);
//                        g2.fillOval(j * SIZE_OF_CELL + GRID_SHIFT_X - STONE_DIAMETER / 2,
//                                x * SIZE_OF_CELL + GRID_SHIFT_X - STONE_DIAMETER / 2,
//                                STONE_DIAMETER, STONE_DIAMETER);
//                    }
//                    j++;
//                }
//                x++;
//            }
//            f.addWindowListener(new WindowAdapter() {
//
//            @Override
//            public void windowClosing(WindowEvent e) {
//                System.exit(0);
//            }
//        });
        }

        private ArrayList<Integer> findClosestPoint(int x, int y) {
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

            return new ArrayList<>(Arrays.asList(closestColumn, closestRow));
        }

        private void drawGrid(Graphics2D g2, Graphics g) {
            super.paint(g);

            g2.setColor(BOARD_COLOR);
            RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(BOARD_SHIFT_X, BOARD_SHIFT_Y,
                    BOARD_SIZE, BOARD_SIZE, 10, 10);
            g2.fill(roundedRectangle);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(3));

            for (int i = GRID_SHIFT_X; i < GRID_SHIFT_X + SIZE_OF_CELL * NUMBER_OF_ROWS; i += SIZE_OF_CELL) {
                for (int j = GRID_SHIFT_X; j < GRID_SHIFT_X + SIZE_OF_CELL * NUMBER_OF_ROWS; j += SIZE_OF_CELL) {
                    g2.drawRect(i, j, SIZE_OF_CELL, SIZE_OF_CELL);
                }
            }
        }

        private void drawAllStonesOnBoard(Graphics2D g2) throws IOException {
            int stoneColor;

            for (int i = 0; i <= NUMBER_OF_ROWS; i++) {
                for (int j = 0; j <= NUMBER_OF_ROWS; j++) {
                    stoneColor = game.getGameBoard().get(i).get(j);
                    if (stoneColor != -1) {
                        drawSingleStone(g2, i, j, stoneColor);
                    }
                }
            }
        }

        private void drawSingleStone(Graphics2D g2, int x, int y, int stoneColor) throws IOException {
            if (stoneColor == COMPUTER.getPlayerNumber()) {
                BufferedImage whiteStonePicture = ImageIO.read(new File("imgs/white.png"));
                g2.drawImage(whiteStonePicture, x * SIZE_OF_CELL + GRID_SHIFT_X - STONE_DIAMETER / 2,
                        y * SIZE_OF_CELL + GRID_SHIFT_X - STONE_DIAMETER / 2,
                        STONE_DIAMETER, STONE_DIAMETER, null);
            } else {
                BufferedImage blackStonePicture = ImageIO.read(new File("imgs/black.png"));
                g2.drawImage(blackStonePicture, x * SIZE_OF_CELL + GRID_SHIFT_X - STONE_DIAMETER / 2,
                        y * SIZE_OF_CELL + GRID_SHIFT_X - STONE_DIAMETER / 2,
                        STONE_DIAMETER, STONE_DIAMETER, null);
            }
        }

        private JLabel getPlayerInfo() {
            JLabel playerInfo = new JLabel("Player's 1 move (black)");
            playerInfo.setFont(new Font(FONT_STYLE, Font.BOLD, BIG_FONT_SIZE));
            playerInfo.setForeground(Color.LIGHT_GRAY);
            playerInfo.setBackground(Color.BLACK);
            return playerInfo;
        }

        private void showGameOverPopUp() {
            String message = "Game Over! Player 1 score: " + game.getPlayerOneScore() +
                    "Player 2 score:" + game.getPlayerTwoScore();
            JOptionPane.showMessageDialog(this, message);
        }

        private void drawPastScoresIcon(Graphics2D g2) {
            try {
                JLabel jLabel = new JLabel();
                BufferedImage pastScores = ImageIO.read(new File("imgs/pastScores1.png"));
                int imageWidth = pastScores.getWidth();
                jLabel.setLayout(null);
                jLabel.setToolTipText("Past game scores");
                jLabel.setBounds(WINDOW_WIDTH - WINDOW_PADDING - imageWidth * 4, WINDOW_PADDING / 2,
                        PAST_SCORES_ICON_SIZE, PAST_SCORES_ICON_SIZE);
                g2.drawImage(pastScores, WINDOW_WIDTH - WINDOW_PADDING - imageWidth * 4, WINDOW_PADDING / 2,
                        PAST_SCORES_ICON_SIZE, PAST_SCORES_ICON_SIZE, null);
                jLabel.addMouseListener(new MouseAdapter() {
                    public void mousePressed(MouseEvent e) {
                        game.showPastScoresWindow();
                    }
                });

                add(jLabel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static class Button extends JButton {
        Button(String name) {
            setBorderPainted(false);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBackground(Color.LIGHT_GRAY);
            setOpaque(true);
            setForeground(Color.BLACK);
            setFont(new Font(FONT_STYLE, Font.BOLD, MEDIUM_FONT_SIZE));
            setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
            setText(name);
        }
    }
}