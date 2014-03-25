package com.midhunarmid.movesapi.place;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.midhunarmid.movesapi.storyline.StorylineData;

/**
 * This class holds the Place Data (usually for {@link StorylineData}), and some related methods to handle those data
 * @author Midhu
 * @see <a href="https://dev.moves-app.com/docs/api_storyline">Read <i>Place</i> Section from 
 * Moves Developer Page for Storyline</a>
 */
public class PlaceData {
	private String id;
	private String name;
	private String type;
	private String foursquareId ;
	private ArrayList<String> foursquareCategoryIds ;
	private LocationData location;
	
	
	/** ***************************************************************************************************** **/	
	/** ******************* Getter methods    *************************************************************** **/
	
	/** A unique identifier (per-user, 64 bit unsigned) of the place **/
	public String getId() {
		return id;
	}
	
	/** Name for the place **/
	public String getName() {
		return name;
	}
	
	/**
	 * Type of place will be one of:<code>
	 * <li><i>unknown</i> : the place has not been identified</li>
	 * <li><i>home</i> : the place is labeled as home</li>
	 * <li><i>school</i> : the place is labeled as school</li>
	 * <li><i>work</i> : the place is labeled as work</li>
	 * <li><i>user</i> : the place has been manually named</li>
	 * <li><i>foursquare</i> : the place has been identified from foursquare</li></code><br><br>
	 */
	public String getType() {
		return type;
	}
	
	/** Foursquare venue id if applicable **/
	public String getFoursquareId() {
		return foursquareId;
	}
	
	/** Foursquare category ids for the place if available **/
	public ArrayList<String> getFoursquareCategoryIds() {
		return foursquareCategoryIds;
	}
	
	/** {@link LocationData} for the place **/
	public LocationData getLocation() {
		return location;
	}
	
	
	/** ***************************************************************************************************** **/	
	/** ******************* Setter methods    *************************************************************** **/
	
	
	/** A unique identifier (per-user, 64 bit unsigned) of the place **/
	public void setId(String id) {
		this.id = id;
	}
	
	/** Name for the place **/
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Type of place will be one of:<code>
	 * <li><i>unknown</i> : the place has not been identified</li>
	 * <li><i>home</i> : the place is labeled as home</li>
	 * <li><i>school</i> : the place is labeled as school</li>
	 * <li><i>work</i> : the place is labeled as work</li>
	 * <li><i>user</i> : the place has been manually named</li>
	 * <li><i>foursquare</i> : the place has been identified from foursquare</li></code><br><br>
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/** Foursquare venue id if applicable **/
	public void setFoursquareId(String foursquareId) {
		this.foursquareId = foursquareId;
	}
	
	/** Foursquare category ids for the place if available **/
	public void setFoursquareCategoryIds(ArrayList<String> foursquareCategoryIds) {
		this.foursquareCategoryIds = foursquareCategoryIds;
	}
	
	/** {@link LocationData} for the place **/
	public void setLocation(LocationData location) {
		this.location = location;
	}
	
	/** ***************************************************************************************************** **/	
	/** ******************* Parser methods    *************************************************************** **/
	
	/**
	 * Parse a {@link JSONObject} of place, then return the corresponding {@link PlaceData} object.
	 * @param jsonObject : the 'place' JSON object to parse 
	 * @return corresponding {@link PlaceData}
	 */
	public static PlaceData parse(JSONObject jsonObject) {
		if (jsonObject != null) {
			PlaceData placeData = new PlaceData();
			
			placeData.id			= jsonObject.optString("id");
			placeData.name 			= jsonObject.optString("name");
			placeData.type			= jsonObject.optString("type");
			placeData.foursquareId	= jsonObject.optString("foursquareId");
			
			JSONObject location = jsonObject.optJSONObject("location");
			if (location != null) {
				placeData.location = LocationData.parse(location);
			}

			JSONArray trackPointsJSONArray		= jsonObject.optJSONArray("foursquareCategoryIds");
			placeData.foursquareCategoryIds = new ArrayList<String>();
			if (trackPointsJSONArray != null) {
				for (int i = 0; i < trackPointsJSONArray.length(); i++) {
					String categoryId  = trackPointsJSONArray.optString(i);
					if (categoryId != null && categoryId.length() > 0) {
						placeData.foursquareCategoryIds.add(categoryId);
					}
				}
			}
			
			return placeData;
		}
    	return null;
	}
}
