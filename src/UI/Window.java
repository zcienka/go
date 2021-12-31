package UI;

import Files.Files;
import Game.Game;

import javax.swing.*;
import java.awt.event.*;

import static UI.Constants.*;

public class Window {
    public Window() {
        JFrame frame = new JFrame("Go");
        Game game = new Game();

        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.add(new Board(game));
        frame.setVisible(true);
        frame.setResizable(false);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                String title = "Confirm exit";
                String message = "Are you sure you want to exit?\nYour game will be saved to file.";
                int closeWindow = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION);

                if (closeWindow == 0) {
                    Files files = new Files();
                    files.saveGameToFile(game);
                    files.saveZobristBoardToFile(game.getZobristTable());
                    files.savePastMovesToFile(game.getPastMoves());
                    System.exit(0);
                }
            }
        });
    }
}


