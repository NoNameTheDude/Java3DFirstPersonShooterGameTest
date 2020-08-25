package main;

import javax.swing.SwingUtilities;

/**
 *
 * @author admin
 */
public class Main {
    
    public static void main(String args[]) {
        
        SwingUtilities.invokeLater(() -> {
            new View().setVisible(true);
        });
    }
    
}
