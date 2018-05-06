package genetic_automata;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class LeftPanel extends JPanel {
	public static final int WIDTH = 220;
	
	// Fields allowing the user to specify properties of the genetic algorithm.
	JPanel settingsPanel;
	JTextField mutationField;
	JTextField epochsField;
	JTextField populationField;
	JTextField elitismField;
	
	// Displays details on the outcome of the genetic algorithm.
	JPanel outcomePanel;
	JLabel epochsRunLabel;
	JLabel bestFitnessLabel;
	JLabel avgFitnessLabel;
	
	public LeftPanel() {
		setPreferredSize(new Dimension(WIDTH, Main.HEIGHT));
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(new EmptyBorder(10, 10, 10, 10));
		
		add(Box.createRigidArea(new Dimension(0, 10)));
		
		settingsPanel = createSettingsPanel();
		
		outcomePanel = createOutcomePanel();
		// Outcome panel is initially hidden.
		outcomePanel.setVisible(false);
		
		add(settingsPanel);
		add(outcomePanel);
		add(createButtonsPanel());
		
		add(Box.createVerticalGlue());
	}
	
	// Create the settings panel.
	private JPanel createSettingsPanel() {
		JPanel settingsPanel = new JPanel();
		settingsPanel.setPreferredSize(new Dimension(WIDTH, 120));
		
		settingsPanel.setBorder(BorderFactory.createTitledBorder("Genetic Algorithm Settings"));
		
		settingsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		
		JPanel populationRow = new JPanel(new BorderLayout());
		JPanel elitismRow = new JPanel(new BorderLayout());
		JPanel mutationRow = new JPanel(new BorderLayout());
		JPanel epochsRow = new JPanel(new BorderLayout());
		
		populationField = new JTextField("200", 5);
		elitismField = new JTextField("30", 5);
		mutationField = new JTextField("0.20", 5);
		epochsField = new JTextField("1000", 5);
		
		populationRow.add(new JLabel("Population Size: "), BorderLayout.WEST);
		populationRow.add(populationField, BorderLayout.EAST);
		
		elitismRow.add(new JLabel("Elitism Offset: "), BorderLayout.WEST);
		elitismRow.add(elitismField, BorderLayout.EAST);
		
		mutationRow.add(new JLabel("Mutation Rate: "), BorderLayout.WEST);
		mutationRow.add(mutationField, BorderLayout.EAST);
		
		epochsRow.add(new JLabel("Max Epochs: "), BorderLayout.WEST);
		epochsRow.add(epochsField, BorderLayout.EAST);
		
		settingsPanel.add(populationRow);
		settingsPanel.add(elitismRow);
		settingsPanel.add(mutationRow);
		settingsPanel.add(epochsRow);
		
		return settingsPanel;
	}
	
	// Create the buttons panel.
	private JPanel createButtonsPanel() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

		JButton startButton = new JButton("Start Algorithm");

		startButton.addActionListener((e) -> startAlgorithm());

		buttonPanel.add(startButton);
		
		return buttonPanel;
	}
	
	private JPanel createOutcomePanel() {
		JPanel outcomePanel = new JPanel();
		outcomePanel.setPreferredSize(new Dimension(WIDTH, 120));

		outcomePanel.setBorder(BorderFactory.createTitledBorder("Genetic Algorithm Outcome"));
		
		outcomePanel.add(Box.createRigidArea(new Dimension(0, 10)));
		
		epochsRunLabel = new JLabel();
		bestFitnessLabel = new JLabel();
		avgFitnessLabel = new JLabel();
		
		outcomePanel.add(epochsRunLabel);
		outcomePanel.add(bestFitnessLabel);
		outcomePanel.add(avgFitnessLabel);
		
		outcomePanel.add(Box.createRigidArea(new Dimension(0, 20)));

		JLabel noteLabel = new JLabel("<html><b>Note: A fitness of 1 <br/> indicates an optimal DFA.</b></html>");
		
		outcomePanel.add(noteLabel);
		
		return outcomePanel;
	}
	
	private void startAlgorithm() {
		try {
			// Parse the values provided by the user.
			int populationSize = Integer.parseInt(populationField.getText());
			int elitismOffset = Integer.parseInt(elitismField.getText());
			double mutationRate = Double.parseDouble(mutationField.getText());
			int epochs = Integer.parseInt(epochsField.getText());
			
			// Reset the background color of the settings panel.
			settingsPanel.setBackground(UIManager.getColor("Panel.background"));
			
			// Initiate the algorithm with the provided settings.
			Main.startAlgorithm(populationSize, elitismOffset, mutationRate, epochs);
			
			// Display the outcome of the algorithm.
			epochsRunLabel.setText("Epochs Run: " + Main.algorithm.getCurrentEpoch());
			
			// Retrieve the best and average fitness of the final population.
			double bestFitness = Main.algorithm.getBestFitnessOverEpochs().get
					(Main.algorithm.getBestFitnessOverEpochs().size() - 1);
			
			double avgFitness = Main.algorithm.getAvgFitnessOverEpochs().get
					(Main.algorithm.getAvgFitnessOverEpochs().size() - 1);
			
			DecimalFormat fmt = new DecimalFormat("0.00");
			
			bestFitnessLabel.setText("Best Final Fitness: " + fmt.format(bestFitness));
			avgFitnessLabel.setText("Avg Final Fitness: " + fmt.format(avgFitness));
			
			outcomePanel.setVisible(true);
			validate();
		} catch (Exception e) {
			// The user entered an invalid value.
			System.out.println("Invalid/malformed settings provided.");
			
			Color errorColor = new Color(240,128,128);
			
			// Change the background color to indicate an error.
			settingsPanel.setBackground(errorColor);
		}
	}
}
