package smile.classification;

import org.junit.Test;
import smile.data.AttributeDataset;
import smile.data.NominalAttribute;
import smile.data.parser.DelimitedTextParser;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;

/**
 * Created by joanna on 11/5/16.
 * Test to check the Stream class
 */
public class StreamTest {
    @Test
    public void testSeaKNN() {
        System.out.println("KNN");
        DelimitedTextParser parser = new DelimitedTextParser();
        parser.setDelimiter(",");
        parser.setResponseIndex(new NominalAttribute("class"), 3);

        try {
            AttributeDataset attributeDataset = parser.parse("SEA", new FileInputStream("/home/joanna/IdeaProjects/smiling/shell/src/universal/data/stream/sea.data"));
            double[][] x = attributeDataset.toArray(new double[0][]);
            int[] y = attributeDataset.toArray(new int[0]);

            Stream stream = new Stream(300, "knn"); //the number indicates the maxWindowSize, string indicates the method used to learn the data

            stream.start(x, y);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test of learn method, of class DecisionTree.
     */
    @Test
    public void testSeaDecisionTree() {
        System.out.println("Decision Tree");
        DelimitedTextParser parser = new DelimitedTextParser();
        parser.setDelimiter(",");
        parser.setResponseIndex(new NominalAttribute("class"), 3);


        try {
            AttributeDataset attributeDataset = parser.parse("SEA", new FileInputStream("/home/joanna/IdeaProjects/smiling/shell/src/universal/data/stream/sea.data"));
            double[][] x = attributeDataset.toArray(new double[0][]);
            int[] y = attributeDataset.toArray(new int[0]);

            Stream stream = new Stream(300, "tree"); //the number indicates the maxWindowSize

            stream.start(x, y);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}