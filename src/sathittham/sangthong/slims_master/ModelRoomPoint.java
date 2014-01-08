package sathittham.sangthong.slims_master;

public class ModelRoomPoint {

	private int pointId;
	private String pointCode;
	private double lat;
	private double lon;
	private String buildingName;
	private String buildingCode;
	private String floorName;
	private String floorCode;
	private String roomName;
	private String roomCode;
	
	public ModelRoomPoint(int pointId, String pointCode, double lat,
			double lon, String buildingName, String buildingCode,
			String floorName, String floorCode, String roomName, String roomCode) {
		super();
		this.pointId = pointId;
		this.pointCode = pointCode;
		this.lat = lat;
		this.lon = lon;
		this.buildingName = buildingName;
		this.buildingCode = buildingCode;
		this.floorName = floorName;
		this.floorCode = floorCode;
		this.roomName = roomName;
		this.roomCode = roomCode;
	}
	
	public int getPointId() {
		return pointId;
	}
	public void setPointId(int pointId) {
		this.pointId = pointId;
	}
	public String getPointCode() {
		return pointCode;
	}
	public void setPointCode(String pointCode) {
		this.pointCode = pointCode;
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
	
	
}
