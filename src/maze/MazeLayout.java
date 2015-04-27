import java.util.*;

public class MazeLayout {
    public static enum state { WALL, PATH }
    private state[][] maze;

    public MazeLayout(int w, int h) {
        maze = new state[h][w];
    }
}