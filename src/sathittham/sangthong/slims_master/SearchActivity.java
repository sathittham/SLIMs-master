package sathittham.sangthong.slims_master;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class SearchActivity extends Activity {
	private String message = "";
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		message = getIntent().getExtras().getString("message");
		
		// Permission StrictMode
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		final Button btn1 = (Button) findViewById(R.id.button_search);
		// Perform action on click
		btn1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				SearchData();
			}
		});

	}

	public void SearchData() {
		// listView1
		final ListView lisView1 = (ListView) findViewById(R.id.list1);

		// txt search
		final EditText buildingName = (EditText) findViewById(R.id.fmw_direction_to_source_edittext_building);
		final EditText floorName = (EditText) findViewById(R.id.fmw_direction_to_source_edittext_floor);
		final EditText roomName = (EditText) findViewById(R.id.fmw_direction_to_source_edittext_room);

		String txtBuilding = buildingName.getText().toString();
		String txtFloorName = floorName.getText().toString();
		String txtRoomName = roomName.getText().toString();
		
		String url = "http://www.sathittham.com/slims/LookupDestination.php";
		
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		
		try {
			JsonDataManager process = new JsonDataManager(this, url);
			
			if(process.checkUrl()){
				final List<ModelPlace> lookupDestination = process.getLookupDestination(url, txtBuilding, txtFloorName, txtRoomName);

				HashMap<String, String> map;
	
				for(ModelPlace p : lookupDestination){
					map = new HashMap<String, String>();
					map.put("id", Integer.toString(p.getId()));
					map.put("buildingName", p.getBuildingName());
					map.put("buildingCode", p.getBuildingCode());
					map.put("floorName", p.getFloorName());
					map.put("floorCode", p.getFloorCode());
					map.put("roomName", p.getRoomName());
					map.put("roomCode", p.getRoomCode());
					map.put("lat", Double.toString(p.getLat()));
					map.put("lon", Double.toString(p.getLon()));
					map.put("image", p.getImage());
					map.put("info", p.getInfo());
					
					list.add(map);
					
				}

				SimpleAdapter sAdap;
				sAdap = new SimpleAdapter(SearchActivity.this, list,
						R.layout.activity_search_column, new String[] { "id",
								"buildingName", "floorName", "roomName" }, new int[] {
								R.id.ddlID, R.id.ddlBuildingName, R.id.dllFloorName,
								R.id.dllRoomName});
				lisView1.setAdapter(sAdap);
				lisView1.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> myAdapter, View myView,
							int position, long mylng) {
						
						// set value
						String buildingName = lookupDestination.get(position).getBuildingName();
						String floorName = lookupDestination.get(position).getFloorName();
						String roomName = lookupDestination.get(position).getRoomName();
						String image = lookupDestination.get(position).getImage();
						String info = lookupDestination.get(position).getInfo();
						final String nodeDesTo = lookupDestination.get(position).getRoomCode();
						
						final Dialog dialog = new Dialog(SearchActivity.this);
						dialog.setContentView(R.layout.popup_search_destination);
						TextView txtRoomName = (TextView) dialog.findViewById(R.id.textRoomName);
						TextView txFloorName = (TextView) dialog.findViewById(R.id.textFloorName);
						TextView txtBuildingName = (TextView) dialog.findViewById(R.id.textBuildingName);
						Button dialogButtonOk = (Button) dialog.findViewById(R.id.dialogButtonOK);
						Button dialogButtonCancel = (Button) dialog.findViewById(R.id.dialogButtonCancel);
						//ImageView image = (ImageView) dialog.findViewById(R.id.image);
						
						// set dialog detail
						dialog.setTitle("Place Info.");
						
						txtRoomName.setText(roomName);
						txFloorName.setText(floorName);
						txtBuildingName.setText(buildingName);
						
						//image.setImageResource(R.drawable.ic_launcher);

						dialogButtonOk.setOnClickListener(new View.OnClickListener() {
							public void onClick(View v) {

								try {
									Intent intent = new Intent(SearchActivity.this,MainActivity.class);
									intent.putExtra("nodeTo", nodeDesTo);
									intent.putExtra("Message", message);
									intent.setAction(Intent.ACTION_VIEW);
								
									startActivity(intent);
								} catch (ActivityNotFoundException e) {
									throw e;
								} finally{
									dialog.dismiss();
								}
							}
						});
						dialogButtonCancel.setOnClickListener(new View.OnClickListener() {
							public void onClick(View v) {
								dialog.dismiss();
							}
						});
			 
						dialog.show();
					}
				});
			}
			
			/*
			final AlertDialog.Builder viewDetail = new AlertDialog.Builder(this);
			// OnClick Item
			lisView1.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> myAdapter, View myView,
						int position, long mylng) {

					String strid = MyArrList.get(position).get("id").toString();
					String strname = MyArrList.get(position).get("name")
							.toString();
					String strlevel = MyArrList.get(position).get("level")
							.toString();
					String strlat = MyArrList.get(position).get("lat")
							.toString();
					String strlng = MyArrList.get(position).get("lng")
							.toString();

					viewDetail.setIcon(android.R.drawable.btn_star_big_on);
					viewDetail.setTitle("Information");
					viewDetail.setMessage("ID : " + strid + "\n" + "Name : "
							+ strname + "\n" + "Level : " + strlevel + "\n"
							+ "Latitude : " + strlat + "\n" + "Longitude : "
							+ strlng);
					viewDetail.setPositiveButton("From Here",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									dialog.dismiss();
								}
							});
					viewDetail.setNegativeButton("To Here",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									dialog.dismiss();
								}
							});
					viewDetail.show();

				}
			});
			*/

		} catch (Exception e) {
			e.printStackTrace();
		}
	}



}