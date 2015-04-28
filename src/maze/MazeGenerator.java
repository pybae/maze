package maze;

import java.util.*;
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

        for(int r = 1; r < height - 1; r += 2) {
            for(int c = 1; c < width - 1; c += 2) {
                if(layout.getState(r, c) == State.NOT_SET) {
                    growMaze(layout, new Position(r, c));
                }
            }
        }

        connectRegions(layout);

        setAll(layout);

        return layout;
    }

    // Define the perimeter as walls
    private void defineWalls(MazeLayout m) {
        // Set left and right
        for(int r = 0; r < height; r++) {
            carve(m, new Position(r, 0), State.WALL);
            carve(m, new Position(r, width - 1), State.WALL);
        }

        // Set top and bottom
        for(int c = 1; c < width; c++) {
            carve(m, new Position(0, c), State.WALL);
            carve(m, new Position(height - 1, c), State.WALL);
        }
    }

    private void setAll(MazeLayout m) {
        for(int r = 1; r < height - 1; r++) {
            for(int c = 1; c < width - 1; c++) {
                if(m.getState(r, c) == State.NOT_SET) {
                    carve(m, new Position(r, c), State.WALL);
                }
            }
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
                        carve(m, new Position(r, c), State.OPEN);
                    }
                }

                // encircle the room with walls
                for(int r = y - 1; r < y + roomHeight + 1; r++) {
                    carve(m, new Position(r, x - 1), State.WALL);
                    carve(m, new Position(r, x + roomWidth), State.WALL);
                }
                for(int c = x - 1; c < x + roomWidth + 1; c++) {
                    carve(m, new Position(y - 1, c), State.WALL);
                    carve(m, new Position(y + roomHeight, c), State.WALL);
                }
            }

            if(rooms.size() >= minRooms) {
                break;
            }
        }
    }
    
    // Implements "growing tree" algorithm to build the maze
    private void growMaze(MazeLayout m, Position pos) {
        ArrayList<Position> cells = new ArrayList<Position>();
        Direction lastDirection = null;

        startRegion();
        carve(m, pos, State.OPEN);

        cells.add(pos);
        while(!cells.isEmpty()) {
            Position current = cells.get(cells.size() - 1);

            ArrayList<Direction> openCells = new ArrayList<Direction>();
            for(Direction d : Direction.values()) {
                if(canCarve(m, current, d)) {
                    openCells.add(d);
                }
            }

            if(!openCells.isEmpty()) {
                Random rand = new Random();
                Direction d;

                if(openCells.contains(lastDirection) && rand.nextInt(100) > windiness) {
                    d = lastDirection;
                } else {
                    d = openCells.get(rand.nextInt(openCells.size()));
                }

                switch(d) {
                    case UP:    current.r -= 1;
                                carve(m, current, State.OPEN);
                                current.r -= 1;
                                carve(m, current, State.OPEN);
                                break;
                    case LEFT:  current.c -= 1;
                                carve(m, current, State.OPEN);
                                current.c -= 1;
                                carve(m, current, State.OPEN);
                                break;
                    case DOWN:  current.r += 1;
                                carve(m, current, State.OPEN);
                                current.r += 1;
                                carve(m, current, State.OPEN);
                                break;
                    case RIGHT: current.c += 1;
                                carve(m, current, State.OPEN);
                                current.c += 1;
                                carve(m, current, State.OPEN);
                                break;
                    default:    return;
                }

                cells.add(current);
                lastDirection = d;
            } else {
                cells.remove(cells.size() - 1);

                lastDirection = null;
            }
        }
    }

    // Ensure that all regions are connected
    private void connectRegions(MazeLayout m) {
        // HashMap<Position, Set<int>> connectorRegions = new HashMap<Position, Set<int>>();


    }

    // Move on to the next region
    private void startRegion() {
        currentRegion++;
    }

    // Adds a junction at a specified location
    private void addJunction(MazeLayout m, Position pos) {}

    // Determines whether an opening can be carved from the cell at the
    //  specified location to the adjacent cell facing the specified direction
    private boolean canCarve(MazeLayout m, Position pos, Direction d) {
        switch(d) {
            case UP:    return pos.r - 2 >= 0 && pos.c >= 0 &&
                               pos.r - 2 < height && pos.c < width &&
                               m.getState(pos.r - 2, pos.c) == State.NOT_SET;
            case LEFT:  return pos.r >= 0 && pos.c - 2 >= 0 &&
                               pos.r < height && pos.c - 2 < width &&
                               m.getState(pos.r, pos.c - 2) == State.NOT_SET;
            case DOWN:  return pos.r + 2 >= 0 && pos.c >= 0 &&
                               pos.r + 2 < height && pos.c < width &&
                               m.getState(pos.r + 2, pos.c) == State.NOT_SET;
            case RIGHT: return pos.r >= 0 && pos.c + 2 >= 0 &&
                               pos.r < height && pos.c + 2 < width &&
                               m.getState(pos.r, pos.c + 2) == State.NOT_SET;
        }

        return false;
    }

    // Set the state at the specified location
    private void carve(MazeLayout m, Position pos, State s) {
        m.setState(pos.r, pos.c, s);
        regions[pos.r][pos.c] = currentRegion;
    }

    private class Position {
        public int r;
        public int c;

        public Position(int rIn, int cIn) {
            r = rIn;
            c = cIn;
        }
    }
}