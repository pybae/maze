package maze;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CylinderCollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.objects.PhysicsCharacter;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;

public class Maze extends SimpleApplication implements ActionListener {

    private BulletAppState bulletAppState;
    private PhysicsCharacter player;
    private boolean left, right, up, down;
    private Vector3f walkDirection = new Vector3f(0, 0, 0);
    private static final float PLAYER_SPEED = 5.0f;
    private static final float RENDER_DISTANCE = 2000.0f;

    public Maze() {
    }

    public void generateMaze() {
        /** @zane
         * you can write the implementation for generating the Maze here.
         * The maze itself should be a 2D array of MazeEntities
         * we can initialize which coordinates to pass into the entities
         */
        MazeEntity mz = new WallEntity(15, 10);
        mz.renderObject(new Vector3f(0, 0, 0),
                        rootNode,
                        assetManager,
                        getPhysicsSpace());

        OpenEntity oz = new OpenEntity(20, 20);
        oz.renderObject(new Vector3f(0, 0, WallEntity.WALL_LENGTH),
                        rootNode,
                        assetManager,
                        getPhysicsSpace());
    }

    /**
     * this is the method that is invoked when the class is first instantiated
     * in other words, game load
     * Here, we setup the user models, add them to Bullet space, and initialize the maze
     */
    @Override
    public void simpleInitApp() {
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.setDebugEnabled(false);

        initPlayer();
        initKeys();
        initLight();

        generateMaze();
    }

    @Override
    public void simpleUpdate(float tpf) {
        Vector3f camDir = cam.getDirection();
        Vector3f camLeft = cam.getLeft();

        walkDirection.set(0, 0, 0);

        if (left) {
            walkDirection.addLocal(camLeft);
        }
        if (right) {
            walkDirection.addLocal(camLeft.negate());
        }
        if (up) {
            walkDirection.addLocal(camDir);
        }
        if (down) {
            walkDirection.addLocal(camDir.negate());
        }

        cam.setLocation(player.getPhysicsLocation());
        player.setWalkDirection(walkDirection.divide(PLAYER_SPEED));
    }

    private void initPlayer() {
        flyCam.setEnabled(true);

        cam.setFrustumFar(RENDER_DISTANCE);

        player = new PhysicsCharacter(new SphereCollisionShape(5.0f), 0.1f);

        // disallow player jump
        player.setJumpSpeed(0);
        player.setFallSpeed(20);
        player.setGravity(30);
        player.setPhysicsLocation(new Vector3f(5, 5, 5));

        getPhysicsSpace().add(player);
    }

    private void initLight() {
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White.mult(5f));
        rootNode.addLight(ambient);
    }

    private void initKeys() {
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addListener(this, "Left");
        inputManager.addListener(this, "Right");
        inputManager.addListener(this, "Up");
        inputManager.addListener(this, "Down");
    }

    public void onAction(String binding, boolean isPressed, float tpf) {
        if (binding.equals("Left")) {
            left = isPressed;
        } else if (binding.equals("Right")) {
            right = isPressed;
        } else if (binding.equals("Up")) {
            up = isPressed;
        } else if (binding.equals("Down")) {
            down = isPressed;
        }
    }

    public PhysicsSpace getPhysicsSpace() {
        return bulletAppState.getPhysicsSpace();
    }

    @Override
    public void simpleRender(RenderManager rm) {
    }

}
