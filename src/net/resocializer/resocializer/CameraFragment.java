package net.resocializer.resocializer;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

public class CameraFragment extends Fragment implements OnClickListener {
	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
	private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
	private boolean pendingPublishReauthorization = false;
	private Button pb;
	private Button lb;
	private ExtendedImageView photo;
	private Bitmap photoBitmap;
	private String mCurrentPhotoPath;
	private int maxFaces = 10;
	private static final int REQUEST_CODE = 1337;
	private static final String JPEG_FILE_PREFIX = "resocializer";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	private Face[] faces;
	private UiLifecycleHelper uiHelper;
	String fbPhotoAddress = null;
	private Session.StatusCallback callback = new Session.StatusCallback() {
	    @Override
	    public void call(Session session, SessionState state, Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
    	View view = inflater.inflate(R.layout.camera, container, false);
    	pb = (Button) view.findViewById(R.id.photoButton);
    	lb = (Button) view.findViewById(R.id.logButton);
    	photo = (ExtendedImageView) view.findViewById(R.id.photo);
    	Typeface resoLite = Typeface.createFromAsset(getActivity().getAssets(), "fonts/titillium-light.otf");
    	pb.setTypeface(resoLite);
    	lb.setTypeface(resoLite);
    	pb.setText("Take Photo");
    	pb.setOnClickListener(this);
    	lb.setOnClickListener(this);
    	lb.setVisibility(View.INVISIBLE);
    	if (savedInstanceState != null) {
    	    pendingPublishReauthorization = 
    	        savedInstanceState.getBoolean(PENDING_PUBLISH_KEY, false);
    	}
    	return view;
    }
    
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
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
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
        pb.setText("New Photo");
        lb.setVisibility(View.VISIBLE);
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.photoButton:

            takePhoto();
            Log.w("resocializer", "take a photo");
            return;
            
        case R.id.logButton:
        	Log.w("camera", "publish photo");
        	int i = 0;
        	for(Face f : faces){
        		if(f != null){
        			i++;
        		}
        	}
        	addConversation(i);
        	publishStory();

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
        photoBitmap = bitmap;
        return bitmap;
    }
    
    private void publishStory() {
        Session session = Session.getActiveSession();

        if (session != null){
        	Bitmap imageSelected = photoBitmap;
            // Check for publish permissions    
            List<String> permissions = session.getPermissions();
            if (!isSubsetOf(PERMISSIONS, permissions)) {
                pendingPublishReauthorization = true;
                Session.NewPermissionsRequest newPermissionsRequest = new Session
                        .NewPermissionsRequest(this, PERMISSIONS);
            session.requestNewPublishPermissions(newPermissionsRequest);
                return;
            }
            
            

            // Part 1: create callback to get URL of uploaded photo
               Request.Callback uploadPhotoRequestCallback = new Request.Callback() {
               @Override
                  public void onCompleted(Response response) {
                   if (response.getError() != null) {  // [IF Failed Posting]
                      Log.d("resocializer", "photo upload problem. Error="+response.getError() );
                   }  //  [ENDIF Failed Posting]

                   Object graphResponse = response.getGraphObject().getProperty("id");
                   if (graphResponse == null || !(graphResponse instanceof String) || 
                       TextUtils.isEmpty((String) graphResponse)) { // [IF Failed upload/no results]
                          Log.d("resocializer", "failed photo upload/no response");
                          Toast.makeText(getActivity()
                                  .getApplicationContext(),
                                  response.getError().getErrorMessage(),
                                  Toast.LENGTH_SHORT).show();
                   } else {  // [ELSEIF successful upload]
                       fbPhotoAddress = "https://www.facebook.com/photo.php?fbid=" +graphResponse;
                       Toast.makeText(getActivity()
                               .getApplicationContext(), 
                               "Conversation Logged",
                               Toast.LENGTH_LONG).show();
                   }  // [ENDIF successful posting or not]
                }  // [END onCompleted]
             }; 



            Bundle postParams = new Bundle();
            postParams.putString("name", "resocializer for Android");
            postParams.putString("caption", "be #resocial!");
            postParams.putString("description", "#resocializer is the great new way to reconnect face to face.");
            postParams.putString("link", "http://resocializer.net/");
            postParams.putString("message", generateString());

            Request.Callback callback= new Request.Callback() {
                public void onCompleted(Response response) {
                    JSONObject graphResponse = response
                                               .getGraphObject()
                                               .getInnerJSONObject();
                    String postId = null;
                    try {
                        postId = graphResponse.getString("id");
                    } catch (JSONException e) {
                        Log.i("resocializer", "JSON error "+ e.getMessage());
                    }
                    FacebookRequestError error = response.getError();
                    if (error != null) {
                        Toast.makeText(getActivity()
                             .getApplicationContext(),
                             error.getErrorMessage(),
                             Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity()
                                 .getApplicationContext(), 
                                 postId,
                                 Toast.LENGTH_LONG).show();
                    }
                }
            };

            //Part 2: upload the photo
            Request request = Request.newUploadPhotoRequest(session, imageSelected, uploadPhotoRequestCallback);
            Bundle parameters = request.getParameters();
            parameters.putString("message", generateString());
            request.setParameters(parameters);
            request.executeAsync();
        }


    }
    
