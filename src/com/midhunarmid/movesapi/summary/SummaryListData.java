package com.midhunarmid.movesapi.summary;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Get daily activity summaries for user, including step count, distance and duration for each activity (if applicable).
 * Final summary for a particular date will only be available at earliest after midnight in the user’s current time zone.
 * @author Midhu
 * @see <a href="https://dev.moves-app.com/docs/api_summaries">Moves Developer Page for Daily Summaries</a>
 * @see SummaryData
 */
public class SummaryListData {
	public static final String 		DATEFORMAT		= "yyyyMMdd";
	
	private String					date;
	private String					caloriesIdle;
	private String					lastUpdate;
	private ArrayList<SummaryData> 	summaries;
	
	/** ***************************************************************************************************** **/	
	/** ******************* Getter methods    *************************************************************** **/
	
	/** Summary date, in format yyyyMMdd **/
	public String getDate() {
		return date;
	}

	/** Daily idle burn in kcal. Available if user has at least once enabled calories **/
	public String getCaloriesIdle() {
		return caloriesIdle;
	}

	/** When the summary data was last updated in ISO 8601 (yyyyMMdd’T’HHmmssZ) format, always in UTC **/
	public String getLastUpdate() {
		return lastUpdate;
	}

	/** An {@link ArrayList} of {@link SummaryData} if there are some summary for this date 
	 * or <code>null</code> if there is no summary available for the date **/
	public ArrayList<SummaryData> getSummaries() {
		return summaries;
	}

	/** ***************************************************************************************************** **/	
	/** ******************* Setter methods    *************************************************************** **/
	
	/** Summary date, in format yyyyMMdd **/
	public void setDate(String date) {
		this.date = date;
	}

	/** Daily idle burn in kcal. Available if user has at least once enabled calories **/
	public void setCaloriesIdle(String caloriesIdle) {
		this.caloriesIdle = caloriesIdle;
	}

	/** When the summary data was last updated in ISO 8601 (yyyyMMdd’T’HHmmssZ) format, always in UTC **/
	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	/** An {@link ArrayList} of {@link SummaryData} if there are some summary for this date 
	 * or <code>null</code> if there is no summary available for the date **/
	public void setSummaries(ArrayList<SummaryData> summaries) {
		this.summaries = summaries;
	}
	
	/** ***************************************************************************************************** **/	
	/** ******************* Parser methods    *************************************************************** **/
	
	/**
	 * Parse a {@link JSONObject} from summary {@link JSONArray}, then return the corresponding {@link SummaryListData} object.
	 * @param jsonObject : the summary list JSON object received from server 
	 * @return corresponding {@link SummaryListData}
	 */
	public static SummaryListData parse(JSONObject jsonObject) {
		if (jsonObject != null) {
				SummaryListData summary = new SummaryListData();
				
				summary.date			= jsonObject.optString("date");
				summary.caloriesIdle	= jsonObject.optString("caloriesIdle");
				summary.lastUpdate		= jsonObject.optString("lastUpdate");
				summary.summaries		= new ArrayList<SummaryData>();
				
				JSONArray summariesJsonArray= jsonObject.optJSONArray("summary");
				if (summariesJsonArray != null) {
					for (int i = 0; i < summariesJsonArray.length(); i++) {
						JSONObject summaryJsonObject = summariesJsonArray.optJSONObject(i);
						if (summaryJsonObject != null) {
							summary.summaries.add(SummaryData.parse(summaryJsonObject));
						}
					}
				}
				
				return summary;
		}
    	return null;
	}

}
