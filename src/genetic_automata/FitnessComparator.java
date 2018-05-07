package genetic_automata;

import java.util.Comparator;

// Necessary to compare and sort DFAs based on their fitness.
public class FitnessComparator implements Comparator<DFA> {

	@Override
	// DFAs with higher fitness come first.
	public int compare(DFA first, DFA second) {
		double firstFitness = first.getFitness();
		double secondFitness = second.getFitness();

		if (firstFitness > secondFitness) {
			return -1;
		} else if (firstFitness == secondFitness) {
			return 0;
		} else {
			return 1;
		}
	}
}
