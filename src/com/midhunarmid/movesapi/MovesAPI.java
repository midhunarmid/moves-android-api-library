package com.midhunarmid.movesapi;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.midhunarmid.movesapi.activity.TrackPointsData;
import com.midhunarmid.movesapi.auth.AuthData;
import com.midhunarmid.movesapi.auth.MovesLoginFragment;
import com.midhunarmid.movesapi.profile.ProfileData;
import com.midhunarmid.movesapi.servercalls.HTTPCall;
import com.midhunarmid.movesapi.storyline.StorylineData;
import com.midhunarmid.movesapi.summary.SummaryListData;
import com.midhunarmid.movesapi.util.MovesAPIPreferences;
import com.midhunarmid.movesapi.util.MovesStatus;

/**
 * This class have static methods to deal with all API calls to Moves Server. Use <code>init()</code> method before 
 * using any other methods of this library.<br><br>
 * 
 * Initialization with <code>init()</code> method requires some details of Client Application which is registered with
 * Moves. You can get all those details while you register your client application.<br><br>
 * 
 * @author Midhu
 * @see MovesHandler
 * @see <a href="https://dev.moves-app.com/apps/new">How To Create New App</a>
 */
public class MovesAPI {
	
	private static final String TAG = "MovesAPI";
	
	/* Moves API */
	public static final String API_BASE = "https://api.moves-app.com/api/1.1";
	
	/* Moves Authentication API */
	public final static String	API_AUTH_BASE			= "https://api.moves-app.com/oauth/v1";
	public final static String	API_PATH_AUTHORIZE		= "/authorize";
	public final static String	API_PATH_ACCESSTOKEN	= "/access_token";
	
	/* Moves API path to get user profile data */
	public final static String 	API_PATH_PROFILE		= "/user/profile";
	
	/* Moves API path to get user summary */
	public final static String 	API_PATH_SUMMARY		= "/user/summary/daily";
	
	/* Moves API path to get user story line */
	public final static String 	API_PATH_STORYLINE		= "/user/storyline/daily";
	
	/* Moves API path to get daily activity breakdown */
	public final static String 	API_PATH_ACTIVITIES		= "/user/activities/daily";
	
	/* Moves API path to get daily activity breakdown */
	public final static String 	API_PATH_PLACES			= "/user/places/daily";
	
	/* Moves Client Application Details */
	private final String mClientID; 
	private final String mClientSecret;
	private final String mClientScopes;
	
	/* To which page application need to be redirected after successful login */
	private final String mRedirectURL;
	
	/* A singleton object for holding MovesAPI Client App Details */
	private static MovesAPI mClientDetails = null;
	
	/**
	 * Constructor method for {@link MovesAPI} objects 
	 * @param clientId : The Client ID you received when registering your application with Moves
	 * @param clientSecret : The Client Secret you received when registering your application with Moves
	 * @param clientScopes : Requested scopes (space-delimited). Should contain either activity, location or both scopes.
	 * @param redirectURL : The URI must match one of the callback URIs registered for your app
	 * @throws Exception will throw if the client details are null or empty
	 */
	private MovesAPI(String clientId, String clientSecret, String clientScopes, String redirectURL) throws Exception {
		if (clientId == null || clientSecret == null || clientScopes == null || redirectURL == null) {
			throw new Exception("Client details must not be null");
		} else if (clientId.length() == 0 || clientSecret.length() == 0 || clientScopes.length() == 0 || redirectURL.length() == 0) {
			throw new Exception("Client details should not be empty");
		}
		
		mClientID = clientId;
		mClientSecret = clientSecret;
		mClientScopes = clientScopes;
		mRedirectURL = redirectURL;
	}
	
	/**
	 * Call this method before using any other functions of this library. Call to this method will 
	 * initialize your {@link MovesAPI} client application details.
	 * @param context : A valid application {@link Context} is required to store data in {@link SharedPreferences}
	 * @param clientId : The Client ID you received when registering your application with Moves
	 * @param clientSecret : The Client Secret you received when registering your application with Moves
	 * @param clientScopes : Requested scopes (space-delimited). Should contain either activity, location or both scopes.
	 * @param redirectURL : The URI must match one of the callback URIs registered for your app
	 * @throws Exception will thrown if the client details are null or empty
	 */
	public static void init(Context context, String clientId, String clientSecret, String clientScopes, String redirectURL) throws Exception {
		if (mClientDetails != null) {
			Log.w(TAG, "API already initialized with client details");
			return;
		}
		mClientDetails = new MovesAPI(clientId, clientSecret, clientScopes, redirectURL);
		MovesAPIPreferences.setContext(context);
	}
	
	
	/**
	 * Use when you finished dealing with {@link MovesAPI} object. Call to this method will free up all related resources.
	 * @throws Exception will thrown if it is already destroyed or never initialized yet.
	 */
	public static void destroy() throws Exception {
		if (mClientDetails == null) {
			throw new Exception("Moves API is not yet initialized");
		} else {
			mClientDetails = null;
		}
	}
	
