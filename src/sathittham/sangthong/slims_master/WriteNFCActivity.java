package sathittham.sangthong.slims_master;

import java.io.IOException;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class WriteNFCActivity extends Activity {

	private NfcAdapter mNfcAdapter;
	private PendingIntent mPendingIntent;
	private IntentFilter[] mIntentFilters;
    private String[][] mTechLists;
	String urlAddress ="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_write_nfc);
		
		//Add Up Action
		getActionBar().setDisplayHomeAsUpEnabled(true);

		final EditText latitudeEditText = (EditText)findViewById(R.id.write_nfc_latitude_editText);
		final EditText logitudeEditText = (EditText)findViewById(R.id.write_nfc_logitude_editText);
		final EditText buildingCodeEditText = (EditText)findViewById(R.id.write_nfc_builing_code_edittext);
		final EditText floorEditText = (EditText)findViewById(R.id.write_nfc_floor_edittext);
		final EditText headingEditText = (EditText)findViewById(R.id.write_nfc_heading_editText);
		final EditText nodeNameEditText = (EditText)findViewById(R.id.write_nfc_nodename_editText);
		final EditText nodeCodeEditText = (EditText)findViewById(R.id.write_nfc_nodecode_editText);
		final EditText sumDistanceEditText = (EditText)findViewById(R.id.write_nfc_distance_editText);
		
		//Write To Tag Button
		Button writeGeoButton = (Button) findViewById(R.id.write_nfc_write2Tag_btn);
		writeGeoButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				String latitude = latitudeEditText.getText().toString();
				String longitude = logitudeEditText.getText().toString();
				String buildingCode = buildingCodeEditText.getText().toString();
				String floor	= floorEditText.getText().toString();
				String heading = headingEditText.getText().toString();
				String nodeName = nodeNameEditText.getText().toString();
				String nodeCode = nodeCodeEditText.getText().toString();
				String sumDistance = sumDistanceEditText.getText().toString();
				
				urlAddress = latitude + "," + longitude + "," + buildingCode + "," +floor + "," +heading+ "," +nodeName+"," +nodeCode+"," +sumDistance ;
				TextView messageText = (TextView)findViewById(R.id.write_nfc_message_text);
				messageText.setText("Touch NFC Tag to Write GEO loacation \n" +
									"Latitude : " + latitude + "\nLongitude : " + longitude 
									+ "\nBuilding Code : " + buildingCode + "\nFloor : "+ floor+ "\nHeading : "+ heading+ "\nNode Name : "+ nodeName+ "\nNode Code : "+ nodeCode+ "\nSum Distance : "+ sumDistance);
			}
		});
		
		
     	mNfcAdapter = NfcAdapter.getDefaultAdapter(this); 
    	 mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
   	 	IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
   	 	mIntentFilters = new IntentFilter[] {
             ndef,
   	 	};
          mTechLists = new String[][] { new String[] { Ndef.class.getName() },
       		   new String[] { NdefFormatable.class.getName() }};
          

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (mNfcAdapter !=null) mNfcAdapter.enableForegroundDispatch(this, mPendingIntent,mIntentFilters, mTechLists);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mNfcAdapter.disableForegroundDispatch(this);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.i("Foreground dispatch","Discovered tag with intent: " +intent);
		Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		String externalType = "sathittham.com/slims:geoService";
		NdefRecord extRecord = new NdefRecord(NdefRecord.TNF_EXTERNAL_TYPE,externalType.getBytes(), new byte[0], urlAddress.getBytes());
		NdefMessage newMessage = new NdefMessage(new NdefRecord[] {extRecord});
		writeNdefMessageToTag(newMessage, tag);
	}

	
	boolean writeNdefMessageToTag (NdefMessage message, Tag detectedTag) {
		int size = message.toByteArray().length;
		
		try {
			Ndef ndef = Ndef.get(detectedTag);
			if (ndef !=null){
				ndef.connect();
				if(!ndef.isWritable()){
					Toast.makeText(this, "Tag is read-only", Toast.LENGTH_SHORT).show();
					return false;
				}
				if(ndef.getMaxSize() < size){
					Toast.makeText(this, "The data cannot written to tag, Tag capacity is " + ndef.getMaxSize() + "bytes, message is " + size + " bytes." , Toast.LENGTH_SHORT).show();
					return false;
				}
				ndef.writeNdefMessage(message);
				ndef.close();
				Toast.makeText(this, "Message is written tag.", Toast.LENGTH_SHORT).show();
				return true;
			} else {
				NdefFormatable ndefFormat = NdefFormatable.get(detectedTag);
				if(ndefFormat !=null){
					try {
						ndefFormat.connect();
						ndefFormat.format(message);
						ndefFormat.close();
						Toast.makeText(this, "The data is written to the tag", Toast.LENGTH_SHORT).show();
						return true;
					} catch (IOException e) {
						Toast.makeText(this, "Failed to format tag", Toast.LENGTH_SHORT).show();
						return false;
					}
				} else {
					Toast.makeText(this, "NDEF is not supported", Toast.LENGTH_SHORT).show();
					return false;
				}
			}
		} catch (Exception e) {
			Toast.makeText(this, "Write operation is failed", Toast.LENGTH_SHORT).show();
		}
		
		return false;
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
	        NavUtils.navigateUpFromSameTask(this);
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}

}
