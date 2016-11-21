package smile.classification;

import org.junit.Test;
import smile.data.AttributeDataset;
import smile.data.NominalAttribute;
import smile.data.StringAttribute;
import smile.data.parser.ArffParser;
import smile.data.parser.DelimitedTextParser;

import java.io.*;
import java.text.ParseException;

/**
 * Created by joanna on 11/5/16.
 * Test to check the Stream class
 */
public class StreamTest {

    @Test
    public void testIrisKNN() throws FileNotFoundException {

        PrintStream out = new PrintStream(new FileOutputStream("output/irisKNN.txt"));
        System.setOut(out);

        System.out.println("IRIS KNN");

        ArffParser parser = new ArffParser();
        parser.setResponseIndex(4);

        try {
            AttributeDataset attributeDataset = parser.parse(new FileInputStream("/home/joanna/IdeaProjects/smiling/shell/src/universal/data/weka/iris.arff"));
            double[][] x = attributeDataset.toArray(new double[0][]);
            int[] y = attributeDataset.toArray(new int[0]);
            Stream stream = new Stream(100, "knn"); //the number indicates the maxWindowSize, string indicates the method used to learn the data

            stream.start(x, y);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testSeaKNN() throws FileNotFoundException {

        PrintStream out = new PrintStream(new FileOutputStream("output/seaKNN.txt"));
        System.setOut(out);

        System.out.println("Sea KNN");
        DelimitedTextParser parser = new DelimitedTextParser();
        parser.setDelimiter(",");
        parser.setResponseIndex(new NominalAttribute("class"), 3);

        try {
            AttributeDataset attributeDataset = parser.parse("SEA", new FileInputStream("/home/joanna/IdeaProjects/smiling/shell/src/universal/data/stream/sea.data"));
            double[][] x = attributeDataset.toArray(new double[0][]);
            int[] y = attributeDataset.toArray(new int[0]);
            Stream stream = new Stream(100, "knn"); //the number indicates the maxWindowSize, string indicates the method used to learn the data

            stream.start(x, y);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test of learn method, of class DecisionTree.
     */
    @Test
    public void testSeaDecisionTree() throws FileNotFoundException {

        PrintStream out = new PrintStream(new FileOutputStream("output/seaDecisionTree.txt"));
        System.setOut(out);

        System.out.println("SEA Decision Tree");
        DelimitedTextParser parser = new DelimitedTextParser();
        parser.setDelimiter(",");
        parser.setResponseIndex(new NominalAttribute("class"), 3);

        try {
            AttributeDataset attributeDataset = parser.parse("SEA", new FileInputStream("/home/joanna/IdeaProjects/smiling/shell/src/universal/data/stream/sea.data"));
            double[][] x = attributeDataset.toArray(new double[0][]);
            int[] y = attributeDataset.toArray(new int[0]);

            Stream stream = new Stream(60, "tree"); //the number indicates the maxWindowSize

            stream.start(x, y);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Test of learn method, of class DecisionTree.
     */
    @Test
    public void testIrisDecisionTree() throws FileNotFoundException {

        PrintStream out = new PrintStream(new FileOutputStream("output/irisDecisionTree.txt"));
        System.setOut(out);

        System.out.println("IRIS Decision Tree");

        ArffParser parser = new ArffParser();
        parser.setResponseIndex(4);

        try {
            AttributeDataset attributeDataset = parser.parse(new FileInputStream("/home/joanna/IdeaProjects/smiling/shell/src/universal/data/weka/iris.arff"));
            double[][] x = attributeDataset.toArray(new double[0][]);
            int[] y = attributeDataset.toArray(new int[0]);

            Stream stream = new Stream(60, "tree"); //the number indicates the maxWindowSize
            stream.start(x, y);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Test of learn method, of class DecisionTree.
     */
    @Test
    public void testSpamDecisionTree() throws FileNotFoundException {

        PrintStream out = new PrintStream(new FileOutputStream("output/spamDecisionTree.txt"));
        System.setOut(out);

        System.out.println("SPAM Decision Tree");
        DelimitedTextParser parser = new DelimitedTextParser();
        parser.setDelimiter(",");

        parser.setResponseIndex(new StringAttribute("class"), 39916);

        try {
            AttributeDataset attributeDataset = parser.parse("SPAM", new FileInputStream("/home/joanna/IdeaProjects/smiling/shell/src/universal/data/stream/spam_nominal.data"));
            double[][] x = attributeDataset.toArray(new double[0][]);
            int[] y = attributeDataset.toArray(new int[0]);

            Stream stream = new Stream(60, "spam"); //the number indicates the maxWindowSize

            stream.start(x, y);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void testAirlinesKNN() throws FileNotFoundException {

        PrintStream out = new PrintStream(new FileOutputStream("output/airlinesKNN.txt"));
        System.setOut(out);

        System.out.println("Airlines KNN");
        DelimitedTextParser parser = new DelimitedTextParser();
        parser.setDelimiter(",");
        parser.setColumnNames(true);
        parser.setResponseIndex(new StringAttribute("class"), 8);

        try {
            AttributeDataset attributeDataset = parser.parse("AIRLINES", new FileInputStream("/home/joanna/IdeaProjects/smiling/shell/src/universal/data/airline/train-0.1m.csv"));
            double[][] x = attributeDataset.toArray(new double[0][]);
            int[] y = attributeDataset.toArray(new int[0]);
            Stream stream = new Stream(100, "knn"); //the number indicates the maxWindowSize, string indicates the method used to learn the data

            stream.start(x, y);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
}