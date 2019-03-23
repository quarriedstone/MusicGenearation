import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;
import jm.util.View;
import jm.util.Write;

import java.sql.Time;
import java.util.Arrays;
import java.util.Random;

public class Main {

    public static void main(String[] args) {

        Random random = new Random();


        int chordBound = 14;
        int swarmChordNum = 20;
        int swarmMelodyNum = 5;

        int chords[][] = new int[8][3];
        int melody[][] = new int[16][1];


        PSO.generateScale(); // Randomly generate scale
        PSO.generateTonality(); // Randomly generating tonality



        Swarm[] chordSwarms = new Swarm[swarmChordNum];
        Swarm[] melodySwarms = new Swarm[swarmMelodyNum];

        System.out.println("Tonality: " + PSO.tonality);
        System.out.println("Scale: " + PSO.scale);
        System.out.println("PossibleNotesForChord: " + Arrays.toString(PSO.possibleNotesForChord));
        System.out.println("PossibleNotesForMelody: " + Arrays.toString(PSO.possibleNotesForMelody));
        System.out.println();


        int previousNote = PSO.tonality;
        int count = 0;

        // Chords generation
        for (int i = 0; i < 8; i++) {

            // Initializing chord swarms
            for (int j = 0; j < swarmChordNum; j++) {

                chordSwarms[j] = new Swarm(3);
                chordSwarms[j].generateRandomInitValues(PSO.tonality, chordBound);
            }

            long k = 0;
            while (PSO.bestFitness != 0) {

                PSO.setFitnessForChord(chordSwarms);

                PSO.setNewMyBest(chordSwarms);
                PSO.setGlobalBest(chordSwarms);

                PSO.recalculateVelocity(chordSwarms);
                PSO.recalculatePosition(chordSwarms);

                if (k == 10){
                    PSO.reset();
                    // Initializing chord swarms
                    for (int j = 0; j < swarmChordNum; j++) {
                        chordSwarms[j] = null;
                        chordSwarms[j] = new Swarm(3);
                        chordSwarms[j].generateRandomInitValues(PSO.tonality, chordBound);
                    }
                    k = 0;
                }

                k++;
            }

            // First and last chords should be tonic chords
            if ((i == 0 || i == 7) && PSO.globalBest[0] != PSO.tonality){
                i--;
            }
            else {
                //Checking for repetition of chords
                if (previousNote == PSO.globalBest[0]){
                    count++;
                }
                else {
                    count = 0;
                }

                //Checking number of chords
                if (count == 2){
                    i--;
                }
                else {
                    chords[i] = PSO.globalBest;
                    System.out.println(Arrays.toString(chords[i]));
                }
            }

            PSO.reset();
        }



        System.out.println();

        long start_time = System.nanoTime();
        // Melody generation
        for (int i = 0; i < 16; i++) {
            
            // Initializing melody swarms
            for (int j = 0; j < swarmMelodyNum; j++) {
                melodySwarms[j] = new Swarm(1);
                melodySwarms[j].generateRandomInitValues(PSO.tonality + chordBound,
                        96 - ( PSO.tonality + chordBound));
            }

            long k = 0;
            while (PSO.bestFitness != 0) {

                PSO.setFitnessForMelody(melodySwarms);

                PSO.setNewMyBest(melodySwarms);
                PSO.setGlobalBest(melodySwarms);

                PSO.recalculateVelocity(melodySwarms);
                PSO.recalculatePosition(melodySwarms);

                if (k == 10){
                    PSO.reset();


                    // Initializing chord swarms
                    for (int j = 0; j < swarmMelodyNum; j++) {
                        melodySwarms[j] = null;
                        melodySwarms[j] = new Swarm(1);
                        melodySwarms[j].generateRandomInitValues(PSO.tonality + chordBound,
                                96 - ( PSO.tonality + chordBound));
                    }

                    k = 0;
                }
                
                k++;
            }

            // First and last notes should be tonic notes
            if ((i == 0 || i == 15) && PSO.globalBest[0] % 12 != PSO.tonality % 12){
                i--;
            }
            else {
                melody[i] = PSO.globalBest;
                System.out.println(Arrays.toString(melody[i]));
            }

            PSO.reset();
        }


        // Saving music and generating patterns
        int drumsInstrument = 9;
        int chordInstrument = 0;
        int melodyInstrument = 0;


        // Generating instruments pairs
        switch (random.nextInt(5)){
            case 0: {
                chordInstrument = 43;
                melodyInstrument = 41;
                break;
            }
            case 1:{
                chordInstrument = 6;
                melodyInstrument = 25;
                break;
            }
            case 2:{
                chordInstrument = 104;
                melodyInstrument = 27;
                break;
            }
            case 3:{
                chordInstrument = 61;
                melodyInstrument = 58;
                break;
            }
            case 4:{
                chordInstrument = 35;
                melodyInstrument = 27;
                break;
            }
        }



        // Setting up MIDI output
        Score score = new Score("music");
        score.setTempo( 110 );
        Part p1 = new Part("Chords", chordInstrument, 0);
        Part p2 = new Part("Melody", melodyInstrument, 1);
        Part drums = new Part("Snare", drumsInstrument , 9);



        // Collecting chords in patterns
        if (random.nextBoolean()){

            for (int[] chord:chords
                    ) {
                Phrase p = new Phrase();
                p.setPan(0.5);
                p.addChord(chord, 2);
                p1.add(p);
            }

            for (int[] chord:chords
                    ) {
                Phrase p = new Phrase();
                p.setPan(0.5);
                p.addChord(chord, 2);
                p1.add(p);
            }
        }
        else
        {
            for (int i = 0; i < 4; i++) {
                Phrase p = new Phrase();
                p.setPan(0.5);
                p.addChord(chords[i], 2);
                p1.add(p);
            }

            for (int i = 4; i < 8; i++) {
                Phrase p = new Phrase();
                p.setPan(0.5);
                p.addChord(chords[i], 2);
                p1.add(p);
            }

            for (int i = 4; i < 8; i++) {
                Phrase p = new Phrase();
                p.setPan(0.5);
                p.addChord(chords[i], 2);
                p1.add(p);
            }

            for (int i = 0; i < 4; i++) {
                Phrase p = new Phrase();
                p.setPan(0.5);
                p.addChord(chords[i], 2);
                p1.add(p);
            }
        }


        //Collecting notes in patterns
        if (random.nextBoolean()) {

            for (int[] note : melody
                    ) {
                Phrase p = new Phrase();
                p.addNoteList(note, 1);
                p2.add(p);
            }

            for (int[] note : melody
                    ) {
                Phrase p = new Phrase();
                p.addNoteList(note, 1);
                p2.add(p);
            }
        }
        else {

            for (int i = 0; i < 8; i++) {
                Phrase p = new Phrase();
                p.addNoteList(melody[i], 1);
                p2.add(p);
            }

            for (int i = 8; i < 16; i++) {
                Phrase p = new Phrase();
                p.addNoteList(melody[i], 1);
                p2.add(p);
            }

            for (int i = 8; i < 16; i++) {
                Phrase p = new Phrase();
                p.addNoteList(melody[i], 1);
                p2.add(p);
            }

            for (int i = 0; i < 8; i++) {
                Phrase p = new Phrase();
                p.addNoteList(melody[i], 1);
                p2.add(p);
            }

        }

        Phrase phr = new Phrase(0.0);

        double[] pattern = new double[5];
        double[] pattern0 = {1.0, 0.5, 0.5, 1.5, 0.5};
        double[] pattern1 = {0.5, 0.5, 1.5, 0.5, 1.0};
        double[] pattern2 = {2.0, 0.5, 0.5, 0.5, 0.5};
        double[] pattern3 = {1.5, 0.5, 1.0, 0.5, 0.5};

        for(int i = 0; i < 8; i++) {

            int x = (int) (Math.random() * 4);
            switch (x) {
                case 0:
                    pattern = pattern0;
                    break;
                case 1:
                    pattern = pattern1;
                    break;
                case 2:
                    pattern = pattern2;
                    break;
                case 3:
                    pattern = pattern3;
                    break;
            }
            for (short j = 0; j < pattern.length; j++) {
                Note note = new Note( 40 , pattern[j]);
                phr.addNote(note);
            }

        }


        drums.add(phr);

        score.add(drums);
        score.add(p1);
        score.add(p2);
        View.show(score);

        Write.midi(score, "music.mid");
    }
}
