/*
 * KAAN AKSOY (1210619069)
 * CSE355 Honors Contract, 5/06/2018
 * 
 * This application uses genetic algorithms to 
 * automatically generate deterministic finite
 * automata for given languages. In this case,
 * the target language is the set of all binary
 * strings that are divisible by 5. The application
 * allows the user to specify various parameters
 * for the algorithm (ie, the mutation rate and
 * population size). It also allows the user to
 * view a visual representation of the created
 * DFA as well as a graph of how DFA fitness
 * changes across epochs.
 * 
 * The application uses the JFreeChart library to
 * create charts, and the JGraphX library to 
 * create DFA visualizations.
 * 
 */
package genetic_automata;

import java.awt.BorderLayout;
import java.awt.Container;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

public class Main extends JFrame {
	// Dimensions of the GUI window.
	public static final int WIDTH = 820;
	public static final int HEIGHT = 600;
	
	// Various subpanels of the GUI.
	private static LeftPanel controlsPanel;
	private static DisplayPanel displayPanel;
	
	// Training data for the genetic algorithm.
	public static final int TRAINING_SIZE = 100;
	private static List<String> inputs;
	private static List<Boolean> expected;
	
	public static GeneticAlgorithm algorithm;
	
	public Main() {
		controlsPanel = new LeftPanel();
		displayPanel = new DisplayPanel();
		
		setSize(WIDTH, HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setTitle("Genetic Automata - Kaan Aksoy");
		
		Container cont = getContentPane();
		cont.setLayout(new BorderLayout());
		
		cont.add(controlsPanel, BorderLayout.WEST);
		cont.add(displayPanel, BorderLayout.CENTER);
	}
	
	public static void main(String[] args) {
		createTrainingExamples();
		
		// Start the application.
		Main gui = new Main();
		gui.setVisible(true);
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
			
			// Only use the number if it isn't a multiple of 5.
			if (num % 5 != 0) {
				inputs.add(Integer.toBinaryString(num));
				expected.add(false);
				count++;
			}
		}
	}

	// Start the algorithm with the settings specified by the user in the settings panel.
	public static void startAlgorithm(int populationSize, int elitismOffset, double mutationRate, int epochs) {
		// Convert lists to arrays before passing to the algorithm.
		String[] inputsArray = new String[TRAINING_SIZE];
		boolean[] expectedArray = new boolean[TRAINING_SIZE];
		
		for (int i = 0; i < TRAINING_SIZE; i++) {
			inputsArray[i] = inputs.get(i);
			expectedArray[i] = expected.get(i);
		}
		
		algorithm = new GeneticAlgorithm(inputsArray, expectedArray);
		algorithm.setSettings(populationSize, elitismOffset, mutationRate, epochs);
		
		algorithm.runEpochs();
		
		displayPanel.showFitnessChart();
		displayPanel.showDFAGraph();
	}
}
