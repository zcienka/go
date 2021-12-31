package ErrorMessage;

import javax.swing.*;

public class ErrorMessage {
    public void koRule() {
        String title = "Invalid move";
        String message = "Chosen field is the same as the last move. Ko rule.";
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.WARNING_MESSAGE);
    }

    public void fieldAlreadyOccupied() {
        String title = "Invalid move";
        String message = "Chosen field is already occupied.";
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.WARNING_MESSAGE);
    }

    public void superKoRule() {
        String title = "Invalid move";
        String message = "Chosen field is invalid because of the superko rule.";
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.WARNING_MESSAGE);
    }

    public void suicideMove() {
        String title = "Invalid move";
        String message = "Chosen field is a suicide.";
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.WARNING_MESSAGE);
    }

    public void brokenFile() {
        String title = "File error";
        String message = "File is broken. Initializing new board.";
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.WARNING_MESSAGE);
    }

    public void missingFile() {
        String title = "File error";
        String message = "File is missing. Initializing new board.";
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.WARNING_MESSAGE);
    }

    public void missingPicture() {
        String title = "File error";
        String message = "File is missing. Quitting the program.";
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.WARNING_MESSAGE);
    }
}
