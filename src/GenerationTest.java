import maze.*;

public class GenerationTest {
    public static void main(String[] args) {
        MazeGenerator generator = new MazeGenerator(21, 21, 100, 1, 3, 20, 1);
        MazeLayout layout = generator.generate();
        layout.print();
    }
}