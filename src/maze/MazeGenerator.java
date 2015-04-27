package maze;

import java.util.ArrayList;
import java.util.Random;
import java.awt.Rectangle;

public class MazeGenerator {
    public enum Direction { UP, DOWN, LEFT, RIGHT }
    private final int MAX_ADD_ROOMS_TRIES = 10;

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
        currentRegion = -1;

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
            carve(m, r, 0, State.WALL);
            carve(m, r, width - 1, State.WALL);
        }

        // Set top and bottom
        for(int c = 1; c < width; c++) {
            carve(m, 0, c, State.WALL);
            carve(m, height - 1, c, State.WALL);
        }
    }

    // Place rooms in the maze
    private void addRooms(MazeLayout m) {
        for(int i = 0; i < MAX_ADD_ROOMS_TRIES; i++) {
            rooms.clear();

            for(int j = 0; j < roomTries; j++) {
                Random rand = new Random();

                int size = (rand.nextInt(maxRoomSize - minRoomSize + 1) + minRoomSize) * 2 + 1;
                int rectangularity = rand.nextInt(1 + size / 2) * 2;
                int roomWidth = size;
                int roomHeight = size;

                if(rand.nextInt(2) % 2 == 0) {
                    roomWidth += rectangularity;
                } else {
                    roomHeight += rectangularity;
                }

                int x = rand.nextInt((width - roomWidth) / 2) * 2 + 1;
                int y = rand.nextInt((height - roomHeight) / 2) * 2 + 1;

                Rectangle room = new Rectangle(x, y, roomWidth, roomHeight);

                boolean overlaps = false;
                for(Rectangle other : rooms) {
                    if(room.intersects(other)) {
                        overlaps = true;
                        break;
                    }
                }

                if(overlaps) continue;

                rooms.add(room);

                startRegion();

                for(int r = y; r < y + roomHeight; r++) {
                    for(int c = x; c < x + roomWidth; c++) {
                        carve(m, r, c, State.OPEN);
                    }
                }

                // encircle the room with walls
                for(int r = y - 1; r < y + roomHeight + 1; r++) {
                    carve(m, r, x - 1, State.WALL);
                    carve(m, r, x + roomWidth, State.WALL);
                }
                for(int c = x - 1; c < x + roomWidth + 1; c++) {
                    carve(m, y - 1, c, State.WALL);
                    carve(m, y + roomHeight, c, State.WALL);
                }
            }

            if(rooms.size() >= minRooms) {
                break;
            }
        }
    }
    
    // Implements "growing tree" algorithm to build the maze
    private void growMaze(MazeLayout m, int r, int c) {}

    // Ensure that all regions are connected
    private void connectRegions(MazeLayout m) {}

    // Move on to the next region
    private void startRegion() {
        currentRegion++;
    }

    // Adds a junction at a specified location
    private void addJunction(MazeLayout m, int r, int c) {}

    // Determines whether an opening can be carved from the cell at the
    //  specified location to the adjacent cell facing the specified direction
    private boolean canCarve(MazeLayout m, int r, int c, Direction d) {
        return false;
    }

    // Set the state at the specified location
    private void carve(MazeLayout m, int r, int c, State s) {
        m.setState(r, c, s);
        regions[r][c] = currentRegion;
    }
}