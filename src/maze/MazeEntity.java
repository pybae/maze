package maze;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 * The class representing an Entity in the Maze
 * a Maze is defined by a 2D grid of MazeEntities
 * Therefore, this Maze should be able to render itself and all of its variables
 * given only the x, y, z coordinates of the entity.
 */
public interface MazeEntity {
    public void renderObject(Vector3f loc, Node rootNode,
                             AssetManager assetManager);
    public float getWidth();
    public float getLength();
    public float getHeight();
}
