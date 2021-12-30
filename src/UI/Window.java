package UI;

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


