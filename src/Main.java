public class Main {
    public static void main(String[] args) {
        // Create an initial world
        GridWorld myWorld = new GridWorld(10, 5);
        // Push it into a new object of GUI class
        new Thread(() -> new Visualize(myWorld)).run();
    }
}
