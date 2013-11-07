package net.resocializer.resocializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

public class Conversation {
	public final int id;
	public final int friends;
	
	private Conversation(int id, int friends){
		this.id = id;
		this.friends = friends;
	}
	
	public static Conversation readConvo(XmlPullParser parser) throws XmlPullParserException, IOException{
		Log.w("resoxml", "read conversation");
		parser.require(XmlPullParser.START_TAG, null, null);
		int id = Integer.parseInt(parser.getAttributeValue(0));
		int friends = Integer.parseInt(parser.getAttributeValue(1));
		return new Conversation(id, friends);
	}
	
	public static void skip(XmlPullParser parser) throws XmlPullParserException, IOException{
		Log.w("resoxml", "skip");
		if (parser.getEventType() != XmlPullParser.START_TAG){
			throw new IllegalStateException();
		}
		
		int depth = 1;
		while (depth != 0){
			switch (parser.next()){
			case XmlPullParser.END_TAG:
				depth--;
				Log.w("resoxml", "endtag");
				break;
			case XmlPullParser.START_TAG:
				depth++;
				Log.w("resoxml", "starttag");
				break;
			}
		}
	}
	
	public int getID(){
		return id;
	}
	
	public int getFriends(){
		return friends;
	}
	
	public static List<Conversation> getConversations(XmlPullParser parser) throws XmlPullParserException, IOException{
		parser.require(XmlPullParser.START_TAG, null, null);
		List<Conversation> conversations = new ArrayList<Conversation>();
		while(parser.next() != XmlPullParser.END_TAG){
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			
			String name = parser.getName();
	        if (name.equals("conversation")) {
	        	Log.w("resoxml", "found a conversation");
	        	parser.require(XmlPullParser.START_TAG, null, "conversation");
	        	conversations.add(readConvo(parser));
	        	parser.nextTag();
	        	parser.require(XmlPullParser.END_TAG, null, "conversation");
	        } else{
	        	Log.w("resoxml", "inside skip");
	        	skip(parser);
	        }
		}
		return conversations;
	}
	
}
