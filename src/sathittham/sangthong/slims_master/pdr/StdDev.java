package sathittham.sangthong.slims_master.pdr;

import java.util.LinkedList;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import sathittham.sangthong.slims_master.MainActivity;

/**
 * An implementation to calculate standard deviation from a rolling window.
 * 
 */
public class StdDev {
	private LinkedList<Double> list = new LinkedList<Double>();
	private double stdDev;
	private DescriptiveStatistics stats = new DescriptiveStatistics();

	/**
	 * Add a sample to the rolling window.
	 * 
	 * @param value
	 *            The sample value.
	 * @return The variance of the rolling window.
	 */
	public double addSample(double value) {
		list.addLast(value);

		enforceWindow();

		return calculateStdDev();
	}

	/**
	 * Enforce the rolling window.
	 */
	private void enforceWindow() {
		if (list.size() > MainActivity.getSampleWindow()) {
			list.removeFirst();
		}
	}

	/**
	 * Calculate the variance of the rolling window.
	 * 
	 * @return The variance of the rolling window.
	 */
	private double calculateStdDev() {
		if (list.size() > 5) {
			stats.clear();

			// Add the data from the array
			for (int i = 0; i < list.size(); i++) {
				stats.addValue(list.get(i));
			}

			stdDev = stats.getStandardDeviation();
		}

		return stdDev;
	}

}
