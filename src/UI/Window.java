package UI;

import GameEngine.GameEngine;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ColorUIResource;
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

//public class UI {
    public record PointInfo(int x, int y, int player) {
    }

    public  class Window {
        public Window() {
            JFrame frame = new JFrame("Go");
            GameEngine game = new GameEngine();

            frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new Board(game));
            frame.setVisible(true);
            frame.setResizable(false);
//            frame.setBackground(Color.BLACK);

            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    game.saveGameToFile();
                }
            });
        }
    }

    public  class Board extends JPanel {
        private PointInfo point;
        private final GameEngine game;

        Board(GameEngine game) {
            this.game = game;
            drawButtons();
            setBackground(Color.BLACK);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    point = new PointInfo(e.getX(), e.getY(), CURRENT_PLAYER.getPlayerNumber());
                    revalidate();
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
                int x, y;
                ArrayList<Integer> closestPoints;
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
                    }
                }
            }
//            setForeground(Color.black);
//            setBackground(Color.black);

//            g.setColor(Color.BLACK);
//            g.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
//            getViewport().setBackground(new Color(36, 36, 36));
            drawGrid(g2, g);
            drawButtons();

            try {
                drawAllStonesOnBoard(g2);
            } catch (IOException e) {
                e.printStackTrace();
            }
            game.setInvalidMove(false);
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
            if (stoneColor == 0) {
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

        private JLabel getCurrentPlayer() {
            JLabel currentPlayer;

            if (CURRENT_PLAYER.getPlayerNumber() == 1) {
                currentPlayer = new JLabel("Player's 1 move (black)");
//                System.out.println("1");
            } else {
                currentPlayer = new JLabel("Player's 2 move (white)");
//                System.out.println("hdhjkasbhdjsak");
            }
            currentPlayer.setFont(new Font(FONT_STYLE, Font.BOLD, BIG_FONT_SIZE));
            currentPlayer.setForeground(Color.LIGHT_GRAY);
//            currentPlayer.setBackground(Color.BLACK);
//            revalidate();
//            currentPlayer.setVisible(true);
//            super.update(this.getGraphics());
//            currentPlayer.paintImmediately(currentPlayer.getVisibleRect());
            return currentPlayer;
        }

        private void showGameOverPopUp() {
            String message = "Game Over! Player 1 score: " + game.getPlayerOneScore() +
                    "Player 2 score:" + game.getPlayerTwoScore();
            JOptionPane.showMessageDialog(this, message);
        }

        private void drawButtons() {
//            super.paint(g);
            setBorder(new EmptyBorder(WINDOW_PADDING, WINDOW_PADDING, WINDOW_PADDING, WINDOW_PADDING));
            JPanel containerPanel = new JPanel(new BorderLayout());
            JPanel currentPlayerContainer = new JPanel();
//            currentPlayerContainer.setBackground(Color.black);
//            currentPlayerContainer.setForeground(Color.black);
//            containerPanel.setForeground(Color.black);
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
                }
            });
            resignButton.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    showGameOverPopUp();
                }
            });

            buttonContainer.add(passButton, BorderLayout.WEST);
            buttonContainer.add(resignButton, BorderLayout.EAST);

            containerPanel.add(currentPlayerContainer, BorderLayout.CENTER);
            containerPanel.add(buttonContainer, BorderLayout.SOUTH);
            add(containerPanel);

        }
    }


//}