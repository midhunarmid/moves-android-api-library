package com.midhunarmid.movesapi.place;

import org.json.JSONArray;
import org.json.JSONObject;

import com.midhunarmid.movesapi.storyline.StorylineData;

/**
 * This class holds the Location Data (usually used within a {@link PlaceData}), and some related methods to handle those data
 * @author Midhu
 * @see {@link StorylineData} <br>{@link PlaceData}
 */
public class LocationData {
	private String lat;
	private String lon;
	
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
	

	/** ***************************************************************************************************** **/	
	/** ******************* Parser methods    *************************************************************** **/
	
	/**
	 * Parse a {@link JSONObject} from trackPoints {@link JSONArray}, then return the corresponding 
	 * {@link LocationData} object.
	 * @param jsonObject : the 'trackPoint' JSON object to parse 
	 * @return corresponding {@link LocationData}
	 */
	public static LocationData parse(JSONObject jsonObject) {
		if (jsonObject != null) {
				LocationData trackPointsData = new LocationData();
				trackPointsData.lat		= jsonObject.optString("lat");
				trackPointsData.lon		= jsonObject.optString("lon");
				return trackPointsData;
		}
    	return null;
	}
	
}
