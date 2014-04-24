package com.midhunarmid.movesapi.storyline;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.midhunarmid.movesapi.segment.SegmentData;
import com.midhunarmid.movesapi.summary.SummaryData;
import com.midhunarmid.movesapi.summary.SummaryListData;

/**
 * This class holds the Storyline Data of a Moves User, and some related methods to handle those data
 * @author Midhu
 * @see <a href="https://dev.moves-app.com/docs/api_storyline">Moves Developer Page for Storyline</a>
 * @see SummaryListData
 */
public class StorylineData {
	private String date;
	private ArrayList<SummaryData> summary;
	private ArrayList<SegmentData> segments;
	private String caloriesIdle;
	private String lastUpdate;
	
	
	/** ***************************************************************************************************** **/	
	/** ******************* Getter methods    *************************************************************** **/
	
	/** Storyline date, in format yyyyMMdd **/
	public String getDate() {
		return date;
	}
	
	/** {@link ArrayList} of {@link SummaryData} **/
	public ArrayList<SummaryData> getSummary() {
		return summary;
	}
	
	/** {@link ArrayList} of {@link SegmentData} **/
	public ArrayList<SegmentData> getSegments() {
		return segments;
	}
	
	/** Daily idle burn in kcal. Available if user has at least once enabled calories **/
	public String getCaloriesIdle() {
		return caloriesIdle;
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
	
	/** {@link ArrayList} of {@link SummaryData} **/
	public void setSummary(ArrayList<SummaryData> summary) {
		this.summary = summary;
	}
	
	/** {@link ArrayList} of {@link SegmentData} **/
	public void setSegments(ArrayList<SegmentData> segments) {
		this.segments = segments;
	}
	
	/** Daily idle burn in kcal. Available if user has at least once enabled calories **/
	public void setCaloriesIdle(String caloriesIdle) {
		this.caloriesIdle = caloriesIdle;
	}
	
	/** When the summary data was last updated in ISO 8601 (yyyyMMdd’T’HHmmssZ) format, always in UTC **/
	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	/** ***************************************************************************************************** **/	
	/** ******************* Parser methods    *************************************************************** **/
	
	/**
	 * Parse a {@link JSONObject} from storyline {@link JSONArray}, then return the corresponding {@link StorylineData} object.
	 * @param jsonObject : the storyline JSON object received from server 
	 * @return corresponding {@link StorylineData}
	 */
	public static StorylineData parse(JSONObject jsonObject) {
		if (jsonObject != null) {
			StorylineData storylineData = new StorylineData();
				
			storylineData.date			= jsonObject.optString("date");
			storylineData.caloriesIdle	= jsonObject.optString("caloriesIdle");
			storylineData.lastUpdate	= jsonObject.optString("lastUpdate");
			storylineData.summary		= new ArrayList<SummaryData>();
			storylineData.segments		= new ArrayList<SegmentData>();
			
			JSONArray summariesJsonArray= jsonObject.optJSONArray("summary");
			if (summariesJsonArray != null) {
				for (int i = 0; i < summariesJsonArray.length(); i++) {
					JSONObject summaryJsonObject = summariesJsonArray.optJSONObject(i);
					if (summaryJsonObject != null) {
						storylineData.summary.add(SummaryData.parse(summaryJsonObject));
					}
				}
			}
			
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
