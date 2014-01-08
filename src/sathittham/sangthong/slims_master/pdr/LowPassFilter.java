package sathittham.sangthong.slims_master.pdr;


/**
 * An interface for classes that implement low-pass filters.
 * 
 */
public interface LowPassFilter
{

	/**
	 * Add a sample.
	 * @param acceleration The acceleration data.
	 * @return Returns the output of the filter.
	 */
	public float[] addSamples(float[] acceleration);

	/**
	 * Indicate if alpha should be static.
	 * @param alphaStatic A static value for alpha
	 */
	public void setAlphaStatic(boolean alphaStatic);

	/**
	 * Set static alpha.
	 * @param alpha The value for alpha, 0 < alpha <= 1
	 */
	public void setAlpha(float alpha);

}
