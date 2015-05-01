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
    private static final float RENDER_DISTANCE = 2000.0f;
    private static final int SHADOWMAP_SIZE = 1024;

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
//        mz.renderObject(new Vector3f(0, 0, 0),
//                        wallNode,
//                        assetManager,
//                        getPhysicsSpace());
//        MazeEntity mz1 = new WallEntity(16, 16);
//        mz1.renderObject(new Vector3f(16, 0, 0),
//                        wallNode,
//                        assetManager,
//                        getPhysicsSpace());
//        MazeEntity mz2 = new WallEntity(16, 16);
//        mz2.renderObject(new Vector3f(0, 16, 0),
//                        wallNode,
//                        assetManager,
//                        getPhysicsSpace());
//        MazeEntity mz3 = new WallEntity(16, 16);
//        mz3.renderObject(new Vector3f(16, 16, 0),
//                        wallNode,
//                        assetManager,
//                        getPhysicsSpace());
//
        OpenEntity oz = new OpenEntity(16, 16);
//        oz.renderObject(new Vector3f(0, 0, WallEntity.WALL_LENGTH),
//                        openNode,
//                        assetManager,
//                        getPhysicsSpace());
//        OpenEntity oz1 = new OpenEntity(16, 16);
//        oz1.renderObject(new Vector3f(16, 0, WallEntity.WALL_LENGTH),
//                        openNode,
//                        assetManager,
//                        getPhysicsSpace());
//        OpenEntity oz2 = new OpenEntity(16, 16);
//        oz2.renderObject(new Vector3f(0, 0, 16 + WallEntity.WALL_LENGTH),
//                        openNode,
//                        assetManager,
//                        getPhysicsSpace());
//        OpenEntity oz3 = new OpenEntity(16, 16);
//        oz3.renderObject(new Vector3f(16, 0, 16 + WallEntity.WALL_LENGTH),
//                        openNode,
//                        assetManager,
//                        getPhysicsSpace());
//
        DoorEntity dz = new DoorEntity(16, 16);
//        dz.renderObject(new Vector3f(0, 0, 16),
//                        doorNode,
//                        assetManager,
//                        getPhysicsSpace());
        //test Room
        
        MazeGenerator generator = new MazeGenerator(61, 41, 100, 2, 6, 20, 1);
        MazeLayout layout = generator.generate();
        layout.print();
        
        
        for(int r = 0; r < layout.maze.length; r++) {
            for(int c = 0; c < layout.maze[0].length; c++) {
                if(layout.maze[r][c] == State.WALL) {
                    //System.out.print("* ");
                    mz.renderObject(new Vector3f(16*r, 0, 16*c),
                        wallNode,
                        assetManager,
                        getPhysicsSpace());
                } else if(layout.maze[r][c] == State.DOOR) {
                    //System.out.print("O ");
                } else if(layout.maze[r][c] == State.NOT_SET) {
                    //System.out.print("- ");
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
        player.setGravity(30);
        player.setPhysicsLocation(new Vector3f(5, 20, 5));
        getPhysicsSpace().add(player);
    }

    private void initLight() {
        flashLight1 = new SpotLight();
        flashLight1.setSpotRange(32f);
        flashLight1.setSpotInnerAngle(0f * FastMath.DEG_TO_RAD);
        flashLight1.setSpotOuterAngle(11f * FastMath.DEG_TO_RAD);
        flashLight1.setColor(ColorRGBA.White.mult(1.5f));
        flashLight1.setPosition(cam.getLocation());
        flashLight1.setDirection(cam.getDirection());
        rootNode.addLight(flashLight1);

        flashLight2 = new SpotLight();
        flashLight2.setSpotRange(18f);
        flashLight2.setSpotInnerAngle(25f * FastMath.DEG_TO_RAD);
        flashLight2.setSpotOuterAngle(39f * FastMath.DEG_TO_RAD);
        flashLight2.setColor(ColorRGBA.White.mult(1.25f));
        flashLight2.setPosition(cam.getLocation());
        flashLight2.setDirection(cam.getDirection());
        rootNode.addLight(flashLight2);

        flashLightRim = new SpotLight();
        flashLightRim.setSpotRange(24f);
        flashLightRim.setSpotInnerAngle(23f * FastMath.DEG_TO_RAD);
        flashLightRim.setSpotOuterAngle(24f * FastMath.DEG_TO_RAD);
        flashLightRim.setColor(ColorRGBA.Gray.mult(0.25f));
        flashLightRim.setPosition(cam.getLocation());
        flashLightRim.setDirection(cam.getDirection());
        rootNode.addLight(flashLightRim);

        //Paul's Decision -> This is the Shadow Renderer however we have the
        //SpotLight positioned right where the camera is looking so we will not
        //generate any actual shadows therefore I leave it up to you to decide
        //if we offset the lightsource by a bit and use shadows or not.
        /*
        SpotLightShadowRenderer slsr = new SpotLightShadowRenderer(assetManager,
            SHADOWMAP_SIZE);
        slsr.setLight(flashLight1);
        viewPort.addProcessor(slsr);

        SpotLightShadowFilter slsf = new SpotLightShadowFilter(assetManager,
            SHADOWMAP_SIZE);
        slsf.setLight(flashLight1);
        slsf.setEnabled(true);
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        fpp.addFilter(slsf);
        viewPort.addProcessor(fpp);
        */
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
