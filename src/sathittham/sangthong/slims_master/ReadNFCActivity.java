package sathittham.sangthong.slims_master;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class ReadNFCActivity extends Activity {

	PendingIntent nfcPendingIntent;
	IntentFilter[] intentFiltersArray;
	String[][] techListsArray;
	NfcAdapter nfcAdapter;
	String payload;
	TextView myText;
	String str = "Hello my map";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_read_nfc);

		// Display on Map Button
		Button mapButton = (Button) findViewById(R.id.read_nfc_display_map_btn);
		mapButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ReadNFCActivity.this,MainActivity.class);
				intent.putExtra("Message", payload);
				intent.setAction(Intent.ACTION_VIEW);
				try {
					startActivity(intent);
				} catch (ActivityNotFoundException e) {
					return;
				}

			}
		});
		


		// Get NFC Adapter
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);

		// Create the Pending Intent.
		int requestCode = 0;
		int flags = 0;

		Intent nfcIntent = new Intent(this, getClass());
		nfcIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

		nfcPendingIntent = PendingIntent.getActivity(this, requestCode,
				nfcIntent, flags);

		// Create an Intent Filter limited to the URI or MIME type to
		// intercept TAG scans from.
		IntentFilter tagIntentFilter = new IntentFilter(
				NfcAdapter.ACTION_NDEF_DISCOVERED);
		intentFiltersArray = new IntentFilter[] { tagIntentFilter };

		// Create an array of technologies to handle.
		techListsArray = new String[][] { new String[] { NfcF.class.getName() } };

	}
	
	

	// Process Intent
	private void processIntent(Intent intent) {
		

		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {

			// GET NDEF MESSAGES IN THE TAG
			NdefMessage[] messages = getNdefMessages(getIntent());

			// PROCESS NDEF MESSAGE, Processing TNF_EXTERNAL_TYPE
				
			for (int i = 0; i < messages.length; i++) {
				TextView myText = (TextView) findViewById(R.id.read_nfc_result_text);
				myText.append("Message " + (i + 1) + ":\n");
				for (int j = 0; j < messages[0].getRecords().length; j++) {
					 NdefRecord record = messages[i].getRecords()[j];
					 
					 myText.append((j+1)+"th. Record Tnf: " +
					 record.getTnf()+"\n");
					 myText.append((j+1)+"th. Record type: " +
					 new String(record.getType())+"\n");
					 myText.append((j+1)+"th. Record id: " + new
					 String(record.getId())+"\n");
					 payload = new String(record.getPayload());
					 myText.append((j+1)+"th. Record payload: "+payload+"\n");

				}
			}
		
		}

	}
	
	//Getting and NDEF Message
	NdefMessage[] getNdefMessages(Intent intent) {
		NdefMessage[] message = null;
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
			// Retrieve the extended data from the intent by using intent.getParcelableArrayExtra
			// This method will get the data contained in the intent:
			Parcelable[] rawMessages = intent
					.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			// Check is made for whether the rawMessage array is null
			// If it is not null, is means that the tag contains NDEF message
			// Then, NDEF messages are saved into the message array, whick is from the NdefMessage type
			if (rawMessages != null) {
				message = new NdefMessage[rawMessages.length];
				for (int i = 0; i < rawMessages.length; i++) {
					message[i] = (NdefMessage) rawMessages[i];
				}
			} else {
				//If rawMessages = null
				//It means that the tag is from an uknown type
				//In this situation, an empty message with the record of TNF_UKNOWN is created
				//and return to the main program.
				byte[] empty = new byte[] {};
				NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN,
						empty, empty, empty);
				NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
				message = new NdefMessage[] { msg };
			}
		} else {
			Log.d("", "Unknown intent.");
			finish();
		}
		return message;
	}

	public void onPause() {
		super.onPause();

		nfcAdapter.disableForegroundDispatch(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		nfcAdapter.enableForegroundDispatch(this,
		// Intent that will be used to package the Tag Intent.
				nfcPendingIntent,
				// Array of Intent Filters used to declare the Intents you
				// wish to intercept.
				intentFiltersArray,
				// Array of Tag technologies you wish to handle.
				techListsArray);

		String action = getIntent().getAction();
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
			processIntent(getIntent());
		}
	}


}
