package WG58.app;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FoodCourtGUI().setVisible(true));
    }
}   
