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
import com.jme3.util.TangentBinormalGenerator;
import com.jme3.texture.Texture;

/**
 * WallEntity class, which represents a wall
 * This has a static width, but varying length (x) and height (z)
 */
public class WallEntity extends Maze implements MazeEntity {
    private float width;
    private float height;
    public static final float WALL_LENGTH = 16.0f;
    public static final float TEXTURE_WIDTH = 50.0f;
    public static final float TEXTURE_HEIGHT = 30.0f;


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
                             AssetManager assetManager,
                             PhysicsSpace physicsSpace) {
        /**
         * documentation for the Box constructor can be found here:
         * http://javadoc.jmonkeyengine.org/com/jme3/scene/shape/Box.html#Box(float, float, float)
         */


        Box b = new Box(width/2, height/2, WALL_LENGTH / 2);
        Geometry box = new Geometry("Wall", b);
        box.setShadowMode(ShadowMode.CastAndReceive);
        TangentBinormalGenerator.generate(b);
        box.setLocalTranslation(new Vector3f(loc.x + width / 2,
                                             loc.y + height / 2,
                                             loc.z + WALL_LENGTH / 2));

        Material mat = new Material(assetManager,
            "Common/MatDefs/Light/Lighting.j3md");

        // This asset is a 3 by 2 texture (3000 x 2000) px
        mat.setTexture("DiffuseMap",
                       assetManager.loadTexture("Textures/Terrain/Wall/moreBricks_d.jpg"));

        mat.setTexture("NormalMap",
                       assetManager.loadTexture("Textures/Terrain/Wall/moreBricks_n.png"));

        mat.setColor("Diffuse", ColorRGBA.White);
        mat.setColor("Specular", ColorRGBA.White);
        mat.setFloat("Shininess", 1f);
        box.setMaterial(mat);

        rootNode.attachChild(box);

        // make the object static
        box.addControl(new RigidBodyControl(0));
        physicsSpace.addAll(box);
    }

    public void renderObject(Vector3f loc, Node rootNode, Node wallNode,
                             AssetManager assetManager,
                             PhysicsSpace physicsSpace,
                             boolean orientation) {
    }
}
