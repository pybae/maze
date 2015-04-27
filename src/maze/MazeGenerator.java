public class MazeGenerator {
    // Given a width, height, and windiness of the passages, this method will
    //  return a MazeLayout object corresponding to the generated maze
    public static MazeLayout generate(int w, int h, int windiness) {
        return new MazeLayout(w, h);
    }
}