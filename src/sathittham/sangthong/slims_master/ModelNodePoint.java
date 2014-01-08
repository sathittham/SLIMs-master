package sathittham.sangthong.slims_master;

public class ModelNodePoint {

	private int nodeMasterId;
	private String nodeMasterName;
	private double lat;
	private double lon;
	private String buildingName;
	private String buildingCode;
	private String floorName;
	private String floorCode;
	
	public ModelNodePoint(int nodeMasterId, String nodeMasterName, double lat, double lon,
			String buildingName, String buildingCode, String floorName, String floorCode){
		this.nodeMasterId = nodeMasterId;
		this.nodeMasterName = nodeMasterName;
		this.lat = lat;
		this.lon = lon;
		this.buildingCode = buildingCode;
		this.buildingName = buildingName;
		this.floorCode = floorCode;
		this.floorName = floorName;
	}
	
	
	public int getNodeMasterId() {
		return nodeMasterId;
	}
	public void setNodeMasterId(int nodeMasterId) {
		this.nodeMasterId = nodeMasterId;
	}
	public String getNodeMasterName() {
		return nodeMasterName;
	}
	public void setNodeMasterName(String nodeMasterName) {
		this.nodeMasterName = nodeMasterName;
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
}
