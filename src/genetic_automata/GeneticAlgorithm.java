package genetic_automata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GeneticAlgorithm {
	// Size of the population of DFAs.
	public static int POPULATION_SIZE = 200;

	// Number of DFAs to keep (without changing at all) across generations.
	public static int ELITISM_OFFSET = 30;

	// Probability of a DFA being selected for mutation.
	public static double MUTATION_RATE = 0.2;

	// The maximum number of iterations to run the genetic algorithm.
	public static int MAX_EPOCHS = 1000;

	// Average fitness at each epoch.
	private List<Double> avgFitnessOverEpochs;
	// Best fitness at each epoch.
	private List<Double> bestFitnessOverEpochs;

	// The population of DFAs.
	private List<DFA> population;
	
	// Tracks the current epoch in the algorithm.
	private int currentEpoch = 0;

	// Training examples for the genetic algorithm.
	private String[] inputs;
	private boolean[] expected;

	private Random rand;

	public GeneticAlgorithm(String[] inputs, boolean[] expected) {
		initializePopulation();
		rand = new Random();
		
		avgFitnessOverEpochs = new ArrayList<>();
		bestFitnessOverEpochs = new ArrayList<>();
		
		this.inputs = inputs;
		this.expected = expected;
	}

	// Create the initial population of random DFAs.
	private void initializePopulation() {
		population = new ArrayList<>();

		for (int i = 0; i < POPULATION_SIZE; i++) {
			population.add(new DFA());
		}
	}

	// Calculate the fitness of each DFA in the population.
	private void calculatePopulationFitness() {
		// Calculate the fitness of each DFA in the population.
		for (DFA dfa : population) {
			dfa.calculateFitness(inputs, expected);
		}

		// Sort in decreasing order of fitness.
		population.sort(new FitnessComparator());
	}

	/*
	 * Implementation of roulette selection for choosing a DFA in the
	 * population. DFAs with higher fitness are selected more often.
	 */
	private DFA rouletteSelect() {
		// Calculate the sum of all the fitnesses of the DFAs.
		double fitnessSum = 0;

		for (DFA dfa : population) {
			fitnessSum += dfa.getFitness();
		}

		double value = rand.nextDouble();
		double threshold = 0;

		for (DFA dfa : population) {
			threshold += dfa.getFitness() / fitnessSum;

			// Choose the DFA if it is less than the current threshold.
			if (value <= threshold) {
				return dfa;
			}
		}

		return null;
	}

	// Create a new DFA from the crossover of two parent DFAs.
	private DFA crossover(DFA first, DFA second) {
		// Attributes of the new child DFA being created.
		int[][] childDelta = new int[DFA.STATE_COUNT][DFA.ALPHABET_SIZE];
		boolean[] childAcceptStates = new boolean[DFA.STATE_COUNT];
		int childStartState = 0;

		Random rand = new Random();

		// Calculate an arbitrary crossover point for the delta function.
		int crossoverRow = rand.nextInt(DFA.STATE_COUNT);
		int crossoverCol = rand.nextInt(DFA.ALPHABET_SIZE);

		for (int y = 0; y < DFA.STATE_COUNT; y++) {
			for (int x = 0; x < DFA.ALPHABET_SIZE; x++) {
				// Choose the child's delta from either to first or second parent.
				if (y >= crossoverRow && x >= crossoverCol) {
					// If after the crossover point, use the second parent's delta function.
					childDelta[y][x] = second.getDelta()[y][x];
				} else {
					// If before the crossover point, use the first parent's delta function.
					childDelta[y][x] = first.getDelta()[y][x];
				}
			}
		}

		// Calculate an arbitrary crossover point for the accept states.
		int acceptCrossover = rand.nextInt(DFA.STATE_COUNT);

		for (int i = 0; i < DFA.STATE_COUNT; i++) {
			if (i >= acceptCrossover) {
				childAcceptStates[i] = second.getAcceptStates()[i];
			} else {
				childAcceptStates[i] = first.getAcceptStates()[i];
			}
		}

		// Random select one of the parent's start states.
		if (rand.nextBoolean()) {
			childStartState = first.getStartState();
		} else {
			childStartState = second.getStartState();
		}

		// Create the new child DFA.
		return new DFA(childDelta, childAcceptStates, childStartState);
	}

	// Calculate the average fitness of the entire population.
	private double calculateAvgFitness() {
		double fitnessSum = 0;

		for (DFA dfa : population) {
			fitnessSum += dfa.getFitness();
		}

		return fitnessSum / POPULATION_SIZE;
	}

	// Run the genetic algorithm and return the best DFA.
	public DFA runEpochs() {
		double bestFitness = 0;

		while (bestFitness != 1.0 && currentEpoch <= MAX_EPOCHS) {
			currentEpoch++;

			calculatePopulationFitness();

			// Log data regarding the best/average fitness of the current epoch's population.
			avgFitnessOverEpochs.add(calculateAvgFitness());
			bestFitnessOverEpochs.add(population.get(0).getFitness());

			// The optimal DFA has been created, so the algorithm can be terminated.
			if (population.get(0).getFitness() == 1.0) {
				break;
			}

			List<DFA> nextPopulation = new ArrayList<>();

			// Add the best DFAs of the current population to the next
			// population.
			for (int i = 0; i < ELITISM_OFFSET; i++) {
				nextPopulation.add(population.get(i));
			}

			// Add children of DFAs chosen via roulette selection to the next population.
			for (int i = 0; i < POPULATION_SIZE - ELITISM_OFFSET; i++) {
				nextPopulation.add(crossover(rouletteSelect(), rouletteSelect()));
			}

			// There is a possibility of mutating DFAs in the next population.
			for (DFA dfa : nextPopulation) {
				if (rand.nextDouble() <= MUTATION_RATE) {
					dfa.mutate();
				}
			}

			// Replace the old population with the newly created one.
			population = nextPopulation;
		}

		// Return the best DFA of the final population.
		population.sort(new FitnessComparator());
		return population.get(0);
	}
	
	public void setSettings(int populationSize, int elitismOffset, double mutationRate, int epochs) {
		POPULATION_SIZE = populationSize;
		ELITISM_OFFSET = elitismOffset;
		MUTATION_RATE = mutationRate;
		MAX_EPOCHS = epochs;
		
		initializePopulation();
	}

	public List<Double> getAvgFitnessOverEpochs() {
		return avgFitnessOverEpochs;
	}
	
	public List<Double> getBestFitnessOverEpochs() {
		return bestFitnessOverEpochs;
	}
	
	public List<DFA> getPopulation() {
		return population;
	}
	
	public int getCurrentEpoch() {
		return currentEpoch;
	}
}
