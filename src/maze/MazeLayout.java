package maze;

import java.util.*;
import java.awt.Rectangle;

public class MazeLayout {
    public State[][] maze;
    public ArrayList<Rectangle> rooms;

    public MazeLayout(int w, int h) {
        maze = new State[h][w];

        for(int r = 0; r < h; r++) {
            for(int c = 0; c < w; c++) {
                maze[r][c] = State.NOT_SET;
            }
        }
    }

    public State getState(int r, int c) {
        return maze[r][c];
    }

    public void setState(int r, int c, State s) {
        maze[r][c] = s;
    }

    public void print() {
        for(int r = 0; r < maze.length; r++) {
            for(int c = 0; c < maze[0].length; c++) {
                if(maze[r][c] == State.WALL) {
                    System.out.print("* ");
                } else if(maze[r][c] == State.DOOR) {
                    System.out.print("O ");
                } else if(maze[r][c] == State.NOT_SET) {
                    System.out.print("- ");
                } else {
                    System.out.print("  ");
                }
            }
            System.out.println();
        }
    }
}