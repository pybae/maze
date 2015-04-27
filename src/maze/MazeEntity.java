package maze;

/**
 * The class representing an Entity in the Maze
 * a Maze is defined by a 2D grid of MazeEntities
 * Therefore, this Maze should be able to render itself and all of its variables
 * given only the x, y, z coordinates of the entity.
 */
public interface MazeEntity {
    public void renderObject(int x, int y, int z);
    public int getWidth();
    public int getLength();
    public int getHeight();
}
