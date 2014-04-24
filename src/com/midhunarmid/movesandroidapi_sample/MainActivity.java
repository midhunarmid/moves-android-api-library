package com.midhunarmid.movesandroidapi_sample;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.midhunarmid.movesapi.MovesAPI;
import com.midhunarmid.movesapi.MovesHandler;
import com.midhunarmid.movesapi.auth.AuthData;
import com.midhunarmid.movesapi.profile.ProfileData;
import com.midhunarmid.movesapi.storyline.StorylineData;
import com.midhunarmid.movesapi.summary.SummaryListData;
import com.midhunarmid.movesapi.util.MovesStatus;

public class MainActivity extends FragmentActivity implements OnClickListener{

	private Spinner mSpinnerAPI;
	private Button mButtonSubmit;
	private ProgressBar mProgressRequest;
	private EditText mEditTextResponse;
	
	private static final String CLIENT_ID = "bLp1V2QssOA3s6_725M0rqKW1IxZ4WEs";
	private static final String CLIENT_SECRET = "TV798yJt0W7ZRO9W783cxtOp3F4k44Tf5zSD1k1xRMDFcZ1OPWFjL2CkCBaoG_g_";
	private static final String CLIENT_REDIRECTURL = "http://midhunarmid.github.io/moves-android-api-library/";
	private static final String CLIENT_SCOPES = "activity location";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mSpinnerAPI = (Spinner) findViewById(R.id.spinnerAPI);
		mButtonSubmit = (Button) findViewById(R.id.buttonSubmit);
		mEditTextResponse = (EditText) findViewById(R.id.editResponse);
		mProgressRequest = (ProgressBar) findViewById(R.id.progressRequest);
		
		mButtonSubmit.setOnClickListener(this);
		
