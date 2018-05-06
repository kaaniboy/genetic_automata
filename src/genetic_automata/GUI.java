package genetic_automata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GUI {
	public static final int TRAINING_SIZE = 100;
	private static List<String> inputs;
	private static List<Boolean> expected;
	
	public static void main(String[] args) {
		createTrainingExamples();
		
		String[] inputsArray = new String[TRAINING_SIZE];
		boolean[] expectedArray = new boolean[TRAINING_SIZE];
		
		for (int i = 0; i < TRAINING_SIZE; i++) {
			inputsArray[i] = inputs.get(i);
			expectedArray[i] = expected.get(i);
		}
		
		GeneticAlgorithm ga = new GeneticAlgorithm(inputsArray, expectedArray);
		
		DFA best = ga.runEpochs();
		System.out.println(best.calculateFitness(inputsArray, expectedArray));
		
		System.out.println(best.run("01011"));
	}
	
	// Create training examples for the language: binary numbers that are a multiple of 5.
	public static void createTrainingExamples() {
		inputs = new ArrayList<>();
		expected = new ArrayList<>();
		
		int num = 0;
		// Generate examples that should be accepted (multiple of 5).
		for (int i = 0; i < TRAINING_SIZE / 2; i++) {
			// Convert the number to its binary string.
			inputs.add(Integer.toBinaryString(num));
			
			expected.add(true);
			num += 5;
		}
		
		// Generate examples that should be rejected (not multiple of 5).
		int count = 0;
		Random rand = new Random();
		
		while (count < TRAINING_SIZE / 2) {
			num = rand.nextInt(1000);
			
			if (num % 5 != 0) {
				inputs.add(Integer.toBinaryString(num));
				expected.add(false);
				count++;
			}
		}
	}
}
