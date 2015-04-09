package com.midhunarmid.movesapi.auth;

import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import org.json.JSONObject;
import org.json.JSONTokener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout.LayoutParams;

import com.midhunarmid.movesapi.MovesAPI;
import com.midhunarmid.movesapi.MovesHandler;
import com.midhunarmid.movesapi.util.MovesStatus;
import com.midhunarmid.movesapi.util.Utilities;

/**
 * This {@link DialogFragment} will take user to Moves login page where user can grant access to application.
 * After successful login, the provided {@link MovesHandler} will get notified in its <code>onSuccess()</code> call back
 * with {@link AuthData} result. If some error occurs or user not authenticated the app, then <code>onFailure()</code> 
 * call back will be invoked with corresponding error information
 * @author Midhu
 * @see MovesHandler
 * @see MovesStatus
 * @see AuthData
 */
@SuppressLint("SetJavaScriptEnabled")
@SuppressWarnings("deprecation")
public class MovesLoginFragment extends DialogFragment {
	
	/*  A long unique string value that is hard to guess. Used to prevent CSRF. */
	private final static String mState				= "123456789";
	
	/* to use in startActivityForResult, Moves app login flow */
	private static final int MOVES_LOGIN_REQUEST_CODE = 109;
	
	private ProgressDialog 		mDialog;
	private boolean isLoginSuccess = false;
	private boolean isBrowserAuth = true;
	private WebView mAuthWebView;
	
	private static MovesHandler<AuthData> mMovesAPIHandler = null;
	
	/** Gets a new instance of this {@link MovesLoginFragment} and sets the {@link MovesHandler} to get notified about
	 * success and failure **/
	public static MovesLoginFragment newInstance(MovesHandler<AuthData> handler) {
		mMovesAPIHandler = handler; 
		MovesLoginFragment frag = new MovesLoginFragment();
		return frag;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NO_FRAME, getCurrentActivityTheme(getActivity()));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mAuthWebView = new WebView(getActivity());
		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mAuthWebView.setLayoutParams(layoutParams);
		return mAuthWebView;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mDialog = new ProgressDialog(getActivity());
		mDialog.setMessage("Loading");
		mDialog.setCancelable(false);
		
