package net.resocializer.resocializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;


public class Achievement {
	public final int id;
	public final int reqTF;
	public final int reqHF;
	public final int reqTC;
	
	private Achievement(int id, int reqTF, int reqHF, int reqTC){
		this.id = id;
		this.reqHF = reqHF;
		this.reqTC = reqTC;
		this.reqTF = reqTF;
	}
	
	public static Achievement readCheevo(XmlPullParser parser) throws XmlPullParserException, IOException{
		parser.require(XmlPullParser.START_TAG, null, "achievement");
		int id = Integer.parseInt(parser.getAttributeValue(0));
		int reqHF = Integer.parseInt(parser.getAttributeValue(1));
		int reqTC = Integer.parseInt(parser.getAttributeValue(2));
		int reqTF = Integer.parseInt(parser.getAttributeValue(3));
		return new Achievement(id, reqTF, reqHF, reqTC);
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
	
	public int getReqHF(){
		return reqHF;
	}
	
	public int getReqTC(){
		return reqTC;
	}
	
	public int getReqTF(){
		return reqTF;
	}
	
	public static List<Achievement> getAchievements(XmlPullParser parser) throws XmlPullParserException, IOException{
		parser.require(XmlPullParser.START_TAG, null, null);
		List<Achievement> Achievements = new ArrayList<Achievement>();
		while(parser.next() != XmlPullParser.END_TAG){
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			
			String name = parser.getName();
	        if (name.equals("achievement")) {
	        	parser.require(XmlPullParser.START_TAG, null, "achievement");
	        	Achievements.add(readCheevo(parser));
	        	parser.nextTag();
	        	parser.require(XmlPullParser.END_TAG, null, "achievement");
	        } else{
	        	skip(parser);
	        }
		}
		return Achievements;
	}

}
