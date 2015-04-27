package maze;

/**
 * The main game state class
 * This class is a singleton and should therefore only be used through the 
 * getInstance method
 * 
 * You can generate a new Maze by invoking the public function generateMaze on 
 * the instance of the Maze. Note that this method is inherently destructive
 * and should be used with care.
 */
public class Maze {
    private Maze () {
    }

    public static Maze getInstance() {
        return Maze.MazeHolder.INSTANCE;
    }
    
    private static class MazeHolder {
        private static final Maze INSTANCE = new Maze();
    }
    
    public void generateMaze() {
        /** @zane
         * you can write the implementation for generating the Maze here.
         */
    }
}
