package maze;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioNode;
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
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.builder.TextBuilder;
import de.lessvoid.nifty.controls.button.builder.ButtonBuilder;
import de.lessvoid.nifty.screen.DefaultScreenController;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Random;

public class Maze extends SimpleApplication implements ActionListener {

    private BulletAppState bulletAppState;
    private Node doorNode, wallNode, openNode, golemNode;
    private Player player;
    private MazeGenerator generator;
    private MazeLayout layout;
    private AudioNode audio_bg;
    private ArrayList<Golem> golems = new ArrayList<Golem>();
    Nifty nifty;

    // note that the width and height must be odd
    public static final int MAZE_WIDTH = 61;
    public static final int MAZE_HEIGHT = 61;
    public static final int MAZE_WINDINESS = 100;
    public static final int MIN_ROOM_SIZE = 1;
    public static final int MAX_ROOM_SIZE = 3;
    public static final int MAX_ROOM_TRIES = 20;
    public static final int MIN_ROOMS = 1;
    public static final int START_ROOM_SIZE = 5;
    public static final int EXTRA_CONNECTOR_CHANCE = 20;

    public static final int MAX_GOLEMS = 7;
    public static final int WALL_WIDTH = 16;
    public static final float GOLEM_CHANCE = 0.2f;

    public static final float RENDER_DISTANCE = 50.0f;

    public Maze() {
    }

    public void generateMaze() {
        MazeEntity mz = new WallEntity(WALL_WIDTH, WALL_WIDTH);
        MazeEntity oz = new OpenEntity(WALL_WIDTH, WALL_WIDTH);
        MazeEntity dz = new DoorEntity(WALL_WIDTH, WALL_WIDTH);

        generator = new MazeGenerator(MAZE_WIDTH,
                                      MAZE_HEIGHT,
                                      MAZE_WINDINESS,
                                      MIN_ROOM_SIZE,
                                      MAX_ROOM_SIZE,
                                      MAX_ROOM_TRIES,
                                      MIN_ROOMS,
                                      START_ROOM_SIZE,
                                      EXTRA_CONNECTOR_CHANCE);

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
                    if(layout.maze[r+1][c] == State.WALL){
                        dz.renderObject(new Vector3f(16*r, 0, 16*c),
                                    doorNode, wallNode,
                                    assetManager,
                                    getPhysicsSpace(),
                                    true);
                    }
                    else {
                        dz.renderObject(new Vector3f(16*r, 0, 16*c),
                                        doorNode, wallNode,
                                        assetManager,
                                        getPhysicsSpace(),
                                        false);
                    }
                    oz.renderObject(new Vector3f(16*r, 0, 16*c),
                                    openNode,
                                    assetManager,
                                    getPhysicsSpace());
                } else if (layout.maze[r][c] == State.NOT_SET) {
                } else {
                    oz.renderObject(new Vector3f(16*r, 0, 16*c),
                                    openNode,
                                    assetManager,
                                    getPhysicsSpace());
                }
            }
        }

        Random rand = new Random();

        int current_golems = 0;
        for (int i = 0; i < layout.rooms.size(); i++) {
            Rectangle room = layout.rooms.get(i);

            if (current_golems < MAX_GOLEMS && rand.nextFloat() > GOLEM_CHANCE) {
                current_golems++;

                Golem golem = new Golem(golemNode, assetManager, player, getPhysicsSpace(), room);
                int x = (room.y + rand.nextInt(room.height)) * WALL_WIDTH;
                int z = (room.x + rand.nextInt(room.width)) * WALL_WIDTH;
                golem.setPosition(x, z);
                player.setSpawn(x, z + 1);

                golems.add(golem);
            }
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
        golemNode = new Node("Golems");

        rootNode.attachChild(doorNode);
        rootNode.attachChild(wallNode);
        rootNode.attachChild(openNode);
        rootNode.attachChild(golemNode);


        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(
                assetManager, inputManager, audioRenderer, guiViewPort);
        nifty = niftyDisplay.getNifty();
        //flyCam.setDragToRotate(true);
        guiViewPort.addProcessor(niftyDisplay);
        nifty.loadStyleFile("nifty-default-styles.xml");
        nifty.loadControlFile("nifty-default-controls.xml");
        nifty.addScreen("start", new ScreenBuilder("start") {{
        controller(new maze.HUD());
        layer(new LayerBuilder("background"){{
            childLayoutCenter();
            backgroundColor("#000f");
            // add image
            //image(new ImageBuilder() {{
              //  filename("Textures/HUD/start.jpg");
            //}});
        }});

        layer(new LayerBuilder("foreground") {{
                childLayoutVertical();

            // panel added
           panel(new PanelBuilder("panel_top") {{
                childLayoutCenter();
                alignCenter();
                height("25%");
                width("75%");

                // add text
                text(new TextBuilder() {{
                    text("Sp00ky Maze");
                    font("Interface/Fonts/Default.fnt");
                    height("100%");
                    width("100%");
                }});
           }});

           panel(new PanelBuilder("panel_mid") {{
                childLayoutCenter();
                alignCenter();
                height("50%");
                width("75%");
                // add text

                text(new TextBuilder() {{
                    text("Escape the maze.");
                    font("Interface/Fonts/Default.fnt");
                    wrap(true);
                    height("100%");
                    width("100%");
                }});
            }});

            panel(new PanelBuilder("panel_bottom") {{
                childLayoutHorizontal();
                alignCenter();
                height("25%");
                width("75%");

                panel(new PanelBuilder("panel_bottom_left") {{
                    childLayoutCenter();
                    valignCenter();
                    height("50%");
                    width("50%");

                    // add control
                    control(new ButtonBuilder("StartButton", "Start") {{
                        alignCenter();
                        valignCenter();
                        height("50%");
                        width("50%");
                        visibleToMouse(true);
                        interactOnClick("startGame(hud)");
                    }});
                }});

                panel(new PanelBuilder("panel_bottom_right") {{
                    childLayoutCenter();
                    valignCenter();
                    height("50%");
                    width("50%");

                    // add control
                    control(new ButtonBuilder("QuitButton", "Quit") {{
                        alignCenter();
                        valignCenter();
                        height("50%");
                        width("50%");
                        visibleToMouse(true);
                        interactOnClick("quitGame()");
                    }});
                }});
            }}); // panel added
        }});

        }}.build(nifty));

        nifty.addScreen("hud", new ScreenBuilder("hud"){{
            controller(new maze.HUD());
        }}.build(nifty));

        nifty.gotoScreen("start");

        // AmbientLight al = new AmbientLight();
        // al.setColor(ColorRGBA.White.mult(1.3f));
        // rootNode.addLight(al);

        initPlayer();
        initKeys();
        initCrossHair();
        generateMaze();
        initSound();
    }

    @Override
    public void simpleUpdate(float tpf) {
        player.update(tpf);
        for (Golem golem : golems) {
            golem.update(tpf);
        }
    }

    private void initSound() {
        audio_bg = new AudioNode(assetManager, "Sound/bg.wav", false);
        audio_bg.setPositional(false);
        audio_bg.setLooping(true);
        audio_bg.setVolume(.5f);
        rootNode.attachChild(audio_bg);
        audio_bg.play();
    }

    private void initPlayer() {
        flyCam.setEnabled(true);
        flyCam.setDragToRotate(false);
        cam.setFrustumFar(RENDER_DISTANCE);

        player = new Player(rootNode, assetManager, cam, getPhysicsSpace(), viewPort);

        // for now, set the player spawn to the center of the maze
        player.setSpawn(30*16, 21*16);
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
