package physics;


import renderer.math.Vec3;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import physics.partition.Octree;
import renderer.core.FpsMouseCapturer;

/**
 * World class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class World {

    private final List<Face> faces = new ArrayList<>();
    private final PhysicsPlayer player;

    private final List<Response> collidedResponses = new ArrayList<>();
    private final Vec3 resultPush = new Vec3();
    
    private MeshLoader terrain;
    
    private Octree spatialPartition;
    private final List<Face> retrievedFaces = new ArrayList<>();
    
    public World(FpsMouseCapturer fpsMouseCapturer) {
        player = new PhysicsPlayer(fpsMouseCapturer);
        createLevel();
    }

    private void createLevel() {
        terrain = new MeshLoader();
        try {
            terrain.load("/res/cs_collider.obj", 100, 0, 0, 0);
            
        } catch (Exception ex) {
            Logger.getLogger(World.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
        faces.addAll(terrain.getFaces());
        spatialPartition = new Octree(terrain.getMin(), terrain.getMax());
        spatialPartition.addFaces(terrain.getFaces());
        
        player.getCollider().getPosition().set(100, 1, 100); // test for cs.obj
    }
    
    public PhysicsPlayer getPlayer() {
        return player;
    }

    public void update() {
        player.update();
        
        // detect collision with polygons retrieved from spatial partition
        collidedResponses.clear();
        
        retrievedFaces.clear();
        spatialPartition.retrieveFaces(player.getBox(), retrievedFaces);
        
//        System.out.println("total faces:" + faces.size() 
//                + " / retrieved faces: " + retrievedFaces.size());
        
        for (Face face : retrievedFaces) {
            Response response = face.checkCollision(player.getCollider());
            if (response.isCollides()) {
                collidedResponses.add(response);
            }
        }
        
        resultPush.set(0, 0, 0);

        // collision response 
        if (!collidedResponses.isEmpty()) {
            collidedResponses.forEach((response) -> {
                resultPush.add(response.getContactNormal());
            });
            //resultPush.scale(1.0 / collidedResponses.size());
            player.getCollider().getPosition().add(resultPush);

            //player.getCollider().getPosition().y += resultPush.y; // <-- teste
        }        
        
        // fix velocity so the player can slide when colliding with walls
        if (!collidedResponses.isEmpty()) {
            resultPush.normalize();
            double dot = resultPush.dot(player.getVelocity());
            resultPush.scale(dot);
            
            player.getVelocity().sub(resultPush);
            //player.getVelocity().x -= resultPush.x;
            //player.getVelocity().y -= resultPush.y;
            //player.getVelocity().z -= resultPush.z;
            
            // avoid player to climb easily
            //player.getVelocity().y *= 0.1;
        }
        
        // update player position
        player.getCollider().getPosition().add(player.getVelocity());    
        
        //System.out.println("player position: " + player.getCollider().getPosition());
    }

    public void draw(Graphics2D g) {
        double scale = 50;
        
        //player.draw(g, scale);
        //face.draw(g, scale);
        
        player.draw3D(g, scale);

//        for (int i = 0; i < 100; i++) {
//            Face face = faces.get(i);
//            face.draw3D(g, scale, player.getAngle(), player.getCollider().getPosition());
//        }
        
        faces.forEach((face) -> {
            face.draw3D(g, scale, player.getAngle()
                    , player.getCollider().getPosition());
        });
    }
    
}
