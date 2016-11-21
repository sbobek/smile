package smile.classification;


import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Created by joanna on 11/3/16.
 * Stream is a class, which imitates streaming data
 */
public class Stream {

    private final static Logger LOGGER = Logger.getLogger(Stream.class.getName());
    private static FileHandler fileHandler;
    private static SimpleFormatter formatter;
    private int maxWindowSize;

    private double minProbability = Double.MAX_VALUE;
    private double minDeviation = Double.MAX_VALUE;

    private Window window;

    private boolean driftDetected = false;
    private int warningIndex;
    private int numberOfDrifts = 0;

    private String algorithmType = "";
    private int driftIndex;
    private double currentProbability;
    private double currentStandardDeviation;
    private double minSum;

    Stream(int maxWindowSize, String algorithmType) throws IOException {
        this.maxWindowSize = maxWindowSize;
        this.algorithmType = algorithmType;

        //set Logger
        LOGGER.setLevel(Level.ALL);
        fileHandler = new FileHandler("Logging.txt");
        fileHandler.setFormatter(formatter);
        LOGGER.addHandler(fileHandler);
    }

    public void start(double[][] x, int[] y) {
        window = new Window(maxWindowSize);
        int iterator;

        //fill the window with the data for the first time to avoid NullPointerException <- it's a zero DataChunk
        for (iterator = 0; iterator < 100; iterator++) {
            window.add(x[iterator], y[iterator]);
        }

        //train model for the first time
        Classifier classifier = trainModel(window, algorithmType);
        initialize();

        //check if we finished processing the data array
        while (iterator != y.length) {

            if (driftDetected) {
                numberOfDrifts++;

                window.clear();

                //fill the window with the data to avoid NullPointerException
                for (iterator = warningIndex; iterator < 50; iterator++) {
                    window.add(x[iterator], y[iterator]);
                }

                initialize();
            } else {
                LOGGER.info("***" + iterator + "***");
                System.out.println("***" + iterator + "***");

                //add an element to window
                if (window != null) {
                    window.add(x[iterator], y[iterator]);
                }

                //test model
                testModel(window, classifier, iterator);

                //update model
                try {
                    classifier = trainModel(window, algorithmType);
                } catch (IllegalArgumentException e) {
                    ++iterator;
                    continue;
                }

                ++iterator;
            }
        }
        LOGGER.info("Number of drifts: " + numberOfDrifts);
        System.out.println("Number of drifts: " + numberOfDrifts);
    }

    private Classifier trainModel(Window window, String algorithmType) throws IllegalArgumentException {

        double[][] newX = window.getX();
        int[] newY = window.getY();

        switch (algorithmType) {
            case "knn":
                KNN<double[]> knn = KNN.learn(newX, newY, 15);
                return knn;
            case "tree":
                DecisionTree tree = new DecisionTree(newX, newY, 350, DecisionTree.SplitRule.ENTROPY);
                return tree;
            default:
                return null;
        }
    }

    private void initialize() {
        LOGGER.info("Initializing all stored constants.");
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

    private void testModel(Window window, Classifier classifier, int iterator) {
        int error = 0;

        for (int i = 0; i < window.getSize(); i++) {
            if (classifier.predict(window.getX()[i]) != window.getY()[i]) {
                error++;
            }
        }

        currentProbability = (double) error / window.getSize();
//      currentProbability /= 100;

        currentStandardDeviation = Math.sqrt((currentProbability * (1 - currentProbability)) / window.getSize());

        //approximated normal distribution according to algorithm 3.5 proposed by Brzezinski
        if (window.getSize() > 30) {

            //checking the min probability and deviation
            if (currentProbability + currentStandardDeviation < minSum) {
                LOGGER.info("Number of errors " + error);
                System.out.println("Number of errors " + error);
                minProbability = currentProbability;
                minDeviation = currentStandardDeviation;
                minSum = currentProbability + currentStandardDeviation;
                LOGGER.info("MIN probability: " + minProbability);
                LOGGER.info("MIN deviation: " + minDeviation);
                System.out.format("MIN probability: %.5f%%%n", minProbability * 100.0);
                System.out.format("MIN deviation: %.5f%%%n", minDeviation);
            }

            //check the first occurrence of warning level
            if (warningIndex == -1) {
                if (currentProbability + currentStandardDeviation >= minProbability + 2 * minDeviation) {
                    warningIndex = iterator;
                    LOGGER.info("WARNING LEVEL on " + warningIndex + " index");
                    LOGGER.info("Probability of misclassified: " + currentProbability);
                    LOGGER.info("Standard deviation: " + currentStandardDeviation);
                    System.out.println("WARNING LEVEL on " + warningIndex + " index");
                    System.out.format("Probability of misclassified: %.5f%%%n", currentProbability);
                    System.out.format("Standard deviation: %.5f%%%n", currentStandardDeviation);
                }
            }

            //check the occurrence of concept drift
            if (currentProbability + currentStandardDeviation >= minProbability + 3 * minDeviation) {
                driftIndex = iterator;
                LOGGER.info("DRIFT LEVEL on " + driftIndex + " index");
                LOGGER.info("Probability of misclassified: " + currentProbability);
                LOGGER.info("Standard deviation: " + currentStandardDeviation);
                System.out.println("DRIFT LEVEL on " + driftIndex + " index");
                System.out.format("Probability of misclassified: %.5f%%%n", currentProbability);
                System.out.format("Standard deviation: %.5f%%%n", currentStandardDeviation);
                driftDetected = true;
            }
        }
    }
}
