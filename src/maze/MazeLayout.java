import java.util.*;

public class MazeLayout {
    public enum State { NOT_SET, WALL, PATH }

    private State[][] maze;

    public MazeLayout(int w, int h) {
        maze = new State[h][w];

        for(int i = 0; i < h; i++) {
            for(int j = 0; j < w; j++) {
                maze[i][j] = State.NOT_SET;
            }
        }
    }

    public State getState(int r, int c) {
        return maze[r][c];
    }

    public void setState(int r, int c, State s) {
        maze[r][c] = s;
    }
}