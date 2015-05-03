package maze;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.objects.PhysicsCharacter;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.SpotLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.shadow.SpotLightShadowFilter;
import com.jme3.shadow.SpotLightShadowRenderer;

public class Maze extends SimpleApplication implements ActionListener {

    private BulletAppState bulletAppState;
    private Node doorNode, wallNode, openNode;
    private PhysicsCharacter player;
    private SpotLight flashLight1, flashLight2, flashLightRim;
    private boolean left, right, up, down, lean;
    private Vector3f walkDirection = new Vector3f(0, 0, 0);
    private static final float PLAYER_SPEED = 5.0f;
    private static final float RENDER_DISTANCE = 50.0f;
    private static final int SHADOWMAP_SIZE = 1024;
    private MazeGenerator generator;
    private MazeLayout layout;
    private int spawnX;
    private int spawnY;
    public Maze() {
    }

    public void generateMaze() {
        /** @zane
         * you can write the implementation for generating the Maze here.
         * The maze itself should be a 2D array of MazeEntities
         * we can initialize which coordinates to pass into the entities
         */
         //test Room
       MazeEntity mz = new WallEntity(16, 16);
       OpenEntity oz = new OpenEntity(16, 16);
       DoorEntity dz = new DoorEntity(16, 16);
//        dz.renderObject(new Vector3f(0, 0, 16),
//                        doorNode,
//                        assetManager,
//                        getPhysicsSpace());
        //test Room

        generator = new MazeGenerator(61, 41, 100, 2, 6, 20, 1);
        layout = generator.generate();
        layout.print();


        for(int r = 0; r < layout.maze.length; r++) {
            for(int c = 0; c < layout.maze[0].length; c++) {
                if(layout.maze[r][c] == State.WALL) {
                    mz.renderObject(new Vector3f(16*r, 0, 16*c),
                        wallNode,
                        assetManager,
                        getPhysicsSpace());
                } else if(layout.maze[r][c] == State.DOOR) {
                } else if(layout.maze[r][c] == State.NOT_SET) {
                } else {
                        spawnX = r;
                        spawnY = c;

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

        generateMaze();
        initPlayer();
        initKeys();
        initLight();
        initCrossHair();
    }

    @Override
    public void simpleUpdate(float tpf) {
        Vector3f camDir = cam.getDirection();
        Vector3f camLeft = cam.getLeft();
        Vector3f camLoc = cam.getLocation();

        Vector3f plLocation = player.getPhysicsLocation();
        plLocation.setY(plLocation.getY() + 7f);

        walkDirection.set(0, 0, 0);

        if (lean && left) {
          /*
            cam.lookAtDirection(camDir, new Vector3f(-.5f, 1, 0));
            plLocation.setX(plLocation.getX() - 4f);*/
            cam.setLocation(plLocation);

        }
        else if (left) {
            walkDirection.addLocal(camLeft);
        }
        if (lean && right) {
          /*
            cam.lookAtDirection(camDir, new Vector3f(.5f, 1, 0));
            plLocation.setX(plLocation.getX() + 4f);*/
            cam.setLocation(plLocation);

        }
        else if (right) {
            walkDirection.addLocal(camLeft.negate());
        }
        if (up) {
            walkDirection.addLocal(camDir);
        }
        if (down) {
            walkDirection.addLocal(camDir.negate());
        }

        player.setWalkDirection(walkDirection.divide(PLAYER_SPEED));
        flashLight1.setPosition(camLoc);
        flashLight2.setPosition(camLoc);
        flashLightRim.setPosition(camLoc);

        flashLight1.setDirection(camDir);
        flashLight2.setDirection(camDir);
        flashLightRim.setDirection(camDir);

        if (!(lean && left) && !(lean && right)) {
            //cam.lookAtDirection(camDir, new Vector3f(0, 1, 0));
            cam.setLocation(plLocation);
        }
    }

    private void initPlayer() {
        flyCam.setEnabled(true);

        cam.setFrustumFar(RENDER_DISTANCE);

        player = new PhysicsCharacter(new SphereCollisionShape(3.0f), 0.1f);

        // disallow player jump
        player.setJumpSpeed(0);
        player.setFallSpeed(20);
        player.setGravity(30);{
        player.setPhysicsLocation(new Vector3f(spawnX*16, 5, spawnY*16));
        }
        getPhysicsSpace().add(player);
    }

    private void initLight() {
        flashLight1 = new SpotLight();
        flashLight1.setSpotRange(38f);
        flashLight1.setSpotInnerAngle(0f * FastMath.DEG_TO_RAD);
        flashLight1.setSpotOuterAngle(8f * FastMath.DEG_TO_RAD);
        flashLight1.setColor(ColorRGBA.White.mult(1.5f));
        flashLight1.setPosition(cam.getLocation());
        flashLight1.setDirection(cam.getDirection());
        rootNode.addLight(flashLight1);

        flashLight2 = new SpotLight();
        flashLight2.setSpotRange(30f);
        flashLight2.setSpotInnerAngle(25f * FastMath.DEG_TO_RAD);
        flashLight2.setSpotOuterAngle(34f * FastMath.DEG_TO_RAD);
        flashLight2.setColor(ColorRGBA.White.mult(1.25f));
        flashLight2.setPosition(cam.getLocation());
        flashLight2.setDirection(cam.getDirection());
        rootNode.addLight(flashLight2);

        flashLightRim = new SpotLight();
        flashLightRim.setSpotRange(32f);
        flashLightRim.setSpotInnerAngle(23f * FastMath.DEG_TO_RAD);
        flashLightRim.setSpotOuterAngle(24f * FastMath.DEG_TO_RAD);
        flashLightRim.setColor(ColorRGBA.Gray.mult(0.375f));
        flashLightRim.setPosition(cam.getLocation());
        flashLightRim.setDirection(cam.getDirection());
        rootNode.addLight(flashLightRim);

        SpotLightShadowRenderer slsr1 = new SpotLightShadowRenderer(assetManager,
            SHADOWMAP_SIZE);
        slsr1.setLight(flashLight1);
        viewPort.addProcessor(slsr1);
        SpotLightShadowFilter slsf1 = new SpotLightShadowFilter(assetManager,
            SHADOWMAP_SIZE);
        slsf1.setLight(flashLight1);
        slsf1.setEnabled(true);
        FilterPostProcessor fpp1 = new FilterPostProcessor(assetManager);
        fpp1.addFilter(slsf1);
        viewPort.addProcessor(fpp1);

        SpotLightShadowRenderer slsr2 = new SpotLightShadowRenderer(assetManager,
            SHADOWMAP_SIZE);
        slsr2.setLight(flashLight2);
        viewPort.addProcessor(slsr2);
        SpotLightShadowFilter slsf2 = new SpotLightShadowFilter(assetManager,
            SHADOWMAP_SIZE);
        slsf2.setLight(flashLight2);
        slsf2.setEnabled(true);
        FilterPostProcessor fpp2 = new FilterPostProcessor(assetManager);
        fpp2.addFilter(slsf2);
        viewPort.addProcessor(fpp2);

        SpotLightShadowRenderer slsrRim = new SpotLightShadowRenderer(assetManager,
            SHADOWMAP_SIZE);
        slsrRim.setLight(flashLightRim);
        viewPort.addProcessor(slsrRim);
        SpotLightShadowFilter slsfRim = new SpotLightShadowFilter(assetManager,
            SHADOWMAP_SIZE);
        slsfRim.setLight(flashLightRim);
        slsfRim.setEnabled(true);
        FilterPostProcessor fppRim = new FilterPostProcessor(assetManager);
        fppRim.addFilter(slsfRim);
        viewPort.addProcessor(fppRim);
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
        inputManager.addMapping("Lean", new KeyTrigger(KeyInput.KEY_LSHIFT));
        inputManager.addMapping("PointerAction",
            new KeyTrigger(KeyInput.KEY_SPACE),
            new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(this, "Left");
        inputManager.addListener(this, "Right");
        inputManager.addListener(this, "Up");
        inputManager.addListener(this, "Down");
        inputManager.addListener(this, "Lean");
        inputManager.addListener(this, "PointerAction");
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
        } else if (binding.equals("Lean") && !up && !down) {
            lean = isPressed;
        } else if (binding.equals("PointerAction") && !isPressed) {
            //list of collisions from raycasting is stored in here
            CollisionResults results = new CollisionResults();
            Ray ray = new Ray(cam.getLocation(), cam.getDirection());

            //collisions being stored in results
            doorNode.collideWith(ray, results);
            //wallNode.collideWith(ray, results);
            //openNode.collideWith(ray, results);

            if(results.size() > 0){
                CollisionResult closest = results.getClosestCollision();

                String hit = closest.getGeometry().getName();
                System.out.println("Pointer picked " + hit + ".");

                float dist = closest.getDistance();
                if(dist < 10f){
                    System.out.println("Can perform an action.");
                }
                else{
                    System.out.println("Too far.");
                }
            }
            else{
                System.out.println("Nothing.");
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
