package UI;

import Files.Files;
import GameEngine.GameEngine;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;

import static UI.Constants.*;

public class Window {
    public Window() throws IOException {
        JFrame frame = new JFrame("Go");
        GameEngine game = new GameEngine();

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
//                    game.saveGameToFile();
                    Files files = new Files(game.getGameBoard());
                    files.saveGameToFile();
//                    game.saveGameToFile();
                    System.exit(0);
                }
            }
        });
    }
}


