package maze;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.objects.PhysicsCharacter;
import com.jme3.light.AmbientLight;
import com.jme3.light.SpotLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.shadow.SpotLightShadowFilter;
import com.jme3.shadow.SpotLightShadowRenderer;

/**
 * This is the class that encapsulated all of the character's actions
 * The character is represented by a PhysicsCharacter and the flyCam from SimpleApplication
 * The character has a flash light as well, which is done in the initFlashlight method
 */
public class Player {
    private Node rootNode;
    private AssetManager assetManager;
    private Camera cam;
    private PhysicsSpace physicsSpace;
    private ViewPort viewPort;

    private PhysicsCharacter physicsCharacter;
    private SpotLight flashlightInner;
    private SpotLight flashlightMiddle;
    private SpotLight flashlightOuter;

    private Vector3f walkDirection = new Vector3f(0, 0, 0);
    private boolean left, right, up, down, sprint, optSprint;
    private float headBob = 0;
    private float lightSway = 0;

    public static final float flashlightInnerIntensity = 1.5f;
    public static final float flashlightMiddleIntensity = 1.25f;
    public static final float flashlightOuterIntensity = 0.75f;

    public static final float PLAYER_SPEED = 5.0f;
    public static final float PLAYER_SPRINT = 3.0f;
    public static final int SHADOWMAP_SIZE = 1024;

    /**
     * the constructor takes in the flyCam from SimpleApplication
     * we modify this value in the class, but never reassign it
     */
    public Player(Node rootNode, AssetManager assetManager, Camera cam, PhysicsSpace physicsSpace, ViewPort viewPort) {
        this.rootNode = rootNode;
        this.assetManager = assetManager;
        this.cam = cam;
        this.physicsSpace = physicsSpace;
        this.viewPort = viewPort;

        initPhysicsCharacter();
        initFlashlight();
    }

    /**
     * a helper method for the constructor that initializes the physics character
     */
    private void initPhysicsCharacter() {
        // the player collision model is done through a sphere
        physicsCharacter = new PhysicsCharacter(new SphereCollisionShape(3.0f), 0.1f);

        // player does not jump
        physicsCharacter.setJumpSpeed(0);
        physicsCharacter.setFallSpeed(20);
        physicsCharacter.setGravity(30);

        physicsSpace.add(physicsCharacter);
    }

    /**
     * a helper method for initializing the flashlight
     */
    private void initFlashlight() {
        flashlightInner = new SpotLight();
        flashlightInner.setSpotRange(38);
        flashlightInner.setSpotInnerAngle(0f * FastMath.DEG_TO_RAD);
        flashlightInner.setSpotOuterAngle(8f * FastMath.DEG_TO_RAD);
        flashlightInner.setColor(ColorRGBA.White.mult(flashlightInnerIntensity));
        rootNode.addLight(flashlightInner);

        flashlightMiddle = new SpotLight();
        flashlightMiddle.setSpotRange(32);
        flashlightMiddle.setSpotInnerAngle(25f * FastMath.DEG_TO_RAD);
        flashlightMiddle.setSpotOuterAngle(34f * FastMath.DEG_TO_RAD);
        flashlightMiddle.setColor(ColorRGBA.Orange.mult(flashlightMiddleIntensity));
        rootNode.addLight(flashlightMiddle);

        flashlightOuter = new SpotLight();
        flashlightOuter.setSpotRange(28);
        flashlightOuter.setSpotInnerAngle(20f * FastMath.DEG_TO_RAD);
        flashlightOuter.setSpotOuterAngle(22f * FastMath.DEG_TO_RAD);
        flashlightOuter.setColor(ColorRGBA.Gray.mult(flashlightOuterIntensity));
        rootNode.addLight(flashlightOuter);

        initShadows(flashlightInner);
        initShadows(flashlightMiddle);
        initShadows(flashlightOuter);
    }