	/**
	 * Use this method to request authorization from Moves. Call to this method will launch the Moves App if it is
	 * already installed on the device, or else it will take user through a web page to complete authorization flow.
	 * @param handler : An implemented {@link MovesHandler} with {@link AuthData} type. This handler will get notified
	 * when the request completes.
	 * @param parent : Call to this method should be initiated from a {@link FragmentActivity}. We need to show some
	 * {@link DialogFragment} while processing authorization flow.
	 */
	public static void authenticate(MovesHandler<AuthData> handler, FragmentActivity parent) {
		FragmentTransaction ft = parent.getSupportFragmentManager().beginTransaction();
		Fragment prev = parent.getSupportFragmentManager().findFragmentByTag("auth-dialog");
		if (null != prev) {
			ft.remove(prev);
		}
		
		ft.addToBackStack(null);
		MovesLoginFragment loginFragment = MovesLoginFragment.newInstance(handler);
		loginFragment.show(ft, "auth-dialog");
	}
	
	/**
	 * Call this method to get currently authenticated user's {@link AuthData}
	 * @return {@link AuthData}
	 */
	public static AuthData getAuthData() {
		return AuthData.getAuthData();
	}
	
	/**
	 * Request {@link ProfileData} of authenticated user from Moves Server
	 * @param handler : An implemented {@link MovesHandler} with {@link ProfileData} type. This handler will get notified
	 * when the request completes.
	 */
	public static void getProfile(MovesHandler<ProfileData> handler) {
		if (AuthData.isAuthenticated()) {
			HTTPCall.getProfile(handler);
		} else {
			handler.onFailure(MovesStatus.NOT_AUTHENTICATED, "You are not yet authenticated with required scopes!");
		}
	}
	
	/**
	 * Get daily activity summaries for user, including step count, distance and duration for each activity (if applicable).
	 * Final summary for a particular date will only be available at earliest after midnight in the user’s current time zone.
	 * @see <a href="https://dev.moves-app.com/docs/api_summaries">Moves Developer Page for Daily Summaries</a>
	 * @param handler : An implemented {@link MovesHandler} with an {@link ArrayList} of {@link SummaryListData}. 
	 * This handler will get notified when the request completes.
	 * @param date : date in yyyyMMdd or yyyy-MM-dd format
	 * @param updatedSince : [optional] if set, return only days which data has been updated since 
	 * given time stamp in ISO 8601 (yyyyMMdd’T’HHmmssZ) format, pass <code>null</code> if not required.
	 */
	public static void getSummary_SingleDay(MovesHandler<ArrayList<SummaryListData>> handler, String date, String updatedSince) {
		if (AuthData.isAuthenticated()) {
			HTTPCall.getDailySummaryList(handler, "/" + date, null, null, null, updatedSince);
		} else {
			handler.onFailure(MovesStatus.NOT_AUTHENTICATED, "You are not yet authenticated with required scopes!");
		}
	}
	
	/**
	 * Get daily activity summaries for user, including step count, distance and duration for each activity (if applicable).
	 * Final summary for a particular date will only be available at earliest after midnight in the user’s current time zone.
	 * @see <a href="https://dev.moves-app.com/docs/api_summaries">Moves Developer Page for Daily Summaries</a>
	 * @param handler : An implemented {@link MovesHandler} with an {@link ArrayList} of {@link SummaryListData}. 
	 * This handler will get notified when the request completes.
	 * @param week : A specific week in yyyy-’W’ww format, for example 2013-W09
	 * @param updatedSince : [optional] if set, return only days which data has been updated since 
	 * given time stamp in ISO 8601 (yyyyMMdd’T’HHmmssZ) format, pass <code>null</code> if not required.
	 */
	public static void getSummary_SpecificWeek(MovesHandler<ArrayList<SummaryListData>> handler, String week, String updatedSince) {
		if (AuthData.isAuthenticated()) {
			HTTPCall.getDailySummaryList(handler, "/" + week, null, null, null, updatedSince);
		} else {
			handler.onFailure(MovesStatus.NOT_AUTHENTICATED, "You are not yet authenticated with required scopes!");
		}
	}
	
