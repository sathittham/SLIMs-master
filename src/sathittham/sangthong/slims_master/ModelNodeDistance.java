package sathittham.sangthong.slims_master;

public class ModelNodeDistance {

	private String nodeFrom;
	private String nodeTo;
	private int distince;
	
	public ModelNodeDistance(String nodeFrom, String nodeTo, int distince) {
		super();
		this.nodeFrom = nodeFrom;
		this.nodeTo = nodeTo;
		this.distince = distince;
	}
	
	public String getNodeFrom() {
		return nodeFrom;
	}
	public void setNodeFrom(String nodeFrom) {
		this.nodeFrom = nodeFrom;
	}
	public String getNodeTo() {
		return nodeTo;
	}
	public void setNodeTo(String nodeTo) {
		this.nodeTo = nodeTo;
	}
	public int getDistince() {
		return distince;
	}
	public void setDistince(int distince) {
		this.distince = distince;
	}
}
