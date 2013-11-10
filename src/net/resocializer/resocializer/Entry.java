package net.resocializer.resocializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

public class Entry {
	
	public final int id;
	public String name;
	
	public Entry(){
		id = 0;
		name = null;
	}
	
	public Entry(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	public static Entry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException{
		parser.require(XmlPullParser.START_TAG, null, null);
		String name = parser.getName();
		int id = Integer.parseInt(parser.getAttributeValue(0));
		return new Entry(id, name);
	}
	
	public static void skip(XmlPullParser parser) throws XmlPullParserException, IOException{
		if (parser.getEventType() != XmlPullParser.START_TAG){
			throw new IllegalStateException();
		}
		
		int depth = 1;
		while (depth != 0){
			switch (parser.next()){
			case XmlPullParser.END_TAG:
				depth--;
				break;
			case XmlPullParser.START_TAG:
				depth++;
				break;
			}
		}
	}
	
	public int getID(){
		return id;
	}
	
	
	public static List<Entry> getEntries(XmlPullParser parser) throws XmlPullParserException, IOException{
		parser.require(XmlPullParser.START_TAG, null, null);
		List<Entry> entries = new ArrayList<Entry>();
		while(parser.next() != XmlPullParser.END_TAG){
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			
			String name = parser.getName();
	        if (name.equals(name)) {
	        	parser.require(XmlPullParser.START_TAG, null, name);
	        	entries.add(readEntry(parser));
	        	parser.nextTag();
	        	parser.require(XmlPullParser.END_TAG, null, name);
	        } else{
	        	Log.w("resoxml", "inside skip");
	        	skip(parser);
	        }
		}
		return entries;
	}
	
}