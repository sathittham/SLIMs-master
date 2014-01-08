package sathittham.sangthong.slims_master;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.client.android.CaptureActivity;

public class QRCodeResultActivity extends Activity{

	public static final int REQUEST_QR_SCAN = 0;
    TextView qrCodeResultText;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_result);
        
        Bundle bundle = getIntent().getExtras();
        String getContent = bundle.getString("CONTENT");
        String getFormat = bundle.getString("FORMAT");
        String getType = bundle.getString("TYPE");
        
        qrCodeResultText = (TextView)findViewById(R.id.qr_code_resut_text);
        qrCodeResultText.setText("Content : " + getContent + "\n"
        		+ "Format : " + getFormat + "\n"
        		+ "Type : " + getType);
        
        Button button1 = (Button)findViewById(R.id.qr_code_open_map_btn);
        button1.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext()
//                        , CaptureActivity.class);
//                startActivityForResult(intent, REQUEST_QR_SCAN);
            }
        });
    }
    
   
}
