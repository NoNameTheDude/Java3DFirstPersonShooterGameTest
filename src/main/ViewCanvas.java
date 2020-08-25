package main;

import audio.Music;
import audio.SoundManager;
import input.Mouse;
import input.Keyboard;
import entity.Player;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import physics.Input;
import physics.World;
import renderer.core.FpsMouseCapturer;
import renderer.core.Light;
import renderer.core.Renderer;
import renderer.core.Shader;
import renderer.core.Time;
import static renderer.core.Renderer.MatrixMode.*;
import renderer.math.Vec2;
import renderer.math.Vec4;
import renderer.parser.wavefront.Obj;
import renderer.parser.wavefront.WavefrontParser;
import renderer.shader.GouraudShaderWithTexture;
import renderer.shader.GouraudShaderWithTexture2;

/**
 *
 * @author leo
 */
public class ViewCanvas extends Canvas {
    
    private boolean running = false;
    private BufferStrategy bs;

    private Renderer renderer;
    private Thread thread;
    
    private final Shader gouraudShader = new GouraudShaderWithTexture();
    private final Shader gouraudShader2 = new GouraudShaderWithTexture2();
    
    private final Light light = new Light();

    private List<Obj> objs;
    private List<Obj> objsPlayer;
    
    // physics
    private World world;
    
    private Player player;

    private FpsMouseCapturer fpsMouseCapturer;
    
    private JFrame parentFrame;
    
    public ViewCanvas() {
        addKeyListener(new Input());
    }
    
    public void init() {
        parentFrame = ((JFrame) getParent().getParent().getParent().getParent());
        fpsMouseCapturer = new FpsMouseCapturer();
        fpsMouseCapturer.install(this);
        
        world = new World(fpsMouseCapturer);
        
        createBufferStrategy(1);
        bs = getBufferStrategy();
        
        try {
            objs = new ArrayList<>(WavefrontParser.load("/res/cs.obj", 100));
            objsPlayer = new ArrayList<>(WavefrontParser.load("/res/sphere.obj", 10));
        } catch (Exception ex) {
            Logger.getLogger(ViewCanvas.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
        
        player = new Player(world.getPlayer(), objsPlayer);
        
        renderer = new Renderer(440, 330);
        
        // light
        light.diffuse.set(1, 1, 1, 1);
        renderer.addLight(light);

        renderer.setMatrixMode(PROJECTION);
        renderer.setPerspectiveProjection(Math.toRadians(60));
        
        renderer.setClipZNear(-0.01);
        
        running = true;
        thread = new Thread(new MainLoop());
        thread.start();
        
        Mouse mouseHandler2 = new Mouse();
        addMouseListener(mouseHandler2);
        
        // https://www.partnersinrhyme.com/midi/Video_Games/Video_Games_Midi_Files/doom7e1.shtml
        Music.initialize();
        Music.play("doom7e1.mid");
    }
    
    private double cameraTargetAngleX = 0;
    private double cameraTargetAngleY = Math.toRadians(30);

    private double cameraAngleX = 0;
    private double cameraAngleY = Math.toRadians(30);
    private final Vec4 cameraPosition = new Vec4(100, 150, 0, 50);
    
    
    public void update() {
        Time.update();
        Time.updateFPS2();
        
        fpsMouseCapturer.update();
        
        if (fpsMouseCapturer.isCaptureMouse()) {
            parentFrame.setTitle("Press ESC to release mouse capture.");
        }
        else {
            parentFrame.setTitle("Click anywhere inside window to capture mouse.");
        }
        
        while (Time.needsFixedUpdate()) {
            world.update();
            player.update();
        }

        // first person camera
        cameraTargetAngleX = fpsMouseCapturer.getValue().getY() * 200;
        cameraTargetAngleY = world.getPlayer().getAngle() + Math.toRadians(90);

        cameraAngleX += (Math.toRadians(cameraTargetAngleX * 0.5) - cameraAngleX) * 0.1;
        cameraAngleY += (cameraTargetAngleY - cameraAngleY) * 0.5;

        cameraPosition.x = world.getPlayer().getCollider().getPosition().x + 0 * Math.cos(world.getPlayer().getAngle() + Math.toRadians(90));
        cameraPosition.y = world.getPlayer().getCollider().getPosition().y + 8 + 60;
        cameraPosition.z = world.getPlayer().getCollider().getPosition().z + 0 * Math.sin(world.getPlayer().getAngle() + Math.toRadians(90));
        
        //light.position.translate(lightX, 100, lightZ);
        renderer.setBackfaceCullingEnabled(true);
        
        renderer.clearAllBuffers(); 
        //renderer.getDepthBuffer().clear();
        
        drawLevel();
        drawPlayer();
    }

    private void drawLevel() {
        renderer.setShader(gouraudShader2);
        
        renderer.setMatrixMode(VIEW);
        renderer.setIdentity();
        renderer.rotateX(cameraAngleX + Math.PI * 2);
        renderer.rotateY(-cameraAngleY + Math.PI * 0.5);
        renderer.translate(-cameraPosition.x, -cameraPosition.y, -cameraPosition.z);

        renderer.setMatrixMode(MODEL);
        renderer.setIdentity();
        
        for (Obj obj : objs) {
            renderer.setMaterial(obj.material);
            renderer.begin();
            for (WavefrontParser.Face face : obj.faces) {
                for (int f=0; f<3; f++) {
                    Vec4 v = face.vertex[f];
                    Vec4 n = face.normal[f];
                    Vec2 t = face.texture[f];
                    renderer.setTextureCoordinates(t.x, t.y);
                    renderer.setNormal(n.x, n.y, n.z);
                    renderer.setVertex(v.x, v.y, v.z);
                }
            }
            renderer.end();            
        }
    }

    private void drawPlayer() {
        renderer.setShader(gouraudShader);
        
        renderer.setMatrixMode(VIEW);
        renderer.setIdentity();
        renderer.rotateX(cameraAngleX + Math.PI * 2);
        renderer.rotateY(-cameraAngleY + Math.PI * 0.5);
        renderer.translate(-cameraPosition.x, -cameraPosition.y, -cameraPosition.z);
        renderer.setMatrixMode(MODEL);
        renderer.setIdentity();
        player.draw(renderer);
    }
    
    int[] color = new int[] { 255, 255, 0, 255 }; // rgba
    int count = 0;

    double crazySize = 200;
    double crazySizeY = 0;
    
    public void draw(Graphics2D g) {
        Graphics2D g2d = (Graphics2D) renderer.getColorBuffer().getColorBuffer().getGraphics();
        g2d.setColor(Color.WHITE);
        g2d.drawString("FPS: " + Time.fps2, 10, 20);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);        
        g2d.drawImage(renderer.getColorBuffer().getColorBuffer(), 0, 0, 400, 300, 0, 0, 440, 330, null);
        g.drawImage(renderer.getColorBuffer().getColorBuffer(), 0, 0, 800, 600, 0, 0, 400, 300, null);
    }
    
    private class MainLoop implements Runnable {

        @Override
        public void run() {
            while (running) {
                Time.update();
                update();
                Graphics2D g = (Graphics2D) bs.getDrawGraphics();
                draw(g);
                g.dispose();
                bs.show();
            }
        }
        
    }
    
}
