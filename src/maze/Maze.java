package maze;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 * The main game state class
 * This class is a singleton and should therefore only be used through the
 * getInstance method
 *
 * You can generate a new Maze by invoking the public function generateMaze on
 * the instance of the Maze. Note that this method is inherently destructive
 * and should be used with care.
 */
public class Maze extends SimpleApplication {

    public Maze() {
    }

    public void generateMaze() {
        /** @zane
         * you can write the implementation for generating the Maze here.
         * The maze itself should be a 2D array of MazeEntities
         * we can initialize which coordinates to pass into the entities
         */
        MazeEntity mz = new OpenEntity(1, 1, 1);
        mz.renderObject(new Vector3f(0, 0, 0),
                        rootNode,
                        assetManager);
        MazeEntity mz2 = new OpenEntity(1, 1, 1);
        mz2.renderObject(new Vector3f(1, 0, 0),
                         rootNode,
                         assetManager);
        MazeEntity mz3 = new OpenEntity(1, 1, 1);
        mz3.renderObject(new Vector3f(1, 1, 0),
                         rootNode,
                         assetManager);
    }

    @Override
    public void simpleInitApp() {
        generateMaze();
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

}