        try {
            Uri uri = createAuthWithAppUri("moves", "app", "/authorize").build();
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivityForResult(intent, MOVES_LOGIN_REQUEST_CODE);
            isBrowserAuth = false;
        } catch (ActivityNotFoundException e) {
        	makeAuthWithBrowser();
        }
	}
	
	/** If Moves app is not installed, use this method to complete the auth flow **/
	private void makeAuthWithBrowser() {
		mAuthWebView.clearCache(true);
		CookieManager.getInstance().removeAllCookie();
		mAuthWebView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				showProgressDialog();
				
				/* After successful login the redirected url will contain a parameter named 'code' */
                if (url.indexOf("code=") > -1 && !isLoginSuccess) {
                	/* This will be possibly the redirected url after successful login. Decode the url to get all params. */
                	HashMap<String, String> params = Utilities.decodeUrl(url);
                	String state = params.get("state");
                	String authCode = params.get("code");
                	
                	/* Check the state value for any CSRF problems */
                	if (state != null && authCode != null && state.equalsIgnoreCase(mState)) {
                        isLoginSuccess = true;
                        performLoginSuccess(authCode);
                	}
                }
			}
			
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				if (url.contains(MovesAPI.getClientDetails().getRedirectURL()) && !url.contains("code=")) {
					/** User not authenticated our app **/
					mMovesAPIHandler.onFailure(MovesStatus.NOT_GRANTED, "Permission not granted");
					dismiss();
				} else if (!url.contains("code=")) {
					killProgressDialog();
				}
			}
		});
		
		mAuthWebView.setWebChromeClient(new WebChromeClient());
		mAuthWebView.getSettings().setJavaScriptEnabled(true);
		mAuthWebView.getSettings().setSavePassword(false);
		mAuthWebView.loadUrl(createAuthWithWebUrl());
	}

	/** This method will return the URI to launch Moves app for auth flow **/
    private Uri.Builder createAuthWithAppUri(String scheme, String authority, String path) {
        return new Uri.Builder()
                .scheme(scheme)
                .authority(authority)
                .path(path)
                .appendQueryParameter("client_id", MovesAPI.getClientDetails().getClientID())
                .appendQueryParameter("redirect_uri", MovesAPI.getClientDetails().getRedirectURL())
                .appendQueryParameter("scope", MovesAPI.getClientDetails().getClientScopes())
                .appendQueryParameter("state", mState);
    }
	
    /** This method will return the auth URL to load in browser (use if Moves App is not present) **/
    private String createAuthWithWebUrl() {
    	String authURL = "";
		/* Exchange the authorization code we obtained after login to get access token */
		HashMap<String, String> nameValuePairs = new HashMap<String, String>();
		nameValuePairs.put("response_type", "code");
		nameValuePairs.put("redirect_uri", MovesAPI.getClientDetails().getRedirectURL());
		nameValuePairs.put("client_id", MovesAPI.getClientDetails().getClientID());
		nameValuePairs.put("scope", MovesAPI.getClientDetails().getClientScopes());
		nameValuePairs.put("state", mState);
		
    	authURL = MovesAPI.API_AUTH_BASE + MovesAPI.API_PATH_AUTHORIZE + "?" + Utilities.encodeUrl(nameValuePairs);
    	return authURL;
    }
    
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
    	if (isBrowserAuth) return;
        switch (requestCode) {
        case MOVES_LOGIN_REQUEST_CODE:
        	if (resultCode == Activity.RESULT_OK) {
	            Uri resultUri = data.getData();
	
	            /* After successful login the redirected url will contain a parameter named 'code' */
	            if (resultUri.toString().indexOf("code=") > -1) {
	            	/* This will be possibly the redirected url after successful login. Decode the url to get all params. */
	            	HashMap<String, String> params = Utilities.decodeUrl(resultUri.toString());
	            	String state = params.get("state");
	            	String authCode = params.get("code");
	            	
	            	/* Check the state value for any CSRF problems */
	            	if (state != null && authCode != null && state.equalsIgnoreCase(mState)) {
	                    performLoginSuccess(authCode);
	                    return;
	            	}
	            }
        	}
            /* If execution reaches here it means the authentication flow failed due to some reasons */
            mMovesAPIHandler.onFailure(MovesStatus.NOT_GRANTED, "Permission not granted");
            dismiss();
            break;
        }
	}

    /**
     * After getting a valid auth code, use this method to exchange that auth code to get an access token
     * @param authCode : The auth code that we got after success full login
     */
	private void performLoginSuccess(final String authCode) {
		showProgressDialog();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					/* Exchange the authorization code we obtained after login to get access token */
					HashMap<String, String> nameValuePairs = new HashMap<String, String>();
					nameValuePairs.put("grant_type", "authorization_code");
					nameValuePairs.put("code", authCode);
					nameValuePairs.put("redirect_uri", MovesAPI.getClientDetails().getRedirectURL());
					nameValuePairs.put("client_id", MovesAPI.getClientDetails().getClientID());
					nameValuePairs.put("client_secret", MovesAPI.getClientDetails().getClientSecret());
					
					URL url 	= new URL(MovesAPI.API_AUTH_BASE + MovesAPI.API_PATH_ACCESSTOKEN + "?" + Utilities.encodeUrl(nameValuePairs));
					HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
					urlConnection.setRequestMethod("POST");
					urlConnection.setDoInput(true);
					urlConnection.connect();
					
					if (urlConnection.getResponseCode() != 200) {
						/* All other HTTP errors from Moves will fall here */
						mMovesAPIHandler.onFailure(MovesStatus.AUTH_FAILED, "Auth request to Moves server failed");
						dismiss();
					}
					
					String response		= Utilities.readStream(urlConnection.getInputStream());
					JSONObject jsonObj 	= (JSONObject) new JSONTokener(response).nextValue();
					String token		= jsonObj.optString("access_token");
					String user_id		= jsonObj.optString("user_id");
					String expires_in	= jsonObj.optString("expires_in");
					String refresh_token= jsonObj.optString("refresh_token");
					
					AuthData.setAccessExpiryInPreference(token, user_id, expires_in, refresh_token);
					killProgressDialog();
					
					AuthData authData = new AuthData(token, user_id, expires_in, refresh_token);
					mMovesAPIHandler.onSuccess(authData);
					dismiss();
				} catch (Exception ex) {
					ex.printStackTrace();
					killProgressDialog();
					mMovesAPIHandler.onFailure(MovesStatus.UNEXPECTED_ERROR, "An unexpected error occured, please check logcat");
					dismiss();
				}
			}
		}).start();
	}
	
	private void showProgressDialog() {
		if (mDialog != null && !mDialog.isShowing()) {
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mDialog.show();
				}
			});
		}
	}

	private void killProgressDialog() {
		if (mDialog != null && mDialog.isShowing()) {
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mDialog.dismiss();
				}
			});
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		killProgressDialog();
	}
	
    public int getCurrentActivityTheme(Activity activity) {
        /* Set default theme as Theme_Black_NoTitleBar, then try to update this fragment with the current activity theme */
        int themeResourceId = android.R.style.Theme_Black_NoTitleBar;
        try {
            /* No direct API is available to do this action, so we are using method reflection here to achieve this goal */
            Class<?> reflectionClass = ContextThemeWrapper.class;
            Method method = reflectionClass.getMethod("getThemeResId");
            method.setAccessible(true);
            themeResourceId = (Integer) method.invoke(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return themeResourceId;
    }
}
