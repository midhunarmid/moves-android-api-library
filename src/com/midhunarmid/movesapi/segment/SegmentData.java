package com.midhunarmid.movesapi.segment;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.midhunarmid.movesapi.activity.ActivityData;
import com.midhunarmid.movesapi.place.PlaceData;

/**
 * This class holds the Segment Data, and some related methods to handle those data
 * @author Midhu
 * @see <a href="https://dev.moves-app.com/docs/api_storyline">Read <i>Segment</i> Section from 
 * Moves Developer Page for Storyline</a>
 */
public class SegmentData {
	private String type;
	private String startTime;
	private String endTime;
	private PlaceData place;
	private ArrayList<ActivityData> activities;
	private String lastUpdate;
	
	
	/** ***************************************************************************************************** **/	
	/** ******************* Getter methods    *************************************************************** **/

	/** Currently one of <code>move</code> or <code>place</code> **/
	public String getType() {
		return type;
	}
	
	/** Segment start time in ISO 8601 (yyyyMMdd’T’HHmmssZ) format **/
	public String getStartTime() {
		return startTime;
	}
	
	/** Segment end time in ISO 8601 (yyyyMMdd’T’HHmmssZ) format **/
	public String getEndTime() {
		return endTime;
	}
	
	/** Info about place, {@link PlaceData} **/
	public PlaceData getPlace() {
		return place;
	}
	
	/** {@link ArrayList} of {@link ActivityData} for this segment **/
	public ArrayList<ActivityData> getActivities() {
		return activities;
	}
	
	/** When the segment was last updated in ISO 8601 (yyyyMMdd’T’HHmmssZ) format, always in UTC **/
	public String getLastUpdate() {
		return lastUpdate;
	}
	
	
	/** ***************************************************************************************************** **/	
	/** ******************* Setter methods    *************************************************************** **/
	
	/** Currently one of <code>move</code> or <code>place</code> **/
	public void setType(String type) {
		this.type = type;
	}
	
	/** Segment start time in ISO 8601 (yyyyMMdd’T’HHmmssZ) format **/
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	
	/** Segment end time in ISO 8601 (yyyyMMdd’T’HHmmssZ) format **/
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	
	/** Info about place, {@link PlaceData} **/
	public void setPlace(PlaceData place) {
		this.place = place;
	}
	
	/** {@link ArrayList} of {@link ActivityData} for this segment **/
	public void setActivities(ArrayList<ActivityData> activities) {
		this.activities = activities;
	}
	
	/** When the segment was last updated in ISO 8601 (yyyyMMdd’T’HHmmssZ) format, always in UTC **/
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
