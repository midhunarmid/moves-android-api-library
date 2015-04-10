package com.midhunarmid.movesapi.servercalls;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;

import com.midhunarmid.movesapi.MovesAPI;
import com.midhunarmid.movesapi.MovesHandler;
import com.midhunarmid.movesapi.auth.AuthData;
import com.midhunarmid.movesapi.profile.ProfileData;
import com.midhunarmid.movesapi.storyline.StorylineData;
import com.midhunarmid.movesapi.summary.SummaryListData;
import com.midhunarmid.movesapi.util.MovesAPIPreferences;
import com.midhunarmid.movesapi.util.MovesStatus;
import com.midhunarmid.movesapi.util.Utilities;

/**
 * All HTTP requests are organized under this class
 * @author Midhu
 * @see MovesHandler
 * @see MovesStatus
 */
public class HTTPCall {
	
	private static final boolean isDebugging = true;
	private static final String TAG = "HTTPCall";
	
	/**
	 * Call this method to refresh the access token. This method will return the new access token and also sets all those
	 * new access details in {@link MovesAPIPreferences} 
	 * @return New access token
	 * @throws Exception
	 */
	public static String refreshAccessToken() throws Exception {
		String refreshToken = MovesAPIPreferences.getPreference(MovesAPIPreferences.MOVES_REFRESH);
		
		/* Use the refresh token to get new access token with extended expiry time */
		HashMap<String, String> nameValuePairs = new HashMap<String, String>();
		nameValuePairs.put("grant_type", "refresh_token");
		nameValuePairs.put("refresh_token", refreshToken);
		nameValuePairs.put("client_id", MovesAPI.getClientDetails().getClientID());
		nameValuePairs.put("client_secret", MovesAPI.getClientDetails().getClientSecret());
		
		URL url 	= new URL(MovesAPI.API_AUTH_BASE + MovesAPI.API_PATH_ACCESSTOKEN + "?" + Utilities.encodeUrl(nameValuePairs));
		
		if (isDebugging) {
			Log.i(TAG, "API Endpoint : " + url.toString());
		}
		
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setRequestMethod("POST");
		urlConnection.setDoInput(true);
		urlConnection.connect();
		
		if (urlConnection.getResponseCode() != 200) {
			/* Some unexpected error happened */
			throw new Exception("HTTP Response not success. Bad Request.");
		}
		
		String response		= Utilities.readStream(urlConnection.getInputStream());
		
		if (isDebugging) {
			Log.i(TAG, "refreshAccessToken() : " + response);
		}
		
		JSONObject jsonObj 	= (JSONObject) new JSONTokener(response).nextValue();
		String access_token	= jsonObj.optString("access_token");
		String user_id		= jsonObj.optString("user_id");
		String expires_in	= jsonObj.optString("expires_in");
		String refresh_token= jsonObj.optString("refresh_token");
		
		
		AuthData.setAccessExpiryInPreference(access_token, user_id, expires_in, refresh_token);
		return access_token;
	}
	
	/**
	 * Use this method to fetch the Profile information of a user from Moves Server
	 * @param handler : A {@link MovesHandler} implementation which will get notified with success/failure
	 * @see <a href="https://dev.moves-app.com/docs/api_profile">Moves Developer Page for Profile</a>
	 */
	public static void getProfile(final MovesHandler<ProfileData> handler) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					/* Refresh access token if only AuthData.MOVES_REFRESHBEFORE days are there to expire current token */
					AuthData.refreshAccessTokenIfNeeded();
					
					/* Exchange the authorization code we obtained after login to get access token */
					HashMap<String, String> nameValuePairs = new HashMap<String, String>();
					nameValuePairs.put("access_token", AuthData.getAuthData().getAccessToken());
					
					URL url 	= new URL(MovesAPI.API_BASE + MovesAPI.API_PATH_PROFILE + "?" + Utilities.encodeUrl(nameValuePairs));
					
					if (isDebugging) {
						Log.i(TAG, "API Endpoint : " + url.toString());
					}
					
					HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
					urlConnection.setRequestMethod("GET");
					urlConnection.setDoInput(true);
					urlConnection.connect();
					
