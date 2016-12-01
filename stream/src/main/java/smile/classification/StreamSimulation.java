package smile.classification;

import smile.data.AttributeDataset;
import smile.data.parser.ArffParser;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;

/**
 * Created by joanna on 12/1/16.
 * A class, which imitates stream. It takes filename, class index and send {@link AttributeDataset} elements into {@link DriftDetector} class instance.
 */
public class StreamSimulation {

    private String filename;
    private int classIndex;

    public StreamSimulation(String filename, int classIndex) {
        this.filename = filename;
        this.classIndex = classIndex;
    }

    public void start(String classifierName, int maxWindowSize, int alpha, int beta) {

        AttributeDataset attributeDataset = this.parseFile();
        double[][] x = attributeDataset.toArray(new double[0][]);
        int[] y = attributeDataset.toArray(new int[0]);

        Window window = Window.getInstance(maxWindowSize);
        for (int i = 0; i < maxWindowSize; i++) {
            window.add(x[i], y[i]);
        }

        DriftDetector driftDetector = DriftDetector.getInstance(maxWindowSize, classifierName);

        for (int i = maxWindowSize; i < y.length; ) {
            i = driftDetector.update(x[i], y[i], i, alpha, beta);
        }
        System.out.println("Number of Drifts: " + driftDetector.getNumberOfDrifts() + " for alpha=" + alpha + ", beta=" + beta);

    }

    public void readStreamData() {
        DriftDetector driftDetector = DriftDetector.getInstance(50, "knn");

        AttributeDataset attributeDataset = this.parseFile();
        double[][] x = attributeDataset.toArray(new double[0][]);
        int[] y = attributeDataset.toArray(new int[0]);

        for (int i = 0; i < y.length; i++) {
            driftDetector.printValue(x[i], y[i], i);
        }
    }

    public AttributeDataset parseFile() {
        ArffParser parser = new ArffParser();
        parser.setResponseIndex(classIndex);

        try {
            AttributeDataset parse = parser.parse(new FileInputStream(filename));
            return parse;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
