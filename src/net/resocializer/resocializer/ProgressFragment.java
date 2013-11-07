package net.resocializer.resocializer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ProgressFragment extends Fragment {
	private TextView xmltest;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.progressscreen, container, false);
		int friends = 0;
		List<Conversation> conversations;
		conversations = parseConversations();
		Log.w("resoxml", "oncreateview");
		for(Conversation c : conversations){
			Log.w("resoxml", "added friend");
			friends += c.getFriends();
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
