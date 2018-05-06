package genetic_automata;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxParallelEdgeLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

public class DisplayPanel extends JPanel {
	// Size (in pixels) of states in the DFA that is displayed.
	public static final int STATE_SIZE = 30;
	
	public static int[] X_POSITIONS = new int[] { 120, 20, 220, 40, 200 };
	public static int[] Y_POSITIONS = new int[] { 20, 120, 120, 200, 200 }; 
	
	private JPanel dfaGraphPanel;
	private JPanel fitnessChartPanel;
	
	public DisplayPanel() {
		setPreferredSize(new Dimension(Main.WIDTH - LeftPanel.WIDTH, Main.HEIGHT));
		setLayout(new BorderLayout());
		
		// Create panels to display the DFA and fitness chart.
		dfaGraphPanel = new JPanel();
		dfaGraphPanel.setPreferredSize(new Dimension(Main.WIDTH - LeftPanel.WIDTH, Main.HEIGHT / 2));
		
		fitnessChartPanel = new JPanel();
		fitnessChartPanel.setPreferredSize(new Dimension(Main.WIDTH - LeftPanel.WIDTH, Main.HEIGHT / 2));
		
		add(dfaGraphPanel, BorderLayout.NORTH);
		add(fitnessChartPanel, BorderLayout.SOUTH);
	}
	
	// Show the DFA graph.
	public void showDFAGraph() {
		// Delete the prior DFA graph.
		dfaGraphPanel.removeAll();
		dfaGraphPanel.revalidate();
		
		dfaGraphPanel.add(createDFAGraph(), BorderLayout.NORTH);
	}
	
	// Create graph representing the best DFA produced by the genetic algorithm.
	public mxGraphComponent createDFAGraph() {
		mxGraph dfaGraph = new mxGraph();
		Object parent = dfaGraph.getDefaultParent();
		
		mxStylesheet stylesheet = dfaGraph.getStylesheet();
		
		// Make DFA states circular.
		Hashtable<String, Object> roundedStyle = new Hashtable<String, Object>();
		roundedStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
		
		// Make accept states have a double circle.
		Hashtable<String, Object> acceptStyle = new Hashtable<String, Object>();
		acceptStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_DOUBLE_ELLIPSE);
		
		stylesheet.putCellStyle("ROUNDED", roundedStyle);
		stylesheet.putCellStyle("ACCEPT", acceptStyle);
		
		dfaGraph.getModel().beginUpdate();
		
		// Retrieve the best DFA produced by the genetic algorithm.
		DFA bestDFA = Main.algorithm.getPopulation().get(0);
		System.out.println(Arrays.toString(bestDFA.getAcceptStates()));
		
		System.out.println(bestDFA.run("1010"));
		
		try {
			List<Object> states = new ArrayList<>();
			
			for (int i = 0; i < DFA.STATE_COUNT; i++) {
				String options = bestDFA.getAcceptStates()[i] ? "ACCEPT;" : "ROUNDED;";
				
				String stateTitle = "Q" + i;
				
				// The start state is marked yellow and has a specific name.
				if (bestDFA.getStartState() == i) {
					options += "fillColor=yellow;";
					stateTitle = "start";
				}
				
				// Create a new state and add it to the DFA graph.
				Object state = dfaGraph.insertVertex(parent, null, stateTitle, X_POSITIONS[i], Y_POSITIONS[i], STATE_SIZE, STATE_SIZE, options);
				states.add(state);
			}
			
			for (int y = 0; y < DFA.STATE_COUNT; y++) {
				// Insert edge associated with the DFA seeing a '0'.
				dfaGraph.insertEdge(parent, null, "0", states.get(y), states.get(bestDFA.getDelta()[y][0]));
				
				// Insert edge associated with the DFA seeing a '1'.
				dfaGraph.insertEdge(parent, null, "1", states.get(y), states.get(bestDFA.getDelta()[y][1]));
			}
			
			dfaGraph.insertEdge(parent, null, "DANK", null, states.get(0));
			
			// Prevent overlapping of edges.
		    new mxParallelEdgeLayout(dfaGraph).execute(parent);
		} finally {
			dfaGraph.getModel().endUpdate();
		}
		
		mxGraphComponent graphComponent = new mxGraphComponent(dfaGraph);
		return graphComponent;
	}
	
	// Display the fitness over epochs chart.
	public void showFitnessChart() {
		ImageIcon chartImage = createFitnessChart();
		
		// Delete the prior fitness chart.
		fitnessChartPanel.removeAll();
		fitnessChartPanel.revalidate();
		
		fitnessChartPanel.add(new JLabel(chartImage));
		// Must validate for the chart to be displayed.
		
		fitnessChartPanel.validate();
	}
	
	// Aggregate fitness scores over epochs to be displayed in the chart.
	private XYSeriesCollection getFitnessData() {
		XYSeriesCollection data = new XYSeriesCollection();
		
		// Display both the average fitness and best fitness of each epoch.
		XYSeries avgSeries = new XYSeries("Avg Fitness");
		XYSeries bestSeries = new XYSeries("Best Fitness");
		
		List<Double> avgOverEpochs = Main.algorithm.getAvgFitnessOverEpochs();
		List<Double> bestOverEpochs = Main.algorithm.getBestFitnessOverEpochs();
		
		for (int i = 0; i < bestOverEpochs.size(); i++) {
			avgSeries.add(i, avgOverEpochs.get(i));
			bestSeries.add(i, bestOverEpochs.get(i));
		}
		
		data.addSeries(avgSeries);
		data.addSeries(bestSeries);
		
		return data;
	}
	
	// Create the fitness over epochs chart.
	private ImageIcon createFitnessChart() {
		String title = "DFA Fitness Over Epochs";
		String xAxisLabel = "Epoch";
		String yAxisLabel = "Fitness";
		
		XYSeriesCollection chartData = getFitnessData();
		
		JFreeChart chart = ChartFactory.createXYLineChart(title, xAxisLabel, yAxisLabel, chartData);
		
		return new ImageIcon(chart.createBufferedImage(Main.WIDTH - LeftPanel.WIDTH, Main.HEIGHT / 2));
	}
}