					if (urlConnection.getResponseCode() != 200) {
						/* All other HTTP errors from Moves will fall here */
						handler.onFailure(getErrorStatus(Utilities.readStream(urlConnection.getErrorStream()), urlConnection.getResponseCode()), "Server not responded with success ("+ urlConnection.getResponseCode() +")");
						return;
					}
					
					String response = Utilities.readStream(urlConnection.getInputStream());
					
					if (isDebugging) {
						Log.i(TAG, "getProfile() : " + response);
					}
					
					JSONObject jsonObject 	= (JSONObject) new JSONTokener(response).nextValue();
					ProfileData profileData = ProfileData.parse(jsonObject);
					handler.onSuccess(profileData);
				} catch (Exception ex) {
					ex.printStackTrace();
					handler.onFailure(MovesStatus.UNEXPECTED_ERROR, "An unexpected error occured, please check logcat");
				}
			}
		}).start();
	}
	
	/**
	 * Use this method to fetch the Summary information of a user from Moves Server
	 * @param handler : A {@link MovesHandler} implementation which will get notified with success/failure
	 * @param specificSummary : If present, should be appended with API path
	 * @param from :  Range start in yyyyMMdd or yyyy-MM-dd format
	 * @param to : Range end in yyyyMMdd or yyyy-MM-dd format
	 * @param pastDays :  How many past days to return, including today (in users current time zone)
	 * @param updatedSince : [optional] if set, return only days which data has been updated since 
	 * given time stamp in ISO 8601 (yyyyMMdd’T’HHmmssZ) format, pass <code>null</code> if not required.
	 * @see <a href="https://dev.moves-app.com/docs/api_summaries">Moves Developer Page for Daily Summaries</a>
	 */
	public static void getDailySummaryList(final MovesHandler<ArrayList<SummaryListData>> handler, 
			final String specificSummary,
			final String from,
			final String to,
			final String pastDays,
			final String updatedSince) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					/* Refresh access token if only AuthData.MOVES_REFRESHBEFORE days are there to expire current token */
					AuthData.refreshAccessTokenIfNeeded();

					/* Exchange the authorization code we obtained after login to get access token */
					HashMap<String, String> nameValuePairs = new HashMap<String, String>();
					nameValuePairs.put("access_token", AuthData.getAuthData().getAccessToken());
					
					if (from != null && from.length() > 0) nameValuePairs.put("from", from);
					if (to != null && to.length() > 0) nameValuePairs.put("to", to);
					if (pastDays != null && pastDays.length() > 0) nameValuePairs.put("pastDays", pastDays);
					if (updatedSince != null && updatedSince.length() > 0) nameValuePairs.put("updatedSince", updatedSince);
					
					
					URL url 	= new URL(MovesAPI.API_BASE + MovesAPI.API_PATH_SUMMARY + (specificSummary != null ? specificSummary : "") + "?" + Utilities.encodeUrl(nameValuePairs));
					
					if (isDebugging) {
						Log.i(TAG, "API Endpoint : " + url.toString());
					}
					
					HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
					urlConnection.setRequestMethod("GET");
					urlConnection.setDoInput(true);
					urlConnection.connect();
					
					if (urlConnection.getResponseCode() != 200) {
						/* All other HTTP errors from Moves will fall here */
						handler.onFailure(getErrorStatus(Utilities.readStream(urlConnection.getErrorStream()), urlConnection.getResponseCode()), "Server not responded with success ("+ urlConnection.getResponseCode() +")");
						return;
					}
					
					String response = Utilities.readStream(urlConnection.getInputStream());
					
					if (isDebugging) {
						Log.i(TAG, "getDailySummaryList() : " + response);
					}
					
					Object object = new JSONTokener(response).nextValue();
					if (object instanceof JSONArray) {
						JSONArray jsonArray = (JSONArray) object;
						ArrayList<SummaryListData> summaryListData = new ArrayList<SummaryListData>();
						if (jsonArray != null) {
							for (int i = 0; i < jsonArray.length(); i++) {
								JSONObject summaryListJsonObject = jsonArray.optJSONObject(i);
								if (summaryListJsonObject != null) {
									summaryListData.add(SummaryListData.parse(summaryListJsonObject));
								}
							}
						}
						handler.onSuccess(summaryListData);
					} else {
						handler.onFailure(MovesStatus.INVALID_RESPONSE, "Expected a JSONArray from server, but failed");;
					}
					
				} catch (Exception ex) {
					ex.printStackTrace();
					handler.onFailure(MovesStatus.UNEXPECTED_ERROR, "An unexpected error occured, please check logcat");
				}
			}
		}).start();
	}
	
	/**
	 * Use this method to fetch the Storyline information of a user from Moves Server
	 * @param handler : A {@link MovesHandler} implementation which will get notified with success/failure
	 * @param specificSummary : If present, should be appended with API path
	 * @param from :  Range start in yyyyMMdd or yyyy-MM-dd format
	 * @param to : Range end in yyyyMMdd or yyyy-MM-dd format
	 * @param pastDays :  How many past days to return, including today (in users current time zone)
	 * @param updatedSince : [optional] if set, return only days which data has been updated since
	 * given time stamp in ISO 8601 (yyyyMMdd’T’HHmmssZ) format, pass <code>null</code> if not required.
	 * @param needTrackPoints : [optional]  if true, the returned activities also include track point information. 
	 * Including track points limits the query range to 7 days.
	 * @see <a href="https://dev.moves-app.com/docs/api_storyline">Moves Developer Page for Storyline</a>
	 */
	public static void getDailyStorylineList(final MovesHandler<ArrayList<StorylineData>> handler, 
			final String specificSummary,
			final String from,
			final String to,
			final String pastDays,
			final String updatedSince,
			final boolean needTrackPoints) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					/* Refresh access token if only AuthData.MOVES_REFRESHBEFORE days are there to expire current token */
					AuthData.refreshAccessTokenIfNeeded();

					/* Exchange the authorization code we obtained after login to get access token */
					HashMap<String, String> nameValuePairs = new HashMap<String, String>();
					nameValuePairs.put("access_token", AuthData.getAuthData().getAccessToken());
					
					if (from != null && from.length() > 0) nameValuePairs.put("from", from);
					if (to != null && to.length() > 0) nameValuePairs.put("to", to);
					if (pastDays != null && pastDays.length() > 0) nameValuePairs.put("pastDays", pastDays);
					if (updatedSince != null && updatedSince.length() > 0) nameValuePairs.put("updatedSince", updatedSince);
					if (needTrackPoints) nameValuePairs.put("trackPoints", "true");
					
					
					URL url 	= new URL(MovesAPI.API_BASE + MovesAPI.API_PATH_STORYLINE + (specificSummary != null ? specificSummary : "") + "?" + Utilities.encodeUrl(nameValuePairs));
					
					if (isDebugging) {
						Log.i(TAG, "API Endpoint : " + url.toString());
					}
					
					HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
					urlConnection.setRequestMethod("GET");
					urlConnection.setDoInput(true);
					urlConnection.connect();
					
					if (urlConnection.getResponseCode() != 200) {
						/* All other HTTP errors from Moves will fall here */
						handler.onFailure(getErrorStatus(Utilities.readStream(urlConnection.getErrorStream()), urlConnection.getResponseCode()), "Server not responded with success ("+ urlConnection.getResponseCode() +")");
						return;
					}
					
					String response = Utilities.readStream(urlConnection.getInputStream());
					
					if (isDebugging) {
						Log.i(TAG, "getDailyStorylineList() : " + response);
					}
					
					Object object = new JSONTokener(response).nextValue();
					if (object instanceof JSONArray) {
						JSONArray jsonArray = (JSONArray) object;
						ArrayList<StorylineData> storylineData = new ArrayList<StorylineData>();
						if (jsonArray != null) {
							for (int i = 0; i < jsonArray.length(); i++) {
								JSONObject storylineJsonObject = jsonArray.optJSONObject(i);
								if (storylineJsonObject != null) {
									storylineData.add(StorylineData.parse(storylineJsonObject));
								}
							}
						}
						handler.onSuccess(storylineData);
					} else {
						handler.onFailure(MovesStatus.INVALID_RESPONSE, "Expected a JSONArray from server, but failed");;
					}
					
				} catch (Exception ex) {
					ex.printStackTrace();
					handler.onFailure(MovesStatus.UNEXPECTED_ERROR, "An unexpected error occured, please check logcat");
				}
			}
		}).start();
	}
	
	/**
	 * Use this method to fetch daily activity breakdown for user from Moves Server
	 * @param handler : A {@link MovesHandler} implementation which will get notified with success/failure
	 * @param specificSummary : If present, should be appended with API path
	 * @param from :  Range start in yyyyMMdd or yyyy-MM-dd format
	 * @param to : Range end in yyyyMMdd or yyyy-MM-dd format
	 * @param pastDays :  How many past days to return, including today (in users current time zone)
	 * @param updatedSince : [optional] if set, return only days which data has been updated since
	 * given time stamp in ISO 8601 (yyyyMMdd’T’HHmmssZ) format, pass <code>null</code> if not required.
	 * @see <a href="https://dev.moves-app.com/docs/api_activity_list">Moves Developer Page for Activity list</a>
	 */
	public static void getDailyActivitiesList(final MovesHandler<ArrayList<StorylineData>> handler, 
			final String specificSummary,
			final String from,
			final String to,
			final String pastDays,
			final String updatedSince) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					/* Refresh access token if only AuthData.MOVES_REFRESHBEFORE days are there to expire current token */
					AuthData.refreshAccessTokenIfNeeded();

					/* Exchange the authorization code we obtained after login to get access token */
					HashMap<String, String> nameValuePairs = new HashMap<String, String>();
					nameValuePairs.put("access_token", AuthData.getAuthData().getAccessToken());
					
					if (from != null && from.length() > 0) nameValuePairs.put("from", from);
					if (to != null && to.length() > 0) nameValuePairs.put("to", to);
					if (pastDays != null && pastDays.length() > 0) nameValuePairs.put("pastDays", pastDays);
					if (updatedSince != null && updatedSince.length() > 0) nameValuePairs.put("updatedSince", updatedSince);
					
					
					URL url 	= new URL(MovesAPI.API_BASE + MovesAPI.API_PATH_ACTIVITIES + (specificSummary != null ? specificSummary : "") + "?" + Utilities.encodeUrl(nameValuePairs));
					
					if (isDebugging) {
						Log.i(TAG, "DailyActivitiesList API Endpoint : " + url.toString());
					}
					
					HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
					urlConnection.setRequestMethod("GET");
					urlConnection.setDoInput(true);
					urlConnection.connect();
					
					if (urlConnection.getResponseCode() != 200) {
						/* All other HTTP errors from Moves will fall here */
						handler.onFailure(getErrorStatus(Utilities.readStream(urlConnection.getErrorStream()), urlConnection.getResponseCode()), "Server not responded with success ("+ urlConnection.getResponseCode() +")");
						return;
					}
					
					String response = Utilities.readStream(urlConnection.getInputStream());
					
					if (isDebugging) {
						Log.i(TAG, "getDailyActivitiesList() : " + response);
					}
					
					Object object = new JSONTokener(response).nextValue();
					if (object instanceof JSONArray) {
						JSONArray jsonArray = (JSONArray) object;
						ArrayList<StorylineData> storylineData = new ArrayList<StorylineData>();
						if (jsonArray != null) {
							for (int i = 0; i < jsonArray.length(); i++) {
								JSONObject storylineJsonObject = jsonArray.optJSONObject(i);
								if (storylineJsonObject != null) {
									storylineData.add(StorylineData.parse(storylineJsonObject));
								}
							}
						}
						handler.onSuccess(storylineData);
					} else {
						handler.onFailure(MovesStatus.INVALID_RESPONSE, "Expected a JSONArray from server, but failed");;
					}
					
				} catch (Exception ex) {
					ex.printStackTrace();
					handler.onFailure(MovesStatus.UNEXPECTED_ERROR, "An unexpected error occured, please check logcat");
				}
			}
		}).start();
	}
	
	private static MovesStatus getErrorStatus(String response, int statusCode) {
		MovesStatus errorStatus = MovesStatus.BAD_RESPONSE;
		if (statusCode == 401) {
			errorStatus = MovesStatus.EXPIRED;
		}
		errorStatus.setStatusMessage(response);
		return errorStatus;
	}
}
