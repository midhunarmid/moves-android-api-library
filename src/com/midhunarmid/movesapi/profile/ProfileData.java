package com.midhunarmid.movesapi.profile;

import org.json.JSONObject;

import com.midhunarmid.movesapi.util.Utilities;

/**
 * This class holds the Profile Data of a Moves User, and some related methods to handle those data
 * @author Midhu
 * @see <a href="https://dev.moves-app.com/docs/api_profile">Moves Developer Page for Profile</a>
 */
public class ProfileData {
	private String userID;
	private String firstDate;
	private String timeZoneId;
	private String timeZoneOffset;
	private String language;
	private String locale;
	private String firstWeekDay;
	private String metric;
	private String caloriesAvailable;
	private String platform;
	
	/** ***************************************************************************************************** **/	
	/** ******************* Utility methods   *************************************************************** **/
	
	/** Get a unique identifier of the user <br><br> 
	 *  returns <b><i>def</b></i> if an error occurred **/
	public long getUserID_Long(long def) {
		try {
			return Long.parseLong(userID);
		} catch (Exception e) {
			return def;
		}
	}
	
	/** Get first date(as a millisecond value) from which there might be data for the user. <br>
	 *  The value is the number of milliseconds since Jan. 1, 1970, midnight GMT.  <br><br> 
	 *  returns <b><i>def</b></i> if an error occurred **/
	public long getFirstDate_Millis(long def) {
		return Utilities.getTimeInMillis(firstDate, "yyyyMMdd", def);
	}
	
	/** Get the user's time zone offset to UTC in seconds <br><br> 
	 *  returns <b><i>def</b></i> if an error occurred **/
	public long getTimeZoneOffset_Long(long def) {
		try {
			return Long.parseLong(timeZoneOffset);
		} catch (Exception e) {
			return def;
		}
	}
	
	/** Get users first day of the week, 1=Sunday, 2=Monday <br><br> 
	 *  returns <b><i>def</b></i> if an error occurred **/
	public int getFirstWeekDay_Int(int def) {
		try {
			return Integer.parseInt(firstWeekDay);
		} catch (Exception e) {
			return def;
		}
	}
	
	/** Get whether user prefers metric units <br><br> 
	 *  returns <b><i>def</b></i> if an error occurred **/
	public boolean isMetric(boolean def) {
		try {
			return metric.contains("true") ? true : false;
		} catch (Exception e) {
			return def;
		}
	}
	
	/** Get whether calories are available for user <br><br> 
	 *  returns <b><i>def</b></i> if an error occurred **/
	public boolean isCaloriesAvailable(boolean def) {
		try {
			return caloriesAvailable.contains("true") ? true : false;
		} catch (Exception e) {
			return def;
		}
	}
	
	/** ***************************************************************************************************** **/	
	/** ******************* Getter methods    *************************************************************** **/
	
	/** a unique identifier (64 bit unsigned) of the user **/
	public String getUserID() {
		return userID;
	}
	
	/** first date from which there might be data for the user, in yyyyMMdd format **/
	public String getFirstDate() {
		return firstDate;
	}
	
	/**  time zone identifier for users current time zone **/
	public String getTimeZoneId() {
		return timeZoneId;
	}
	
	/** offset to UTC in seconds **/
	public String getTimeZoneOffset() {
		return timeZoneOffset;
	}
	
	/** users language as BCP 47 language code **/
	public String getLanguage() {
		return language;
	}
	
	/** users locale ID **/
	public String getLocale() {
		return locale;
	}
	
	/**  users first day of the week, 1=Sunday, 2=Monday **/
	public String getFirstWeekDay() {
		return firstWeekDay;
	}
	
	/** whether user prefers metric units **/
	public String getMetric() {
		return metric;
	}
	
	/** whether calories are available for user **/
	public String getCaloriesAvailable() {
		return caloriesAvailable;
	}
	
	/** users current platform, one of “ios” or “android” **/
	public String getPlatform() {
		return platform;
	}
	
	/** ***************************************************************************************************** **/	
	/** ******************* Setter methods    *************************************************************** **/
	
	/** String representation of unique identifier (64 bit unsigned) of the user **/
	public void setUserID(String userID) {
		this.userID = userID;
	}
	
	/** first date from which there might be data for the user, in yyyyMMdd format **/
	public void setFirstDate(String firstDate) {
		this.firstDate = firstDate;
	}
	
	/**  time zone identifier for users current time zone **/
	public void setTimeZoneId(String timeZoneId) {
		this.timeZoneId = timeZoneId;
	}
	
	/** offset to UTC in seconds **/
	public void setTimeZoneOffset(String timeZoneOffset) {
		this.timeZoneOffset = timeZoneOffset;
	}
	
	/** users language as BCP 47 language code **/
	public void setLanguage(String language) {
		this.language = language;
	}
	
	/** users locale ID **/
	public void setLocale(String locale) {
		this.locale = locale;
	}
	
	/**  users first day of the week, 1=Sunday, 2=Monday **/
	public void setFirstWeekDay(String firstWeekDay) {
		this.firstWeekDay = firstWeekDay;
	}
	
	/** whether user prefers metric units, <code>"true"</code> or <code>"false"</code> **/
	public void setMetric(String metric) {
		this.metric = metric;
	}
	
	/** whether calories are available for user, <code>"true"</code> or <code>"false"</code> **/
	public void setCaloriesAvailable(String caloriesAvailable) {
		this.caloriesAvailable = caloriesAvailable;
	}
	
	/** users current platform, one of “ios” or “android” **/
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	
	/** ***************************************************************************************************** **/	
	/** ******************* Parser methods    *************************************************************** **/
	
	/**
	 * Parse a {@link JSONObject} of Profile API response, then return the corresponding {@link ProfileData} object.
	 * @param jsonObject : the 'profile' JSON object to parse 
	 * @return corresponding {@link ProfileData}
	 */
	public static ProfileData parse(JSONObject jsonObject) throws Exception {
		if (jsonObject != null) {
			ProfileData profileData = new ProfileData();
			
			JSONObject profile				= jsonObject.optJSONObject("profile");
			
			profileData.userID				= jsonObject.optString("userId");
			if (profile != null) {
				profileData.firstDate			= profile.optString("firstDate");
				profileData.caloriesAvailable	= profile.optString("caloriesAvailable");
				profileData.platform			= profile.optString("platform");
				JSONObject currentTimeZone		= profile.optJSONObject("currentTimeZone");
				JSONObject localization			= profile.optJSONObject("localization");
				
				if (currentTimeZone != null) {
					profileData.timeZoneId		= currentTimeZone.optString("id");
					profileData.timeZoneOffset	= currentTimeZone.optString("offset");
				}
				
				if (localization != null) {
					profileData.language		= localization.optString("language");
					profileData.locale			= localization.optString("locale");
					profileData.firstWeekDay	= localization.optString("firstWeekDay");
					profileData.metric			= localization.optString("metric");
				}
			}
			
			return profileData;
		}
    	return null;
	}
}