	/**
	 * Get daily activity summaries for user, including step count, distance and duration for each activity (if applicable).
	 * Final summary for a particular date will only be available at earliest after midnight in the user’s current time zone.
	 * @see <a href="https://dev.moves-app.com/docs/api_summaries">Moves Developer Page for Daily Summaries</a>
	 * @param handler : An implemented {@link MovesHandler} with an {@link ArrayList} of {@link SummaryListData}. 
	 * This handler will get notified when the request completes.
	 * @param month : A specific month in yyyyMM or yyyy-MM format
	 * @param updatedSince : [optional] if set, return only days which data has been updated since 
	 * given time stamp in ISO 8601 (yyyyMMdd’T’HHmmssZ) format, pass <code>null</code> if not required.
	 */
	public static void getSummary_SpecificMonth(MovesHandler<ArrayList<SummaryListData>> handler, String month, String updatedSince) {
		if (AuthData.isAuthenticated()) {
			HTTPCall.getDailySummaryList(handler, "/" + month, null, null, null, updatedSince);
		} else {
			handler.onFailure(MovesStatus.NOT_AUTHENTICATED, "You are not yet authenticated with required scopes!");
		}
	}
	
	/**
	 * Get daily activity summaries for user, including step count, distance and duration for each activity (if applicable).
	 * Final summary for a particular date will only be available at earliest after midnight in the user’s current time zone.
	 * @see <a href="https://dev.moves-app.com/docs/api_summaries">Moves Developer Page for Daily Summaries</a>
	 * @param handler : An implemented {@link MovesHandler} with an {@link ArrayList} of {@link SummaryListData}. 
	 * This handler will get notified when the request completes.
	 * @param from :  Range start in yyyyMMdd or yyyy-MM-dd format
	 * @param to : Range end in yyyyMMdd or yyyy-MM-dd format
	 * @param updatedSince : [optional] if set, return only days which data has been updated since 
	 * given time stamp in ISO 8601 (yyyyMMdd’T’HHmmssZ) format, pass <code>null</code> if not required.
	 */
	public static void getSummary_WithinRange(MovesHandler<ArrayList<SummaryListData>> handler, String from, String to, String updatedSince) {
		if (AuthData.isAuthenticated()) {
			HTTPCall.getDailySummaryList(handler, null, from, to, null, updatedSince);
		} else {
			handler.onFailure(MovesStatus.NOT_AUTHENTICATED, "You are not yet authenticated with required scopes!");
		}
	}
	
	/**
	 * Get daily activity summaries for user, including step count, distance and duration for each activity (if applicable).
	 * Final summary for a particular date will only be available at earliest after midnight in the user’s current time zone.
	 * @see <a href="https://dev.moves-app.com/docs/api_summaries">Moves Developer Page for Daily Summaries</a>
	 * @param handler : An implemented {@link MovesHandler} with an {@link ArrayList} of {@link SummaryListData}. 
	 * This handler will get notified when the request completes.
	 * @param pastDays :  How many past days to return, including today (in users current time zone)
	 * @param updatedSince : [optional] if set, return only days which data has been updated since 
	 * given time stamp in ISO 8601 (yyyyMMdd’T’HHmmssZ) format, pass <code>null</code> if not required.
	 */
	public static void getSummary_PastDays(MovesHandler<ArrayList<SummaryListData>> handler, String pastDays, String updatedSince) {
		if (AuthData.isAuthenticated()) {
			HTTPCall.getDailySummaryList(handler, null, null, null, pastDays, updatedSince);
		} else {
			handler.onFailure(MovesStatus.NOT_AUTHENTICATED, "You are not yet authenticated with required scopes!");
		}
	}
	
