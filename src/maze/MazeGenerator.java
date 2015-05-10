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
    private int startRoomSize;
    private int extraConnectorChance;

    private ArrayList<Rectangle> rooms;
    private int[][] regions;
    private int currentRegion;

    private int startRegion;
    private int endRegion;

    public MazeGenerator(int widthIn, int heightIn, int windinessIn,
                         int minRoomSizeIn, int maxRoomSizeIn,
                         int roomTriesIn, int minRoomsIn,
                         int startRoomSizeIn, int extraConnectorChanceIn) {
        width = widthIn;
        height = heightIn;
        windiness = windinessIn;
        minRoomSize = minRoomSizeIn;
        maxRoomSize = maxRoomSizeIn;
        roomTries = roomTriesIn;
        minRooms = minRoomsIn;
        startRoomSize = startRoomSizeIn;
        extraConnectorChance = extraConnectorChanceIn;

        currentRegion = -1;
        startRegion = -1;
        endRegion = -1;
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

        for(int r = 0; r < height; r+=1) {
            for(int c = 0; c < width; c+=1) {
                regions[r][c] = -1;
            }
        }

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

    private boolean canBeBlasted(MazeLayout m, Position pos) {
        int count = 0;

        if(m.getState(pos.r - 1, pos.c) == State.NOT_SET ||
           m.getState(pos.r - 1, pos.c) == State.WALL)
            count++;
        if(m.getState(pos.r, pos.c - 1) == State.NOT_SET ||
           m.getState(pos.r, pos.c - 1) == State.WALL)
            count++;
        if(m.getState(pos.r + 1, pos.c) == State.NOT_SET ||
           m.getState(pos.r + 1, pos.c) == State.WALL)
            count++;
        if(m.getState(pos.r, pos.c + 1) == State.NOT_SET ||
           m.getState(pos.r, pos.c + 1) == State.WALL)
            count++;

        return count < 3;
    }

    // Place rooms in the maze
    private void addRooms(MazeLayout m) {
        for(int i = 0; i < MAX_ADD_ROOMS_TRIES; i++) {
            rooms.clear();

            int startX = width / 2 - startRoomSize / 2 + 1;
            int startY = height / 2 - startRoomSize / 2 + 1;

            Rectangle start = new Rectangle(startX, startY, startRoomSize, startRoomSize);

            rooms.add(start);

            startRegion();

            startRegion = currentRegion;

            for(int r = startY; r < startY + startRoomSize; r++) {
                for(int c = startX; c < startX + startRoomSize; c++) {
                    carve(m, new Position(r, c), State.OPEN);
                }
            }

            // encircle the room with walls
            for(int r = startY - 1; r < startY + startRoomSize + 1; r++) {
                carve(m, new Position(r, startX - 1), State.WALL);
                carve(m, new Position(r, startX + startRoomSize), State.WALL);
            }
            for(int c = startX - 1; c < startX + startRoomSize + 1; c++) {
                carve(m, new Position(startY - 1, c), State.WALL);
                carve(m, new Position(startY + startRoomSize, c), State.WALL);
            }

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

        m.rooms = new ArrayList<Rectangle>(rooms);

        endRegion = currentRegion;
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
        HashMap<Position, HashSet<Integer>> connectorRegions = new HashMap<Position, HashSet<Integer>>();

        for(int r = 1; r < height - 1; r++) {
            for(int c = 1; c < width - 1; c++) {
                if(m.getState(r, c) != State.NOT_SET && m.getState(r, c) != State.WALL) {
                    continue;
                }

                HashSet<Integer> connectableRegions = new HashSet<Integer>();
                for(Direction d : Direction.values()) {
                    switch(d) {
                        case UP:    if(regions[r - 1][c] != -1) {
                                        connectableRegions.add(regions[r - 1][c]);
                                    }
                                    break;
                        case LEFT:  if(regions[r][c - 1] != -1) {
                                        connectableRegions.add(regions[r][c - 1]);
                                    }
                                    break;
                        case DOWN:  if(regions[r + 1][c] != -1) {
                                        connectableRegions.add(regions[r + 1][c]);
                                    }
                                    break;
                        case RIGHT: if(regions[r][c + 1] != -1) {
                                        connectableRegions.add(regions[r][c + 1]);
                                    }
                                    break;
                    }
                }

                if(connectableRegions.size() > 1) {
                    connectorRegions.put(new Position(r, c), new HashSet<Integer>(connectableRegions));
                }
            }
        }

        Set<Position> connectors = connectorRegions.keySet();

        HashMap<Integer, Integer> merged = new HashMap<Integer, Integer>();
        HashSet<Integer> openRegions = new HashSet<Integer>();
        for(int i = 0; i < currentRegion; i++) {
            merged.put(i, i);
            openRegions.add(i);
        }

        while(openRegions.size() > 1) {
            Random rand = new Random();
            Position connector = null;

            if(connectors.size() == 0) {
                System.out.println("Regions possibly joined incorrectly.");
                break;
            }
            int selection = rand.nextInt(connectors.size());

            int count = 0;
            for(Position pos : connectors) {
                if(count == selection) {
                    connector = pos;
                }

                count++;
            }

            addJunction(m, connector);

            HashSet<Integer> connectableRegions = connectorRegions.get(connector);
            for(Integer reg : new HashSet<Integer>(connectableRegions)) {
                connectableRegions.remove(reg);
                connectableRegions.add(merged.get(reg));
            }

            Integer dest = -1;
            HashSet<Integer> sources = new HashSet<Integer>();

            count = 0;
            for(Integer reg : connectableRegions) {
                if(count == 0) {
                    dest = reg;
                } else {
                    sources.add(reg);
                }

                count++;
            }

            for(int i = 0; i < currentRegion; i++) {
                if(sources.contains(merged.get(i))) {
                    merged.put(i, dest);
                }
            }

            for(Integer source : sources) {
                openRegions.remove(source);
            }


            ArrayList<Position> toRemove = new ArrayList<Position>();
            for(Position pos : connectors) {
                if(connector.isAdjacentTo(pos)) {
                    toRemove.add(pos);
                    continue;
                }

                connectableRegions = connectorRegions.get(pos);
                for(Integer reg : new HashSet<Integer>(connectableRegions)) {
                    connectableRegions.remove(reg);
                    connectableRegions.add(merged.get(reg));
                }

                if(connectableRegions.size() > 1) {
                    continue;
                }

                if(rand.nextInt(extraConnectorChance) == 0 &&
                   regions[pos.r - 1][pos.c] != startRegion && regions[pos.r][pos.c - 1] != startRegion &&
                   regions[pos.r + 1][pos.c] != startRegion && regions[pos.r][pos.c + 1] != startRegion &&
                   regions[pos.r - 1][pos.c] != endRegion && regions[pos.r][pos.c - 1] != endRegion &&
                   regions[pos.r + 1][pos.c] != endRegion && regions[pos.r][pos.c + 1] != endRegion) {
                    carve(m, pos, State.OPEN);
                }

                toRemove.add(pos);
            }

            connectors.removeAll(toRemove);
        }
    }

    // Move on to the next region
    private void startRegion() {
        currentRegion++;
    }

    // Adds a junction at a specified location
    private void addJunction(MazeLayout m, Position pos) {
        m.setState(pos.r, pos.c, State.DOOR);
    }

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
        if(s != State.WALL && s != State.DOOR) {
            regions[pos.r][pos.c] = currentRegion;
        }
    }

    private class Position {
        public int r;
        public int c;

        public Position(int rIn, int cIn) {
            r = rIn;
            c = cIn;
        }

        public boolean equals(Position other) {
            return r == other.r && c == other.c;
        }

        public boolean isAdjacentTo(Position other) {
            return Math.abs(r - other.r) + Math.abs(c - other.c) < 2;
        }
    }
}
