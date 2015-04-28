package maze;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
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
    public static final float WALL_HEIGHT = 0.1f;

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
        return WALL_HEIGHT;
    }

    public void renderObject(Vector3f loc, Node rootNode,
                             AssetManager assetManager,
                             PhysicsSpace physicsSpace) {
        /**
         * documentation for the Box constructor can be found here:
         * http://javadoc.jmonkeyengine.org/com/jme3/scene/shape/Box.html#Box(float, float, float)
         */

        Box b = new Box(width/2, WALL_HEIGHT/2, length/2);
        Geometry box = new Geometry("Box", b);
        box.setLocalTranslation(new Vector3f(loc.x + width / 2,
                                             loc.y + WALL_HEIGHT / 2,
                                             loc.z + length / 2));

        Material mat = new Material(assetManager,
                                    "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);
        box.setMaterial(mat);

        rootNode.attachChild(box);

        CollisionShape sceneShape = CollisionShapeFactory.createBoxShape(box);
        RigidBodyControl landscape = new RigidBodyControl(sceneShape, 0);

        box.addControl(landscape);
        physicsSpace.add(landscape);
    }
}
