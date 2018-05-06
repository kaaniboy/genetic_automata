package genetic_automata;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class DisplayPanel extends JPanel {
	public DisplayPanel() {
		setPreferredSize(new Dimension(Main.WIDTH - ControlsPanel.WIDTH, Main.HEIGHT));
		setLayout(new BorderLayout());
	}
	
	// Display the fitness over epochs chart.
	public void showFitnessChart() {
		ChartPanel chartPanel = createFitnessChart();
		
		add(chartPanel, BorderLayout.CENTER);
		// Must validate for the chart to be displayed.
		validate();
		add(chartPanel);
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
	private ChartPanel createFitnessChart() {
		String title = "Fitness Over Epochs";
		String xAxisLabel = "Epoch";
		String yAxisLabel = "Fitness";
		
		XYSeriesCollection chartData = getFitnessData();
		
		JFreeChart chart = ChartFactory.createXYLineChart(title, xAxisLabel, yAxisLabel, chartData);
		
		return new ChartPanel(chart);
	}
}
