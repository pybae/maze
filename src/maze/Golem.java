package maze;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.bounding.BoundingVolume;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import de.lessvoid.nifty.Nifty;
import java.awt.Rectangle;

public class Golem {
    private Node rootNode;
    private AssetManager assetManager;
    private Player player;
    private PhysicsSpace physicsSpace;
    private Rectangle room;
    private Nifty nifty;

    private Node golem;
    private RigidBodyControl control;
    private AudioNode grumble;
    private float elapsedTime;

    public static final float GOLEM_SPEED = 0.2f;
    public static final float GRUMBLE_LOOP = 30;

    /**
     * The constructor for the golem class
     * this takes in the current player to use for viewport and position
     */
    public Golem(Node rootNode, AssetManager assetManager, Player player, PhysicsSpace physicsSpace, Rectangle room, Nifty nifty) {

        this.rootNode = rootNode;
        this.assetManager = assetManager;
        this.player = player;
        this.physicsSpace = physicsSpace;
        this.room = room;
        this.nifty = nifty;

        this.elapsedTime = (float) 0.0;

        initModel();
        initSound();
    }

    /**
     * a helper model to initialize the models and node of the golem
     */
    private void initModel() {
        golem = (Node) assetManager.loadModel("Models/Oto/Oto.mesh.xml");
        golem.setLocalScale(1.4f);

        CollisionShape shape = CollisionShapeFactory.createMeshShape(golem);
        control = new RigidBodyControl(shape, 1.0f);
        control.setKinematic(true);
        physicsSpace.add(control);
        golem.addControl(control);

        rootNode.attachChild(golem);
    }

    /**
     *
     */
    private void initSound() {
        grumble = new AudioNode(assetManager, "Sound/zane_grumble.wav", false);
        grumble.setPositional(false);
        grumble.setLooping(false);
        grumble.setVolume(2);
        rootNode.attachChild(grumble);
    }

    /**
     * sets the position of the golem
     */
    public void setPosition(float x, float z) {
        control.setPhysicsLocation(new Vector3f(x, 6, z));
        golem.setLocalTranslation(new Vector3f(x, 6, z));
    }

    /**
     * checks if the player is within the golem's room
     */
    private boolean containsPlayer() {
        Vector3f player_pos = player.getPosition();

        return room.contains(player_pos.getZ() / Maze.WALL_WIDTH,
                             player_pos.getX() / Maze.WALL_WIDTH);

    }

    /**
     * updates the position and direction of the golem
     */
    public void update(float tpf) {
        if (containsPlayer()) {
            BoundingVolume bv = golem.getWorldBound();

            Vector3f player_pos = player.getPosition();
            Vector3f pos = control.getPhysicsLocation();

            float distance = pos.subtract(player_pos).length();

            if (distance < 6.5f) {
                nifty.gotoScreen("end");
            }

            if (!player.isLooking(bv)) {
                Quaternion orientation = control.getPhysicsRotation();
                Vector3f direction = player_pos.subtract(pos).normalize();

                direction.setY(0);
                orientation.lookAt(direction, new Vector3f(0, 1, 0));
                control.setPhysicsRotation(orientation);

                pos.addLocal(direction.mult(GOLEM_SPEED));
                setPosition(pos.getX(), pos.getZ());
            } else {
                // do nothing
            }
        }

        elapsedTime += tpf;
        if(elapsedTime > GRUMBLE_LOOP) {
            elapsedTime = (float) 0.0;

            float dist = player.getPosition().distance(control.getPhysicsLocation());

            if(dist < 20) {
                grumble.setVolume(3);
            } else if(dist < 50) {
                grumble.setVolume(2);
            } else if(dist < 100) {
                grumble.setVolume(1);
            }

            grumble.playInstance();
        }
    }
}
