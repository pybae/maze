package maze;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;
import com.jme3.util.TangentBinormalGenerator;

/**
 * Door Entity used to represent the doors placed between rooms
 * and hall. Door enitity may have custom mesh, or texture.
 * Depending on texture we'll have to sundivide the maze unit to fit a door
 * reasonable size in. It should not be full width/height of an
 * maze entity.
 */
public class DoorEntity implements MazeEntity {
    private float width;
    private float height;
    private float width1;
    private float height1;
    private float width2;
    private float height2;
    private float doorWidth;
    private float doorHeight;
    public static final float DOOR_LENGTH = 1f;

    public DoorEntity(float w, float h) {
        width = w;
        height = h;
        width1 = width/8;
        width2 = width/2;
        height1 = 3*height/8;
        height2 = height/8;
        doorWidth = width/4;
        doorHeight = 3*height/8;
    }

    public float getWidth() {
        return width;
    }

    public float getLength() {
        return DOOR_LENGTH;
    }

    public float getHeight() {
        return height;
    }

    public void renderObject(Vector3f loc, Node rootNode,
                             AssetManager assetManager,
                             PhysicsSpace physicsSpace) {
    }

    public void renderObject(Vector3f loc, Node rootNode, Node wallNode,
                             AssetManager assetManager,
                             PhysicsSpace physicsSpace,
                             boolean orientation){
        Box door = new Box(doorWidth, doorHeight, DOOR_LENGTH / 2);
        Geometry doorBox = new Geometry("Door", door);
        doorBox.setShadowMode(ShadowMode.CastAndReceive);
        TangentBinormalGenerator.generate(door);
        doorBox.setLocalTranslation(new Vector3f(loc.x + 2*width1 + doorWidth,
                                             loc.y + doorHeight,
                                             loc.z + 16/ 2));
        Material matDoor = new Material(assetManager,
          "Common/MatDefs/Light/Lighting.j3md");

        // This asset is a 3 by 2 texture (3000 x 2000) px
        matDoor.setTexture("DiffuseMap",
                     assetManager.loadTexture("Textures/Terrain/Door/door_d.png"));

        matDoor.setTexture("NormalMap",
                     assetManager.loadTexture("Textures/Terrain/Door/door_n.png"));

        matDoor.setColor("Diffuse", ColorRGBA.White);
        matDoor.setColor("Specular", ColorRGBA.White);
        matDoor.setFloat("Shininess", 1f);
        doorBox.setMaterial(matDoor);
        rootNode.attachChild(doorBox);

        Box b1 = new Box(width1, height1, DOOR_LENGTH / 2);
        Box b2 = new Box(width2, height2, DOOR_LENGTH / 2);
        Geometry box1 = new Geometry("Wall", b1);
        Geometry box2 = new Geometry("Wall", b2);
        Geometry box3 = new Geometry("Wall", b1);
        box1.setShadowMode(ShadowMode.CastAndReceive);
        box2.setShadowMode(ShadowMode.CastAndReceive);
        box3.setShadowMode(ShadowMode.CastAndReceive);
        TangentBinormalGenerator.generate(b1);
        TangentBinormalGenerator.generate(b2);
        box1.setLocalTranslation(new Vector3f(loc.x + width1,
                                             loc.y + height1,
                                             loc.z + 16/ 2));
        box2.setLocalTranslation(new Vector3f(loc.x + width2,
                                             loc.y + height - height2,
                                             loc.z + 16/ 2));
        box3.setLocalTranslation(new Vector3f(loc.x + width - width1,
                                             loc.y + height1,
                                             loc.z + 16/ 2));
        Material mat = new Material(assetManager,
           "Common/MatDefs/Light/Lighting.j3md");

        // This asset is a 3 by 2 texture (3000 x 2000) px
        Texture texture = assetManager.loadTexture("Textures/Terrain/Wall/moreBricks_d.jpg");
        texture.setWrap(Texture.WrapMode.Repeat);

        Texture bTexture = assetManager.loadTexture("Textures/Terrain/Wall/moreBricks_n.png");
        bTexture.setWrap(Texture.WrapMode.Repeat);

        box1.getMesh().scaleTextureCoordinates(new Vector2f((float) Math.ceil(width1 / 1000),
                                                           (float) Math.ceil(height1 / 1000)));
        box2.getMesh().scaleTextureCoordinates(new Vector2f((float) Math.ceil(width2 / 1000),
                                                          (float) Math.ceil(height2 / 1000)));
        box3.getMesh().scaleTextureCoordinates(new Vector2f((float) Math.ceil(width1 / 1000),
                                                           (float) Math.ceil(height1/ 1000)));

        mat.setTexture("DiffuseMap", texture);

        mat.setTexture("NormalMap", bTexture);

        mat.setColor("Diffuse", ColorRGBA.White);
        mat.setColor("Specular", ColorRGBA.White);
        mat.setFloat("Shininess", 1f);
        box1.setMaterial(mat);
        box2.setMaterial(mat);
        box3.setMaterial(mat);
        wallNode.attachChild(box1);
        wallNode.attachChild(box2);
        wallNode.attachChild(box3);

        box1.addControl(new RigidBodyControl(0));
        physicsSpace.addAll(box1);
        box2.addControl(new RigidBodyControl(0));
        physicsSpace.addAll(box2);
        box3.addControl(new RigidBodyControl(0));
        physicsSpace.addAll(box3);
        doorBox.addControl(new RigidBodyControl(0));
        physicsSpace.addAll(doorBox);
    }
}
