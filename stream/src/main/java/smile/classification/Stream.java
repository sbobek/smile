package smile.classification;

import smile.data.AttributeDataset;
import smile.data.NominalAttribute;
import smile.data.parser.DelimitedTextParser;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;

/**
 * Created by joanna on 11/3/16.
 * Stream is a class, which imitates streaming data
 */
public class Stream {

    private int maxWindowSize;

    private double minProbability = Double.MAX_VALUE;
    private double minDeviation = Double.MAX_VALUE;

    private Window window;
    private Window possibleWindow;

    private boolean driftDetected = false;
    private int warningIndex = -1;
    private int numberOfDrifts = 0;

    private String algorithmType = "";

    Stream(int maxWindowSize, String algorithmType) {
        this.maxWindowSize = maxWindowSize;
        this.algorithmType = algorithmType;
    }

    public void start(double[][] x, int[] y) {
        window = new Window(x[0].length);
        possibleWindow = new Window(x[0].length);

        int iterator;

        //fill the window with the data for the first time to avoid NullPointerException
        for (iterator = 0; iterator < 100; iterator++) {
            window.add(x[iterator], y[iterator]);
        }

        while (true) {
            //check if we finished processing the data array
            if (iterator == y.length) {
                System.out.println("Number of drifts: " + numberOfDrifts);
                return;
            }

            if (driftDetected) {
                //in case of drift
                window.changeInto(possibleWindow);
                numberOfDrifts++;

                System.out.println("RESET all stored constants");
                possibleWindow.clear();

                minProbability = Double.MAX_VALUE;
                minDeviation = Double.MAX_VALUE;

                iterator = warningIndex;
                driftDetected = false;
                warningIndex = -1;
            } else {
                System.out.println("***" + iterator + "***");
                update(x[iterator], y[iterator], iterator);
                ++iterator;
            }
        }
    }

    private void update(double[] x, int y, int iterator) {

        //add data to window
        if (window != null) {
            window.add(x, y);
        }

        //learn the algorithm and check the error rate of the data in window
        try {
            int error = learnAndGetErrorRate(window, algorithmType);
            //check the Concept Drift

            double probabilityOfMisclassifying = 100.0 * error / window.getIterator();
//            probabilityOfMisclassifying /= 100;

            //there should be also divided by t - ilość próbek
            double standardDeviation = Math.sqrt((probabilityOfMisclassifying * (1 - probabilityOfMisclassifying)) / window.getIterator());

            //checking the min probability and deviation
            if (probabilityOfMisclassifying < minProbability) {
                minProbability = probabilityOfMisclassifying;
                System.out.format("MIN probability: %.5f%%%n", minProbability);
            }
            if (standardDeviation < minDeviation) {
                minDeviation = standardDeviation;
                System.out.format("MIN deviation: %.5f%%%n", minDeviation);
            }

            //check the first occurrence of warning level
            if (warningIndex == -1) {
                if (probabilityOfMisclassifying + standardDeviation >= minProbability + 2 * minDeviation) {
                    System.out.format("Probability of misclassifying: %.5f%%%n", probabilityOfMisclassifying);
                    System.out.format("Standard deviation: %.5f%%%n", standardDeviation);
                    System.out.println("WARNING LEVEL");
                    warningIndex = iterator;
                    //store data from a warning level
                    possibleWindow.add(x, y);
                }
            }
            if (probabilityOfMisclassifying + standardDeviation >= minProbability + 3 * minDeviation) {
                System.out.format("Probability of misclassifying: %.5f%%%n", probabilityOfMisclassifying);
                System.out.format("Standard deviation: %.5f%%%n", standardDeviation);
                System.out.println("DRIFT LEVEL");
                //concept drift detected
                driftDetected = true;
            }
        } catch (IllegalArgumentException e) {
            return;
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
    }


    private int learnAndGetErrorRate(Window window, String algorithmType) throws IllegalArgumentException, IOException, ParseException {

        int error = 0;
        double[][] newX = window.getX();
        int[] newY = window.getY();

        switch (algorithmType) {
            case "knn":
                if (newX != null && newY != null) {
                    KNN<double[]> knn = KNN.learn(newX, newY, 15); // the number indicates the number of neighbours
                    for (int i = 0; i < newY.length; i++) {
                        if (knn.predict(window.getX()[i]) != window.getY()[i]) {
                            error++; //set the error rate in the error array
                        }
                    }
                }
                break;

            case "tree":
                DelimitedTextParser parser = new DelimitedTextParser();
                parser.setResponseIndex(new NominalAttribute("class"), 0);
                AttributeDataset test = parser.parse("USPS Test", new FileInputStream("/home/joanna/IdeaProjects/smiling/shell/src/universal/data/usps/zip.test"));
                double[][] testx = test.toArray(new double[test.size()][]);
                int[] testy = test.toArray(new int[test.size()]);

                DecisionTree tree = new DecisionTree(newX, newY, 350, DecisionTree.SplitRule.ENTROPY);

                for (int i = 0; i < testx.length; i++) {
                    if (tree.predict(testx[i]) != testy[i]) {
                        error++;
                    }
                }
                break;
        }
        return error;
    }

    /**
     * Window is a class, which gather data to process by the Drift Detection Method
     */
    private class Window {

        double[][] x;
        int[] y;
        int length; //the number of attributes for key
        int iterator;

        Window(int length) {
            this.length = length;
            this.x = new double[maxWindowSize][length];
            this.y = new int[maxWindowSize];
            this.iterator = 0;
        }

        double[][] getX() {
            return x;
        }

        int[] getY() {
            return y;
        }

        void add(double[] xElement, int yElement) {
            if (iterator < maxWindowSize) {
                x[iterator] = xElement;
                y[iterator] = yElement;

                iterator++;
            } else { // create new array when the previous window size was full
                iterator = 0;

                x = new double[maxWindowSize][length];
                y = new int[maxWindowSize];

                x[iterator] = xElement;
                y[iterator] = yElement;

                iterator++;
            }
        }

        void clear() {
            for (int i = 0; i < y.length; i++) {
                this.y[i] = 0;
                for (int j = 0; j < x[i].length; j++) {
                    this.x[i][j] = 0;
                }
            }
            length = 0;
            iterator = 0;
        }

        void changeInto(Window newWindow) {
            for (int i = 0; i < y.length; i++) {
                this.y[i] = newWindow.getY()[i];
                System.arraycopy(newWindow.getX()[i], 0, this.x[i], 0, x[i].length);
            }
        }

        int getIterator() {
            return iterator;
        }
    }
}
