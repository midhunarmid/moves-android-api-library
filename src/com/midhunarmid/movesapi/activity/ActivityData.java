package com.midhunarmid.movesapi.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * This class holds the Activity Data, and some related methods to handle those data
 * @author Midhu
 * @see <a href="https://dev.moves-app.com/docs/api_storyline">Read <i>Activity</i> Section from 
 * Moves Developer Page for Storyline</a>
 */
public class ActivityData {
	private String activity;
	private String group;
	private String manual;
	private String startTime;
	private String endTime;
	private String duration;
	private String distance;
	private String steps;
	private String calories;
	private ArrayList<TrackPointsData> trackPoints;
	
	
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
	
	/** Whether the activity was manually added by user **/
	public String getManual() {
		return manual;
	}
	
	/** Start time of the activity if known in ISO 8601 (yyyyMMdd’T’HHmmssZ) format **/
	public String getStartTime() {
		return startTime;
	}
	
	/** End time of the activity if known in ISO 8601 (yyyyMMdd’T’HHmmssZ) format **/
	public String getEndTime() {
		return endTime;
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
	
	/** {@link TrackPointsData} for this activity **/
	public ArrayList<TrackPointsData> getTrackPoints() {
		return trackPoints;
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
	
	/** Whether the activity was manually added by user **/
	public void setManual(String manual) {
		this.manual = manual;
	}
	
	/** Start time of the activity if known in ISO 8601 (yyyyMMdd’T’HHmmssZ) format **/
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	
	/** End time of the activity if known in ISO 8601 (yyyyMMdd’T’HHmmssZ) format **/
	public void setEndTime(String endTime) {
		this.endTime = endTime;
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
	
	/** {@link TrackPointsData} for this activity **/
	public void setTrackPoints(ArrayList<TrackPointsData> trackPoints) {
		this.trackPoints = trackPoints;
	}
	
	/** ***************************************************************************************************** **/	
	/** ******************* Parser methods    *************************************************************** **/
	
	/**
	 * Parse a {@link JSONObject} from activities {@link JSONArray}, then return the corresponding 
	 * {@link ActivityData} object.
	 * @param jsonObject : the 'activity' JSON object to parse 
	 * @return corresponding {@link ActivityData}
	 */
	public static ActivityData parse(JSONObject jsonObject) {
		if (jsonObject != null) {
			ActivityData activityData = new ActivityData();
				activityData.activity		= jsonObject.optString("activity");
				activityData.group			= jsonObject.optString("group");
				activityData.manual			= jsonObject.optString("manual");
				activityData.startTime		= jsonObject.optString("startTime");
				activityData.endTime		= jsonObject.optString("endTime");
				activityData.duration		= jsonObject.optString("duration");
				activityData.distance		= jsonObject.optString("distance");
				activityData.steps			= jsonObject.optString("steps");
				activityData.calories		= jsonObject.optString("calories");
				JSONArray trackPointsJSONArray		= jsonObject.optJSONArray("trackPoints");
				
				activityData.trackPoints = new ArrayList<TrackPointsData>();
				if (trackPointsJSONArray != null) {
					for (int i = 0; i < trackPointsJSONArray.length(); i++) {
						JSONObject summaryJsonObject = trackPointsJSONArray.optJSONObject(i);
						if (summaryJsonObject != null) {
							activityData.trackPoints.add(TrackPointsData.parse(summaryJsonObject));
						}
					}
				}
				
				return activityData;
		}
    	return null;
	}
}
