package input;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 *
 * @author leonardo
 */
public class Mouse extends MouseAdapter {
 
    public static double x;
    public static double y;
    
    public static List<Runnable> pressedListeners = new ArrayList<>();
    
    public static void addPressedListener(Runnable listener) {
        pressedListeners.add(listener);
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            pressedListeners.forEach((listener) -> {
                listener.run();
            });
        }
    }
    
    
    
}
