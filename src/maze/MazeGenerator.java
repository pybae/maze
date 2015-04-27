import java.util.ArrayList;
import java.awt.Rectangle;

public class MazeGenerator {
    public enum Direction { UP, DOWN, LEFT, RIGHT }

    private int width;
    private int height;
    private int windiness;
    private int minRoomSize;
    private int maxRoomSize;
    private int roomTries;
    private int minRooms;

    private ArrayList<Rectangle> rooms;
    private int[][] regions;
    private int currentRegion;

    public MazeGenerator(int widthIn, int heightIn, int windinessIn,
                         int minRoomSizeIn, int maxRoomSizeIn,
                         int roomTriesIn, int minRoomsIn) {
        width = widthIn;
        height = heightIn;
        windiness = windinessIn;
        minRoomSize = minRoomSizeIn;
        maxRoomSize = maxRoomSizeIn;
        roomTries = roomTriesIn;
        minRooms = minRoomsIn;

        currentRegion = -1;
    }

    // Using the width, height, and windiness of the passages, this method will
    //  return a MazeLayout object corresponding to the generated maze
    public MazeLayout generate() {
        if(width % 2 == 0 || height % 2 == 0) {
            return null;
        }

        MazeLayout layout = new MazeLayout(width, height);
        rooms = new ArrayList<Rectangle>();
        regions = new int[height][width];

        defineWalls(layout);

        addRooms(layout);

        for(int r = 1; r < height; r += 2) {
            for(int c = 1; c < width; c += 2) {
                if(layout.getState(r, c) == State.NOT_SET) {
                    growMaze(layout, r, c);
                }
            }
        }

        connectRegions(layout);

        return layout;
    }

    // Define the perimeter as walls
    private void defineWalls(MazeLayout m) {
        // Set left and right
        for(int r = 0; r < height; r++) {
            m.setState(r, 0, State.WALL);
            m.setState(r, width - 1, State.WALL);
        }

        // Set top and bottom
        for(int c = 1; c < width; c++) {
            m.setState(0, c, State.WALL);
            m.setState(height - 1, c, State.WALL);
        }
    }

    // Place rooms in the maze
    private void addRooms(MazeLayout m) {}
    
    // Implements "growing tree" algorithm to build the maze
    private void growMaze(MazeLayout m, int r, int c) {}

    // Ensure that all regions are connected
    private void connectRegions(MazeLayout m) {}

    // Adds a junction at a specified location
    private void addJunction(int r, int c) {}

    // Determines whether an opening can be carved from the cell at the
    //  specified location to the adjacent cell facing the specified direction
    private boolean canCarve(int r, int c, Direction d) {
        return false;
    }

    // Set the state at the specified location
    private void carve(int r, int c, State s) {}
}