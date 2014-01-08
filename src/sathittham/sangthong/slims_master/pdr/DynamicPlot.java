package sathittham.sangthong.slims_master.pdr;

import java.util.LinkedList;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.SparseArray;

/**
 * Acceleration View is responsible for creating and managing all of the View
 * components related to Acceleration.
 */
public class DynamicPlot {
	private static final int VERTEX_WIDTH = 2;
	private static final int LINE_WIDTH = 2;

	private int windowSize = 50;

	private double maxRange = 10;
	private double minRange = -10;

	private XYPlot dynamicPlot;

	private SparseArray<SimpleXYSeries> series;
	private SparseArray<LinkedList<Number>> history;

	/**
	 * Initialize a new Acceleration View object.
	 * 
	 * @param activity
	 *            the Activity that owns this View.
	 */
	public DynamicPlot(XYPlot dynamicPlot) {
		this.dynamicPlot = dynamicPlot;

		series = new SparseArray<SimpleXYSeries>();
		history = new SparseArray<LinkedList<Number>>();

		initPlot();
	}

	/**
	 * Get the max range of the plot.
	 * 
	 * @return Returns the maximum range of the plot.
	 */
	public double getMaxRange() {
		return maxRange;
	}

	/**
	 * Get the min range of the plot.
	 * 
	 * @return Returns the minimum range of the plot.
	 */
	public double getMinRange() {
		return minRange;
	}

	/**
	 * Get the window size of the plot.
	 * 
	 * @return Returns the window size of the plot.
	 */
	public int getWindowSize() {
		return windowSize;
	}

	/**
	 * Set the max range of the plot.
	 * 
	 * @param maxRange
	 *            The maximum range of the plot.
	 */
	public void setMaxRange(double maxRange) {
		this.maxRange = maxRange;
		dynamicPlot.setRangeBoundaries(minRange, maxRange, BoundaryMode.FIXED);
	}

	/**
	 * Set the min range of the plot.
	 * 
	 * @param minRange
	 *            The minimum range of the plot.
	 */
	public void setMinRange(double minRange) {
		this.minRange = minRange;
		dynamicPlot.setRangeBoundaries(minRange, maxRange, BoundaryMode.FIXED);
	}

	/**
	 * Set the plot window size.
	 * 
	 * @param windowSize
	 *            The plot window size.
	 */
	public void setWindowSize(int windowSize) {
		this.windowSize = windowSize;
	}

	/**
	 * Set the data.
	 * 
	 * @param data
	 *            the data.
	 */
	public void setData(double data, int key) {

		if (history.get(key).size() > windowSize) {
			history.get(key).removeFirst();
		}

		history.get(key).addLast(data);

		series.get(key).setModel(history.get(key),
				SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
	}

	/**
	 * Draw the plot.
	 */
	public void draw() {
		dynamicPlot.redraw();
	}

	/**
	 * Add a series to the plot.
	 * 
	 * @param seriesName
	 *            The name of the series.
	 * @param key
	 *            The unique series key.
	 * @param color
	 *            The series color.
	 */
	public void addSeriesPlot(String seriesName, int key, int color) {
		int seriesNum = series.size();

		history.append(key, new LinkedList<Number>());

		series.append(key, new SimpleXYSeries(seriesName));

		LineAndPointFormatter formatter = new LineAndPointFormatter(Color.rgb(
				0, 0, 128), Color.rgb(0, 0, 128), Color.TRANSPARENT,
				new PointLabelFormatter(Color.TRANSPARENT));

		Paint linePaint = new Paint();
		linePaint.setAntiAlias(true);
		linePaint.setStyle(Paint.Style.STROKE);
		linePaint.setColor(color);
		linePaint.setStrokeWidth(LINE_WIDTH);

		formatter.setLinePaint(linePaint);

		Paint vertexPaint = new Paint();
		vertexPaint.setAntiAlias(true);
		vertexPaint.setStyle(Paint.Style.STROKE);
		vertexPaint.setColor(color);
		vertexPaint.setStrokeWidth(VERTEX_WIDTH);

		formatter.setVertexPaint(vertexPaint);

		dynamicPlot.addSeries(series.get(seriesNum), formatter);

	}

	/**
	 * Remove a series from the plot.
	 * 
	 * @param key
	 *            The unique series key.
	 */
	public void removeSeriesPlot(int key) {
		history.remove(key);
		series.remove(key);
	}

	/**
	 * Create the plot.
	 */
	private void initPlot() {
		this.dynamicPlot.setRangeBoundaries(minRange, maxRange,
				BoundaryMode.FIXED);
		this.dynamicPlot.setDomainBoundaries(0, windowSize, BoundaryMode.FIXED);

		this.dynamicPlot.setDomainStepValue(5);
		this.dynamicPlot.setTicksPerRangeLabel(3);
		this.dynamicPlot.setDomainLabel("Time(s.)");
		this.dynamicPlot.getDomainLabelWidget().pack();
		this.dynamicPlot.setRangeLabel("Acceleration(m/s^2)");
		this.dynamicPlot.getRangeLabelWidget().pack();
		this.dynamicPlot.getLegendWidget().setWidth(0.7f);
		this.dynamicPlot.setGridPadding(15, 15, 15, 15);
		this.dynamicPlot.getGraphWidget().setGridBackgroundPaint(null);
		this.dynamicPlot.getGraphWidget().setGridDomainLinePaint(null);
		this.dynamicPlot.getGraphWidget().setGridRangeLinePaint(null);
		this.dynamicPlot.getGraphWidget().setBackgroundPaint(null);
		this.dynamicPlot.getGraphWidget().setBorderPaint(null);

		Paint paint = new Paint();

		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		paint.setColor(Color.rgb(0, 0, 128));
		paint.setStrokeWidth(2);

		this.dynamicPlot.getGraphWidget().setDomainOriginLinePaint(paint);
		this.dynamicPlot.getGraphWidget().setRangeOriginLinePaint(paint);

		this.dynamicPlot.getGraphWidget().getRangeLabelPaint()
				.setColor(Color.BLACK);
		this.dynamicPlot.getGraphWidget().getRangeOriginLinePaint()
				.setColor(Color.BLACK);

		this.dynamicPlot.getGraphWidget().getDomainLabelPaint()
				.setColor(Color.BLACK);

		this.dynamicPlot.setBorderPaint(null);
		this.dynamicPlot.setBackgroundPaint(null);

		this.dynamicPlot.redraw();
	}
}
