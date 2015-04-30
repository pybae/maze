package maze;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

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
    public static final float DOOR_LENGTH = 0.1f;

    public DoorEntity(float w, float h) {
        width = w;
        height = h;
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
                             PhysicsSpace physicsSpace){
        //temporary, until mouse picking and animation works
        Box b = new Box(width/2, height/2, DOOR_LENGTH / 2);
        Geometry box = new Geometry("Door", b);
        box.setShadowMode(ShadowMode.CastAndReceive);
        box.setLocalTranslation(new Vector3f(loc.x + width / 2,
                                             loc.y + height / 2,
                                             loc.z + DOOR_LENGTH / 2));
        Material mat = new Material(assetManager,
            "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        box.setMaterial(mat);
        rootNode.attachChild(box);

        box.addControl(new RigidBodyControl(0));
        physicsSpace.addAll(box);
    }
}
