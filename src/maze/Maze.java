package maze;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;

public class Maze extends SimpleApplication implements ActionListener {

    private BulletAppState bulletAppState;
    private Node doorNode, wallNode, openNode;
    private Player player;
    private Golem golem;
    private MazeGenerator generator;
    private MazeLayout layout;


    // note that the width and height must be odd
    public static final int MAZE_WIDTH = 61;
    public static final int MAZE_HEIGHT = 61;
    public static final int MAZE_WINDINESS = 100;
    public static final int MIN_ROOM_SIZE = 2;
    public static final int MAX_ROOM_SIZE = 6;
    public static final int MAX_ROOM_TRIES = 20;
    public static final int MIN_ROOMS = 1;

    public static final float RENDER_DISTANCE = 50.0f;

    public Maze() {
    }

    public void generateMaze() {
        MazeEntity mz = new WallEntity(16, 16);
        OpenEntity oz = new OpenEntity(16, 16);
        DoorEntity dz = new DoorEntity(16, 16);

        generator = new MazeGenerator(MAZE_WIDTH,
                                      MAZE_HEIGHT,
                                      MAZE_WINDINESS,
                                      MIN_ROOM_SIZE,
                                      MAX_ROOM_SIZE,
                                      MAX_ROOM_TRIES,
                                      MIN_ROOMS);

        layout = generator.generate();
        layout.print();


        for (int r = 0; r < layout.maze.length; r++) {
            for (int c = 0; c < layout.maze[0].length; c++) {
                if (layout.maze[r][c] == State.WALL) {
                    mz.renderObject(new Vector3f(16*r, 0, 16*c),
                                    wallNode,
                                    assetManager,
                                    getPhysicsSpace());
                } else if (layout.maze[r][c] == State.DOOR) {
                } else if (layout.maze[r][c] == State.NOT_SET) {
                } else {
                    oz.renderObject(new Vector3f(16*r, 0, 16*c),
                                    openNode,
                                    assetManager,
                                    getPhysicsSpace());
                }
            }
            System.out.println();
        }

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

        doorNode = new Node("Doors");
        wallNode = new Node("Walls");
        openNode = new Node("Floors");
        rootNode.attachChild(doorNode);
        rootNode.attachChild(wallNode);
        rootNode.attachChild(openNode);

        initKeys();
        initCrossHair();

        generateMaze();

        initPlayer();
        initMobs();
    }

    @Override
    public void simpleUpdate(float tpf) {
        player.update(tpf);
        golem.update(tpf);
    }

    private void initMobs() {
        // AmbientLight al = new AmbientLight();
        // al.setColor(ColorRGBA.White.mult(1.3f));
        // rootNode.addLight(al);

        Node golemNode = new Node("Golems");
        rootNode.attachChild(golemNode);

        golem = new Golem(golemNode, assetManager, player, getPhysicsSpace());
        golem.setPosition(30*16, 21*16);
    }

    private void initPlayer() {
        flyCam.setEnabled(true);
        cam.setFrustumFar(RENDER_DISTANCE);

        player = new Player(rootNode, assetManager, cam, getPhysicsSpace(), viewPort);

        // for now, set the player spawn to the center of the maze
        player.setSpawn(30*16, 20*16);
    }

    private void initCrossHair(){
        setDisplayStatView(false);
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText ch = new BitmapText(guiFont, false);
        ch.setSize(guiFont.getCharSet().getRenderedSize()*2);
        ch.setText("+");
        ch.setLocalTranslation(settings.getWidth() / 2 - ch.getLineWidth()/2,
            settings.getHeight() / 2 + ch.getLineHeight()/2, 0);
        guiNode.attachChild(ch);
    }

    private void initKeys() {
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Sprint", new KeyTrigger(KeyInput.KEY_LSHIFT));
        inputManager.addMapping("PointerAction",
            new KeyTrigger(KeyInput.KEY_SPACE),
            new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(this, "Left");
        inputManager.addListener(this, "Right");
        inputManager.addListener(this, "Up");
        inputManager.addListener(this, "Down");
        inputManager.addListener(this, "Sprint");
        inputManager.addListener(this, "PointerAction");
    }

    public void onAction(String binding, boolean isPressed, float tpf) {
        if (!player.onAction(binding, isPressed, tpf)) {
            // the binding is not taken care of by the player
            if (binding.equals("PointerAction") && !isPressed) {
                // list of collisions from raycasting is stored in here
                CollisionResults results = new CollisionResults();
                Ray ray = new Ray(cam.getLocation(), cam.getDirection());

                // collisions are stored in results
                doorNode.collideWith(ray, results);

                if (results.size() > 0) {
                    CollisionResult closest = results.getClosestCollision();

                    String hit = closest.getGeometry().getName();
                    System.out.println("Pointer picked " + hit + ".");

                    float dist = closest.getDistance();
                    if (dist < 10f) {
                        System.out.println("Can perform an action.");
                    } else {
                        System.out.println("Too far.");
                    }
                } else {
                    System.out.println("Nothing.");
                }
            }
        }
    }

    public PhysicsSpace getPhysicsSpace() {
        return bulletAppState.getPhysicsSpace();
    }

    @Override
    public void simpleRender(RenderManager rm) {
    }

}
