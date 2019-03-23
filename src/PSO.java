import java.util.Random;

public class PSO {

    private static double M = 0.05;
    private static double c1 = 2;
    private static double c2 = 2;

    private static int[] scaleStructure; // Scale structure for particular scale
    static int[] possibleNotesForChord;
    static int[] possibleNotesForMelody;
    static Scale scale; // Type of scale
    static int tonality;
    static int[] globalBest;
    static int bestFitness = Integer.MAX_VALUE;

    private static Random random = new Random();


    /**
     *
     * Sets fitness value for chord swarms
     *
     * @param swarms
     */
    static void setFitnessForChord(Swarm[] swarms){

        for (int i = 0; i < swarms.length; i++) {

            int temp = 0;

            if (swarms[i].position[0] == possibleNotesForChord[0]) {

                temp += Math.abs(swarms[i].position[1] - possibleNotesForChord[2]) +
                        Math.abs(swarms[i].position[2] - possibleNotesForChord[4]);

            } else if (swarms[i].position[0] == possibleNotesForChord[3]) {

                temp += Math.abs(swarms[i].position[1] - possibleNotesForChord[5]) +
                        Math.abs(swarms[i].position[2] - possibleNotesForChord[7]);

            } else if (swarms[i].position[0] == possibleNotesForChord[4]) {

                temp += Math.abs(swarms[i].position[1] - possibleNotesForChord[6]) +
                        Math.abs(swarms[i].position[2] - possibleNotesForChord[8]);

            } else {
                temp += 200;
            }

            swarms[i].currentFitnessValue = temp;
        }
    }

    /**
     *
     * Sets fitness value for melody swarms
     *
     * @param swarms
     */

    static void setFitnessForMelody(Swarm[] swarms){

        for (int i = 0; i < swarms.length; i++) {

            int temp = 0;

            for (int j = 0; j < swarms[i].position.length; j++) {
                if ( swarms[i].position[j] > 60 && swarms[i].position[j] < 96)
                    temp += findNearest(swarms[i].position[j]);
                else
                    temp += 100;
            }

            swarms[i].currentFitnessValue = temp;
        }
    }

    /**
     *
     * Recalculates velocity for swarms
     *
     * @param swarm
     */

    static void recalculateVelocity(Swarm[] swarm){

        for (int i = 0; i < swarm.length; i++) {


            for (int j = 0; j < swarm[i].velocity.length; j++) {

                // New velocity
                swarm[i].velocity[j] = (M * swarm[i].velocity[j] +
                        c1 * random.nextDouble() * (swarm[i].myBest[j] - swarm[i].position[j])
                        + c2 * random.nextDouble() * (globalBest[j] - swarm[i].position[j]));

            }
        }
    }


    /**
     *
     * Recalculates position for swarms
     *
     * @param swarm
     */

    static void recalculatePosition(Swarm[] swarm){

        for (int i = 0; i < swarm.length; i++) {

            for (int j = 0; j < swarm[i].velocity.length; j++) {

                // New position
                swarm[i].position[j] = (int) Math.round(swarm[i].position[j] + swarm[i].velocity[j]);

            }
        }
    }

    /**
     *
     * Sets new local best score for swarms
     *
     * @param swarms
     */

    static void setNewMyBest(Swarm[] swarms) {

        for (int i = 0; i < swarms.length; i++) {
            for (int j = 0; j < swarms[i].myBest.length; j++) {

                if (swarms[i].currentFitnessValue < swarms[i].bestFitnessValue) {

                    swarms[i].myBest = swarms[i].position;
                    swarms[i].bestFitnessValue = swarms[i].currentFitnessValue;

                }
            }
        }
    }

    /**
     *
     * Sets new global best for swarms
     *
     * @param swarms
     */

    static void setGlobalBest(Swarm[] swarms){
        for (Swarm swarm:swarms
                ) {
            if (swarm.currentFitnessValue < bestFitness){
                globalBest = swarm.position;
                bestFitness = swarm.currentFitnessValue;
            }
        }
    }

    /**
     *
     * Generates new Scale for PSO
     *
     */

    static void generateScale(){

        if (random.nextBoolean()){
             scale = Scale.Major;
        }
        else {
            scale = Scale.Minor;
        }

        switch (scale){
            case Major: {
                PSO.scaleStructure = new int[]{2, 4, 5, 7, 9, 11};
                break;
            }
            case Minor: {
                PSO.scaleStructure = new int[]{2, 3, 5, 7, 8, 10};
                break;
            }
        }
    }

    /**
     *
     * Generates new tonality
     *
     */

    static void generateTonality(){
        tonality = 48 + random.nextInt(12);

        possibleNotesForChord = new int[]{tonality, tonality + scaleStructure[0], tonality + scaleStructure[1],
                tonality + scaleStructure[2], tonality + scaleStructure[3], tonality + scaleStructure[4],
                tonality + scaleStructure[5], tonality + 12, tonality + 12 + scaleStructure[0]};

        possibleNotesForMelody = new int[]{tonality % 12, (tonality + scaleStructure[0]) % 12,
                (tonality + scaleStructure[1]) % 12, (tonality + scaleStructure[2]) % 12,
                (tonality + scaleStructure[3]) % 12, (tonality + scaleStructure[4]) % 12,
                (tonality + scaleStructure[5]) % 12};

    }


    /**
     *
     * Performs smoothing on Inertion coeff
     *
     */

    static void smoothM(){
        if (M > 0.4) {
            M =+ -0.01;
        }
    }

    /**
     * Resets bestFitness and global best value of PSO
     */
    static void reset(){

        bestFitness = Integer.MAX_VALUE;
        globalBest = null;

    }

    /**
     *
     * Finds nearest note from gamma
     *
     * @return it position in PossibleNotesForMelody
     */
    private static int findNearest(int note){
        int bestDiff = Integer.MAX_VALUE;

        for (int melNote:possibleNotesForMelody
             ) {
            int diff = Math.abs(note % 12 - melNote);
            if (diff < bestDiff){
                bestDiff = diff;
            }
        }

        return bestDiff;
    }
}
