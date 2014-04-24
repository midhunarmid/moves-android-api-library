package com.midhunarmid.movesapi.summary;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * This class holds the Summary Data of a Moves User, and some related methods to handle those data
 * @author Midhu
 * @see <a href="https://dev.moves-app.com/docs/api_summaries">Moves Developer Page for Daily Summaries</a>
 * @see SummaryListData
 */
public class SummaryData {
	private String		activity;
	private String		group;
	private String		duration;
	private String 		distance;
	private String 		steps;
	private String 		calories;
	
	/** ***************************************************************************************************** **/	
	/** ******************* Utility methods   *************************************************************** **/
	
	/** Duration of the activity in seconds <br><br> 
	 *  returns <b><i>def</b></i> if an error occurred **/
	public long geDuration_Long(long def) {
		try {
			return Long.parseLong(duration);
		} catch (Exception e) {
			return def;
		}
	}
	
	/** Distance for the activity in meters <br><br> 
	 *  returns <b><i>def</b></i> if an error occurred **/
	public long geDistance_Long(long def) {
		try {
			return Long.parseLong(distance);
		} catch (Exception e) {
			return def;
		}
	}
	
	/** Step count for the activity <br><br> 
	 *  returns <b><i>def</b></i> if an error occurred **/
	public long geSteps_Long(long def) {
		try {
			return Long.parseLong(steps);
		} catch (Exception e) {
			return def;
		}
	}
	
	/** Calories burn for the activity (if applicable) in kcal <br><br> 
	 *  returns <b><i>def</b></i> if an error occurred **/
	public long geCalories_Long(long def) {
		try {
			return Long.parseLong(calories);
		} catch (Exception e) {
			return def;
		}
	}
	
	/** ***************************************************************************************************** **/	
	/** ******************* Getter methods    *************************************************************** **/

	/** 
	 * Specific name of the activity. See <a href="https://dev.moves-app.com/docs/api_activity_list#activity_table">
	 * Activity list API</a> for list of currently supported activities
	 */
	public String getActivity() {
		return activity;
	}

	/**
	 * A more generic activity super-group to which the action belongs to. If defined, one of:<code>
	 * <li>walking</li>
	 * <li>running</li>
	 * <li>cycling</li>
	 * <li>transport</li></code><br><br>
	 * Note that there can be additional groups in the future.<br><br>
	 */
	public String getGroup() {
		return group;
	}

	/** Duration of the activity in seconds **/
	public String getDuration() {
		return duration;
	}

	/** Distance for the activity in meters **/
	public String getDistance() {
		return distance;
	}

	/** Step count for the activity **/
	public String getSteps() {
		return steps;
	}

	/** Calories burn for the activity in kcal **/
	public String getCalories() {
		return calories;
	}

	/** ***************************************************************************************************** **/	
	/** ******************* Setter methods    *************************************************************** **/
	
	/** 
	 * Specific name of the activity. See <a href="https://dev.moves-app.com/docs/api_activity_list#activity_table">
	 * Activity list API</a> for list of currently supported activities
	 */
	public void setActivity(String activity) {
		this.activity = activity;
	}

	/**
	 * A more generic activity super-group to which the action belongs to. If defined, one of:<code>
	 * <li>walking</li>
	 * <li>running</li>
	 * <li>cycling</li>
	 * <li>transport</li></code><br><br>
	 * Note that there can be additional groups in the future.<br><br>
	 */
	public void setGroup(String group) {
		this.group = group;
	}

	/** Duration of the activity in seconds **/
	public void setDuration(String duration) {
		this.duration = duration;
	}

	/** Distance for the activity in meters **/
	public void setDistance(String distance) {
		this.distance = distance;
	}

	/** Step count for the activity **/
	public void setSteps(String steps) {
		this.steps = steps;
	}

	/** Calories burn for the activity in kcal **/
	public void setCalories(String calories) {
		this.calories = calories;
	}

	/** ***************************************************************************************************** **/	
	/** ******************* Parser methods    *************************************************************** **/
	
	/**
	 * Parse a {@link JSONObject} from summary {@link JSONArray}, then return the corresponding {@link SummaryData} object.
	 * @param jsonObject : the 'summary' JSON object to parse 
	 * @return corresponding {@link SummaryData}
	 */
	public static SummaryData parse(JSONObject jsonObject) {
		if (jsonObject != null) {
				SummaryData summary = new SummaryData();
				summary.activity		= jsonObject.optString("activity");
				summary.group			= jsonObject.optString("group");
				summary.duration		= jsonObject.optString("duration");
				summary.distance		= jsonObject.optString("distance");
				summary.steps			= jsonObject.optString("steps");
				summary.calories		= jsonObject.optString("calories");
				return summary;
		}
    	return null;
	}
}
