package com.midhunarmid.movesapi.storyline;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.midhunarmid.movesapi.segment.SegmentData;
import com.midhunarmid.movesapi.summary.SummaryData;

public class StorylineData {
	private String date;
	private ArrayList<SummaryData> summary;
	private ArrayList<SegmentData> segments;
	private String caloriesIdle;
	private String lastUpdate;
	
	
	/** ***************************************************************************************************** **/	
	/** ******************* Getter methods    *************************************************************** **/
	
	
	public String getDate() {
		return date;
	}
	public ArrayList<SummaryData> getSummary() {
		return summary;
	}
	public ArrayList<SegmentData> getSegments() {
		return segments;
	}
	public String getCaloriesIdle() {
		return caloriesIdle;
	}
	public String getLastUpdate() {
		return lastUpdate;
	}
	

	/** ***************************************************************************************************** **/	
	/** ******************* Setter methods    *************************************************************** **/
	
	public void setDate(String date) {
		this.date = date;
	}
	public void setSummary(ArrayList<SummaryData> summary) {
		this.summary = summary;
	}
	public void setSegments(ArrayList<SegmentData> segments) {
		this.segments = segments;
	}
	public void setCaloriesIdle(String caloriesIdle) {
		this.caloriesIdle = caloriesIdle;
	}
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
