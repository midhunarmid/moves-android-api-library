package com.midhunarmid.movesapi.segment;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.midhunarmid.movesapi.activity.ActivityData;
import com.midhunarmid.movesapi.place.PlaceData;

public class SegmentData {
	private String type;
	private String startTime;
	private String endTime;
	private PlaceData place;
	private ArrayList<ActivityData> activities;
	private String lastUpdate;
	
	
	/** ***************************************************************************************************** **/	
	/** ******************* Getter methods    *************************************************************** **/

	
	public String getType() {
		return type;
	}
	public String getStartTime() {
		return startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public PlaceData getPlace() {
		return place;
	}
	public ArrayList<ActivityData> getActivities() {
		return activities;
	}
	public String getLastUpdate() {
		return lastUpdate;
	}
	
	
	/** ***************************************************************************************************** **/	
	/** ******************* Setter methods    *************************************************************** **/
	
	public void setType(String type) {
		this.type = type;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public void setPlace(PlaceData place) {
		this.place = place;
	}
	public void setActivities(ArrayList<ActivityData> activities) {
		this.activities = activities;
	}
	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	
	/**
	 * Parse a {@link JSONObject} from segments {@link JSONArray}, then return the corresponding {@link SegmentData} object.
	 * @param jsonObject : the 'segment' JSON object to parse 
	 * @return corresponding {@link SegmentData}
	 */
	public static SegmentData parse(JSONObject jsonObject) {
		if (jsonObject != null) {
			SegmentData segmentData = new SegmentData();
			segmentData.type		= jsonObject.optString("type");
			segmentData.startTime	= jsonObject.optString("startTime");
			segmentData.endTime		= jsonObject.optString("endTime");
			segmentData.lastUpdate 	= jsonObject.optString("lastUpdate");
			
			JSONArray activitiesJSONArray = jsonObject.optJSONArray("activities");
			segmentData.activities = new ArrayList<ActivityData>();
			if (activitiesJSONArray != null) {
				for (int i = 0; i < activitiesJSONArray.length(); i++) {
					JSONObject activityJsonObject = activitiesJSONArray.optJSONObject(i);
					if (activityJsonObject != null) {
						segmentData.activities.add(ActivityData.parse(activityJsonObject));
					}
				}
			}
			
			JSONObject placeJsonObject = jsonObject.optJSONObject("place");
			if (placeJsonObject != null) {
				segmentData.place = PlaceData.parse(placeJsonObject);
			}
			
			return segmentData;
		}
    	return null;
	}
}
