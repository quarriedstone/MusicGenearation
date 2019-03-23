import java.util.Arrays;
import java.util.Random;

public class Swarm {

    int[] position; // notes
    double[] velocity;
    int[] myBest;
    int currentFitnessValue;
    int bestFitnessValue = Integer.MAX_VALUE;

    private Random random = new Random();

    Swarm(int notesNum){
        position = new int[notesNum];
        velocity = new double[notesNum];
        myBest = new int[notesNum];
    }

    /**
     *
     * Generates Random values within bounds
     *
     * @param lowerBound
     * @param upperBound
     */
    public void generateRandomInitValues(int lowerBound, int upperBound){

        for (int i = 0; i < position.length; i++) {
            position[i] = lowerBound + random.nextInt(upperBound);
            velocity[i] = 0;
        }

        Arrays.sort(position);

    }

}
