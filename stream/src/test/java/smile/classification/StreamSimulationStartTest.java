package smile.classification;

import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * Created by joanna on 12/1/16.
 */
public class StreamSimulationStartTest {

    @Test
    public void testStartHyperplaneKNN() throws FileNotFoundException {

        PrintStream out = new PrintStream(new FileOutputStream("output/hyperplaneKNNStart.txt"));
        System.setOut(out);

        StreamSimulation simulation = new StreamSimulation("/home/joanna/IdeaProjects/smiling/shell/src/universal/data/stream/hyperplane/hyperplane1.arff", 10);

        simulation.start("knn", 100, 2, 3);
    }

    @Test
    public void testStartHyperplaneTree() throws FileNotFoundException {

        PrintStream out = new PrintStream(new FileOutputStream("output/hyperplaneTreeStart.txt"));
        System.setOut(out);

        StreamSimulation simulation = new StreamSimulation("/home/joanna/IdeaProjects/smiling/shell/src/universal/data/stream/hyperplane/hyperplane1.arff", 10);

        simulation.start("tree", 100, 2, 3);
    }

    @Test
    public void testStartSea() throws FileNotFoundException {

        PrintStream out = new PrintStream(new FileOutputStream("output/seaKNNStart.txt"));
        System.setOut(out);

        StreamSimulation simulation = new StreamSimulation("/home/joanna/IdeaProjects/smiling/shell/src/universal/data/stream/sea/sea.arff", 3);

        simulation.start("knn", 100, 2, 3);
    }

    @Test
    public void testStartSeaTree() throws FileNotFoundException {

        PrintStream out = new PrintStream(new FileOutputStream("output/seaTreeStart.txt"));
        System.setOut(out);

        StreamSimulation simulation = new StreamSimulation("/home/joanna/IdeaProjects/smiling/shell/src/universal/data/stream/sea/sea.arff", 3);

        simulation.start("tree", 100, 2, 3);
    }
}