	/**
	 * Get daily storylines for user.
	 * @see <a href="https://dev.moves-app.com/docs/api_storyline">Moves Developer Page for Daily Storyline</a>
	 * @see SummaryListData
	 * @param handler : An implemented {@link MovesHandler} with an {@link ArrayList} of {@link StorylineData}. 
	 * This handler will get notified when the request completes.
	 * @param date : date in yyyyMMdd or yyyy-MM-dd format
	 * @param updatedSince : [optional] if set, return only days which data has been updated since 
	 * given time stamp in ISO 8601 (yyyyMMdd’T’HHmmssZ) format, pass <code>null</code> if not required.
	 * @param needTrackPoints : if true, the returned activities also include {@link TrackPointsData} information. 
	 * Including track points limits the query range to 7 days.
	 */
	public static void getStoryline_SingleDay(MovesHandler<ArrayList<StorylineData>> handler, String date, String updatedSince, boolean needTrackPoints) {
		if (AuthData.isAuthenticated()) {
			HTTPCall.getDailyStorylineList(handler, "/" + date, null, null, null, updatedSince, needTrackPoints);
		} else {
			handler.onFailure(MovesStatus.NOT_AUTHENTICATED, "You are not yet authenticated with required scopes!");
		}
	}
	
	/**
	 * Get daily storylines for user.
	 * @see <a href="https://dev.moves-app.com/docs/api_storyline">Moves Developer Page for Daily Storyline</a>
	 * @see SummaryListData
	 * @param handler : An implemented {@link MovesHandler} with an {@link ArrayList} of {@link StorylineData}. 
	 * This handler will get notified when the request completes.
	 * @param week : A specific week in yyyy-’W’ww format, for example 2013-W09
	 * @param updatedSince : [optional] if set, return only days which data has been updated since 
	 * given time stamp in ISO 8601 (yyyyMMdd’T’HHmmssZ) format, pass <code>null</code> if not required.
	 * @param needTrackPoints : if true, the returned activities also include {@link TrackPointsData} information. 
	 * Including track points limits the query range to 7 days.
	 */
	public static void getStoryline_SpecificWeek(MovesHandler<ArrayList<StorylineData>> handler, String week, String updatedSince, boolean needTrackPoints) {
		if (AuthData.isAuthenticated()) {
			HTTPCall.getDailyStorylineList(handler, "/" + week, null, null, null, updatedSince, needTrackPoints);
		} else {
			handler.onFailure(MovesStatus.NOT_AUTHENTICATED, "You are not yet authenticated with required scopes!");
		}
	}
	
	/**
	 * Get daily storylines for user.
	 * @see <a href="https://dev.moves-app.com/docs/api_storyline">Moves Developer Page for Daily Storyline</a>
	 * @see SummaryListData
	 * @param handler : An implemented {@link MovesHandler} with an {@link ArrayList} of {@link StorylineData}. 
	 * This handler will get notified when the request completes.
	 * @param month : A specific month in yyyyMM or yyyy-MM format
	 * @param updatedSince : [optional] if set, return only days which data has been updated since 
	 * given time stamp in ISO 8601 (yyyyMMdd’T’HHmmssZ) format, pass <code>null</code> if not required.
	 * @param needTrackPoints : if true, the returned activities also include {@link TrackPointsData} information. 
	 * Including track points limits the query range to 7 days.
	 */
	public static void getStoryline_SpecificMonth(MovesHandler<ArrayList<StorylineData>> handler, String month, String updatedSince, boolean needTrackPoints) {
		if (AuthData.isAuthenticated()) {
			HTTPCall.getDailyStorylineList(handler, "/" + month, null, null, null, updatedSince, needTrackPoints);
		} else {
			handler.onFailure(MovesStatus.NOT_AUTHENTICATED, "You are not yet authenticated with required scopes!");
		}
	}
	
	/**
	 * Get daily storylines for user.
	 * @see <a href="https://dev.moves-app.com/docs/api_storyline">Moves Developer Page for Daily Storyline</a>
	 * @see SummaryListData
	 * @param handler : An implemented {@link MovesHandler} with an {@link ArrayList} of {@link StorylineData}. 
	 * This handler will get notified when the request completes.
	 * @param from :  Range start in yyyyMMdd or yyyy-MM-dd format
	 * @param to : Range end in yyyyMMdd or yyyy-MM-dd format
	 * @param updatedSince : [optional] if set, return only days which data has been updated since 
	 * given time stamp in ISO 8601 (yyyyMMdd’T’HHmmssZ) format, pass <code>null</code> if not required.
	 * @param needTrackPoints : if true, the returned activities also include {@link TrackPointsData} information. 
	 * Including track points limits the query range to 7 days.
	 */
	public static void getStoryline_WithinRange(MovesHandler<ArrayList<StorylineData>> handler, String from, String to, String updatedSince, boolean needTrackPoints) {
		if (AuthData.isAuthenticated()) {
			HTTPCall.getDailyStorylineList(handler, null, from, to, null, updatedSince, needTrackPoints);
		} else {
			handler.onFailure(MovesStatus.NOT_AUTHENTICATED, "You are not yet authenticated with required scopes!");
		}
	}
	
