package smile.classification;

/**
 * DriftDetector interface
 * Created by joanna on 10/25/16.
 */
public class DriftDetector {

    private static DriftDetector instance = null;
    int maxWindowSize;
    String method;
    private Window window = new Window(maxWindowSize);

    private DriftDetector(int maxWindowSize, String method) {
        this.maxWindowSize = maxWindowSize;
        this.method = method;
    }

    public static DriftDetector getInstance(int maxWindowSize, String method) {
        if (instance == null) {
            instance = new DriftDetector(maxWindowSize, method);
        }
        return instance;
    }

    public void update(double[] x, int y, int i) {
        System.out.println(i + " To działa");
    }

    public void printValue(double[] x, int y, int i) {
        System.out.print(i + " Attributes: ");
        for (double element : x) {
            System.out.print(element + " ");
        }
        System.out.println("Class: " + y);
    }

}
