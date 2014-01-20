package sathittham.sangthong.slims_master;

public class SettingsItem extends Object {

	private String title;
	private String description;
	private boolean isChecked;
	
	//Constructor
	public SettingsItem(){
		title = null;
		description = null;
		isChecked = false;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public boolean isChecked() {
		return isChecked;
	}
	
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	
	
}
