package smile.classification;

import org.junit.Test;
import smile.data.AttributeDataset;
import smile.data.NominalAttribute;
import smile.data.parser.DelimitedTextParser;

import java.io.FileInputStream;

/**
 * Created by joanna on 11/5/16.
 * Test to check the Stream class
 */
public class StreamTest {
    @Test
    public void testSea() throws Exception {
        DelimitedTextParser parser = new DelimitedTextParser();
        parser.setDelimiter(",");
        parser.setResponseIndex(new NominalAttribute("class"), 3);

        AttributeDataset attributeDataset = parser.parse("SEA", new FileInputStream("/home/joanna/IdeaProjects/smiling/shell/src/universal/data/stream/sea.data"));
        double[][] x = attributeDataset.toArray(new double[0][]);
        int[] y = attributeDataset.toArray(new int[0]);

        Stream stream = new Stream(200); //the number indicates the maxWindowSize

        stream.start(x, y);
    }
}