package genetic_automata;

import java.util.HashSet;
import java.util.Random;

public class DFA {
	// Number of states in the DFA.
	public static final int STATE_COUNT = 5;

	// Size of the alphabet recognized by the DFA.
	public static final int ALPHABET_SIZE = 2;

	// Probability of a state being an accept state in the initial random DFA.
	public static final double ACCEPT_RATE = 0.20;
	
	/* 
	 * Probability that a particular attribute of the DFA will be mutated.
	 * If the DFA is not selected for mutation in the first place, this has no effect.
	 */
	public static final double MUTATION_RATE = 0.10;

	// Transition (delta) function of the DFA.
	private int[][] delta;

	// Start state of the DFA.
	private int startState;

	// Accept states of the DFA.
	boolean[] acceptStates;
	
	// Fitness of the DFA.
	private double fitness;

	// Create a random DFA.
	public DFA() {
		delta = new int[STATE_COUNT][ALPHABET_SIZE];

		Random rand = new Random();

		// Randomize the delta function.
		for (int y = 0; y < STATE_COUNT; y++) {
			for (int x = 0; x < ALPHABET_SIZE; x++) {
				delta[y][x] = rand.nextInt(STATE_COUNT);
			}
		}

		// Randomize the accept states.
		acceptStates = new boolean[STATE_COUNT];

		for (int i = 0; i < STATE_COUNT; i++) {
			if (rand.nextDouble() <= ACCEPT_RATE) {
				acceptStates[i] = true;
			}
		}

		// Randomize the start state.
		startState = rand.nextInt(STATE_COUNT);
		
		fitness = 0;
	}

	// Create a DFA with the specified delta function and accept states.
	public DFA(int[][] delta, boolean[] acceptStates, int startState) {
		this.delta = delta;
		this.acceptStates = acceptStates;
		this.startState = startState;
		fitness = 0;
	}

	/*
	 * Run the DFA on the input. Return true if the DFA accepts, false
	 * otherwise.
	 */
	public boolean run(String input) {
		int state = 0;

		for (char c : input.toCharArray()) {
			int symbol = Character.getNumericValue(c);

			// Move to the next state according to the delta function.
			state = delta[state][symbol];
		}

		return acceptStates[state];
	}

	// Calculate the fitness of the DFA on the provided examples.
	public double calculateFitness(String[] inputs, boolean[] expected) {
		int correct = 0;

		for (int i = 0; i < inputs.length; i++) {
			// Check if the output of the DFA matches the expected output.
			if (run(inputs[i]) == expected[i]) {
				correct++;
			}
		}
		
		fitness = (correct * 1.0) / inputs.length;
		return fitness;
	}
	
	// Mutates the DFA according to the specified mutation rate.
	public void mutate() {
		Random rand = new Random();
		
		// Mutate the DFA's delta function.
		for (int y = 0; y < STATE_COUNT; y++) {
			for (int x = 0; x < ALPHABET_SIZE; x++) {
				if (rand.nextDouble() <= MUTATION_RATE) {
					delta[y][x] = rand.nextInt(STATE_COUNT);
				}
			}
		}
		
		// Mutate the DFA's accept states.
		for (int i = 0; i < STATE_COUNT; i++) {
			if (rand.nextDouble() <= MUTATION_RATE) {
				acceptStates[i] = !acceptStates[i];
			}
		}
		
		// TODO: Not sure if it makes sense to mutate the start state.
	}
	
	public int[][] getDelta() {
		return delta;
	}
	
	public boolean[] getAcceptStates() {
		return acceptStates;
	}
	
	public int getStartState() {
		return startState;
	}
	
	// Returns the fitness only AFTER it has been calculated earlier.
	public double getFitness() {
		return fitness;
	}
}
