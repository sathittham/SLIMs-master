package sathittham.sangthong.slims_master;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class JsonDataManager {

	private Context con;
	private String url;
	
	public JsonDataManager(Context con,String url){
		this.con = con;
		this.url = url;
	}
	
	public boolean checkUrl(){
		   InputStream is = null;
		   boolean check=false;

			try { 
				HttpClient httpclient = new DefaultHttpClient();
			 	HttpPost httppost = new HttpPost(this.url);
			 	HttpResponse response = httpclient.execute(httppost);
			 	check=true;
			} catch (Exception e) {
				 Toast.makeText(this.con, "Error in http connection " + e.toString(), Toast.LENGTH_LONG).show();
				 Log.e("JsonDataManager", "Error in http connection " + e.toString()+" : checkUrl");
			 }

			return check; 
	}

	public ModelRoomPoint getRoom(String building, String floor){
		
		ModelRoomPoint room = null;
		InputStream is = null;
		String result = "";
	   
		// Sent parameter to PHP file to query DB
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("buildingCode", building));
		nameValuePairs.add(new BasicNameValuePair("floorCode", floor));

		 // Get Connection
		 try { 
			 HttpClient httpclient = new DefaultHttpClient();
			 HttpPost httppost = new HttpPost(this.url);
			 httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			 HttpResponse response = httpclient.execute(httppost);
			 HttpEntity entity = response.getEntity();
			 is = entity.getContent(); 
		 } catch (Exception e) {			 
			 Toast.makeText(this.con, "Error in http connection " + e.toString(), Toast.LENGTH_LONG).show(); 
			 Log.e("JsonDataManager", "Error in http connection " + e.toString()+" : getRoom");
		 } 

		 // Get Json Result
		 try {
			 BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf8"), 8);
			 StringBuilder sb = new StringBuilder();
			 String line = null;
			 while ((line = reader.readLine()) != null) {
				 sb.append(line + "\n");
			 }
			 is.close();
			 result = sb.toString();
		 } catch (Exception e) {
			 Log.e("log_tag", "Error converting result " + e.toString());
			 
		 }

		 // Convert Json data to Object(ModelRoomPoint)
		 try {
			 JSONArray jArray = new JSONArray(result);
			 
			 for (int j = 0; j < jArray.length(); j++) {
				 
				 JSONObject json_data = jArray.getJSONObject(j); 
				 
				 int pointId = Integer.parseInt(json_data.getString("POINT_ID"));
				 String pointCode = json_data.getString("POINT_CODE");
				 double lat = Double.parseDouble(json_data.getString("LAT"));
				 double lon = Double.parseDouble(json_data.getString("LON"));
				 String buildingName = json_data.getString("BUILDING_NAME"); 
				 String buildingCode = json_data.getString("BUILDING_CODE"); 
				 String floorName = json_data.getString("FLOOR_NAME"); 
				 String floorCode = json_data.getString("FLOOR_CODE"); 
				 String roomName = json_data.getString("ROOM_NAME"); 
				 String roomCode = json_data.getString("ROOM_CODE"); 
				 
				 ModelRoomPoint item = new ModelRoomPoint(pointId,pointCode,lat,lon,buildingName, buildingCode, floorName, floorCode, roomName, roomCode);
				 		 	
				 room = item;	
			 }
			 
		 } catch (JSONException e) { 
			 Toast.makeText(this.con, "Error! "+e.toString(), Toast.LENGTH_LONG).show();
		 } catch (Exception e){
			 Log.e("log_tag", "" + e.toString());
		 }
		 
		 return room;	
	}

	public List<ModelRoomPoint> getRoomList(String building, String floor){
		
		List<ModelRoomPoint> roomList = new ArrayList<ModelRoomPoint>();
		InputStream is = null;
		String result = "";
	   
		// Sent parameter to PHP file to query DB
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("buildingCode", building));
		nameValuePairs.add(new BasicNameValuePair("floorCode", floor));

		// Get Connection
		 try { 
			 HttpClient httpclient = new DefaultHttpClient();
			 HttpPost httppost = new HttpPost(this.url);
			 httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			 HttpResponse response = httpclient.execute(httppost);
			 HttpEntity entity = response.getEntity();
			 is = entity.getContent(); 
		 } catch (Exception e) {			 
			 Toast.makeText(this.con, "Error in http connection " + e.toString(), Toast.LENGTH_LONG).show(); 
			 Log.e("JsonDataManager", "Error in http connection " + e.toString()+" : getRoomList");
		 } 

		 // Get Json Result
		 try {
			 BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf8"), 8);
			 StringBuilder sb = new StringBuilder();
			 String line = null;
			 while ((line = reader.readLine()) != null) {
				 sb.append(line + "\n");
			 }
			 is.close();
			 result = sb.toString();
			 //Log.e("log_tag: getRoomList", "" + result.toString());
		 } catch (Exception e) {
			 Log.e("log_tag", "Error converting result " + e.toString());
			 
		 }

		// Convert Json data to List of Object(ModelRoomPoint) 
		 try {
			 JSONArray jArray = new JSONArray(result);
			 
			 for (int j = 0; j < jArray.length(); j++) {
				 
				 JSONObject json_data = jArray.getJSONObject(j); 
				 
				 int pointId = Integer.parseInt(json_data.getString("POINT_ID"));
				 String pointCode = json_data.getString("POINT_CODE");
				 double lat = Double.parseDouble(json_data.getString("LAT"));
				 double lon = Double.parseDouble(json_data.getString("LON"));
				 String buildingName = json_data.getString("BUILDING_NAME"); 
				 String buildingCode = json_data.getString("BUILDING_CODE"); 
				 String floorName = json_data.getString("FLOOR_NAME"); 
				 String floorCode = json_data.getString("FLOOR_CODE"); 
				 String roomName = json_data.getString("ROOM_NAME"); 
				 String roomCode = json_data.getString("ROOM_CODE"); 
				 
				 ModelRoomPoint item = new ModelRoomPoint(pointId,pointCode,lat,lon,buildingName, buildingCode, floorName, floorCode, roomName, roomCode);
				 		 	
				 roomList.add(item);	
			 }
			 
		 } catch (JSONException e) { 
			 Toast.makeText(this.con, "Error! "+e.toString(), Toast.LENGTH_LONG).show();
		 } catch (Exception e){
			 Log.e("log_tag", "" + e.toString());
		 }
		 
		 return roomList;	
	}

	public List<ModelNodePoint> getNodeList(String urlGetshortPath, String building, String floor, String nodeFrom, String nodeTo){
		
		List<ModelNodePoint> nodeList = new ArrayList<ModelNodePoint>();
		InputStream is = null;
		String result = "";
	   
		// Sent parameter to PHP file to query DB
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("buildingCode", building));
		nameValuePairs.add(new BasicNameValuePair("floorCode", floor));
		nameValuePairs.add(new BasicNameValuePair("nodeFrom", nodeFrom));
		nameValuePairs.add(new BasicNameValuePair("nodeTo", nodeTo));

		// Get Connection
		 try { 
			 HttpClient httpclient = new DefaultHttpClient();
			 HttpPost httppost = new HttpPost(urlGetshortPath);
			 httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			 HttpResponse response = httpclient.execute(httppost);
			 HttpEntity entity = response.getEntity();
			 is = entity.getContent(); 
		 } catch (Exception e) {			 
			 Toast.makeText(this.con, "Error in http connection " + e.toString(), Toast.LENGTH_LONG).show(); 
			 Log.e("JsonDataManager", "Error in http connection " + e.toString()+" : getNodeList");
		 } 

		 // Get Json Result
		 try {
			 BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf8"), 8);
			 StringBuilder sb = new StringBuilder();
			 String line = null;
			 while ((line = reader.readLine()) != null) {
				 sb.append(line + "\n");
			 }
			 is.close();
			 result = sb.toString();
			 //Log.e("log_tag: getNodeList", "" + result.toString());
		 } catch (Exception e) {
			 Log.e("log_tag", "Error converting result " + e.toString());
			 
		 }

		// Convert Json data to List of Object(ModelRoomPoint) 
		 try {
			 JSONArray jArray = new JSONArray(result);
			 
			 for (int j = 0; j < jArray.length(); j++) {
				 
				 JSONObject json_data = jArray.getJSONObject(j); 
				 
				 int nodeMasterId = Integer.parseInt(json_data.getString("NODE_MASTER_ID"));
				 String nodeMasterName = json_data.getString("NODE_MASTER_NAME");
				 double lat = Double.parseDouble(json_data.getString("LAT"));
				 double lon = Double.parseDouble(json_data.getString("LON"));
				 String buildingName = json_data.getString("BUILDING_NAME"); 
				 String buildingCode = json_data.getString("BUILDING_CODE"); 
				 String floorName = json_data.getString("FLOOR_NAME"); 
				 String floorCode = json_data.getString("FLOOR_CODE"); 
				 
				 ModelNodePoint item = new ModelNodePoint(nodeMasterId,nodeMasterName,lat,lon,buildingName, buildingCode, floorName, floorCode);
				 		 	
				 nodeList.add(item);	
			 }
			 
		 } catch (JSONException e) { 
			 Toast.makeText(this.con, "Error! "+e.toString(), Toast.LENGTH_LONG).show();
		 } catch (Exception e){
			 Log.e("log_tag", "" + e.toString());
		 }
		 
		 return nodeList;	
	}
	
	public Object[] getNodeListPlusDistince(String urlGetshortPath, String building, String floor, String nodeFrom, String nodeTo){
		Object[] obj = new Object[3];
		List<ModelNodePoint> nodeList = new ArrayList<ModelNodePoint>();
		List<ModelNodeDistance> nodeDistance = new ArrayList<ModelNodeDistance>();
		InputStream is = null;
		String result = "";
	   
		// Sent parameter to PHP file to query DB
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("buildingCode", building));
		nameValuePairs.add(new BasicNameValuePair("floorCode", floor));
		nameValuePairs.add(new BasicNameValuePair("nodeFrom", nodeFrom));
		nameValuePairs.add(new BasicNameValuePair("nodeTo", nodeTo));

		// Get Connection
		 try { 
			 HttpClient httpclient = new DefaultHttpClient();
			 HttpPost httppost = new HttpPost(urlGetshortPath);
			 httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			 HttpResponse response = httpclient.execute(httppost);
			 HttpEntity entity = response.getEntity();
			 is = entity.getContent(); 
		 } catch (Exception e) {			 
			 Toast.makeText(this.con, "Error in http connection " + e.toString(), Toast.LENGTH_LONG).show(); 
			 Log.e("JsonDataManager", "Error in http connection " + e.toString()+" : getNodeList");
		 } 

		 // Get Json Result
		 try {
			 BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf8"), 8);
			 StringBuilder sb = new StringBuilder();
			 String line = null;
			 while ((line = reader.readLine()) != null) {
				 sb.append(line + "\n");
			 }
			 is.close();
			 result = sb.toString();
			 //Log.e("log_tag: getNodeList", "" + result.toString());
		 } catch (Exception e) {
			 Log.e("log_tag", "Error converting result " + e.toString());
			 
		 }

		// Convert Json data to List of Object(ModelRoomPoint) 
		 try {
			 JSONArray jArray = new JSONArray(result);
			 
			 for (int j = 0; j < (jArray.length()/2); j++) {
				 
				 JSONObject json_data = jArray.getJSONObject(j); 
				 
				 int nodeMasterId = Integer.parseInt(json_data.getString("NODE_MASTER_ID"));
				 String nodeMasterName = json_data.getString("NODE_MASTER_NAME");
				 double lat = Double.parseDouble(json_data.getString("LAT"));
				 double lon = Double.parseDouble(json_data.getString("LON"));
				 String buildingName = json_data.getString("BUILDING_NAME"); 
				 String buildingCode = json_data.getString("BUILDING_CODE"); 
				 String floorName = json_data.getString("FLOOR_NAME"); 
				 String floorCode = json_data.getString("FLOOR_CODE"); 
				 
				 ModelNodePoint item = new ModelNodePoint(nodeMasterId,nodeMasterName,lat,lon,buildingName, buildingCode, floorName, floorCode);
				 		 	
				 nodeList.add(item);	
			 }
			 obj[0] = nodeList;
			 int distance = 0;
			 for (int k = (jArray.length()/2); k < (jArray.length()-1); k++) {
				 
				 JSONObject json_data = jArray.getJSONObject(k); 
				 
				 String fromNode = json_data.getString("FROM_NODE");
				 String toNode = json_data.getString("TO_NODE");
				 if(!json_data.getString("DISTANCE").trim().equals("")){
					  distance =  Integer.parseInt(json_data.getString("DISTANCE"));
				 }
				 
				 
				 ModelNodeDistance item  = new ModelNodeDistance(fromNode, toNode, distance);
				 nodeDistance.add(item);
			 }
			 obj[1] = nodeDistance;
			 
			 JSONObject json_data = jArray.getJSONObject(jArray.length()-1);
			 String allDistance = json_data.getString("ALL_DISTANCE");
			 obj[2] = allDistance;
			 
		 } catch (JSONException e) { 
			 Toast.makeText(this.con, "Error! "+e.toString(), Toast.LENGTH_LONG).show();
		 } catch (Exception e){
			 Log.e("log_tag", "" + e.toString());
		 }
		 
		 return obj;	
	}
	
	public List<ModelPlace> getLookupDestination(String urlGetLookupDestination, String txtSearchBuilding, String txtSearchFloor, String txtSearchRoom){
		
		List<ModelPlace> resultList = new ArrayList<ModelPlace>();
		InputStream is = null;
		String result = "";
	   
		// Sent parameter to PHP file to query DB
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("buildingName", txtSearchBuilding));
		nameValuePairs.add(new BasicNameValuePair("floorName", txtSearchFloor));
		nameValuePairs.add(new BasicNameValuePair("roomName", txtSearchRoom));

		// Get Connection
		 try { 
			 HttpClient httpclient = new DefaultHttpClient();
			 HttpPost httppost = new HttpPost(urlGetLookupDestination);
			 httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			 HttpResponse response = httpclient.execute(httppost);
			 HttpEntity entity = response.getEntity();
			 is = entity.getContent(); 
		 } catch (Exception e) {			 
			 Toast.makeText(this.con, "Error in http connection " + e.toString(), Toast.LENGTH_LONG).show(); 
			 Log.e("JsonDataManager", "Error in http connection " + e.toString()+" : getLookupDestination");
		 } 

		 // Get Json Result
		 try {
			 BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf8"), 8);
			 StringBuilder sb = new StringBuilder();
			 String line = null;
			 while ((line = reader.readLine()) != null) {
				 sb.append(line + "\n");
			 }
			 is.close();
			 result = sb.toString();
			 Log.e("log_tag: getLookupDestination", "" + result.toString());
		 } catch (Exception e) {
			 Log.e("log_tag", "Error converting result " + e.toString());
			 
		 }

		// Convert Json data to List of Object(ModelRoomPoint) 
		 try {
			 JSONArray jArray = new JSONArray(result);
			 
			 for (int j = 0; j < jArray.length(); j++) {
				 
				 JSONObject json_data = jArray.getJSONObject(j); 
				 
				 int id = Integer.parseInt(json_data.getString("ID"));
				 String buildingName = json_data.getString("BUILDING_NAME"); 
				 String buildingCode = json_data.getString("BUILDING_CODE"); 
				 String floorName = json_data.getString("FLOOR_NAME"); 
				 String floorCode = json_data.getString("FLOOR_CODE"); 
				 String roomName = json_data.getString("ROOM_NAME"); 
				 String roomrCode = json_data.getString("ROOM_CODE"); 
				 double lat = Double.parseDouble(json_data.getString("LAT"));
				 double lon = Double.parseDouble(json_data.getString("LON"));
				 String image = json_data.getString("IMAGE"); 
				 String info = json_data.getString("INFO");
				 
				 ModelPlace item = new ModelPlace(id,buildingName,buildingCode,floorName,floorCode, roomName, roomrCode, lat, lon, image, info);
				 		 	
				 resultList.add(item);	
			 }
			 
		 } catch (JSONException e) { 
			 Toast.makeText(this.con, "Error! "+e.toString(), Toast.LENGTH_LONG).show();
		 } catch (Exception e){
			 Log.e("log_tag", "" + e.toString());
		 }
		 
		 return resultList;	
	}
}
