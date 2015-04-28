package maze;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

/**
 * WallEntity class, which represents a wall
 * This has a static width, but varying length (x) and height (z)
 */
public class WallEntity extends Maze implements MazeEntity {
    private float width;
    private float height;
    private Spatial sceneModel;
  private BulletAppState bulletAppState;
  private RigidBodyControl landscape;

    public static final float WALL_LENGTH = 0.1f;

    public WallEntity(float w, float h) {
        width = w;
        height = h;
    }

    public float getWidth() {
        return width;
    }

    public float getLength() {
        return WALL_LENGTH;
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

        
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        Box b = new Box(width/2, height/2, WALL_LENGTH / 2);
        Geometry box = new Geometry("Box", b);
        box.setLocalTranslation(new Vector3f(loc.x + width / 2,
                                             loc.y + height / 2,
                                             loc.z + WALL_LENGTH / 2));

        Material mat = new Material(assetManager,
                                    "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        box.setMaterial(mat);
        sceneModel = box;
       CollisionShape sceneShape = CollisionShapeFactory.createMeshShape( sceneModel);
       landscape = new RigidBodyControl(sceneShape, 0);
       sceneModel.addControl(landscape);
        
        rootNode.attachChild(sceneModel);
        bulletAppState.getPhysicsSpace().add(landscape);
    }
}