package sathittham.sangthong.slims_master.pdr;

public class StepDetector {

	// Constants for the low-pass filters
	private float timeConstant = 0.18f;
	private float alpha = 0.1f;
	private float dt = 0;

	private float accMagnitude = 0;
	
	private int stepCount = 0;

	private int count = 0;


	// Low-pass accelerometer data
	private float[] input = new float[] { 0, 0, 0 };

	public int StepCount(float[] lpfOutput) {

		// Get a local copy of the sensor values
		System.arraycopy(lpfOutput, 0, this.input, 0, lpfOutput.length);


		// Magnitude Calculation
		float x = this.input[0];
		float y = this.input[1];
		float z = this.input[2];

		accMagnitude = (float) Math.sqrt((x * x) + (y * y) + (z * z));

		count++;

		while (count > 5) {

		}

		return stepCount;
	}

}
