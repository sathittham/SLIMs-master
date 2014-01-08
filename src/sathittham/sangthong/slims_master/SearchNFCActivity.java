package sathittham.sangthong.slims_master;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.client.android.CaptureActivity;

public class SearchNFCActivity extends Activity {
	
	public static final int REQUEST_QR_SCAN = 0;
    TextView qr_code_result_text;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_nfc);

		GifMovieView gmv = (GifMovieView) findViewById(R.id.gifMovieView);
		gmv.setMovieResource(R.drawable.nfc_qr_tab_512);
		
		qr_code_result_text = (TextView)findViewById(R.id.qr_code_resut_text);

		try {
			Button qrScan = (Button) findViewById(R.id.slims_scan_qr_code_btn);
			qrScan.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(SearchNFCActivity.this,CaptureActivity.class);
					startActivityForResult(intent, REQUEST_QR_SCAN);
				}
			});

		} catch (ActivityNotFoundException anfe) {
			Log.e("onCreate", "Scanner Not Found", anfe);
		}

	}

	  public void onActivityResult(int requestCode, int resultCode
	            , Intent intent) {
	        if (requestCode == REQUEST_QR_SCAN && resultCode == RESULT_OK) {
	            String content = intent.getStringExtra("CONTENT");
	            String format = intent.getStringExtra("FORMAT");
	            String type = intent.getStringExtra("TYPE");
	            //qr_code_result_text.setText(content + "\n" + format + "\n" + type);
	            
	            Intent intent2 = new Intent(SearchNFCActivity.this,QRCodeResultActivity.class);
	            intent2.putExtra("CONTENT", content);
	            intent2.putExtra("FORMAT", format);
	            intent2.putExtra("TYPE", type);
	            startActivity(intent2);
	            
	        } else if(requestCode == REQUEST_QR_SCAN 
	                && resultCode == RESULT_CANCELED) {
	        	qr_code_result_text.setText("");
	        }
	    }

}
