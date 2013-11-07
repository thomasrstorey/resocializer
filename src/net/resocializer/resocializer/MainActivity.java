package net.resocializer.resocializer;

import android.app.ActionBar;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends FragmentActivity {
	Button lcb;
	Typeface resoLite;
	private SplashFragment splashFragment;
	public String user;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	    if (savedInstanceState == null) {
	        // Add the fragment on initial activity setup
	        splashFragment = new SplashFragment();
	        getSupportFragmentManager()
	        .beginTransaction()
	        .add(android.R.id.content, splashFragment)
	        .commit();
	    } else {
	        // Or set the fragment from restored state info
	        splashFragment = (SplashFragment) getSupportFragmentManager()
	        .findFragmentById(android.R.id.content);
	    }
		
	    SpannableString s = new SpannableString("resocializer");
	    s.setSpan(new TypefaceSpan(this, "titillium-bold"), 0, s.length(),
	            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	 
	    // Update the action bar title with the TypefaceSpan instance
	    ActionBar actionBar = getActionBar();
	    //actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
	    actionBar.setTitle(s);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void logConversation(View view){
		Log.w("resocializer", "hahaha");
		CameraFragment cameraFragment = new CameraFragment();
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(android.R.id.content, cameraFragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}
	
	public void viewProgress(View view){
		ProgressFragment progressFragment = new ProgressFragment();
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(android.R.id.content, progressFragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_logout:
	            SplashFragment.callFacebookLogout(this);
	            return true;
	        case R.id.action_settings:
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public String getUser(){
		return user;
	}
	public void setUser(String u){
		user = u;
	}

}


