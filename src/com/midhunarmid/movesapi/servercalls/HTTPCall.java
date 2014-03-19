package com.midhunarmid.movesapi.servercalls;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import org.json.JSONObject;
import org.json.JSONTokener;

import com.midhunarmid.movesapi.MovesAPI;
import com.midhunarmid.movesapi.MovesHandler;
import com.midhunarmid.movesapi.auth.AuthData;
import com.midhunarmid.movesapi.profile.ProfileData;
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
					HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
					urlConnection.setRequestMethod("GET");
					urlConnection.setDoInput(true);
					urlConnection.connect();
					
					if (urlConnection.getResponseCode() != 200) {
						/* All other HTTP errors from Moves will fall here */
						handler.onFailure(MovesStatus.SERVER_FAILURE, "Server not responded with success");
						return;
					}
					
					String response			= Utilities.readStream(urlConnection.getInputStream());
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
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setRequestMethod("POST");
		urlConnection.setDoInput(true);
		urlConnection.connect();
		
		if (urlConnection.getResponseCode() != 200) {
			/* Some unexpected error happened */
			throw new Exception("HTTP Response not success. Bad Request.");
		}
		
		String response		= Utilities.readStream(urlConnection.getInputStream());
		JSONObject jsonObj 	= (JSONObject) new JSONTokener(response).nextValue();
		String access_token	= jsonObj.optString("access_token");
		String user_id		= jsonObj.optString("user_id");
		String expires_in	= jsonObj.optString("expires_in");
		String refresh_token= jsonObj.optString("refresh_token");
		
		
		AuthData.setAccessExpiryInPreference(access_token, user_id, expires_in, refresh_token);
		return access_token;
	}
}