		try {
			MovesAPI.init(this, CLIENT_ID, CLIENT_SECRET, CLIENT_SCOPES, CLIENT_REDIRECTURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		toggleProgress(true);
		switch (mSpinnerAPI.getSelectedItemPosition()) {
		case 0: // Authenticate
			MovesAPI.authenticate(authDialogHandler, MainActivity.this);
			break;
		case 1: // Get Auth Data
			AuthData auth = MovesAPI.getAuthData();
			if (auth != null) {
				updateResponse("Access Token : " + auth.getAccessToken() + "\n"
					+ "Expires In : " + auth.getExpiresIn() + "\n"
							+ "User ID : " + auth.getUserID());
			} else {
				updateResponse("Not Authenticated Yet!");
			}
			toggleProgress(false);
			break;
		case 2: // Get Profile
			MovesAPI.getProfile(profileHandler);
			break;
		case 4: // Get Summary Day
			MovesAPI.getSummary_SingleDay(summaryHandler, "20140318", null);
			break;
		case 5: // Get Summary Week
			MovesAPI.getSummary_SpecificWeek(summaryHandler, "2014-W09", null);
			break;
		case 6: // Get Summary Month
			MovesAPI.getSummary_SpecificMonth(summaryHandler, "201403", null);
			break;
		case 7: // Get Summary Range
			MovesAPI.getSummary_WithinRange(summaryHandler, "20140201", "20140228", null);
			break;
		case 8: // Get Summary PastDays
			MovesAPI.getSummary_PastDays(summaryHandler, "31", null);
			break;
		case 10: // Get Storyline Day
			MovesAPI.getStoryline_SingleDay(storylineHandler, "20140318", null, false);
			break;
		case 11: // Get Storyline Week
			MovesAPI.getStoryline_SpecificWeek(storylineHandler, "2014-W09", null, false);
			break;
		case 12: // Get Storyline Month
			MovesAPI.getStoryline_SpecificMonth(storylineHandler, "201403", null, true);
			break;
		case 13: // Get Storyline Range
			MovesAPI.getStoryline_WithinRange(storylineHandler, "20140201", "20140228", null, false);
			break;
		case 14: // Get Storyline PastDays
			MovesAPI.getStoryline_PastDays(storylineHandler, "31", null, true);
			break;
		case 16: // Get Activities Day
			MovesAPI.getActivities_SingleDay(storylineHandler, "20140318", null);
			break;
		case 17: // Get Activities Week
			MovesAPI.getActivities_SpecificWeek(storylineHandler, "2014-W09", null);
			break;
		case 18: // Get Activities Month
			MovesAPI.getActivities_SpecificMonth(storylineHandler, "201403", "20140314T073812Z");
			break;
		case 19: // Get Activities Range
			MovesAPI.getActivities_WithinRange(storylineHandler, "20140201", "20140228", null);
			break;
		case 20: // Get Activities PastDays
			MovesAPI.getActivities_PastDays(storylineHandler, "31", null);
			break;
		default:
			toggleProgress(false);
			break;
		}
	}
	
	private MovesHandler<AuthData> authDialogHandler = new MovesHandler<AuthData>() {
		@Override
		public void onSuccess(AuthData arg0) {
			toggleProgress(false);
			updateResponse("Access Token : " + arg0.getAccessToken() + "\n"
					+ "Expires In : " + arg0.getExpiresIn() + "\n"
							+ "User ID : " + arg0.getUserID());
		}
		
		@Override
		public void onFailure(MovesStatus status, String message) {
			toggleProgress(false);
			updateResponse("Request Failed! \n"
					+ "Status Code : " + status + "\n"
							+ "Status Message : " + message + "\n\n"
							+ "Specific Message : " + status.getStatusMessage());
		}
	};
	
	private MovesHandler<ProfileData> profileHandler = new MovesHandler<ProfileData>() {
		
		@Override
		public void onSuccess(ProfileData arg0) {
			toggleProgress(false);
			updateResponse("User ID : " + arg0.getUserID() 
					+ "\nUser Platform : " + arg0.getPlatform());
		}
		
		@Override
		public void onFailure(MovesStatus status, String message) {
			toggleProgress(false);
			updateResponse("Request Failed! \n"
					+ "Status Code : " + status + "\n"
							+ "Status Message : " + message + "\n\n"
							+ "Specific Message : " + status.getStatusMessage());
		}
	};
	
	private MovesHandler<ArrayList<SummaryListData>> summaryHandler = new MovesHandler<ArrayList<SummaryListData>>() {
		@Override
		public void onSuccess(ArrayList<SummaryListData> result) {
			toggleProgress(false);
			updateResponse("Summary Items : " + result.size());
		}
		
		@Override
		public void onFailure(MovesStatus status, String message) {
			toggleProgress(false);
			updateResponse("Request Failed! \n"
					+ "Status Code : " + status + "\n"
							+ "Status Message : " + message + "\n\n"
							+ "Specific Message : " + status.getStatusMessage());
		}
	};
	
	private MovesHandler<ArrayList<StorylineData>> storylineHandler = new MovesHandler<ArrayList<StorylineData>>() {
		@Override
		public void onSuccess(ArrayList<StorylineData> result) {
			toggleProgress(false);
			updateResponse("Summary Items : " + result.size());
		}
		
		@Override
		public void onFailure(MovesStatus status, String message) {
			toggleProgress(false);
			updateResponse("Request Failed! \n"
					+ "Status Code : " + status + "\n"
							+ "Status Message : " + message + "\n\n"
							+ "Specific Message : " + status.getStatusMessage());
		}
	};
	
	public void toggleProgress(final boolean isProgrressing) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mProgressRequest.setVisibility(isProgrressing ? View.VISIBLE : View.GONE);
				mButtonSubmit.setVisibility(isProgrressing ? View.GONE : View.VISIBLE);
			}
		});
	}
	
	public void updateResponse(final String message) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mEditTextResponse.setText(message);
			}
		});
	}
}
