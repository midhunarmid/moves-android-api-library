package com.midhunarmid.movesapi.auth;

import java.util.Calendar;

import com.midhunarmid.movesapi.MovesAPI;
import com.midhunarmid.movesapi.servercalls.HTTPCall;
import com.midhunarmid.movesapi.util.MovesAPIPreferences;

/**
 * This class holds all Auth related data of a Moves User
 * @author Midhu
 * @see <a href="https://dev.moves-app.com/docs/authentication">Moves Developer Page for Authentication</a>
 */
public class AuthData {
	/* We need to refresh the access token before it get expired (sets number of days here) */
	private static final int MOVES_REFRESHBEFORE = 10;
	
	private final String mAccessToken;
	private final String mUserID;
	private final String mExpiresIn;
	private final String mRefreshToken;
	
	/**
	 * Constructor method of {@link AuthData}
	 * @param accessToken : A valid access token after obtained successful authentication
	 * @param userID : Authenticated user's unique ID
	 * @param expiresIn : Access token expire time in seconds
	 * @param refreshToken : Refresh token
	 */
	public AuthData(String accessToken, String userID, String expiresIn, String refreshToken) {
		super();
		mAccessToken = accessToken;
		mUserID = userID;
		mExpiresIn = expiresIn;
		mRefreshToken = refreshToken;
	}
	
	/** ***************************************************************************************************** **/	
	/** ******************* Utility methods   *************************************************************** **/
	
	/**
	 * Use this method to get {@link AuthData} of currently authenticated user
	 * @return A valid {@link AuthData} if authentication informations are available, or else <code>null</code> 
	 */
	public static AuthData getAuthData() {
		try {
			if (isAuthenticated()) {
				String accessToken 	= MovesAPIPreferences.getPreference(MovesAPIPreferences.MOVES_ACCESS);
				String userID 		= MovesAPIPreferences.getPreference(MovesAPIPreferences.MOVES_USERID);
				String expiresIn 	= MovesAPIPreferences.getPreference(MovesAPIPreferences.MOVES_EXPIRE);
				String refreshToken = MovesAPIPreferences.getPreference(MovesAPIPreferences.MOVES_REFRESH);
				AuthData authData 	= new AuthData(accessToken, userID, expiresIn, refreshToken);
				return authData;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static boolean isAuthenticated() {
		try {
			String authStatus = MovesAPIPreferences.getPreference(MovesAPIPreferences.MOVES_AUTHSTATUS);
			if (authStatus.equalsIgnoreCase(MovesAPIPreferences.STATUS_YES)) return true;
			else return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/** Get a unique identifier of the user <br><br> 
	 *  returns <b><i>def</b></i> if an error occurred **/
	public long getUserID_Long(long def) {
		try {
			return Long.parseLong(mUserID);
		} catch (Exception e) {
			return def;
		}
	}
	
	/** Get the access token lifetime in seconds <br><br> 
	 *  returns <b><i>def</b></i> if an error occurred **/
	public long getExpiresIn_Long(long def) {
		try {
			return Long.parseLong(mExpiresIn);
		} catch (Exception e) {
			return def;
		}
	}
	
	
	/**
	 * Call this method before each and every API calls to make sure that we are refreshing the token before it get expired
	 * @return If the old one is refreshed, then this method will return the new access token or else <code>null</code>
	 * @throws Exception
	 */
	public static String refreshAccessTokenIfNeeded() throws Exception {
		if (AuthData.isRefreshTokenNeeded(MOVES_REFRESHBEFORE)) {
			String newAccessToken = HTTPCall.refreshAccessToken();
			return newAccessToken;
		}
		return null;
	}
	
	/** Checks whether the access token is going to expire in <b><i>expiresIn</i></b> days or not  
	 * @param expiresIn number of days
	 * @return <code>true</code> if token is going to expire after specified number of days, <code>false</code> otherwise. <br>
	 * Will return <code>null</code> if an error is occurred
	 */
	public static boolean isRefreshTokenNeeded(int expiresIn) {
		try {
			String expiresInMillis_String = MovesAPIPreferences.getPreference(MovesAPIPreferences.MOVES_EXPIRE);
			if (expiresInMillis_String == null || expiresInMillis_String.length() == 0) {
				return false;
			}
			long expiresInMillis = Long.parseLong(expiresInMillis_String);
			Calendar expiryTime = Calendar.getInstance();
			expiryTime.setTimeInMillis(expiresInMillis);
			expiryTime.add(Calendar.DATE, -(expiresIn));
			
			Calendar now = Calendar.getInstance();
			boolean isRefreshNeeded = expiryTime.before(now);
			return isRefreshNeeded;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Sets the refresh token and expire time info into preference for later use
	 * @throws Exception if {@link MovesAPI} init() is not called yet
	 */
	public static void setAccessExpiryInPreference(String token, String user_id, String expires_in, String refresh_token) throws Exception {
		int secondsToExpire = Integer.parseInt(expires_in);
		Calendar expiryTime = Calendar.getInstance();
		expiryTime.add(Calendar.SECOND, secondsToExpire);
		String expiresInMillis = String.valueOf(expiryTime.getTimeInMillis());
		MovesAPIPreferences.setPreference(MovesAPIPreferences.MOVES_ACCESS, token);
		MovesAPIPreferences.setPreference(MovesAPIPreferences.MOVES_USERID, user_id);
		MovesAPIPreferences.setPreference(MovesAPIPreferences.MOVES_EXPIRE, expiresInMillis);
		MovesAPIPreferences.setPreference(MovesAPIPreferences.MOVES_REFRESH, refresh_token);
		MovesAPIPreferences.setPreference(MovesAPIPreferences.MOVES_AUTHSTATUS, MovesAPIPreferences.STATUS_YES);
	}
	
	/** ***************************************************************************************************** **/	
	/** ******************* Getter methods    *************************************************************** **/
	
	/** Get a valid access token to use Moves API **/
	public String getAccessToken() {
		return mAccessToken;
	}
	
	/** Get a unique identifier (64 bit unsigned) of the user associated with the access token **/
	public String getUserID() {
		return mUserID;
	}
	
	/** Get the access token lifetime in seconds **/
	public String getExpiresIn() {
		return mExpiresIn;
	}
	
	/** Get the refresh token used to refresh an access token via refresh token call **/
	public String getRefreshToken() {
		return mRefreshToken;
	}
}
