package com.midhunarmid.movesapi.activity;

import org.json.JSONArray;
import org.json.JSONObject;

import com.midhunarmid.movesapi.storyline.StorylineData;

/**
 * This class holds the Trackpoints Data (usually used within a {@link ActivityData}), and some related methods to handle those data
 * @author Midhu
 * @see {@link StorylineData} <br>{@link ActivityData}
 */
public class TrackPointsData {
	private String lat;
	private String lon;
	private String time;
	
	/** ***************************************************************************************************** **/	
	/** ******************* Getter methods    *************************************************************** **/
	
	/** Latitude coordinate **/
	public String getLat() {
		return lat;
	}
	
	/** Longitude coordinate **/
	public String getLon() {
		return lon;
	}
	
	/** Time stamp in ISO 8601 (yyyyMMdd’T’HHmmssZ) format **/
	public String getTime() {
		return time;
	}
	
	/** ***************************************************************************************************** **/	
	/** ******************* Setter methods    *************************************************************** **/
	
	/** Latitude coordinate **/
	public void setLat(String lat) {
		this.lat = lat;
	}
	
	/** Longitude coordinate **/
	public void setLon(String lon) {
		this.lon = lon;
	}
	
	/** Time stamp in ISO 8601 (yyyyMMdd’T’HHmmssZ) format **/
	public void setTime(String time) {
		this.time = time;
	}
	
	
	/** ***************************************************************************************************** **/	
	/** ******************* Parser methods    *************************************************************** **/
	
	/**
	 * Parse a {@link JSONObject} from trackPoints {@link JSONArray}, then return the corresponding 
	 * {@link TrackPointsData} object.
	 * @param jsonObject : the 'trackPoint' JSON object to parse 
	 * @return corresponding {@link TrackPointsData}
	 */
	public static TrackPointsData parse(JSONObject jsonObject) {
		if (jsonObject != null) {
				TrackPointsData trackPointsData = new TrackPointsData();
				trackPointsData.lat		= jsonObject.optString("lat");
				trackPointsData.lon		= jsonObject.optString("lon");
				trackPointsData.time	= jsonObject.optString("time");
				return trackPointsData;
		}
    	return null;
	}
	
}
