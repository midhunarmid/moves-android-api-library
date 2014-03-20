package com.midhunarmid.movesapi.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * This is a utility class for Moves API Application, to deal with its shared preference values. 
 * @author Midhu
 */
public class MovesAPIPreferences {
	
	/* Member Variables */
	private static SharedPreferences 	prefSetting 			= null;
	private static Context 				prefContext 			= null;
	public static final String 			PREFERENCE_NAME 		= "MovesAPIPref";

	/** Keys Used In This Preference **/
	public static final String 			MOVES_REFRESH			= "moves_refresh_tocken";
	public static final String 			MOVES_EXPIRE 			= "moves_access_expires";
	public static final String 			MOVES_ACCESS			= "moves_access_tocken";
	public static final String 			MOVES_USERID 			= "moves_user_id";
	public static final String 			MOVES_AUTHSTATUS		= "moves_is_authenticated";
	
	public static final String 			STATUS_YES				= "moves_status_yes";
	public static final String 			STATUS_NO				= "moves_status_no";
	
	/** 
	 * This method must be called before using any other functions of {@link MovesAPIPreferences} 
	 * @param context : Pass the calling context or Base Context or Application Context
	 **/
	public static void setContext(Context context) {				
		prefContext = context;
		prefSetting = prefContext.getSharedPreferences(PREFERENCE_NAME, 0);
	}
	
	/**
	 * Use this method to GET a value for the supplied key
	 */
	public static String getPreference(String key) throws Exception {
		if (prefContext == null) {
			throw new Exception("context not initialised yet");
		}
		return prefSetting.getString(key, "");
	}

	/**
	 * Use this method to SET a value for the supplied key
	 */
	public static void setPreference(String key, String value) throws Exception {	
		if (prefContext == null) {
			throw new Exception("context not initialised yet");
		}
		Editor editor = prefSetting.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	/**
	 * Use this method to clear all preference data
	 */
	public static void clearPreference() throws Exception {
		if (prefContext == null) {
			throw new Exception("context not initialised yet");
		}
		Editor editor = prefSetting.edit();
		editor.clear();
		editor.commit();
	}
}