package net.resocializer.resocializer;

import java.util.Arrays;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

public class SplashFragment extends Fragment {
	private static final String TAG = "SplashFragment";
	private Button lcb;
	private Button abt;
	private Button ps;
	private LoginButton authButton;
	private TextView wtxt;
	private Session.StatusCallback callback = new Session.StatusCallback() {
	    @Override
	    public void call(Session session, SessionState state, Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};
	private UiLifecycleHelper uiHelper;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    uiHelper = new UiLifecycleHelper(getActivity(), callback);
	    uiHelper.onCreate(savedInstanceState);
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    uiHelper.onResume();
	    // For scenarios where the main activity is launched and user
	    // session is not null, the session state change notification
	    // may not be triggered. Trigger it if it's open/closed.
	    Session session = Session.getActiveSession();
	    if (session != null &&
	           (session.isOpened() || session.isClosed()) ) {
	        onSessionStateChange(session, session.getState(), null);
	    }

	    uiHelper.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, Bundle savedInstanceState) {
	    		View view = inflater.inflate(R.layout.activity_main, container, false);
	    		authButton = (LoginButton) view.findViewById(R.id.authButton);
	    		authButton.setFragment(this);
	    		authButton.setReadPermissions(Arrays.asList("user_location", "user_birthday", "user_likes"));
	    		lcb = (Button) view.findViewById(R.id.lcb);
	    		ps = (Button) view.findViewById(R.id.ps);
	    		abt = (Button) view.findViewById(R.id.abt);
	    		wtxt = (TextView) view.findViewById(R.id.welcomeTextView);
	    		Typeface resoLite = Typeface.createFromAsset(getActivity().getAssets(), "fonts/titillium-light.otf");
	    		Typeface resoItalic = Typeface.createFromAsset(getActivity().getAssets(), "fonts/titillium-lightitalic.otf");
	    		lcb.setTypeface(resoLite);
	    		ps.setTypeface(resoLite);
	    		abt.setTypeface(resoLite);
	    		wtxt.setTypeface(resoItalic);
	    		wtxt.setText("Ready to resocialize? ");
	    return view;
	}
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) { 
	    if (state.isOpened()) {
	        Log.i(TAG, "Logged in...");
	        authButton.setVisibility(View.GONE);
	        lcb.setVisibility(View.VISIBLE);
	        ps.setVisibility(View.VISIBLE);
	        abt.setVisibility(View.VISIBLE);
	        Request.newMeRequest(session, new Request.GraphUserCallback() {
				
	            @Override
	            public void onCompleted(GraphUser user, Response response) {
	                if (user != null) {
	                    // Display the parsed user info
	                    wtxt.setText("Welcome, " + buildUserInfoDisplay(user) + "! ");
	                }
	            }
	        }).executeAsync();

	    } else if (state.isClosed()) {
	        Log.i(TAG, "Logged out...");
	        lcb.setVisibility(View.INVISIBLE);
	        ps.setVisibility(View.INVISIBLE);
	        abt.setVisibility(View.INVISIBLE);
	        authButton.setVisibility(View.VISIBLE);
	        wtxt.setText("Ready to resocialize? ");
	    }
	}
	
	private String buildUserInfoDisplay(GraphUser user) {
	    String userName = "";

	    // Example: typed access (name)
	    // - no special permissions required
	    userName = user.getName();
	    int spaceIndex = userName.indexOf(" ");
	    if (spaceIndex != -1)
	    {
	        userName = userName.substring(0, spaceIndex);
	    }

	    return userName;
	}
	
	public static void callFacebookLogout(Context context) {
	    Session session = Session.getActiveSession();
	    if (session != null) {

	        if (!session.isClosed()) {
	            session.closeAndClearTokenInformation();
	            //clear your preferences if saved
	        }
	    } else {

	        session = new Session(context);
	        Session.setActiveSession(session);

	        session.closeAndClearTokenInformation();
	            //clear your preferences if saved

	    }

	}
	
	
}

