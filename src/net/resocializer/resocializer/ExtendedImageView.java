package net.resocializer.resocializer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.FaceDetector.Face;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class ExtendedImageView extends ImageView {
	private Paint paint;
	private Face[] faces;
	private PointF center;
	public ExtendedImageView(Context context){
		super(context);
        paint = new Paint();
        center = new PointF();

	}
	public ExtendedImageView(Context context, AttributeSet attrs){
		super(context, attrs);
        paint = new Paint();
        center = new PointF();
	}    	
	public ExtendedImageView(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
        paint = new Paint();
        center = new PointF();
	}
    public void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    Log.w("resocializer", "canvas width " + canvas.getWidth());
    Log.w("resocializer", "canvas height " + canvas.getHeight());
   	paint.setColor(Color.YELLOW);
	paint.setStyle(Paint.Style.STROKE);
	paint.setStrokeWidth(2);
	if(faces != null){
		Log.w("resocializer", "there are faces");
		for(Face face : faces){
			Log.w("resocializer", "test a face");
			if(face != null){
				face.getMidPoint(center);
				float l = (center.x-face.eyesDistance()/2) - face.eyesDistance()*1.0f;
				float t = (center.y-face.eyesDistance()/2) - face.eyesDistance()*1.2f;
				float r = (center.x-face.eyesDistance()/2) + face.eyesDistance()*1.0f;
				float b = (center.y-face.eyesDistance()/2) + face.eyesDistance()*1.2f;
				canvas.drawRect(l,t,r,b,paint);
				canvas.drawPoint(center.x-face.eyesDistance()/2, center.y-face.eyesDistance()/2, paint);
				Log.w("resocializer", "drew rectangle");
			}
		}
	}
   }
   public void setFaces(Face[] _faces){
	   faces = _faces;
   }
}
