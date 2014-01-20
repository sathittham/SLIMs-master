package sathittham.sangthong.slims_master;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class SettingActivity extends Activity {

	//context
	private Context context;
	
	//listview & adapter & data
	private ListView listview;
	private ArrayList<SettingsItem> data;
	private SettingAdapter listAdapter;

	//shared preferences
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);

		// Context
		context = this;

		// View Matching
		listview = (ListView) findViewById(R.id.settings_listview);

		// Data preparation
		data = new ArrayList<SettingsItem>();
		for (int i = 1; i <= 3; i++) {
			SettingsItem item = new SettingsItem();
			item.setTitle("Option " + i);
			item.setDescription("Description");
			data.add(item);
		}

		// list view and adapter
		listAdapter = new SettingAdapter();
		listview.setAdapter(listAdapter);
		
		//list view event
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adpater, View view, int position,
					long id) {
				data.get(position).setTitle("Test");
				listAdapter.notifyDataSetChanged();
				
			}
		});

	}

	private class SettingAdapter extends BaseAdapter {

		private ViewHolder viewHolder;

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if (view == null) {
				view = LayoutInflater.from(context).inflate(
						R.layout.layout_setting_item, null);
				viewHolder = new ViewHolder();
				// view mathching
				viewHolder.title = (TextView) view
						.findViewById(R.id.settings_item_title);
				viewHolder.description = (TextView) view
						.findViewById(R.id.settings_item_description);
				viewHolder.checker = (CheckBox) view
						.findViewById(R.id.settings_item_checker);
				// set tag
				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}
			// Assign
			viewHolder.title.setText(data.get(position).getTitle());
			viewHolder.description.setText(data.get(position).getDescription());

			if (data.get(position).isChecked()) {
				// checked
			} else {
				// unchecked
			}

			return view;
		}

		private class ViewHolder {
			public TextView title, description;
			public CheckBox checker;

		}
	}
}
