package maze;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 * represents empty space in a maze, such as those in the boundaries
 * note that this is different from OpenEntity, which represents an open path
 * available to the user to walk on
 */
public class EmptyEntity implements MazeEntity {
    private float width;
    private float length;
    private float height;

    public EmptyEntity(float w, float l, float h) {
        width = w;
        length = l;
        height = h;
    }

    public float getWidth() {
        return width;
    }

    public float getLength() {
        return length;
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
                             boolean orientation) {
    }
}
