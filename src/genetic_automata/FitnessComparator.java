package genetic_automata;

import java.util.Comparator;

// Necessary to compare and sort DFAs based on their fitness.

public class FitnessComparator implements Comparator<DFA> {

	@Override
	// DFAs with higher fitness come first.
	public int compare(DFA first, DFA second) {
		if (first.getFitness() > second.getFitness()) {
			return -1;
		} else {
			return 1;
		}
	}
}