	/**
	 * Get daily storylines for user.
	 * @see <a href="https://dev.moves-app.com/docs/api_storyline">Moves Developer Page for Daily Storyline</a>
	 * @see SummaryListData
	 * @param handler : An implemented {@link MovesHandler} with an {@link ArrayList} of {@link StorylineData}. 
	 * This handler will get notified when the request completes.
	 * @param pastDays :  How many past days to return, including today (in users current time zone)
	 * @param updatedSince : [optional] if set, return only days which data has been updated since 
	 * given time stamp in ISO 8601 (yyyyMMdd’T’HHmmssZ) format, pass <code>null</code> if not required.
	 * @param needTrackPoints : if true, the returned activities also include {@link TrackPointsData} information. 
	 * Including track points limits the query range to 7 days.
	 */
	public static void getStoryline_PastDays(MovesHandler<ArrayList<StorylineData>> handler, String pastDays, String updatedSince, boolean needTrackPoints) {
		if (AuthData.isAuthenticated()) {
			HTTPCall.getDailyStorylineList(handler, null, null, null, pastDays, updatedSince, needTrackPoints);
		} else {
			handler.onFailure(MovesStatus.NOT_AUTHENTICATED, "You are not yet authenticated with required scopes!");
		}
	}
	
	/**
	 * Get daily activity breakdown for user. <br><br><i>Location/Trackpoints will not be there (or it will be null) 
	 * in Activity response</i>
	 * @see <a href="https://dev.moves-app.com/docs/api_activities">Moves Developer Page for Daily Activities</a>
	 * @see SummaryListData
	 * @see StorylineData
	 * @param handler : An implemented {@link MovesHandler} with an {@link ArrayList} of {@link StorylineData}. 
	 * This handler will get notified when the request completes.
	 * @param date : date in yyyyMMdd or yyyy-MM-dd format
	 * @param updatedSince : [optional] if set, return only days which data has been updated since 
	 * given time stamp in ISO 8601 (yyyyMMdd’T’HHmmssZ) format, pass <code>null</code> if not required.
	 */
	public static void getActivities_SingleDay(MovesHandler<ArrayList<StorylineData>> handler, String date, String updatedSince) {
		if (AuthData.isAuthenticated()) {
			HTTPCall.getDailyActivitiesList(handler, "/" + date, null, null, null, updatedSince);
		} else {
			handler.onFailure(MovesStatus.NOT_AUTHENTICATED, "You are not yet authenticated with required scopes!");
		}
	}
	
	/**
	 * Get daily activity breakdown for user. <br><br><i>Location/Trackpoints will not be there (or it will be null) 
	 * in Activity response</i>
	 * @see <a href="https://dev.moves-app.com/docs/api_activities">Moves Developer Page for Daily Activities</a>
	 * @see SummaryListData
	 * @see StorylineData
	 * @param handler : An implemented {@link MovesHandler} with an {@link ArrayList} of {@link StorylineData}. 
	 * This handler will get notified when the request completes.
	 * @param week : A specific week in yyyy-’W’ww format, for example 2013-W09
	 * @param updatedSince : [optional] if set, return only days which data has been updated since 
	 * given time stamp in ISO 8601 (yyyyMMdd’T’HHmmssZ) format, pass <code>null</code> if not required.
	 */
	public static void getActivities_SpecificWeek(MovesHandler<ArrayList<StorylineData>> handler, String week, String updatedSince) {
		if (AuthData.isAuthenticated()) {
			HTTPCall.getDailyActivitiesList(handler, "/" + week, null, null, null, updatedSince);
		} else {
			handler.onFailure(MovesStatus.NOT_AUTHENTICATED, "You are not yet authenticated with required scopes!");
		}
	}
	
