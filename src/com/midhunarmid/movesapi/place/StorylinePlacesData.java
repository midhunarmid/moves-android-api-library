package com.midhunarmid.movesapi.place;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.midhunarmid.movesapi.activity.ActivityData;
import com.midhunarmid.movesapi.segment.SegmentData;
import com.midhunarmid.movesapi.storyline.StorylineData;

/**
 * This class holds the details about daily places for a Moves User, and some related methods to handle those data
 * <br><br><i>{@link ActivityData} will not be there (or it will be empty) in {@link SegmentData} response</i>
 * @author Midhu
 * @see <a href="https://dev.moves-app.com/docs/api_places">Moves Developer Page for Places</a>
 * @see StorylineData
 */
public class StorylinePlacesData {
	private String date;
	private ArrayList<SegmentData> segments;
	private String lastUpdate;
	
	
	/** ***************************************************************************************************** **/	
	/** ******************* Getter methods    *************************************************************** **/
	
	/** Storyline date, in format yyyyMMdd **/
	public String getDate() {
		return date;
	}
	
	/** {@link ArrayList} of {@link SegmentData} **/
	public ArrayList<SegmentData> getSegments() {
		return segments;
	}
	
	/** When the storyline was last updated in ISO 8601 (yyyyMMdd’T’HHmmssZ) format, always in UTC **/
	public String getLastUpdate() {
		return lastUpdate;
	}
	

	/** ***************************************************************************************************** **/	
	/** ******************* Setter methods    *************************************************************** **/
	
	/** Storyline date, in format yyyyMMdd **/
	public void setDate(String date) {
		this.date = date;
	}
	
	/** {@link ArrayList} of {@link SegmentData} **/
	public void setSegments(ArrayList<SegmentData> segments) {
		this.segments = segments;
	}
	
	/** When the summary data was last updated in ISO 8601 (yyyyMMdd’T’HHmmssZ) format, always in UTC **/
	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	/** ***************************************************************************************************** **/	
	/** ******************* Parser methods    *************************************************************** **/
	
	/**
	 * Parse a {@link JSONObject} from storyline {@link JSONArray}, then return the corresponding {@link StorylinePlacesData} object.
	 * @param jsonObject : the storyline JSON object received from server 
	 * @return corresponding {@link StorylinePlacesData}
	 */
	public static StorylinePlacesData parse(JSONObject jsonObject) {
		if (jsonObject != null) {
			StorylinePlacesData storylineData = new StorylinePlacesData();
				
			storylineData.date			= jsonObject.optString("date");
			storylineData.lastUpdate	= jsonObject.optString("lastUpdate");
			storylineData.segments		= new ArrayList<SegmentData>();
			
			JSONArray segmentsJsonArray= jsonObject.optJSONArray("segments");
			if (segmentsJsonArray != null) {
				for (int i = 0; i < segmentsJsonArray.length(); i++) {
					JSONObject segment = segmentsJsonArray.optJSONObject(i);
					if (segment != null) {
						storylineData.segments.add(SegmentData.parse(segment));
					}
				}
			}
				
			return storylineData;
		}
    	return null;
	}
}
