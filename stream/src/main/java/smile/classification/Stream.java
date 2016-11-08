package smile.classification;

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

    Stream(int maxWindowSize) {
        this.maxWindowSize = maxWindowSize;
    }

    public void start(double[][] x, int[] y) {
        window = new Window(x[0].length);
        possibleWindow = new Window(x[0].length);

        int iterator;

        //fill the window with the data for the first time to avoid NullPointerException
        for (iterator = 0; iterator < 10; iterator++) {
            window.add(x[iterator], y[iterator]);
        }

        while (true) {
            System.out.println("***" + iterator + "***");
            //check if we finished processing the data array
            if (iterator == y.length) {
                System.out.println("Number of drifts: " + numberOfDrifts);
                return;
            }

            if (driftDetected) {
                //in case of drift
                window.changeInto(possibleWindow);
                possibleWindow.clear();
                numberOfDrifts++;
                minProbability = Double.MAX_VALUE;
                minDeviation = Double.MAX_VALUE;

                iterator = warningIndex;
                driftDetected = false;
            }
            update(x[iterator], y[iterator], iterator++);
        }
    }

    private void update(double[] x, int y, int iterator) {

        //add data to window
        if (window != null) {
            window.add(x, y);
        }

        //learn the algorithm and check the error rate of the data in window
        try {
            int error = learnAndGetErrorRate(window);
            //check the Concept Drift

            double probabilityOfMisclassifying = (error * 100) / maxWindowSize;
            probabilityOfMisclassifying /= 100;

            //there should be also divided by t - ilość próbek
            double standardDeviation = Math.sqrt((probabilityOfMisclassifying * (1 - probabilityOfMisclassifying)));

            //checking the min probability and deviation
            if (probabilityOfMisclassifying < minProbability) {
                minProbability = probabilityOfMisclassifying;
                System.out.println("MIN probability: " + minProbability);
            }
            if (standardDeviation < minDeviation) {
                minDeviation = standardDeviation;
                System.out.println("MIN deviation: " + minDeviation);
            }

            //checking the warning and drift level
            if (probabilityOfMisclassifying + standardDeviation >= minProbability + 2 * minDeviation) {
                System.out.println("WARNING LEVEL");
                System.out.println("Standard deviation: " + standardDeviation);
                System.out.println("Probability of misclassifying: " + probabilityOfMisclassifying);
                warningIndex = iterator;
                //keep data from warning level
                possibleWindow.add(x, y);
            }
            if (probabilityOfMisclassifying + standardDeviation >= minProbability + 3 * minDeviation) {
                System.out.println("DRIFT LEVEL");
                System.out.println("Standard deviation: " + standardDeviation);
                System.out.println("Probability of misclassifying: " + probabilityOfMisclassifying);
                //set window into possibleWindow
                driftDetected = true;

            }
        } catch (IllegalArgumentException e) {
            return;
        }
    }

    private int learnAndGetErrorRate(Window window) throws IllegalArgumentException {
        double[][] newX = window.getX();
        int[] newY = window.getY();

        int error = 0;

        if (newX != null && newY != null) {
            KNN<double[]> knn = KNN.learn(newX, newY, 15); // the number indicates the number of neighbours
            for (int i = 0; i < newY.length; i++) {
                if (knn.predict(window.getX()[i]) != window.getY()[i]) {
                    error++; //set the error rate in the error array
                }
            }
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
    }
}
