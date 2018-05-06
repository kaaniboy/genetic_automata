package genetic_automata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GeneticAlgorithm {
	// Size of the population of DFAs.
	public static final int POPULATION_SIZE = 100;

	// Number of DFAs to keep (without changing at all) across generations.
	public static final int ELITISM_OFFSET = 30;

	// Probability of a DFA being selected for mutation.
	public static final double MUTATION_RATE = 0.05;

	// The maximum number of iterations to run the genetic algorithm.
	public static final int MAX_EPOCHS = 1000;

	private List<DFA> population;

	// Training examples for the genetic algorithm.
	private String[] inputs;
	private boolean[] expected;

	private Random rand;

	public GeneticAlgorithm(String[] inputs, boolean[] expected) {
		initializePopulation();
		rand = new Random();

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
				// Choose the child's delta from either to first or second
				// parent.
				if (y >= crossoverRow && x >= crossoverCol) {
					// If after the crossover point, use the second parent's
					// delta function.
					childDelta[y][x] = second.getDelta()[y][x];
				} else {
					// If before the crossover point, use the first parent's
					// delta function.
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
	private double calculateAverageFitness() {
		double fitnessSum = 0;

		for (DFA dfa : population) {
			fitnessSum += dfa.getFitness();
		}

		return fitnessSum / POPULATION_SIZE;
	}

	// Run the genetic algorithm and return the best DFA.
	public DFA runEpochs() {
		int epoch = 0;
		double bestFitness = 0;
		
		while (bestFitness != 1.0 && epoch <= MAX_EPOCHS) {
			epoch++;
			
			calculatePopulationFitness();
			
			// The optimal DFA has been created, so the algorithm can be terminated.
			if (population.get(0).getFitness() == 1.0) {
				break;
			}

			double avgFitness = calculateAverageFitness();
			System.out.printf("EPOCH %d AVG FITNESS: %f\n", epoch, avgFitness);
			
			List<DFA> nextPopulation = new ArrayList<>();

			// Add the best DFAs of the current population to the next
			// population.
			for (int i = 0; i < ELITISM_OFFSET; i++) {
				nextPopulation.add(population.get(i));
			}

			// Add children of DFAs chosen via roulette selection to the next
			// population.
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
}
