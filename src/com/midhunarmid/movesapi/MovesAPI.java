package com.midhunarmid.movesapi;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.midhunarmid.movesapi.auth.AuthData;
import com.midhunarmid.movesapi.auth.MovesLoginFragment;
import com.midhunarmid.movesapi.profile.ProfileData;
import com.midhunarmid.movesapi.servercalls.HTTPCall;
import com.midhunarmid.movesapi.util.MovesAPIPreferences;

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
	public static void showAuthDialog(MovesHandler<AuthData> handler, FragmentActivity parent) {
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
	public static void getProfile(final MovesHandler<ProfileData> handler) {
		HTTPCall.getProfile(handler);
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
