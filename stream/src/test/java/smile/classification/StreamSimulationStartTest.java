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

        PrintStream out = new PrintStream(new FileOutputStream("output/hyperplaneKNNStartBeta8.txt"));
        System.setOut(out);

        long start = System.nanoTime();

        StreamSimulation simulation = new StreamSimulation("/home/joanna/IdeaProjects/smiling/shell/src/universal/data/stream/hyperplane/hyperplane1.arff", 10);

        simulation.start("knn", 100, 2, 8);
        long elapsedTime = System.nanoTime() - start;

        System.out.println("Time: " + elapsedTime);
    }

    @Test
    public void testStartHyperplaneTree() throws FileNotFoundException {

        PrintStream out = new PrintStream(new FileOutputStream("output/hyperplaneTreeStartBeta.txt"));
        System.setOut(out);

        long start = System.nanoTime();

        StreamSimulation simulation = new StreamSimulation("/home/joanna/IdeaProjects/smiling/shell/src/universal/data/stream/hyperplane/hyperplane1.arff", 10);

        simulation.start("tree", 100, 2, 3);
        long elapsedTime = System.nanoTime() - start;

        System.out.println("Time: " + elapsedTime);
    }

    @Test
    public void testStartSeaKNN() throws FileNotFoundException {

        PrintStream out = new PrintStream(new FileOutputStream("output/seaKNNStartBeta5.txt"));
        System.setOut(out);

        long start = System.nanoTime();
        StreamSimulation simulation = new StreamSimulation("/home/joanna/IdeaProjects/smiling/shell/src/universal/data/stream/sea/sea.arff", 3);

        simulation.start("knn", 100, 2, 5);
        long elapsedTime = System.nanoTime() - start;

        System.out.println("Time: " + elapsedTime);
    }

    @Test
    public void testStartSeaTree() throws FileNotFoundException {

        PrintStream out = new PrintStream(new FileOutputStream("output/seaTreeStartBeta5.txt"));
        System.setOut(out);
        long start = System.nanoTime();

        StreamSimulation simulation = new StreamSimulation("/home/joanna/IdeaProjects/smiling/shell/src/universal/data/stream/sea/sea.arff", 3);

        simulation.start("tree", 100, 2, 5);

        long elapsedTime = System.nanoTime() - start;

        System.out.println("Time: " + elapsedTime);
    }

    @Test
    public void testStartAirlinesKNN() throws Exception {

        PrintStream out = new PrintStream(new FileOutputStream("output/airlinesKNNStart.txt"));
        System.setOut(out);
        long start = System.nanoTime();

        StreamSimulation simulation = new StreamSimulation("/home/joanna/IdeaProjects/smiling/shell/src/universal/data/stream/airlines.arff", 7);

        simulation.start("knn", 500, 2, 3);
        long elapsedTime = System.nanoTime() - start;

        System.out.println("Time: " + elapsedTime);
    }

    @Test
    public void testStartAirlinesTree() throws Exception {

        PrintStream out = new PrintStream(new FileOutputStream("output/airlinesTreeStart.txt"));
        System.setOut(out);

        long start = System.nanoTime();

        StreamSimulation simulation = new StreamSimulation("/home/joanna/IdeaProjects/smiling/shell/src/universal/data/stream/airlines.arff", 7);

        simulation.start("tree", 500, 2, 3);
        long elapsedTime = System.nanoTime() - start;

        System.out.println("Time: " + elapsedTime);
    }
}