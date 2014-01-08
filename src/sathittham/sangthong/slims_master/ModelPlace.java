package sathittham.sangthong.slims_master;

public class ModelPlace {

	private int id;
	private String buildingName;
	private String buildingCode;
	private String floorName;
	private String floorCode;
	private String roomName;
	private String roomCode;
	private double lat;
	private double lon;
	private String image;
	private String info;
	
	public ModelPlace(int id, String buildingName, String buildingCode,
			String floorName, String floorCode, String roomName,
			String roomCode, double lat, double lon, String image, String info) {
		super();
		this.id = id;
		this.buildingName = buildingName;
		this.buildingCode = buildingCode;
		this.floorName = floorName;
		this.floorCode = floorCode;
		this.roomName = roomName;
		this.roomCode = roomCode;
		this.lat = lat;
		this.lon = lon;
		this.image = image;
		this.info = info;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getBuildingName() {
		return buildingName;
	}
	public void setBuildingName(String buildingName) {
		this.buildingName = buildingName;
	}
	public String getBuildingCode() {
		return buildingCode;
	}
	public void setBuildingCode(String buildingCode) {
		this.buildingCode = buildingCode;
	}
	public String getFloorName() {
		return floorName;
	}
	public void setFloorName(String floorName) {
		this.floorName = floorName;
	}
	public String getFloorCode() {
		return floorCode;
	}
	public void setFloorCode(String floorCode) {
		this.floorCode = floorCode;
	}
	public String getRoomName() {
		return roomName;
	}
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	public String getRoomCode() {
		return roomCode;
	}
	public void setRoomCode(String roomCode) {
		this.roomCode = roomCode;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLon() {
		return lon;
	}
	public void setLon(double lon) {
		this.lon = lon;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
}
