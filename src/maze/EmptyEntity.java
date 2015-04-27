package maze;

/**
 * represents empty space in a maze, such as those in the boundaries
 * note that this is different from OpenEntity, which represents an open path
 * available to the user to walk on
 */
public class EmptyEntity implements MazeEntity {
    private int width;
    private int length;
    private int height;

    public EmptyEntity(int w, int l, int h) {
        width = w;
        length = l;
        height = h;
    }

    public int getWidth() {
        return width;
    }

    public int getLength() {
        return length;
    }

    public int getHeight() {
        return height;
    }

    public void renderObject(int x, int y, int z) {}
}
