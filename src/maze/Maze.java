package maze;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.objects.PhysicsCharacter;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.SpotLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.shadow.SpotLightShadowFilter;
import com.jme3.shadow.SpotLightShadowRenderer;

public class Maze extends SimpleApplication implements ActionListener {

    private BulletAppState bulletAppState;
    private PhysicsCharacter player;
    private SpotLight flashLight1, flashLight2, flashLightRim;
    private boolean left, right, up, down;
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
        mz.renderObject(new Vector3f(0, 0, 0),
                        rootNode,
                        assetManager,
                        getPhysicsSpace());
        MazeEntity mz1 = new WallEntity(16, 16);
        mz1.renderObject(new Vector3f(16, 0, 0),
                        rootNode,
                        assetManager,
                        getPhysicsSpace());
        MazeEntity mz2 = new WallEntity(16, 16);
        mz2.renderObject(new Vector3f(0, 16, 0),
                        rootNode,
                        assetManager,
                        getPhysicsSpace());
        MazeEntity mz3 = new WallEntity(16, 16);
        mz3.renderObject(new Vector3f(16, 16, 0),
                        rootNode,
                        assetManager,
                        getPhysicsSpace());

        OpenEntity oz = new OpenEntity(16, 16);
        oz.renderObject(new Vector3f(0, 0, WallEntity.WALL_LENGTH),
                        rootNode,
                        assetManager,
                        getPhysicsSpace());
        OpenEntity oz1 = new OpenEntity(16, 16);
        oz1.renderObject(new Vector3f(16, 0, WallEntity.WALL_LENGTH),
                        rootNode,
                        assetManager,
                        getPhysicsSpace());
        OpenEntity oz2 = new OpenEntity(16, 16);
        oz2.renderObject(new Vector3f(0, 0, 16 + WallEntity.WALL_LENGTH),
                        rootNode,
                        assetManager,
                        getPhysicsSpace());
        OpenEntity oz3 = new OpenEntity(16, 16);
        oz3.renderObject(new Vector3f(16, 0, 16 + WallEntity.WALL_LENGTH),
                        rootNode,
                        assetManager,
                        getPhysicsSpace());

        DoorEntity dz = new DoorEntity(16, 16);
        dz.renderObject(new Vector3f(0, 0, 16),
                        rootNode,
                        assetManager,
                        getPhysicsSpace());
        //test Room
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
        initCrossHair();

        generateMaze();
    }

    @Override
    public void simpleUpdate(float tpf) {
        Vector3f camDir = cam.getDirection();
        Vector3f camLeft = cam.getLeft();
        Vector3f camLoc = cam.getLocation();

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

        player.setWalkDirection(walkDirection.divide(PLAYER_SPEED));
        flashLight1.setPosition(camLoc);
        flashLight2.setPosition(camLoc);
        flashLightRim.setPosition(camLoc);

        flashLight1.setDirection(camDir);
        flashLight2.setDirection(camDir);
        flashLightRim.setDirection(camDir);

        cam.setLocation(player.getPhysicsLocation());
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
