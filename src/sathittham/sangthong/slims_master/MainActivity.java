package sathittham.sangthong.slims_master;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import sathittham.sangthong.slims_master.pdr.DynamicPlot;
import sathittham.sangthong.slims_master.pdr.LPFAndroidDeveloper;
import sathittham.sangthong.slims_master.pdr.LPFWikipedia;
import sathittham.sangthong.slims_master.pdr.LowPassFilter;
import sathittham.sangthong.slims_master.pdr.MeanFilter;
import sathittham.sangthong.slims_master.pdr.PlotColor;
import sathittham.sangthong.slims_master.pdr.SettingsDialog;
import sathittham.sangthong.slims_master.pdr.StdDev;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.xy.XYPlot;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MainActivity extends Activity implements OnClickListener, Runnable {

	/***************** Utils *****************/
	// Context
	private Context context;

	// TAG
	private static final String TAG = "SLIMs-master";

	// checkk is running?
	private boolean mIsRunning = false;

	// Handler for the UI plots so everything plots smoothly
	private Handler handler;

	// Decimal formats for the UI outputs
	private DecimalFormat df;
	private DecimalFormat dfLong;

	// Setting Dialog
	private SettingsDialog settingsDialog;

	// Sensor Manager
	private SensorManager sensorManager;

	/******************* UI Output *******************/
	// Display Fields for Accelerometer
	private TextView mAccXValue;
	private TextView mAccYValue;
	private TextView mAccZValue;
	private TextView mAccMagnitude;

	// Display for command
	private TextView mCommandValue;

	// Display for all distance
	private TextView mSumDistance;

	// Display for steps
	private TextView mStepValue;

	// Display for waling distance
	private TextView mDistanceValue;;

	// Display for compass value
	private TextView mCompassValue;

	// Display for Compass picture
	private ImageView mCompassImage;

	// Button
	private Button mStartBtn;
	private Button mStopBtn;
	private Button mResetBtn;

	/******************* PDR Variable *******************/
	// Outputs for the acceleration and LPFs
	private float[] acceleration = new float[3];
	private float[] lpfWikiOutput = new float[3];
	private float[] lpfAndDevOutput = new float[3];
	private float[] meanFilterOutput = new float[3];

	// Sensor
	Sensor accSensor;
	Sensor orientationSensor;

	// Values to calculate Number of steps,distance,compass
	private float previousY;
	private float currentY;
	private float accMagnitude;
	private float input;
	private float resetValue;
	private float mDistance;
	private float mHeading;
	private int numSteps;
	private int state;

	// recode the compass picture angle turned
	private float currentDegree = 0f;

	// point at which we want to trigger a step
	private double thr;
	private double pos_peek_thr;
	private double neg_peek_thr;
	private double neg_thr;

	/***************** Map *****************/
	private Object[] obj;

	// Google Map
	private GoogleMap googleMap;

	// Initial MAp
	LatLng tagCoordinates;
	Double lati, lngi, sourceLat, sourceLon;;
	String buildingID, floorID, heading, nodeName, sumDistance;
	String message;

	private List<ModelRoomPoint> roomList = new ArrayList<ModelRoomPoint>();
	private List<ModelNodePoint> nodeList = new ArrayList<ModelNodePoint>();
	private String urlMap = "http://www.sathittham.com/slims/GetMap.php";
	private String urlNode = "http://www.sathittham.com/slims/GetShortestPath.php";
	private String urlNodePlusDistance = "http://www.sathittham.com/slims/newDijktraPost.php";

	private String nodeTo = "";
	private String nodeFrom = "";

	/***************** Filters *****************/
	// Low-Pass Filters
	private LowPassFilter lpfWiki;
	private LowPassFilter lpfAndDev;
	private MeanFilter meanFilter;

	private boolean lpfAccelerationStaticAlpha = false;
	private float accelerationLPFAlpha;
	private int accelerationMeanFilterWindow;

	// Indicate if a static alpha should be used for the LPF Wikipedia
	private boolean staticWikiAlpha = false;

	// Indicate if a static alpha should be used for the LPF Android Developer
	private boolean staticAndDevAlpha = false;

	// The static alpha for the LPF Wikipedia
	private static float WIKI_STATIC_ALPHA = 0.1f;

	// The static alpha for the LPF Android Developer
	private static float AND_DEV_STATIC_ALPHA = 0.9f;

	// The size of the sample window that determines RMS Amplitude Noise
	// (standard deviation)
	private static int MEAN_SAMPLE_WINDOW = 20;

	// RMS Noise levels
	private StdDev varianceAccel;
	private StdDev varianceLPFWiki;
	private StdDev varianceLPFAndDev;
	private StdDev varianceMean;

	/***************** Log Data *****************/
	// Indicate if the output should be logged to a .csv file
	private boolean logData = false;

	// The generation of the log output
	private int generation = 0;

	// Log output time stamp
	private long logTime = 0;

	// Output log
	private String log;

	// Raw Acceleration plot titles
	private String plotAccelXAxisTitle = "AX";
	private String plotAccelYAxisTitle = "AY";
	private String plotAccelZAxisTitle = "AZ";

	// Acceleration plot titles
	private String plotLPFWikiXAxisTitle = "WX";
	private String plotLPFWikiYAxisTitle = "WY";
	private String plotLPFWikiZAxisTitle = "WZ";

	// LPF plot titles
	private String plotLPFAndDevXAxisTitle = "ADX";
	private String plotLPFAndDevYAxisTitle = "ADY";
	private String plotLPFAndDevZAxisTitle = "ADZ";

	// Mean filter plot tiltes
	private String plotMeanXAxisTitle = "MX";
	private String plotMeanYAxisTitle = "MY";
	private String plotMeanZAxisTitle = "MZ";

	// Icon to indicate logging is active
	private ImageView iconLogger;

	/***************** Graph Plot *****************/
	private XYPlot plot;

	// Plot colors
	private PlotColor color;

	// check if want to display graph or not
	Boolean showGraph = false;

	// Graph plot for the UI outputs
	private DynamicPlot dynamicPlot;

	// Indicate if the AndDev LPF should be plotted
	private boolean plotLPFAndDev = false;

	// Indicate if the Wiki LPF should be plotted
	private boolean plotLPFWiki = false;

	// Indicate if the Mean Filter should be plotted
	private boolean plotMeanFilter = false;

	// Indicate the plots are ready to accept inputs
	private boolean plotLPFWikiReady = false;
	private boolean plotLPFAndDevReady = false;
	private boolean plotMeanReady = false;

	// Touch to zoom constants for the dynamicPlot
	private float distance = 0;
	private float zoom = 1.2f;

	// Plot keys for the acceleration plot
	private final static int PLOT_ACCEL_X_AXIS_KEY = 0;
	private final static int PLOT_ACCEL_Y_AXIS_KEY = 1;
	private final static int PLOT_ACCEL_Z_AXIS_KEY = 2;

	// Plot keys for the LPF Wikipedia plot
	private final static int PLOT_LPF_WIKI_X_AXIS_KEY = 3;
	private final static int PLOT_LPF_WIKI_Y_AXIS_KEY = 4;
	private final static int PLOT_LPF_WIKI_Z_AXIS_KEY = 5;

	// Plot keys for the LPF Android Developer plot
	private final static int PLOT_LPF_AND_DEV_X_AXIS_KEY = 6;
	private final static int PLOT_LPF_AND_DEV_Y_AXIS_KEY = 7;
	private final static int PLOT_LPF_AND_DEV_Z_AXIS_KEY = 8;

	// Plot keys for the mean filter plot
	private final static int PLOT_MEAN_X_AXIS_KEY = 9;
	private final static int PLOT_MEAN_Y_AXIS_KEY = 10;
	private final static int PLOT_MEAN_Z_AXIS_KEY = 11;

	// Color keys for the acceleration plot
	private int plotAccelXAxisColor;
	private int plotAccelYAxisColor;
	private int plotAccelZAxisColor;

	// Color keys for the LPF Wikipedia plot
	private int plotLPFWikiXAxisColor;
	private int plotLPFWikiYAxisColor;
	private int plotLPFWikiZAxisColor;

	// Color keys for the LPF Android Developer plot
	private int plotLPFAndDevXAxisColor;
	private int plotLPFAndDevYAxisColor;
	private int plotLPFAndDevZAxisColor;

	// Color keys for the mean filter plot
	private int plotMeanXAxisColor;
	private int plotMeanYAxisColor;
	private int plotMeanZAxisColor;

	/**
	 * Get the sample window size for the standard deviation.
	 * 
	 * @return Sample window size for the standard deviation.
	 */
	public static int getSampleWindow() {
		return MEAN_SAMPLE_WINDOW;
	}

	/****************** on Create ******************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "[ACTIVITY] on Create");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// thread
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		// Read in the saved prefs
		readPrefs();

		// Get the sensor manager ready
		sensorManager = (SensorManager) this
				.getSystemService(Context.SENSOR_SERVICE);

		initTextViewOutputs();
		initButton();
		initCompass();
		initIcons();
		initStatistics();
		initFilters();
		enableSensorListening();

		// Initialize the plots
		initColor();
		initPlots();

		try {
			// Loading map
			initilizeMap();

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			// get Tag Message
			message = getTagMessage();

			if (message != null || !message.isEmpty()) {
				String[] geo = message.split(",");

				// change type string to double
				double lati = Double.parseDouble(geo[0]);
				double lngi = Double.parseDouble(geo[1]);

				// value of geo[]
				buildingID = String.valueOf(geo[2]);
				floorID = String.valueOf(geo[3]);
				heading = String.valueOf(geo[4]);
				nodeName = String.valueOf(geo[5]);
				nodeFrom = String.valueOf(geo[6]); // nodeCode
				// sumDistance = String.valueOf(geo[7]);

				// set Instruction command
				mCommandValue.setText("You are here @ " + nodeName);

				// Draw path
				nodeTo = getIntent().getExtras().getString("nodeTo");
				if ((nodeFrom != "" || !nodeFrom.equals(""))
						&& (nodeTo != null)) {
					JsonDataManager process = new JsonDataManager(this, urlMap);
					// nodeList = process.getNodeList(urlNode, buildingID,
					// floorID, nodeFrom, nodeTo);
					obj = process.getNodeListPlusDistince(urlNodePlusDistance,
							buildingID, floorID, nodeFrom, nodeTo);
					if (obj != null) {
						nodeList = (List<ModelNodePoint>) obj[0];
						List<LatLng> nodePointList = new ArrayList<LatLng>();
						LatLng nodePoint = null;

						for (ModelNodePoint np : nodeList) {
							nodePoint = new LatLng(np.getLat(), np.getLon());
							nodePointList.add(nodePoint);
						}
						sumDistance = (String) obj[2];

						drawPath(nodePointList);
						mSumDistance.setText("Distance :" + sumDistance);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			// use default location
			getDefaultLocation();
		}

		handler = new Handler();
	}

	/**************** onClick ****************/
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.start_btn:
			Log.i(TAG, "[ACTIVITY] Start Button");
			mStartBtn.setEnabled(false);
			mStopBtn.setEnabled(true);
			mResetBtn.setEnabled(true);

			mIsRunning = true;

			readPrefs();
			initTextViewOutputs();

			handler.post(this);
			Log.i(TAG, "[ACTIVITY] handle post");

			// Enable Accelerometer sensor listener
			sensorManager.registerListener(accelListener, accSensor,
					SensorManager.SENSOR_DELAY_NORMAL);
			// Enable Orientation Sensor listener
			sensorManager.registerListener(orientationListener,
					orientationSensor, SensorManager.SENSOR_DELAY_NORMAL);
			updateTextViewOutputs();
			break;

		case R.id.stop_btn:
			Log.i(TAG, "[ACTIVITY] Stop button");
			mStartBtn.setEnabled(true);
			mStopBtn.setEnabled(false);
			mResetBtn.setEnabled(true);
			iconLogger.setVisibility(View.INVISIBLE);
			mIsRunning = false;

			// Disable Accelerometer sensor listener
			sensorManager.unregisterListener(accelListener);
			// Disable Orientation sensor listener
			sensorManager.unregisterListener(orientationListener);

			if (logData) {
				writeLogToFile();
			}
			handler.removeCallbacks(this);

			break;

		case R.id.reset_btn:
			Log.i(TAG, "[ACTIVITY] Reset button");
			mStartBtn.setEnabled(true);
			mStopBtn.setEnabled(false);
			mResetBtn.setEnabled(false);
			iconLogger.setVisibility(View.INVISIBLE);
			mIsRunning = false;

			// Disable Accelerometer sensor listener
			sensorManager.unregisterListener(accelListener);
			// Disable Orientation sensor listener
			sensorManager.unregisterListener(orientationListener);

			resetValue = 0.0f;
			mAccMagnitude.setText(String.valueOf("Acceleration Magnitude : "
					+ resetValue));
			mAccXValue.setText(String.valueOf("X: " + resetValue));
			mAccYValue.setText(String.valueOf("Y: " + resetValue));
			mAccZValue.setText(String.valueOf("Z: " + resetValue));
			mDistanceValue.setText(String.valueOf(resetValue));
			mCompassValue.setText(String.valueOf("Heading: " + resetValue));

			numSteps = 0;
			mStepValue.setText(String.valueOf(numSteps));

			// Intent intent = getIntent();
			// finish();
			// startActivity(intent);
			//
			break;
		}

	}

	/**************** Accelerometer Sensor Event Listener ****************/
	// Event handler for accelerometer events
	SensorEventListener accelListener = new SensorEventListener() {

		// Listen for change in Acceleration, Display and Compute the Steps
		@Override
		public void onSensorChanged(SensorEvent event) {

			Log.i(TAG, "[ACTIVITY] Accelerometer sensor event listener");

			// Get a local copy of the sensor values
			System.arraycopy(event.values, 0, acceleration, 0,
					event.values.length);

			acceleration[0] = acceleration[0] / SensorManager.GRAVITY_EARTH; // x
			acceleration[1] = acceleration[1] / SensorManager.GRAVITY_EARTH; // y
			acceleration[2] = acceleration[2] / SensorManager.GRAVITY_EARTH; // z

			if (plotLPFWiki) {
				lpfWikiOutput = lpfWiki.addSamples(acceleration);
			}
			if (plotLPFAndDev) {
				lpfAndDevOutput = lpfAndDev.addSamples(acceleration);
			}
			if (plotMeanFilter) {
				meanFilterOutput = meanFilter.filterFloat(acceleration);
			}

		}

		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
		}
	};

	/**************** Orientation Sensor Event Listener ****************/
	// Event handler for Orientation events
	SensorEventListener orientationListener = new SensorEventListener() {

		@Override
		public void onSensorChanged(SensorEvent event) {
			Log.i(TAG, "[ACTIVITY] Orientation sensor event listener");
			// get the angle around the z-axis rotated
			float mHeading = Math.round(event.values[0]);

			mCompassValue.setText("Heading : " + Float.toString(mHeading)
					+ " degree");

			// create a rotation animation (reverse turn degree degrees)
			RotateAnimation ra = new RotateAnimation(currentDegree, -mHeading,
					Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);

			// how long the animation will take place
			ra.setDuration(210);

			// set the animation after the end of the reservation status
			ra.setFillAfter(true);

			// Start the animation
			mCompassImage.startAnimation(ra);
			currentDegree = -mHeading;
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// not in use
		}
	};

	/**************** Enable Sensor Event Listener ****************/
	private void enableSensorListening() {
		Log.i(TAG, "[ACTIVITY] Enable Sensor Listening");
		// Initialize the sensor Manager
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

		// Linear Acceleration Sensor
		accSensor = sensorManager
				.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

		// Orientation Sensor
		orientationSensor = sensorManager
				.getDefaultSensor(Sensor.TYPE_ORIENTATION);

	}

	/****************** get Default Location ******************/
	private void getDefaultLocation() {
		Log.i(TAG, "[ACTIVITY] get Default Location");

		mCommandValue
				.setText("Please scan Tag ! to get your current position.");

		// Default for latitude and longitude....E12 Building
		double latitude = 13.727264170612084;
		double longitude = 100.7724192738533;

		// Tag Coordinates
		tagCoordinates = new LatLng(latitude, longitude);

		// Zoom to tag's position
		googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
				tagCoordinates, 18));

		// Json Draw Room
		JsonDataManager process = new JsonDataManager(this, urlMap);
		if (process.checkUrl()) {
			roomList = process.getRoomList("E12", "E12F09");

			List<LatLng> pointList = null;
			LatLng point = null;
			String tempRoomCode = "";

			for (ModelRoomPoint _rp : roomList) {
				if (_rp.getRoomCode() == tempRoomCode
						|| _rp.getRoomCode().equals(tempRoomCode)) {
					point = new LatLng(_rp.getLat(), _rp.getLon());
					pointList.add(point);
				} else {
					if (pointList != null) {
						drawPolygon(pointList);
					}

					tempRoomCode = _rp.getRoomCode();
					pointList = new ArrayList<LatLng>();

					point = new LatLng(_rp.getLat(), _rp.getLon());
					pointList.add(point);

				}
			}

			if (pointList != null) { // draw for the last room
				drawPolygon(pointList);
			}

		}

	}

	/****************** get Tag Message ******************/
	private String getTagMessage() {
		Log.i(TAG, "[ACTIVITY] Get Tag Message");

		// get string Intent data
		String text = getIntent().getExtras().getString("Message");
		String[] geo = text.split(",");

		// change type string to double
		double lati = Double.parseDouble(geo[0]);
		double lngi = Double.parseDouble(geo[1]);

		// value of geo[]
		buildingID = String.valueOf(geo[2]);
		floorID = String.valueOf(geo[3]);
		heading = String.valueOf(geo[4]);
		nodeName = String.valueOf(geo[5]);
		nodeFrom = String.valueOf(geo[6]); // nodeCode
		sumDistance = String.valueOf(geo[7]);

		// Tag Coordinates
		tagCoordinates = new LatLng(lati, lngi);

		// Zoom to tag's position
		googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
				tagCoordinates, 18));

		// marker
		googleMap.addMarker(new MarkerOptions()
				.position(new LatLng(lati, lngi))
				.icon(BitmapDescriptorFactory.fromAsset("blue_point.png"))
				.title("You are here !").snippet(nodeName));

		// Json Draw Room
		JsonDataManager process = new JsonDataManager(this, urlMap);
		if (process.checkUrl()) {
			roomList = process.getRoomList(buildingID, floorID);

			List<LatLng> pointList = null;
			LatLng point = null;
			String tempRoomCode = "";

			for (ModelRoomPoint _rp : roomList) {
				if (_rp.getRoomCode() == tempRoomCode
						|| _rp.getRoomCode().equals(tempRoomCode)) {
					point = new LatLng(_rp.getLat(), _rp.getLon());
					pointList.add(point);
				} else {
					if (pointList != null) {
						drawPolygon(pointList);
					}

					tempRoomCode = _rp.getRoomCode();
					pointList = new ArrayList<LatLng>();

					point = new LatLng(_rp.getLat(), _rp.getLon());
					pointList.add(point);

				}
			}

			if (pointList != null) { // draw for the last room
				drawPolygon(pointList);
			}

		}

		return text;
	}

	/**************** Draw Map ****************/
	/* Draw Polygon */
	private void drawPolygon(List<LatLng> pointList) {

		PolygonOptions _polygon = new PolygonOptions();

		_polygon.addAll(pointList); // point_1 - point_n
		_polygon.add(pointList.get(0)); // point_1

		_polygon.strokeColor(Color.argb(0xFF, 0x8B, 0x83, 0x78));
		_polygon.fillColor(Color.argb(0x50, 0xEE, 0xE9, 0xE9)).strokeWidth(2);

		googleMap.addPolygon(_polygon);
	}

	/* Draw Path */
	private void drawPath(List<LatLng> nodeList) {

		PolylineOptions _polyLine = new PolylineOptions();

		_polyLine.addAll(nodeList);
		_polyLine.color(Color.BLUE);
		_polyLine.width(3);
		googleMap.addPolyline(_polyLine);

		googleMap.addMarker(new MarkerOptions()
				.position(nodeList.get(nodeList.size() - 1))
				.icon(BitmapDescriptorFactory.fromAsset("blue_point.png"))
				.title("Destination!"));
	}

	/****************** Initial ******************/
	/**
	 * Initialize the icons.
	 */
	private void initIcons() {
		Log.i(TAG, "[ACTIVITY] initIcons()");
		// Create the logger icon
		iconLogger = (ImageView) findViewById(R.id.log_image);
		iconLogger.setVisibility(View.INVISIBLE);
	}

	/**
	 * Initialize the Compass.
	 */
	private void initCompass() {
		Log.i(TAG, "[ACTIVITY] initCompass()");
		// Attach compass image view and value
		mCompassImage = (ImageView) findViewById(R.id.compass_image);
		mCompassValue = (TextView) findViewById(R.id.compass_value);
	}

	/**
	 * function to load map. If map is not created it will create it for you
	 * */
	private void initilizeMap() {
		Log.i(TAG, "[ACTIVITY] Intitial Map");
		if (googleMap == null) {
			googleMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.SlimsMap)).getMap();

			googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			googleMap.setMyLocationEnabled(false); // false to disable
			googleMap.getUiSettings().setZoomControlsEnabled(false); // true to
																		// enable
			googleMap.getUiSettings().setCompassEnabled(true);

			// check if map is created successfully or not
			if (googleMap == null) {
				Toast.makeText(getApplicationContext(),
						"Sorry! unable to create maps", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	/**
	 * Initial Plot
	 */
	private void initPlots() {
		Log.i(TAG, "[ACTIVITY] initPlots()");
		// Create the graph plot
		XYPlot plot = (XYPlot) findViewById(R.id.plot_sensor);
		plot.setTitle("Acceleration");
		dynamicPlot = new DynamicPlot(plot);
		dynamicPlot.setMaxRange(1.2);
		dynamicPlot.setMinRange(-1.2);

		addAccelerationPlot();
		addLPFWikiPlot();
		addLPFAndDevPlot();
		addMeanFilterPlot();
	}

	/**
	 * Initialize the Button Outputs.
	 */
	private void initButton() {
		Log.i(TAG, "[ACTIVITY] initButton()");
		// Attach the Button to XML
		mStartBtn = (Button) findViewById(R.id.start_btn);
		mStopBtn = (Button) findViewById(R.id.stop_btn);
		mResetBtn = (Button) findViewById(R.id.reset_btn);

		// Initialize Button
		mStartBtn.setOnClickListener(this);
		mStopBtn.setOnClickListener(this);
		mResetBtn.setOnClickListener(this);

		mStartBtn.setEnabled(true);
		mStopBtn.setEnabled(false);
		mResetBtn.setEnabled(false);
	}

	/**
	 * Initialize the Text View Sensor Outputs.
	 */
	private void initTextViewOutputs() {
		Log.i(TAG, "initTextViewOutputs()");
		// Attach objects to XML View
		mAccXValue = (TextView) findViewById(R.id.accel_x_value);
		mAccYValue = (TextView) findViewById(R.id.accel_y_value);
		mAccZValue = (TextView) findViewById(R.id.accel_z_value);
		mAccMagnitude = (TextView) findViewById(R.id.accel_magnitude_text);
		mCommandValue = (TextView) findViewById(R.id.command_value);
		mSumDistance = (TextView) findViewById(R.id.sumDistance_textView);

		// Attach Step and distance View object to XML
		mStepValue = (TextView) findViewById(R.id.step_value);
		mDistanceValue = (TextView) findViewById(R.id.distance_value);

		// Format the UI outputs so they look nice
		df = new DecimalFormat("#.##");
		dfLong = new DecimalFormat("#.####");

		// set the values of threshold
		thr = 0.6;
		pos_peek_thr = 1.8;
		neg_peek_thr = -1;
		neg_thr = -0.6;

		// Initialize state
		state = 0;

		// Initialize Values
		previousY = 0;
		currentY = 0;
		numSteps = 0;
		accMagnitude = 0;
		mDistance = 0;
		mHeading = 0;
		input = 0;
	}

	/**
	 * Initialize the filters.
	 */
	private void initFilters() {
		Log.i(TAG, "[ACTIVITY] initFilters()");
		// Create the low-pass filters
		lpfWiki = new LPFWikipedia();
		lpfAndDev = new LPFAndroidDeveloper();
		meanFilter = new MeanFilter();

		meanFilter.setWindowSize(MEAN_SAMPLE_WINDOW);

		// Initialize the low-pass filters with the saved prefs
		lpfWiki.setAlphaStatic(staticWikiAlpha);
		lpfWiki.setAlpha(WIKI_STATIC_ALPHA);

		lpfAndDev.setAlphaStatic(staticAndDevAlpha);
		lpfAndDev.setAlpha(AND_DEV_STATIC_ALPHA);

	}

	/**
	 * Initialize the statistics.
	 */
	private void initStatistics() {
		// Create the RMS Noise calculations
		varianceAccel = new StdDev();
		varianceLPFWiki = new StdDev();
		varianceLPFAndDev = new StdDev();
		varianceMean = new StdDev();
	}

	/**************** Set Graph Plot ****************/
	/**
	 * Indicate if the Wikipedia LPF should be plotted.
	 * 
	 * @param plotLPFWiki
	 *            Plot the filter if true.
	 */
	public void setPlotLPFWiki(boolean plotLPFWiki) {
		Log.i(TAG, "[ACTIVITY] setPlotLPFWiki");
		this.plotLPFWiki = plotLPFWiki;

		if (this.plotLPFWiki) {
			addLPFWikiPlot();
		} else {
			removeLPFWikiPlot();
		}
	}

	/**
	 * Indicate if the Android Developer LPF should be plotted.
	 * 
	 * @param plotLPFAndDev
	 *            Plot the filter if true.
	 */
	public void setPlotLPFAndDev(boolean plotLPFAndDev) {
		Log.i(TAG, "[ACTIVITY] setPlotLPFAndDev");
		this.plotLPFAndDev = plotLPFAndDev;

		if (this.plotLPFAndDev) {
			addLPFAndDevPlot();
		} else {
			removeLPFAndDevPlot();
		}
	}

	/**
	 * Indicate if the Mean Filter should be plotted.
	 * 
	 * @param plotMean
	 *            Plot the filter if true.
	 */
	public void setPlotMean(boolean plotMean) {
		this.plotMeanFilter = plotMean;

		if (this.plotMeanFilter) {
			addMeanFilterPlot();
		} else {
			removeMeanFilterPlot();
		}
	}

	/**************** Add the Plot ****************/
	/*
	 * Add the Acceleration Plot. Create the output graph line chart.
	 */
	private void addAccelerationPlot() {
		Log.i(TAG, "[ACTIVITY] addAccelerationPlot()");
		addPlot(plotAccelXAxisTitle, PLOT_ACCEL_X_AXIS_KEY, plotAccelXAxisColor);
		addPlot(plotAccelYAxisTitle, PLOT_ACCEL_Y_AXIS_KEY, plotAccelYAxisColor);
		addPlot(plotAccelZAxisTitle, PLOT_ACCEL_Z_AXIS_KEY, plotAccelZAxisColor);

	}

	/**
	 * Add the Android Developer LPF plot.
	 */
	private void addLPFAndDevPlot() {
		Log.i(TAG, "[ACTIVITY] addLPFAndDevPlot()");
		if (plotLPFAndDev) {
			addPlot(plotLPFAndDevXAxisTitle, PLOT_LPF_AND_DEV_X_AXIS_KEY,
					plotLPFAndDevXAxisColor);
			addPlot(plotLPFAndDevYAxisTitle, PLOT_LPF_AND_DEV_Y_AXIS_KEY,
					plotLPFAndDevYAxisColor);
			addPlot(plotLPFAndDevZAxisTitle, PLOT_LPF_AND_DEV_Z_AXIS_KEY,
					plotLPFAndDevZAxisColor);

			plotLPFAndDevReady = true;
		}
	}

	/**
	 * Add the Wikipedia LPF plot.
	 */
	private void addLPFWikiPlot() {
		Log.i(TAG, "[ACTIVITY] addLPFWikiPlot()");
		if (plotLPFWiki) {
			addPlot(plotLPFWikiXAxisTitle, PLOT_LPF_WIKI_X_AXIS_KEY,
					plotLPFWikiXAxisColor);//
			addPlot(plotLPFWikiYAxisTitle, PLOT_LPF_WIKI_Y_AXIS_KEY,
					plotLPFWikiYAxisColor);
			addPlot(plotLPFWikiZAxisTitle, PLOT_LPF_WIKI_Z_AXIS_KEY,
					plotLPFWikiZAxisColor);

			plotLPFWikiReady = true;
		}
	}

	/**
	 * Add the Mean Filter plot.
	 */
	private void addMeanFilterPlot() {
		if (plotMeanFilter) {
			addPlot(plotMeanXAxisTitle, PLOT_MEAN_X_AXIS_KEY,
					plotMeanXAxisColor);
			addPlot(plotMeanYAxisTitle, PLOT_MEAN_Y_AXIS_KEY,
					plotMeanYAxisColor);
			addPlot(plotMeanZAxisTitle, PLOT_MEAN_Z_AXIS_KEY,
					plotMeanZAxisColor);

			plotMeanReady = true;
		}
	}

	/**
	 * Add a plot to the graph.
	 * 
	 * @param title
	 *            The name of the plot.
	 * @param key
	 *            The unique plot key
	 * @param color
	 *            The color of the plot
	 */
	private void addPlot(String title, int key, int color) {
		Log.i(TAG, "[ACTIVITY] addPlot()");
		dynamicPlot.addSeriesPlot(title, key, color);

	}

	/**************** Remove Plot ****************/
	/**
	 * Remove a plot from the graph.
	 * 
	 * @param key
	 */
	private void removePlot(int key) {
		Log.i(TAG, "[ACTIVITY] removePlot()");
		dynamicPlot.removeSeriesPlot(key);
	}

	/**
	 * Remove the Acceleration plot.
	 */
	// private void removeAccelerationPlot() {
	// Log.i(TAG, "[ACTIVITY] removeAccelerationPlot()");
	//
	// removePlot(PLOT_ACCEL_X_AXIS_KEY);
	// removePlot(PLOT_ACCEL_Y_AXIS_KEY);
	// removePlot(PLOT_ACCEL_Z_AXIS_KEY);
	//
	// }

	/**
	 * Remove the Android Developer LPF plot.
	 */
	private void removeLPFAndDevPlot() {
		Log.i(TAG, "[ACTIVITY] removeLPFAndDevPlot()");
		if (!plotLPFAndDev) {
			plotLPFAndDevReady = false;

			removePlot(PLOT_LPF_AND_DEV_X_AXIS_KEY);
			removePlot(PLOT_LPF_AND_DEV_Y_AXIS_KEY);
			removePlot(PLOT_LPF_AND_DEV_Z_AXIS_KEY);
		}
	}

	/**
	 * Remove the Wikipedia LPF plot.
	 */
	private void removeLPFWikiPlot() {
		Log.i(TAG, "[ACTIVITY] removeLPFWikiPlot()");
		if (!plotLPFWiki) {
			plotLPFWikiReady = false;

			removePlot(PLOT_LPF_WIKI_X_AXIS_KEY);
			removePlot(PLOT_LPF_WIKI_Y_AXIS_KEY);
			removePlot(PLOT_LPF_WIKI_Z_AXIS_KEY);
		}
	}

	/**
	 * Remove the Mean Filter plot.
	 */
	private void removeMeanFilterPlot() {
		if (!plotMeanFilter) {
			plotMeanReady = false;

			removePlot(PLOT_MEAN_X_AXIS_KEY);
			removePlot(PLOT_MEAN_Y_AXIS_KEY);
			removePlot(PLOT_MEAN_Z_AXIS_KEY);
		}
	}

	/**************** Plot Color ****************/
	/**
	 * Create the plot colors.
	 */
	private void initColor() {
		Log.i(TAG, "[ACTIVITY] initColor()");
		color = new PlotColor(this);

		plotAccelXAxisColor = color.getDarkBlue();
		plotAccelYAxisColor = color.getDarkGreen();
		plotAccelZAxisColor = color.getDarkRed();

		plotLPFWikiXAxisColor = color.getMidBlue();
		plotLPFWikiYAxisColor = color.getMidGreen();
		plotLPFWikiZAxisColor = color.getMidRed();

		plotLPFAndDevXAxisColor = color.getLightBlue();
		plotLPFAndDevYAxisColor = color.getLightGreen();
		plotLPFAndDevZAxisColor = color.getLightRed();

		plotMeanXAxisColor = color.getLightBlue();
		plotMeanYAxisColor = color.getLightGreen();
		plotMeanZAxisColor = color.getLightRed();

	}

	/**************** Plot Data ****************/
	// Plot the output data in the UI
	private void plotData() {
		Log.i(TAG, "[ACTIVITY] PlotData");
		dynamicPlot.setData(acceleration[0], PLOT_ACCEL_X_AXIS_KEY);
		dynamicPlot.setData(acceleration[1], PLOT_ACCEL_Y_AXIS_KEY);
		dynamicPlot.setData(acceleration[2], PLOT_ACCEL_Z_AXIS_KEY);

		if (plotLPFWikiReady) {
			dynamicPlot.setData(lpfWikiOutput[0], PLOT_LPF_WIKI_X_AXIS_KEY);
			dynamicPlot.setData(lpfWikiOutput[1], PLOT_LPF_WIKI_Y_AXIS_KEY);
			dynamicPlot.setData(lpfWikiOutput[2], PLOT_LPF_WIKI_Z_AXIS_KEY);
		}

		if (plotLPFAndDevReady) {
			dynamicPlot
					.setData(lpfAndDevOutput[0], PLOT_LPF_AND_DEV_X_AXIS_KEY);
			dynamicPlot
					.setData(lpfAndDevOutput[1], PLOT_LPF_AND_DEV_Y_AXIS_KEY);
			dynamicPlot
					.setData(lpfAndDevOutput[2], PLOT_LPF_AND_DEV_Z_AXIS_KEY);
		}

		if (plotMeanFilter) {
			dynamicPlot.setData(meanFilterOutput[0], PLOT_MEAN_X_AXIS_KEY);
			dynamicPlot.setData(meanFilterOutput[1], PLOT_MEAN_Y_AXIS_KEY);
			dynamicPlot.setData(meanFilterOutput[2], PLOT_MEAN_Z_AXIS_KEY);
		}

		dynamicPlot.draw();

	}

	/**************** Run ****************/
	/*
	 * Output and logs are run on their own thread to keep the UI from hanging
	 * and the output smooth.
	 */
	@Override
	public void run() {
		Log.i(TAG, "[ACTIVITY] Run");

		handler.postDelayed(this, 100);

		plotData();
		updateTextViewOutputs();
		logData();

	}

	/****************** on Resume ******************/
	@Override
	protected void onResume() {
		Log.i(TAG, "[ACTIVITY] on Resume");
		super.onResume();

	}

	/****************** on Stop ******************/
	@Override
	protected void onStop() {
		Log.i(TAG, "[ACTIVITY] on Stop");
		super.onStop();
		// Disable Accelerometer sensor listener
		sensorManager.unregisterListener(accelListener);
		// Disable Orientation sensor listener
		sensorManager.unregisterListener(orientationListener);

		if (logData) {
			writeLogToFile();
		}

		handler.removeCallbacks(this);

	}

	/****************** on Pause ******************/
	@Override
	protected void onPause() {
		Log.i(TAG, "[ACTIVITY] on Pause");
		super.onPause();

		// Disable Accelerometer sensor listener
		sensorManager.unregisterListener(accelListener);
		// Disable Orientation sensor listener
		sensorManager.unregisterListener(orientationListener);

		if (logData) {
			writeLogToFile();
		}
	}

	/**************** Update UI ****************/
	private void updateTextViewOutputs() {
		Log.i(TAG, "[ACTIVITY] updateTextViewOutputs()");
		// Update the view with the new acceleration data
		mAccXValue.setText(df.format(acceleration[0]));
		mAccYValue.setText(df.format(acceleration[1]));
		mAccZValue.setText(df.format(acceleration[2]));
		// mAccMagnitude.setText("Acceleration Magnitude : "+
		// String.valueOf(accMagnitude));
	}

	/**************** Log Data ****************/
	/* Begin logging data to an external .csv file. */
	private void startDataLog() {
		Log.i(TAG, "[ACTIVITY] start Data log");
		if (logData == false) {
			CharSequence text = "Logging Data";
			int duration = Toast.LENGTH_SHORT;

			Toast toast = Toast.makeText(this, text, duration);
			toast.show();

			String headers = "Generation" + ",";

			headers += "Timestamp" + ",";

			headers += this.plotAccelXAxisTitle + ",";
			headers += this.plotAccelYAxisTitle + ",";
			headers += this.plotAccelZAxisTitle + ",";

			headers += this.plotLPFWikiXAxisTitle + ",";
			headers += this.plotLPFWikiYAxisTitle + ",";
			headers += this.plotLPFWikiZAxisTitle + ",";

			headers += this.plotLPFAndDevXAxisTitle + ",";
			headers += this.plotLPFAndDevYAxisTitle + ",";
			headers += this.plotLPFAndDevZAxisTitle + ",";

			headers += this.plotMeanXAxisTitle + ",";
			headers += this.plotMeanYAxisTitle + ",";
			headers += this.plotMeanZAxisTitle + ",";

			log = headers + "\n";
			iconLogger.setVisibility(View.VISIBLE);
			logData = true;
		} else {
			iconLogger.setVisibility(View.INVISIBLE);

			logData = false;
			writeLogToFile();
		}
	}

	/* Log output data to an external .csv file */
	private void logData() {
		Log.i(TAG, "[ACTIVITY] Log data");
		if (logData) {
			if (generation == 0) {
				logTime = System.currentTimeMillis();
			}

			log += System.getProperty("line.separator");
			log += generation++ + ",";
			log += System.currentTimeMillis() - logTime + ",";

			log += acceleration[0] + ",";
			log += acceleration[1] + ",";
			log += acceleration[2] + ",";

			log += lpfWikiOutput[0] + ",";
			log += lpfWikiOutput[1] + ",";
			log += lpfWikiOutput[2] + ",";

			log += lpfAndDevOutput[0] + ",";
			log += lpfAndDevOutput[1] + ",";
			log += lpfAndDevOutput[2] + ",";

			log += meanFilterOutput[0] + ",";
			log += meanFilterOutput[1] + ",";
			log += meanFilterOutput[2] + ",";
		}
	}

	/* Write the logged data out to persisted file. */
	private void writeLogToFile() {
		Log.i(TAG, "[ACTIVITY] write log to file");

		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss");
		String TimeStampDB = sdf.format(c.getTime());

		String filename = "SLIMs_PDR-" + TimeStampDB + ".csv";

		File dir = new File(Environment.getExternalStorageDirectory()
				+ File.separator + "SLIMs" + File.separator + "Logs"
				+ File.separator + "Acceleration");

		if (!dir.exists()) {
			dir.mkdirs();
		}

		File file = new File(dir, filename);

		FileOutputStream fos;
		byte[] data = log.getBytes();
		try {
			fos = new FileOutputStream(file);
			fos.write(data);
			fos.flush();
			fos.close();

			CharSequence text = "Log Saved !";
			int duration = Toast.LENGTH_SHORT;

			Toast toast = Toast.makeText(this, text, duration);
			toast.show();
		} catch (FileNotFoundException e) {
			CharSequence text = e.toString();
			int duration = Toast.LENGTH_SHORT;

			Toast toast = Toast.makeText(this, text, duration);
			toast.show();
		} catch (IOException e) {
			// handle exception
		} finally {
			// Update the MediaStore so we can view the file without rebooting
			// Note that it appears that the ACTION_MEDIA_MOUTED approach is
			// now blocked for non-system app on Android 4.4.
			MediaScannerConnection.scanFile(this, new String[] { "file://"
					+ Environment.getExternalStorageDirectory() }, null,
					new MediaScannerConnection.OnScanCompletedListener() {

						@Override
						public void onScanCompleted(final String path,
								final Uri uri) {

						}
					});

		}
	}

	/****************** Options Menu ******************/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.i(TAG, "[ACTIVITY] on Create Options Menu");
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.slims_menu, menu);

		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * Event Handling for Individual menu item selected Identify single menu
	 * item by it's id
	 * */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i(TAG, "[ACTIVITY] on Options Item Selected");
		switch (item.getItemId()) {
		/* Search Menu */
		case R.id.slims_menu_search:
			return true;

			/* Search */
		case R.id.slims_menu_search_search_dialog:
			// showSearchDialog();
			Intent intent3 = new Intent(MainActivity.this, SearchActivity.class);
			intent3.putExtra("message", message);
			startActivity(intent3);
			return true;

			/* Search with QR Code */
		case R.id.slims_menu_search_search_qrcode:
			Toast.makeText(MainActivity.this, "Search with QR Code",
					Toast.LENGTH_SHORT).show();
			return true;

			/* Search with NFC */
		case R.id.slims_menu_search_search_nfc:
			Intent intent = new Intent(MainActivity.this,
					SearchNFCActivity.class);
			startActivity(intent);
			return true;

			/* Setting Menu */
		case R.id.slims_menu_settings:
			return true;

			/* Log the data */
		case R.id.slims_menu_settings_logger_plotdata:
			startDataLog();
			return true;

			/* Write NFC Activity */
		case R.id.slims_menu_settings_write_nfc:
			Intent intent2 = new Intent(MainActivity.this,
					WriteNFCActivity.class);
			startActivity(intent2);
			return true;

			/* Setting */
		case R.id.slims_menu_settings_setting:
			showSettingsDialog();
			return true;

			/* Graph */
		case R.id.slims_menu_graph:
			Toast.makeText(this, "Graph", Toast.LENGTH_SHORT).show();
			// showGraphDisplay();
			return true;

			/* Help */
		case R.id.slims_menu_settings_help:
			showHelpDialog();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**************** Show Dialog ****************/
	private void showGraphDisplay() {
		if (showGraph == false) {
			Log.i(TAG, "[ACTIVITY] show graph display visible");
			plot.setVisibility(View.VISIBLE);
			showGraph = true;
		} else {
			Log.i(TAG, "[ACTIVITY] show graph display invisible");
			plot.setVisibility(View.INVISIBLE);
			showGraph = false;
		}

	}

	private void showSearchDialog() {
		Dialog SearchDialog = new Dialog(this);
		SearchDialog.setCancelable(true);
		SearchDialog.setCanceledOnTouchOutside(true);

		SearchDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		SearchDialog.setContentView(getLayoutInflater().inflate(
				R.layout.activity_search, null));

		SearchDialog.show();
	}

	private void showSearchNFCDialog() {
		Dialog searchNFCDialog = new Dialog(this);
		searchNFCDialog.setCancelable(true);
		searchNFCDialog.setCanceledOnTouchOutside(true);

		searchNFCDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		searchNFCDialog.setContentView(getLayoutInflater().inflate(
				R.layout.activity_search_nfc, null));

		searchNFCDialog.show();
	}

	private void showWriteNFCDialog() {
		Dialog writeNFCDialog = new Dialog(this);
		writeNFCDialog.setCancelable(true);
		writeNFCDialog.setCanceledOnTouchOutside(true);

		writeNFCDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		writeNFCDialog.setContentView(getLayoutInflater().inflate(
				R.layout.activity_write_nfc, null));

		writeNFCDialog.show();
	}

	private void showHelpDialog() {
		Dialog helpDialog = new Dialog(this);
		helpDialog.setCancelable(true);
		helpDialog.setCanceledOnTouchOutside(true);

		helpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		helpDialog.setContentView(getLayoutInflater().inflate(
				R.layout.activity_help, null));

		helpDialog.show();
	}

	/**
	 * Show a settings dialog.
	 */
	private void showSettingsDialog() {

		Log.i(TAG, "[ACTIVITY] Show Setting Dialog");

		if (settingsDialog == null) {
			settingsDialog = new SettingsDialog(this, lpfWiki, lpfAndDev,
					meanFilter);
			settingsDialog.setCancelable(true);
			settingsDialog.setCanceledOnTouchOutside(true);
		}

		settingsDialog.show();
	}

	/**************** Preference ****************/
	/**
	 * Read in the current user preferences.
	 */
	private void readPrefs() {
		Log.i(TAG, "[ACTIVITY] Read Prefs");
		SharedPreferences prefs = this.getSharedPreferences("filter_prefs",
				Activity.MODE_PRIVATE);

		this.plotLPFAndDev = prefs.getBoolean("plot_lpf_and_dev", false);
		this.plotLPFWiki = prefs.getBoolean("plot_lpf_wiki", false);
		this.plotMeanFilter = prefs.getBoolean("plot_mean", false);

		WIKI_STATIC_ALPHA = prefs.getFloat("lpf_wiki_alpha", 0.1f);
		AND_DEV_STATIC_ALPHA = prefs.getFloat("lpf_and_dev_alpha", 0.9f);
		MEAN_SAMPLE_WINDOW = prefs.getInt("window_mean", 50);

	}

}
