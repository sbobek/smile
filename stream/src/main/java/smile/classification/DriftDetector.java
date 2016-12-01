package smile.classification;

/**
 * Created by joanna on 10/25/16.
 * Class which impements Drift Detector Method algorithm
 */
public class DriftDetector {

    private static DriftDetector instance = null;
    private Classifier classifier;
    private int maxWindowSize;
    private String classifierName;
    private Window window = Window.getInstance(maxWindowSize);

    private double minProbability = Double.MAX_VALUE;
    private double minDeviation = Double.MAX_VALUE;

    private boolean driftDetected = false;
    private int warningIndex;
    private int numberOfDrifts = 0;

    private int driftIndex;
    private double currentProbability;
    private double currentStandardDeviation;
    private double minSum = Double.MAX_VALUE;

    private DriftDetector(int maxWindowSize, String method) {
        this.maxWindowSize = maxWindowSize;
        this.classifierName = method;
        this.classifier = trainModel(window, classifierName);
    }

    public static DriftDetector getInstance(int maxWindowSize, String classifierName) {
        if (instance == null) {
            instance = new DriftDetector(maxWindowSize, classifierName);
        }
        return instance;
    }

    public boolean isDriftDetected() {
        return driftDetected;
    }

    public int getNumberOfDrifts() {
        return numberOfDrifts;
    }

    public int update(double[] x, int y, int i, int alpha, int beta) {
//TODO: Entropy
        //fill the window with the data to avoid NullPointerException
        if (window.getSize() < maxWindowSize) {
            window.add(x, y);
            return ++i;
        } else {
            if (driftDetected) {
                numberOfDrifts++;

                window.clear();

                i = warningIndex;

                initialize();

                return i;
            } else {
                System.out.println("***" + i + "***");

                //add an element to window
                if (window != null) {
                    window.add(x, y);
                    System.out.print("Added do window: ");
                    printValue(x, y);
                }

                //test model
                testModel(window, classifier, i, alpha, beta);

                //update model
                classifier = trainModel(window, classifierName);

                return ++i;
            }
        }
    }

    private Classifier trainModel(Window window, String algorithmType) throws IllegalArgumentException {

        System.out.println("Train model");

        double[][] newX = window.getX();
        int[] newY = window.getY();

        switch (algorithmType) {
            case "knn":
                KNN<double[]> knn = KNN.learn(newX, newY, 15);
                return knn;
            case "tree":
                DecisionTree tree = new DecisionTree(newX, newY, 100, DecisionTree.SplitRule.ENTROPY);
                return tree;
            default:
                return null;
        }
    }

    private void initialize() {
        System.out.println("Initializing all stored constants.");

        driftDetected = false;
        warningIndex = -1;
        driftIndex = -1;

        currentProbability = 1.0;
        currentStandardDeviation = 1.0;
        minSum = Double.MAX_VALUE;
        minProbability = (Double.MAX_VALUE / 2) - 1;
        minDeviation = (Double.MAX_VALUE / 2) - 1;

    }

    private void testModel(Window window, Classifier classifier, int iterator, int alpha, int beta) {
        System.out.println("Test model");

        int error = 0;

        for (int i = 0; i < window.getSize(); i++) {
            if (classifier.predict(window.getX()[i]) != window.getY()[i]) {
                error++;
            }
        }

        System.out.println("Errors: " + error);

        currentProbability = (double) error / window.getSize();

        currentStandardDeviation = Math.sqrt((currentProbability * (1 - currentProbability)) / window.getSize());


        System.out.format("Probability: %.5f%%%n", currentProbability * 100.0);
        System.out.format("Deviation: %.5f%%%n", currentStandardDeviation);

        //approximated normal distribution according to algorithm 3.5 proposed by Brzezinski
        if (window.getSize() > 30) {
            System.out.println("Window.getSize()=" + window.getSize());
            //checking the min probability and deviation
            if (currentProbability + currentStandardDeviation < minSum) {
                minProbability = currentProbability;
                minDeviation = currentStandardDeviation;
                minSum = currentProbability + currentStandardDeviation;
                System.out.format("MIN probability: %.5f%%%n", minProbability * 100.0);
                System.out.format("MIN deviation: %.5f%%%n", minDeviation);
            }

            System.out.println("Warning index=" + warningIndex);
            //check the first occurrence of warning level
            if (warningIndex <= 0) {
                if (currentProbability + currentStandardDeviation >= minProbability + alpha * minDeviation) {
                    warningIndex = iterator;
                    System.out.println("WARNING LEVEL on " + warningIndex + " index");
                    System.out.format("Probability of misclassified: %.5f%%%n", currentProbability);
                    System.out.format("Standard deviation: %.5f%%%n", currentStandardDeviation);
                }
            }

            //check the occurrence of concept drift
            if (currentProbability + currentStandardDeviation >= minProbability + beta * minDeviation) {
                driftIndex = iterator;
                System.out.println("DRIFT LEVEL on " + driftIndex + " index");
                System.out.format("Probability of misclassified: %.5f%%%n", currentProbability);
                System.out.format("Standard deviation: %.5f%%%n", currentStandardDeviation);
                driftDetected = true;
            }
        }
    }

    public void printValue(double[] x, int y, int i) {
        System.out.print(i + " Attributes: ");
        for (double element : x) {
            System.out.print(element + " ");
        }
        System.out.println("Class: " + y);
    }

    public void printValue(double[] x, int y) {
        for (double element : x) {
            System.out.print(element + " ");
        }
        System.out.println("Class: " + y);
    }

}
