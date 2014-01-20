package sathittham.sangthong.slims_master;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class UserInfoActivity extends Activity {

	private static final String TAG = "SLIMs-master";
	private EditText mUserHeightValue;
	private EditText mUserWeightValue;
	private RadioGroup mUserGender;
	private RadioButton mUserMale;
	private RadioButton mUserFemale;
	private Button mUserSubmit;
	private Button mUserReset;
	private TextView mUserStepLengthValue;

	private String userHeightPref;
	private String userWeightPref;
	private String userGenderPref;

	private Double mUserStepLength;
	private Double maleStepLength;
	private Double femaleStepLength;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_info);

		Log.i(TAG, "[USERINFOACTIVITY] onCreate()");

		mUserHeightValue = (EditText) findViewById(R.id.user_height_value);
		mUserHeightValue.setInputType(InputType.TYPE_CLASS_NUMBER);

		mUserWeightValue = (EditText) findViewById(R.id.user_weight_value);
		mUserWeightValue.setInputType(InputType.TYPE_CLASS_NUMBER);

		mUserStepLengthValue = (TextView) findViewById(R.id.user_step_length);

		mUserGender = (RadioGroup) findViewById(R.id.user_gender_group);
		mUserMale = (RadioButton) findViewById(R.id.user_gender_male);
		mUserFemale = (RadioButton) findViewById(R.id.user_gender_female);

		// Submit Clicked
		mUserSubmit = (Button) findViewById(R.id.user_submit);
		mUserSubmit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				savePreferences();
				
			}
		});

		// Reset Clicked
		mUserReset = (Button) findViewById(R.id.user_reset);
		mUserReset.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				resetPreferences(v);
			}
		});

		readPreferences();
		

	}

	/*
	 * calculation Step Length
	 */
	private Double calSteplength() {

		Log.i(TAG, "[USERINFOACTIVITY] calSteplength()");

		double userHeight;

		// Read Height
		String userHeightPref = ""
				+ PreferenceConnector.readInteger(this,
						PreferenceConnector.HEIGHT, 0);
		userHeight = Double.parseDouble(userHeightPref);

		// Read Gender
		String userGenderPref = ""
				+ PreferenceConnector.readString(this,
						PreferenceConnector.GENDER, "Male");

		if (userGenderPref.equals("Male")) {
			maleStepLength = 0.415 * userHeight;
			return maleStepLength;
		} else {
			femaleStepLength = 0.413 * userHeight;
			return femaleStepLength;
		}

	}

	/*
	 * Read the data refer to save and display them into edit text
	 */
	private void readPreferences() {

		Log.i(TAG, "[USERINFOACTIVITY] readPreferences()");

		// Read Height
		String userHeightPref = ""
				+ PreferenceConnector.readInteger(this,
						PreferenceConnector.HEIGHT, 0);
		mUserHeightValue.setText((userHeightPref.equals("0")) ? null
				: userHeightPref);

		// Read Weight
		String userWeightPref = ""
				+ PreferenceConnector.readInteger(this,
						PreferenceConnector.WEIGHT, 0);
		mUserWeightValue.setText((userWeightPref.equals("0")) ? null
				: userWeightPref);

		// Read Gender
		String userGenderPref = ""
				+ PreferenceConnector.readString(this,
						PreferenceConnector.GENDER, "Male");
		if (userGenderPref.equals("Male")) {
			// If Gender = Male
			mUserMale.setChecked(true);
		} else {
			// If Gender = Feale
			mUserFemale.setChecked(true);
		}
	}

	/*
	 * Save the data
	 */
	private void savePreferences() {

		Log.i(TAG, "[USERINFOACTIVITY] savePreferences()");

		String userHeightValueText = mUserHeightValue.getText().toString();
		String userWeightValueText = mUserWeightValue.getText().toString();

		// Save Height
		if (userHeightValueText != null && !userHeightValueText.equals("")) {
			PreferenceConnector.writeInteger(this, PreferenceConnector.HEIGHT,
					Integer.parseInt(userHeightValueText));
		}

		// Save Weight
		if (userWeightValueText != null && !userWeightValueText.equals("")) {
			PreferenceConnector.writeInteger(this, PreferenceConnector.WEIGHT,
					Integer.parseInt(userWeightValueText));
		}

		// Save Gender
		int selectedId = mUserGender.getCheckedRadioButtonId();
		switch (selectedId) {
		case R.id.user_gender_male:
			PreferenceConnector.writeString(this, PreferenceConnector.GENDER,
					"Male");
			break;
		case R.id.user_gender_female:
			PreferenceConnector.writeString(this, PreferenceConnector.GENDER,
					"Female");
			break;
		}
		
		mUserStepLength = calSteplength();
		mUserStepLengthValue.setText("Your Step Length is : " + String.valueOf(mUserStepLength)+ " cm.");

		Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
	}

	/*
	 * Reset the data also :
	 */
	public void resetPreferences(View View) {

		Log.i(TAG, "[USERINFOACTIVITY] resetPreferences()");

		PreferenceConnector.getEditor(this).clear().commit();
		mUserStepLengthValue.setText("");
		Toast.makeText(this, "Reseted", Toast.LENGTH_SHORT).show();
		readPreferences();
	}

}
