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
        rooms = new ArrayList<Rectangle>();
        regions = new int[height][width];

        return new MazeLayout(width, height);
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
    private void carve(int r, int c, MazeLayout.State s) {}
}