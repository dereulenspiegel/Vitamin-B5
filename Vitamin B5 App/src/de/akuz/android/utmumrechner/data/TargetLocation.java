package de.akuz.android.utmumrechner.data;

public class TargetLocation {

	private String mgrsCoordinate;
	private String description;
	private String pictureUrl;
	private String name;
	private long id;

	public String getMgrsCoordinate() {
		return mgrsCoordinate;
	}

	public void setMgrsCoordinate(String mgrsCoordinate) {
		this.mgrsCoordinate = mgrsCoordinate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPictureUrl() {
		return pictureUrl;
	}

	public void setPictureUrl(String pictureUrl) {
		this.pictureUrl = pictureUrl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	long getId() {
		return id;
	}

	void setId(long id) {
		this.id = id;
	}

}
