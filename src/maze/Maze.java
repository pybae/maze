package maze;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;

public class Maze extends SimpleApplication implements ActionListener {

    private BulletAppState bulletAppState;
    private Node player;
    private BetterCharacterControl playerController;
    private boolean left,right,up,down;
    private Vector3f walkDirection = new Vector3f(0, 0, 0);


    public Maze() {
    }

    public void generateMaze() {
        /** @zane
         * you can write the implementation for generating the Maze here.
         * The maze itself should be a 2D array of MazeEntities
         * we can initialize which coordinates to pass into the entities
         */
        MazeEntity mz = new WallEntity(1, 1);
        mz.renderObject(new Vector3f(0, 0, 0),
                        rootNode,
                        assetManager,
                        getPhysicsSpace());
        mz.renderObject(new Vector3f(0, 1, 0),
                        rootNode,
                        assetManager,
                        getPhysicsSpace());
        mz.renderObject(new Vector3f(1, 2, 0),
                        rootNode,
                        assetManager,
                        getPhysicsSpace());
        
        OpenEntity oz = new OpenEntity(1, 1);
        oz.renderObject(new Vector3f(0, 0, WallEntity.WALL_LENGTH),
                        rootNode,
                        assetManager,
                        getPhysicsSpace());
        oz.renderObject(new Vector3f(0, 0, WallEntity.WALL_LENGTH),
                        rootNode,
                        assetManager,
                        getPhysicsSpace());
        oz.renderObject(new Vector3f(1, 0, WallEntity.WALL_LENGTH),
                        rootNode,
                        assetManager,
                        getPhysicsSpace());
        oz.renderObject(new Vector3f(1, 0, 1),
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
        bulletAppState.setDebugEnabled(true);

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

        // uncomment this line and one cannot "fly" anymore
        // walkDirection.setY(0.0f);

        playerController.setWalkDirection(walkDirection);
        cam.setLocation(player.getLocalTranslation());
    }

    private void initPlayer() {
        player = new Node("Character Node");
        player.setLocalTranslation(new Vector3f(0, 10.0f, 0));

        playerController = new BetterCharacterControl(0.3f, 2.5f, 8f);
        playerController.setGravity(new Vector3f(0, 0, 0));
        player.addControl(playerController);
        getPhysicsSpace().add(playerController);

        rootNode.attachChild(player);
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
