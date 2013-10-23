package net.resocializer.resocializer;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

public class CameraFragment extends Fragment implements OnClickListener {
	private Button pb;
	private ExtendedImageView photo;
	private Bitmap photoBitmap;
	private String mCurrentPhotoPath;
	private int maxFaces = 10;
	private static final int REQUEST_CODE = 1337;
	private static final String JPEG_FILE_PREFIX = "resocializer";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	private Canvas faceCanvas;
	private Paint facepaint;
	private Face[] faces;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
    	View view = inflater.inflate(R.layout.camera, container, false);
    	pb = (Button) view.findViewById(R.id.photoButton);
    	photo = (ExtendedImageView) view.findViewById(R.id.photo);
    	Typeface resoLite = Typeface.createFromAsset(getActivity().getAssets(), "fonts/titillium-light.otf");
    	pb.setTypeface(resoLite);
    	pb.setOnClickListener(this);
        return view;
    }
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	}
    
    private void dispatchTakePictureIntent(int actionCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try{
	        File f = createImageFile();
	        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
	        startActivityForResult(takePictureIntent, actionCode);
        }
        catch(IOException io){
        	Log.w("resocializer", io.getMessage());
        }
    }
    
    public static boolean isIntentAvailable(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list =
                packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }
    
    public void takePhoto(){
    	if(isIntentAvailable(getActivity(), MediaStore.ACTION_IMAGE_CAPTURE)){
    		dispatchTakePictureIntent(REQUEST_CODE);
    		Log.w("resocializer", "dispatch");
    	}
    }
    
    private void handleSmallCameraPhoto(Intent intent) {
        Bundle extras = intent.getExtras();
        photoBitmap = (Bitmap) extras.get("data");
        photo.setImageBitmap(photoBitmap);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
    	//handleSmallCameraPhoto(data);
    	Bitmap wfacesBm = setPic();
    	Bitmap mfacesBm = wfacesBm.copy(Bitmap.Config.RGB_565, true);
    	faces = new Face[maxFaces];
        FaceDetector fd = new FaceDetector(mfacesBm.getWidth(), mfacesBm.getHeight(), maxFaces);
        fd.findFaces(mfacesBm, faces);
        photo.setFaces(faces);
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.photoButton:

            takePhoto();
            Log.w("resocializer", "take a photo");

            break;
        }
    }
    
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
    
    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory. 
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("resocializer", "Directory not created");
        }
        return file;
    }
    
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File image = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, getAlbumStorageDir(JPEG_FILE_PREFIX));
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    
    private Bitmap setPic() {
        // Get the dimensions of the View
        int targetW = photo.getWidth();
        int targetH = photo.getHeight();
      
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
      
        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
      
        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
      
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        photo.setImageBitmap(bitmap);
        return bitmap;
    }
    

    
}




