package entity;

import audio.SoundManager;
import audio.Sounds;
import input.Mouse;
import java.awt.event.KeyEvent;
import java.util.List;
import md2.MD2Obj;
import physics.Input;
import physics.PhysicsPlayer;
import physics.PlayerListener;
import renderer.math.Vec3;
import renderer.core.Material;
import renderer.core.Renderer;
import renderer.parser.wavefront.Obj;

/**
 *
 * @author admin
 */
public class Player extends Entity implements PlayerListener, Runnable {
    
    private PhysicsPlayer player;
    private final List<Obj> objsPlayer;
    
    private MD2Obj md2FPS;
    private MD2Obj md2Current;
    
    private Material playerMd2Material = new Material("player");
    
    private boolean fire;
    private boolean reloading;
    private long fireTime;
    
    public Player(PhysicsPlayer player, List<Obj> objsPlayer) {
        this.player = player;
        player.setListener(this);
        
        this.objsPlayer = objsPlayer;
        
        try {
            md2FPS = new MD2Obj("/res/fps_gun.md2", 40);
            md2FPS.createAnimation("idle", 0, 180);
            md2FPS.createAnimation("reload", 181, 249);
            md2FPS.createAnimation("fire", 250, 266);
            md2FPS.createAnimation("walk", 267, 290);
            
            md2Current = md2FPS;
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        playerMd2Material.map_kd = new renderer.core.Image("/res/Arm_Texture_Digital_Camo_Desert_01.png");
        Mouse.addPressedListener(this);
    }

    @Override
    public void update() {
        player.update();
        
        if (!reloading && !fire && Input.isKeyPressed(KeyEvent.VK_R)) {
            reloading = true;
            md2FPS.setAnimation("reload");
            SoundManager.getInstance().play(Sounds.RELOAD);
        }

        md2Current.nextFrame(0.5);     
        
        if (fire && "fire".equals(md2FPS.getCurrentAnimation()) && md2Current.getCurrentFrame() >= md2Current.getEndFrame()) {
            md2FPS.setAnimation("idle");
            fire = false;
        }
        else if (reloading && "reload".equals(md2FPS.getCurrentAnimation()) && md2Current.getCurrentFrame() >= md2Current.getEndFrame()) {
            md2FPS.setAnimation("idle");
            reloading = false;
        }
        
    }

    @Override
    public void draw(Renderer renderer) {
        //renderer.translate(0, -50, 50);        
        
        Vec3 playerPosition = player.getCollider().getPosition();
        
        renderer.translate(playerPosition.x, playerPosition.y, playerPosition.z);        
        renderer.rotateY(player.getAngle() + Math.toRadians(180));
        renderer.translate(0, 5 + 60, 6);        
        
        // draw collider
//        for (Obj obj : objsPlayer) {
//            renderer.setMaterial(obj.material);
//            renderer.begin();
//            for (WavefrontParser.Face face : obj.faces) {
//                //uniform[0] = 1;
//                //uniform[1] = 1;
//                //uniform[2] = 1;
//                //renderer.setUniforms(uniform);
//                for (int f=0; f<3; f++) {
//                    Vec4 v = face.vertex[f];
//                    Vec4 n = face.normal[f];
//                    Vec2 t = face.texture[f];
//                    renderer.setTextureCoordinates(t.x, t.y);
//                    renderer.setNormal(n.x, n.y, n.z);
//                    renderer.setVertex(v.x, v.y, v.z);
//                }
//            }
//            renderer.end();            
//        }
        
        // draw md2 animation
        for (int t = 0; t < md2Current.getTriangles().length; t++) {
            renderer.setMaterial(playerMd2Material);
            renderer.begin();
                for (int p=0; p<3; p++) {
                    double[] tv = md2Current.getTriangleVertex(t, p);
                    renderer.setTextureCoordinates(tv[6], 1 - tv[7]);
                    renderer.setNormal(tv[4], tv[5], tv[3]);
                    renderer.setVertex(tv[1], tv[2], tv[0]);
                }
            renderer.end();            
        }
            
    }

    @Override
    public void onPlayerJumpStart() {
    }

    @Override
    public void onPlayerJumpTop() {
    }

    @Override
    public void onPlayerJumpEnd() {
    }

    @Override
    public void onPlayerFoward() {
        if (!reloading && !fire && !"walk".equals(md2FPS.getCurrentAnimation())) {
            md2FPS.setAnimation("walk");
            SoundManager.getInstance().play(Sounds.WALKING, true);
        }
    }

    @Override
    public void onPlayerBackward() {
        if (!reloading && !fire && !"walk".equals(md2FPS.getCurrentAnimation())) {
            md2FPS.setAnimation("walk");
            SoundManager.getInstance().play(Sounds.WALKING, true);
        }
    }

    @Override
    public void onPlayerRotateLeft() {
        if (!reloading && !fire && !"walk".equals(md2FPS.getCurrentAnimation())) {
            md2FPS.setAnimation("walk");
            SoundManager.getInstance().play(Sounds.WALKING, true);
        }
    }

    @Override
    public void onPlayerRotateRight() {
        if (!reloading && !fire && !"walk".equals(md2FPS.getCurrentAnimation())) {
            md2FPS.setAnimation("walk");
            SoundManager.getInstance().play(Sounds.WALKING, true);
        }
    }

    @Override
    public void onPlayerStrafe() {
        if (!reloading && !fire && !"walk".equals(md2FPS.getCurrentAnimation())) {
            md2FPS.setAnimation("walk");
            SoundManager.getInstance().play(Sounds.WALKING, true);
        }
    }

    @Override
    public void onPlayerStop() {
        if (!reloading && !fire && !"idle".equals(md2FPS.getCurrentAnimation())) {
            md2FPS.setAnimation("idle");
            SoundManager.getInstance().stop(Sounds.WALKING);
        }
    }

    // when click fire
    @Override
    public void run() {
        if (!reloading && (System.currentTimeMillis() - fireTime) > 150) {
            fire = true;
            fireTime = System.currentTimeMillis();
            md2FPS.setAnimation("fire");
            //SoundManager.getInstance().stop(Sounds.SHOOT);
            SoundManager.getInstance().play(Sounds.SHOOT);
        }
    }

}
