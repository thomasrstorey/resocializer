package net.resocializer.resocializer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ProgressFragment extends Fragment {
	private TextView xmltest;
	private ArrayList<ImageView> gviews;
	private ArrayList<ImageView> lviews;
	private ArrayList<TextView> tviews;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.progressscreen, container, false);
		int friends = 0;
		int totalConversations = 0;
		int highFriends = 0;
		gviews = new ArrayList<ImageView>();
		lviews = new ArrayList<ImageView>();
		tviews = new ArrayList<TextView>();
		
		Typeface resoLite = Typeface.createFromAsset(getActivity().getAssets(), "fonts/titillium-light.otf");
		RelativeLayout rootLayout = (RelativeLayout)view.findViewById(R.id.progrootlayout);
		getViews(rootLayout);
		for(TextView t : tviews){
			t.setTypeface(resoLite);
			t.setTextColor(getResources().getColor(R.color.textMidGray));
		}
		List<Conversation> conversations;
		List<Achievement> achievements;
		conversations = parseConversations();
		achievements = parseAchievements();
		Log.w("resoxml", "oncreateview");
		for(Conversation c : conversations){
			Log.w("resoxml", "added friend");
			if(c.getFriends() > highFriends){
				highFriends = c.getFriends();
			}
			totalConversations++;
			friends += c.getFriends();
		}
		Log.w("resoxml", "TF = " + friends + " TC = " + totalConversations + " HF = " + highFriends);
		Log.w("resoxml", "size = " + achievements.size());
		for (int i = 0; i < achievements.size(); i++){
			Log.w("resoxml", "reqTF = " + achievements.get(i).reqTF + 
					" reqTC = " + achievements.get(i).reqTC + 
					" reqHF = " + achievements.get(i).reqHF);
			if(friends >= achievements.get(i).reqTF &&
				totalConversations >= achievements.get(i).reqTC &&
				highFriends >= achievements.get(i).reqHF){
				Log.w("resoxml", "congrats!!!!!");
				String imagename = "active_ms_0" + (i+1);
				int id = getResources().getIdentifier("net.resocializer.resocializer:drawable/" + imagename, null, null);
				gviews.get(i).setImageResource(id);
				tviews.get(i).setTextColor(getResources().getColor(R.color.textDarkGray));
				if(i < lviews.size()){
					imagename = "active_line";
					id = getResources().getIdentifier("net.resocializer.resocializer:drawable/" + imagename, null, null);
					lviews.get(i).setImageResource(id);
				}
			}
		}
		xmltest = (TextView) view.findViewById(R.id.xmlTest);
		xmltest.setText("Friends: " + friends);
		return view;
	}
	
	public List<Conversation> parseConversations(){
		String savePath = getActivity().getExternalFilesDir(null) + "/progress/save_progress.xml";
		//Log.w("resoxml", savePath);
		try{
			FileReader convofr = new FileReader(savePath);
			BufferedReader convobr = new BufferedReader(convofr);
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(convobr);
			parser.nextTag();
			List<Conversation> feed;
			feed = readFeed(parser);
			convobr.close();
			return feed;
		} catch(FileNotFoundException e){
			Log.w("resoxml", e.getMessage());
			return null;
		} catch(XmlPullParserException e){
			Log.w("resoxml", e.getMessage());
			return null;
		} catch(IOException e){
			Log.w("resoxml", e.getMessage());
			return null;
		}
	
	}
	
	public List<Achievement> parseAchievements(){
		String savePath = getActivity().getExternalFilesDir(null) + "/progress/achievements.xml";
		try{
			FileReader convofr = new FileReader(savePath);
			BufferedReader convobr = new BufferedReader(convofr);
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(convobr);
			parser.nextTag();
			List<Achievement> feed;
			feed = Achievement.getAchievements(parser);
			convobr.close();
			return feed;
		} catch(FileNotFoundException e){
			Log.w("resoxml", e.getMessage());
			return null;
		} catch(XmlPullParserException e){
			Log.w("resoxml", e.getMessage());
			return null;
		} catch(IOException e){
			Log.w("resoxml", e.getMessage());
			return null;
		}
	
	}
	
	private void getViews(View inView){
		if(inView instanceof RelativeLayout){
			Log.w("resoxml","its a layout");
			RelativeLayout layout = (RelativeLayout)inView;
			layout = (RelativeLayout)inView;
			int count = layout.getChildCount();
			for(int i = 0; i <= count; i++){
				View v = layout.getChildAt(i);
				getViews(v);
			}
		}
		if(inView instanceof LinearLayout){
			Log.w("resoxml","its a layout");
			LinearLayout layout = (LinearLayout)inView;
			layout = (LinearLayout)inView;
			int count = layout.getChildCount();
			for(int i = 0; i <= count; i++){
				View v = layout.getChildAt(i);
				getViews(v);
			}
		}
		else if(inView instanceof TextView){
			Log.w("resoxml","its a tv");
			TextView tview = (TextView)inView;
			tviews.add(tview);
		} 
		else if(inView instanceof ImageView){
			Log.w("resoxml","its a iv");
				ImageView iview = (ImageView)inView;
				Log.w("resoxml", iview.getTag().toString());
				if(iview.getTag().toString().equals("line")){
					lviews.add(iview);
					Log.w("resoxml", "added");
				}else if(iview.getTag().toString().equals("graphic")){
					gviews.add(iview);
					Log.w("resoxml", "added");
				}
		}	
	}

	
	private List<Conversation> readFeed (XmlPullParser parser) throws XmlPullParserException, IOException {
		List<Conversation> conversations = new ArrayList<Conversation>();
		
		parser.require(XmlPullParser.START_TAG, null, null);
		while(parser.next() != XmlPullParser.END_TAG) {
			Log.w("resoxml", "start loop");
			if(parser.getEventType() != XmlPullParser.START_TAG){
				Log.w("resoxml", "event type = " + parser.getEventType());
				continue;
			}
			String name = parser.getName();
			Log.w("resoxml", "name = " + name);
			if (name.equals(((MainActivity)getActivity()).getUser())){
				conversations = Conversation.getConversations(parser);
			} else {
				Log.w("resoxml", "skip");
				Conversation.skip(parser);
				name = parser.getName();
				Log.w("resoxml", "skip name = " + name);
			}
		}
		return conversations;
	}
	
	
}
