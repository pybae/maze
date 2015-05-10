package maze;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingVolume;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

public class Golem {
    private Node rootNode;
    private AssetManager assetManager;
    private Player player;
    private PhysicsSpace physicsSpace;

    private Node golem;
    private RigidBodyControl control;

    public static final float GOLEM_SPEED = 0.2f;

    /**
     * The constructor for the golem class
     * this takes in the current player to use for viewport and position
     */
    public Golem(Node rootNode, AssetManager assetManager, Player player, PhysicsSpace physicsSpace) {

        this.rootNode = rootNode;
        this.assetManager = assetManager;
        this.player = player;
        this.physicsSpace = physicsSpace;

        initModel();
    }

    /**
     * a helper model to initialize the models and node of the golem
     */
    private void initModel() {
        golem = (Node) assetManager.loadModel("Models/Oto/Oto.mesh.xml");
        golem.setLocalScale(1.4f);

        CollisionShape shape = CollisionShapeFactory.createMeshShape(golem);
        control = new RigidBodyControl(shape, 0.0f);
        physicsSpace.add(control);
        golem.addControl(control);

        rootNode.attachChild(golem);
    }

    /**
     * sets the position of the golem
     */
    public void setPosition(float x, float z) {
        control.setPhysicsLocation(new Vector3f(x, 6, z));
    }

    /**
     * updates the position and direction of the golem
     */
    public void update(float tpf) {
        BoundingVolume bv = golem.getWorldBound();

        if (!player.isLooking(bv)) {
            Quaternion orientation = control.getPhysicsRotation();
            Vector3f pos = control.getPhysicsLocation();
            Vector3f player_pos = player.getPosition();

            Vector3f direction = player_pos.subtract(pos).normalize();

            direction.setY(0);
            orientation.lookAt(direction, new Vector3f(0, 1, 0));
            control.setPhysicsRotation(orientation);

            pos.addLocal(direction.mult(GOLEM_SPEED));
            setPosition(pos.getX(), pos.getZ());
        }
    }
}
