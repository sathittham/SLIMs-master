package sathittham.sangthong.slims_master;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class SettingActivity  extends Activity{

	private ListView listview;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		listview = (ListView)findViewById(R.id.settings_listview);
	}
}
