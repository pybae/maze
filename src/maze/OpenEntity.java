package maze;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;

/**
 * represents open space in a maze, such as those in the middle of the maze
 * note that this is different from EmptyEntity, which users cannot walk on (or see)
 */
public class OpenEntity implements MazeEntity {
    private float width;
    private float length;
    public static final float FLOOR_HEIGHT = 0.1f;
    public static final float TEXTURE_WIDTH = 50.0f;
    public static final float TEXTURE_LENGTH = 50.0f;

    public OpenEntity(float w, float l) {
        width = w;
        length = l;
    }

    public float getWidth() {
        return width;
    }

    public float getLength() {
        return length;
    }

    public float getHeight() {
        return FLOOR_HEIGHT;
    }

    public void renderObject(Vector3f loc, Node rootNode,
                             AssetManager assetManager,
                             PhysicsSpace physicsSpace) {
        /**
         * documentation for the Box constructor can be found here:
         * http://javadoc.jmonkeyengine.org/com/jme3/scene/shape/Box.html#Box(float, float, float)
         */

        Box b = new Box(width/2, FLOOR_HEIGHT/2, length/2);
        Geometry box = new Geometry("Box", b);
        box.setLocalTranslation(new Vector3f(loc.x + width / 2,
                                             loc.y + FLOOR_HEIGHT / 2,
                                             loc.z + length / 2));

        Material mat = new Material(assetManager,
                                    "Common/MatDefs/Misc/Unshaded.j3md");

        Texture texture = assetManager.loadTexture("Textures/Terrain/Floor/DarkFloor.jpg");
        texture.setWrap(Texture.WrapMode.Repeat);

        box.getMesh().scaleTextureCoordinates(new Vector2f((float) Math.ceil(width / TEXTURE_WIDTH),
                                                           (float) Math.ceil(length / TEXTURE_LENGTH)));

        mat.setTexture("ColorMap", texture);
        box.setMaterial(mat);
        rootNode.attachChild(box);

        // make the object static
        box.addControl(new RigidBodyControl(0));
        physicsSpace.addAll(box);
    }
}
