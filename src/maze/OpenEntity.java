package maze;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

/**
 * represents open space in a maze, such as those in the middle of the maze
 * note that this is different from EmptyEntity, which users cannot walk on (or see)
 */
public class OpenEntity implements MazeEntity {
    private float width;
    private float length;
    private float height;

    public OpenEntity(float w, float l, float h) {
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
            AssetManager assetManager) {
        /**
         * documentation for the Box constructor can be found here:
         * http://javadoc.jmonkeyengine.org/com/jme3/scene/shape/Box.html#Box(float, float, float)
         */

        Box b = new Box(width/2, length/2, height/2);
        Geometry box = new Geometry("Box", b);
        box.setLocalTranslation(new Vector3f(loc.x + width / 2,
                                             loc.y + length / 2,
                                             loc.z + height / 2));

        Material mat = new Material(assetManager,
                                    "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        box.setMaterial(mat);

        rootNode.attachChild(box);
    }
}
