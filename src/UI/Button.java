package UI;

import javax.swing.*;
import java.awt.*;

import static UI.Constants.*;
import static UI.Constants.BUTTON_HEIGHT;

public class Button extends JButton {
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