    private String generateString(){
    	
    	double r = Math.random();
    	if(r > 0.0 && r < 0.1) return "I just #resocialized with #resocializer!";
    	else if(r > 0.1 && r < 0.2) return "Talking face to face is fun again with #resocializer!";
    	else if(r > 0.2 && r < 0.3) return "Talking irl is cool again with #resocializer!";
    	else if(r > 0.3 && r < 0.4) return "I'm getting #resocial with #resocializer!";
    	else if(r > 0.4 && r < 0.5) return "Just being my #resocial, authentic self with #resocializer.";
    	else if(r > 0.5 && r < 0.6) return "#resocializing!";
    	else if(r > 0.6 && r < 0.7) return "Got off the computer and #resocialized!";
    	else if(r > 0.7 && r < 0.8) return "Seeing real faces, hearing real voices. #resocializer";
    	else if(r > 0.8 && r < 0.9) return "I put down the phone and #resocialized!";
    	else return "#real #present #resocializer";
    	
    }
    
    private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
        for (String string : subset) {
            if (!superset.contains(string)) {
                return false;
            }
        }
        return true;
    }
    
    private void onSessionStateChange(Session session, SessionState state, Exception exception) { 
	    if (state.isOpened()) {
	    	if (pendingPublishReauthorization && 
	    	        state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
	    	    pendingPublishReauthorization = false;
	    	}
	    }else if (state.isClosed()) {
	    	
	    }
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(PENDING_PUBLISH_KEY, pendingPublishReauthorization);
        uiHelper.onSaveInstanceState(outState);
    }
    
    public void addConversation(int friends){
    	String filePath = getActivity().getExternalFilesDir(null) + "/progress/save_progress.xml";
    	Document doc = openXMLFile(filePath);
    	File file = new File(filePath);
    	Element conversation = doc.createElement("conversation");
    	
    	NodeList nl = doc.getElementsByTagName(((MainActivity)getActivity()).getUser());
    	Node recent = nl.item(0).getLastChild();
    	Log.w("resoxml", "lastchild attributes: " + recent.getAttributes().item(0).toString() + " " + recent.getAttributes().item(1).toString());
    	int id = 0;
    	try{
    		id = Integer.parseInt(recent.getAttributes().getNamedItem("id").getNodeValue()) + 1;
    	}catch(NumberFormatException nfe){
    		Log.w("resoxml", nfe.getMessage());
    		
    	}
    	conversation.setAttribute("id", Integer.toString(id));
    	conversation.setAttribute("friends", Integer.toString(friends));
    	nl.item(0).appendChild(conversation);
    	filePutContents(doc, file);
    }
    
    protected Document openXMLFile(String filepath){
    	Document doc = null;
    	try {
    		File xml = new File(filepath);
    		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    		doc = dBuilder.parse(xml);
    	} catch(Exception e){
    		Log.w("resoxml", e.getMessage());
    	}
    	return doc;
    }
    
    protected void filePutContents(Document doc, File file){
    	try{
    		TransformerFactory tFactory = TransformerFactory.newInstance();
    		Transformer transformer = tFactory.newTransformer();
    		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    		DOMSource source = new DOMSource(doc);
    		StreamResult result = new StreamResult(file);
    		transformer.transform(source, result);
    	}catch(TransformerConfigurationException tce){
    		Log.w("resoxml", tce.getMessage());
    	}catch(TransformerException te){
    		Log.w("resoxml", te.getMessage());
    	}
    }
    
}




