package renderer.core;


import java.awt.AWTException;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Fps Mouse Capturer.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class FpsMouseCapturer extends MouseAdapter implements KeyListener {

    private Robot robot;
    private boolean captureMouse = false;
    
    private Component component;
    private Point2D componentHalfSize = new Point2D.Double();
    
    private final Point2D sensibility = new Point2D.Double(0.0075, 0.0075);
    private final Point2D value = new Point2D.Double();
    private final Point2D minValue = new Point2D.Double(-1, -0.20);
    private final Point2D maxValue = new Point2D.Double(1, 1);
    
    private boolean limitX = false;
    private boolean limitY = true;
    
    public FpsMouseCapturer() {
        try {
            robot = new Robot();
        } catch (AWTException ex) {
            Logger.getLogger(FpsMouseCapturer.class.getName())
                .log(Level.SEVERE, null, ex);
            
            System.exit(1);
        }
    }
    
    public Point2D getSensibility() {
        return sensibility;
    }

    public Point2D getMaxValue() {
        return maxValue;
    }

    public Point2D getMinValue() {
        return minValue;
    }

    public Point2D getValue() {
        return value;
    }

    public boolean isCaptureMouse() {
        return captureMouse;
    }

    public boolean isLimitX() {
        return limitX;
    }

    public void setLimitX(boolean limitX) {
        this.limitX = limitX;
    }

    public boolean isLimitY() {
        return limitY;
    }

    public void setLimitY(boolean limitY) {
        this.limitY = limitY;
    }
    
    public void install(Component component) {
        this.component = component;
        componentHalfSize.setLocation(
            component.getWidth() / 2, component.getHeight() / 2);
        
        component.addKeyListener(this);
        component.addMouseListener(this);
    }
    
    public void update() {
        if (captureMouse) {
            Point globalMouse = MouseInfo.getPointerInfo().getLocation();

            double dx = (component.getLocationOnScreen().x 
                    + componentHalfSize.getX()) - globalMouse.x;
            
            double dy = (component.getLocationOnScreen().y 
                    + componentHalfSize.getY()) - globalMouse.y;

            double lx = value.getX();
            double ly = value.getY();
            
            double vx = lx - dx * sensibility.getX();
            double vy = ly + dy * sensibility.getY();
            
            if (limitX) {
                vx = clamp(vx, minValue.getX(), maxValue.getX());
            }
            if (limitY) {
                vy = clamp(vy, minValue.getY(), maxValue.getY());
            }
            
            value.setLocation(vx, vy);

            robot.mouseMove(
              (int)(component.getLocationOnScreen().x 
                    + componentHalfSize.getX()),
              (int)(component.getLocationOnScreen().y 
                    + componentHalfSize.getY())
            );
        }
    }
    
    private double clamp(double value, double min, double max) {
        return Math.min(Math.max(value, min), max);
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            captureMouse = false;
            showMouseCursor();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // do nothing
    }

    @Override
    public void mousePressed(MouseEvent e) {
        captureMouse = true;
        hideMouseCursor();
    }

    private void hideMouseCursor() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        component.setCursor(toolkit.createCustomCursor(
                    new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB)
                    , new Point(0, 0), "null"));        
    }

    private void showMouseCursor() {
        component.setCursor(Cursor.getDefaultCursor());
    }
    
}
