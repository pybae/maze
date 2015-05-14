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
import com.jme3.light.PointLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
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
    private AudioNode audio_door;
    private ArrayList<Golem> golems = new ArrayList<Golem>();
    Nifty nifty;
    private HUD hud;
    private Rectangle endRoom;


    // note that the width and height must be odd
    public static final int MAZE_WIDTH = 25;
    public static final int MAZE_HEIGHT = 25;
    public static final int MAZE_WINDINESS = 100;
    public static final int MIN_ROOM_SIZE = 1;
    public static final int MAX_ROOM_SIZE = 3;
    public static final int MAX_ROOM_TRIES = 20;
    public static final int MIN_ROOMS = 1;
    public static final int START_ROOM_SIZE = 5;
    public static final int EXTRA_CONNECTOR_CHANCE = 20;

    public static final int MAX_GOLEMS = 7;
    public static final int WALL_WIDTH = 16;
    public static final float GOLEM_CHANCE = 0.4f;

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

            if (room.contains(MAZE_WIDTH / 2, MAZE_HEIGHT /2 )) {
                continue;
            }

            if (current_golems < MAX_GOLEMS && rand.nextFloat() > GOLEM_CHANCE) {
                current_golems++;

                Golem golem = new Golem(golemNode, assetManager, player, getPhysicsSpace(), room, nifty);
                int x = (room.y + rand.nextInt(room.height)) * WALL_WIDTH;
                int z = (room.x + rand.nextInt(room.width)) * WALL_WIDTH;
                golem.setPosition(x, z);

                golems.add(golem);
            }
        }

        endRoom = layout.rooms.get(layout.rooms.size() - 1);
        PointLight lamp_light = new PointLight();
        lamp_light.setColor(ColorRGBA.Yellow);
        lamp_light.setRadius(4f);
        lamp_light.setPosition(new Vector3f((endRoom.y + rand.nextInt(endRoom.height)) * WALL_WIDTH,
                                            6,
                                            (endRoom.x + rand.nextInt(endRoom.width)) * WALL_WIDTH));

        rootNode.addLight(lamp_light);
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

        hud = new maze.HUD();
        hud.initialize(null, this);
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(
                assetManager, inputManager, audioRenderer, guiViewPort);
        nifty = niftyDisplay.getNifty();

        //flyCam.setDragToRotate(true);
        guiViewPort.addProcessor(niftyDisplay);
        nifty.loadStyleFile("nifty-default-styles.xml");
        nifty.loadControlFile("nifty-default-controls.xml");
        nifty.addScreen("start", new ScreenBuilder("start") {{
        controller(hud);
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
                    text("The Maze");
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

        nifty.addScreen("end", new ScreenBuilder("end") {{
        controller(hud);
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
                    text("The Maze");
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
                    text("YOU DIED");
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


        nifty.addScreen("success", new ScreenBuilder("success") {{
        controller(hud);
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
                    text("The Maze");
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
                    text("YOU WON");
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
            controller(hud);
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
        Vector3f player_pos = player.getPosition();

        if (endRoom.contains(player_pos.getZ() / Maze.WALL_WIDTH,
                             player_pos.getX() / Maze.WALL_WIDTH)) {
            nifty.gotoScreen("success");
        }

        player.update(tpf);
        for (Golem golem : golems) {
            golem.update(tpf);
        }
    }

    private void initSound() {
        audio_bg = new AudioNode(assetManager, "Sound/bg.wav", false);
        audio_bg.setPositional(false);
        audio_bg.setLooping(true);
        audio_bg.setVolume(0.5f);
        rootNode.attachChild(audio_bg);
        audio_bg.play();

        audio_door = new AudioNode(assetManager, "Sound/door.wav", false);
        audio_door.setPositional(false);
        audio_door.setLooping(false);
        audio_door.setVolume(1f);
        rootNode.attachChild(audio_door);
    }

    private void initPlayer() {
        flyCam.setEnabled(true);
        flyCam.setDragToRotate(false);
        cam.setFrustumFar(RENDER_DISTANCE);

        player = new Player(rootNode, assetManager, cam, getPhysicsSpace(), viewPort);

        // for now, set the player spawn to the center of the maze
        player.setSpawn((MAZE_WIDTH / 2) * WALL_WIDTH,
                        (MAZE_HEIGHT / 2) * WALL_WIDTH);
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

                    float dist = closest.getDistance();
                    if (dist < 10f) {
                        Geometry door = closest.getGeometry();

                        if (door.getName().equals("Door")) {
                            Quaternion current_rotation = door.getLocalRotation();
                            current_rotation = current_rotation.fromAngleAxis(FastMath.PI / 2, new Vector3f(0, 1, 0));

                            door.setLocalRotation(current_rotation);
                            door.move(4, 0, 4);

                            door.setName("OpenDoor");
                        } else {
                            Quaternion current_rotation = door.getLocalRotation();
                            current_rotation = current_rotation.fromAngleAxis(0, new Vector3f(0, 1, 0));

                            door.setLocalRotation(current_rotation);
                            door.move(-4, 0, -4);

                            door.setName("Door");
                        }

                        audio_door.play();
                    } else {
                        // do nothing
                    }
                } else {
                    // do nothing
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
