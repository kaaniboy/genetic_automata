package genetic_automata;

import java.util.Arrays;

public class GUI {
	public static void main(String[] args) {
		DFA dfa = new DFA();
		boolean result = dfa.run("010");
		
		System.out.println(result);
	}
}
