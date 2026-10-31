package src;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Main Class
 * Entry point for the Employee Management System application.
 */
public class Main {

    public static void main(String[] args) {
        // Set modern platform look and feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Fallback to default look and feel if Nimbus isn't available
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
        }

        // Launch the GUI on Event Dispatch Thread (EDT) for thread safety
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginGUI loginWindow = new LoginGUI();
                loginWindow.setVisible(true);
            }
        });
    }
}
