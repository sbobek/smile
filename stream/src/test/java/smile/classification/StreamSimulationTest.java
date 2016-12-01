package smile.classification;

import org.junit.Test;

/**
 * Created by joanna on 12/1/16.
 */
public class StreamSimulationTest {

    @Test
    public void testReadIris() throws Exception {
        StreamSimulation simulation = new StreamSimulation("/home/joanna/IdeaProjects/smiling/shell/src/universal/data/weka/iris.arff", 4);

        simulation.readStreamData();
    }

    @Test
    public void testReadSea() throws Exception {
        StreamSimulation simulation = new StreamSimulation("/home/joanna/IdeaProjects/smiling/shell/src/universal/data/stream/sea/sea.arff", 3);

        simulation.readStreamData();
    }

    @Test
    public void testReadHyperplan() throws Exception {
        StreamSimulation simulation = new StreamSimulation("/home/joanna/IdeaProjects/smiling/shell/src/universal/data/stream/hyperplane/hyperplane1.arff", 10);

        simulation.readStreamData();
    }

    @Test
    public void testReadAirlines() throws Exception {
        StreamSimulation simulation = new StreamSimulation("/home/joanna/IdeaProjects/smiling/shell/src/universal/data/stream/airlines.arff", 7);

        simulation.readStreamData();
    }

    //It doesn't work yet.
    @Test
    public void testReadKddcup() throws Exception {
        StreamSimulation simulation = new StreamSimulation("/home/joanna/IdeaProjects/smiling/shell/src/universal/data/stream/kddcup.arff", 41);

        simulation.readStreamData();
    }
}