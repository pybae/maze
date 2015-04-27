import maze.*;

public class GenerationTest {
    public static void main(String[] args) {
        MazeGenerator generator = new MazeGenerator(31, 31, 100, 1, 1, 20, 1);
        MazeLayout layout = generator.generate();
        layout.print();
    }
}