	/**
	 * Get daily activity breakdown for user. <br><br><i>Location/Trackpoints will not be there (or it will be null) 
	 * in Activity response</i>
	 * @see <a href="https://dev.moves-app.com/docs/api_activities">Moves Developer Page for Daily Activities</a>
	 * @see SummaryListData
	 * @see StorylineData
	 * @param handler : An implemented {@link MovesHandler} with an {@link ArrayList} of {@link StorylineData}. 
	 * This handler will get notified when the request completes.
	 * @param month : A specific month in yyyyMM or yyyy-MM format
	 * @param updatedSince : [optional] if set, return only days which data has been updated since 
	 * given time stamp in ISO 8601 (yyyyMMdd’T’HHmmssZ) format, pass <code>null</code> if not required.
	 */
	public static void getActivities_SpecificMonth(MovesHandler<ArrayList<StorylineData>> handler, String month, String updatedSince) {
		if (AuthData.isAuthenticated()) {
			HTTPCall.getDailyActivitiesList(handler, "/" + month, null, null, null, updatedSince);
		} else {
			handler.onFailure(MovesStatus.NOT_AUTHENTICATED, "You are not yet authenticated with required scopes!");
		}
	}
	
	/**
	 * Get daily activity breakdown for user. <br><br><i>Location/Trackpoints will not be there (or it will be null) 
	 * in Activity response</i>
	 * @see <a href="https://dev.moves-app.com/docs/api_activities">Moves Developer Page for Daily Activities</a>
	 * @see SummaryListData
	 * @see StorylineData
	 * @param handler : An implemented {@link MovesHandler} with an {@link ArrayList} of {@link StorylineData}. 
	 * This handler will get notified when the request completes.
	 * @param from :  Range start in yyyyMMdd or yyyy-MM-dd format
	 * @param to : Range end in yyyyMMdd or yyyy-MM-dd format
	 * @param updatedSince : [optional] if set, return only days which data has been updated since 
	 * given time stamp in ISO 8601 (yyyyMMdd’T’HHmmssZ) format, pass <code>null</code> if not required.
	 */
	public static void getActivities_WithinRange(MovesHandler<ArrayList<StorylineData>> handler, String from, String to, String updatedSince) {
		if (AuthData.isAuthenticated()) {
			HTTPCall.getDailyActivitiesList(handler, null, from, to, null, updatedSince);
		} else {
			handler.onFailure(MovesStatus.NOT_AUTHENTICATED, "You are not yet authenticated with required scopes!");
		}
	}
	
	/**
	 * Get daily activity breakdown for user. <br><br><i>Location/Trackpoints will not be there (or it will be null) 
	 * in Activity response</i>
	 * @see <a href="https://dev.moves-app.com/docs/api_activities">Moves Developer Page for Daily Activities</a>
	 * @see SummaryListData
	 * @see StorylineData
	 * @param handler : An implemented {@link MovesHandler} with an {@link ArrayList} of {@link StorylineData}. 
	 * This handler will get notified when the request completes.
	 * @param pastDays :  How many past days to return, including today (in users current time zone)
	 * @param updatedSince : [optional] if set, return only days which data has been updated since 
	 * given time stamp in ISO 8601 (yyyyMMdd’T’HHmmssZ) format, pass <code>null</code> if not required.
	 */
	public static void getActivities_PastDays(MovesHandler<ArrayList<StorylineData>> handler, String pastDays, String updatedSince) {
		if (AuthData.isAuthenticated()) {
			HTTPCall.getDailyActivitiesList(handler, null, null, null, pastDays, updatedSince);
		} else {
			handler.onFailure(MovesStatus.NOT_AUTHENTICATED, "You are not yet authenticated with required scopes!");
		}
	}
	
	/** ***************************************************************************************************** **/
	/** ******************* Moves API Getters *************************************************************** **/
	
	/** Gets the singleton {@link MovesAPI} object **/
	public static MovesAPI getClientDetails() {
		return mClientDetails;
	}
	
	/** Gets the client ID which set on init() **/
	public String getClientID() {
		return mClientID;
	}

	/** Gets the client Secret which set on init() **/
	public String getClientSecret() {
		return mClientSecret;
	}

	/** Gets the client Scopes which set on init() **/
	public String getClientScopes() {
		return mClientScopes;
	}

	/** Gets the client Redirect URL which set on init() **/
	public String getRedirectURL() {
		return mRedirectURL;
	}
}