    /**
     * a helper method to initialize the shadows for each light source
     */
    private void initShadows(SpotLight flashlight) {
        SpotLightShadowRenderer slsr = new SpotLightShadowRenderer(assetManager, SHADOWMAP_SIZE);
        slsr.setLight(flashlight);
        viewPort.addProcessor(slsr);
        SpotLightShadowFilter slsf = new SpotLightShadowFilter(assetManager, SHADOWMAP_SIZE);
        slsf.setLight(flashlight);
        slsf.setEnabled(true);
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        fpp.addFilter(slsf);
        viewPort.addProcessor(fpp);
    }

    /**
     * set the spawn point of the player
     * we separate this from the Player constructor, since the Maze needs to be generated after the player
     */
    public void setSpawn(int x, int z) {
        physicsCharacter.setPhysicsLocation(new Vector3f(x, 3, z));
    }

    /**
     * the key handler for the player
     */
    public boolean onAction(String binding, boolean isPressed, float tpf) {
        if (binding.equals("Left") && !isPressed) {
            left = false;
        } else if (binding.equals("Left")) {
            left = isPressed;
        } else if (binding.equals("Right") && !isPressed) {
            right = false;
        } else if (binding.equals("Right")) {
            right = isPressed;
        } else if (binding.equals("Up") && !isPressed) {
            up = false;
            sprint = false;
        } else if (binding.equals("Up")) {
            up = isPressed;
        } else if (binding.equals("Down") && !isPressed) {
            down = false;
        } else if (binding.equals("Down")) {
            down = isPressed;
        } else if (binding.equals("Sprint") && !isPressed) {
            sprint = false;
        } else if (binding.equals("Sprint")) {
            sprint = isPressed;
        } else {
            return false;
        }

        return true;
    }

    /**
     * get the location of the player
     */
    public Vector3f getPosition() {
        return physicsCharacter.getPhysicsLocation();
    }

    /**
     * the update function for the player
     * this is called in simpleUpdate in Maze
     */
    public void update(float tpf) {
        Vector3f camDir = cam.getDirection();
        Vector3f camLeft = cam.getLeft();
        Vector3f camLoc = cam.getLocation();
        float speed = PLAYER_SPEED;

        Vector3f plLocation = physicsCharacter.getPhysicsLocation();
        plLocation.setY(plLocation.getY() + 7f);

        Vector3f lightDir = camDir;

        walkDirection.set(0, 0, 0);

        if (sprint) {
            if (headBob < 360){
                headBob += 10*tpf;
                plLocation.setY(plLocation.getY() + 0.2f*FastMath.sin(headBob));
                plLocation.setX(plLocation.getX() + 0.1f*FastMath.sin(headBob));
            }
            else {
                headBob = 0;
            }
            if (lightSway < 360){
                lightSway += 7*tpf;
                lightDir.setY(lightDir.getY() + 0.025f*FastMath.sin(lightSway));
                flashlightInner.setColor(ColorRGBA.White.mult(flashlightInnerIntensity + 0.2f*FastMath.sin(lightSway)));
                flashlightMiddle.setColor(ColorRGBA.Orange.mult(flashlightMiddleIntensity + 0.1f*FastMath.sin(lightSway)));
                flashlightOuter.setColor(ColorRGBA.Gray.mult(flashlightOuterIntensity + 0.2f*FastMath.sin(lightSway)));
            }
            else {
                lightSway = 0;
            }
            speed = PLAYER_SPRINT;
        }
        else {
            if(headBob != 0){
                headBob = 0;
            }
            if(lightSway != 0){
                lightSway = 0;
            }
            flashlightInner.setColor(ColorRGBA.White.mult(flashlightInnerIntensity));
            flashlightMiddle.setColor(ColorRGBA.Orange.mult(flashlightMiddleIntensity));
            flashlightOuter.setColor(ColorRGBA.Gray.mult(flashlightOuterIntensity));
        }
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

        // the user cannot gain vertical distance
        walkDirection.setY(0);

        physicsCharacter.setWalkDirection(walkDirection.divide(speed));
        flashlightInner.setPosition(camLoc);
        flashlightMiddle.setPosition(camLoc);
        flashlightOuter.setPosition(camLoc);

        flashlightInner.setDirection(lightDir);
        flashlightMiddle.setDirection(lightDir);
        flashlightOuter.setDirection(lightDir);

        cam.setLocation(plLocation);
    }
}
