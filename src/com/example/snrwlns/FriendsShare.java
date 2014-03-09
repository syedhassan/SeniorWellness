package com.example.snrwlns;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.snrwlns.threads.DownloadPictures;
import com.example.snrwlns.threads.UpdateUserShare;

public class FriendsShare extends Activity {
	
	/**
	 **  Called when the user confirms sign up.
	 */
	
	private byte[] friends;
	public static final String TAG = "SNRWLNS";
	public static final String PREFS_NAME = "SNRWLNS";
	private SharedPreferences settings;
	private ArrayList<HashMap<String, String>> friendsData;
	private HashMap<String, String> data;
	private ImageButton saveButton;
	int count = 0;

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (!isTaskRoot()) {
		    final Intent intent = getIntent();
		    final String intentAction = intent.getAction();
		    if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) &&
		            intentAction != null && intentAction.equals(Intent.ACTION_MAIN)) {
		        finish();
		    }
		}
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.friendsshare);
	    
	    friends = getIntent().getExtras().getByteArray("friendsData");	
	    {
			ObjectInputStream ois;
			try {
				ois = new ObjectInputStream(new ByteArrayInputStream(friends));
				friendsData = (ArrayList<HashMap<String, String>>) ois.readObject();
			} catch (StreamCorruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	    
		data =  new HashMap<String, String>();
		saveButton = (ImageButton) findViewById(R.id.saveButton);
		saveButton.setClickable(false);
		saveButton.setImageResource(R.drawable.savedisabled346x80);
		
		RelativeLayout TagLayout = (RelativeLayout) findViewById(R.id.TagLayout);
		TagLayout.removeAllViews();
		LinearLayout outerLayout = new LinearLayout(this);
		LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		outerLayout.setLayoutParams(params);
		outerLayout.setOrientation(LinearLayout.VERTICAL);
		outerLayout.removeAllViews();
		for (int i=0; i<friendsData.size();i++) {
			LinearLayout innerLayout = new LinearLayout(this);
			LayoutParams paramsInner = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
			innerLayout.setLayoutParams(paramsInner);
			innerLayout.setPadding(0, 10, 0, 10);
			innerLayout.setOrientation(LinearLayout.HORIZONTAL);
			innerLayout.setTag(i);
			innerLayout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
				 	saveButton.setClickable(true);
				 	saveButton.setImageResource(R.drawable.save);
				 	int position = Integer.parseInt(v.getTag().toString());
				 	ImageView shareImage = (ImageView) v.findViewWithTag("checkImage");
				 	if (friendsData.get(position).get("share").equalsIgnoreCase("share")) {
				 		shareImage.setImageResource(R.drawable.checkbox75x66);
				 		friendsData.get(position).put("share", "unshare");
				 		data.put(friendsData.get(position).get("id"), "unshare");
				 	}
				 	else {
				 		shareImage.setImageResource(R.drawable.checkboxselected75x66);
				 		friendsData.get(position).put("share", "share");
				 		data.put(friendsData.get(position).get("id"), "share");
				 	}
				 	saveButton.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							settings =  getSharedPreferences(PREFS_NAME, 0);
							UpdateUserShare updateShareInfo = new UpdateUserShare(FriendsShare.this, settings.getString("userId", "100"), data, messageFromThread);
							updateShareInfo.start();
						}
					});
				}
			});
			
			String url = friendsData.get(i).get("uri").toString();
	    	String fileName = url.substring(url.lastIndexOf("/")+1);
	    	url = url.replace(" ", "%20");
	    	File imageFile = new File(Environment.getExternalStorageDirectory().getPath()+"/SeniorWellness/"+fileName);
	    	
	    	ImageView friendImage = new ImageView(this);
	    	LayoutParams paramsFriendImage = new LinearLayout.LayoutParams(100,100);
	    	paramsFriendImage.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
			friendImage.setLayoutParams(paramsFriendImage);
			friendImage.setAdjustViewBounds(true);
			friendImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
			BitmapFactory.Options options = new BitmapFactory.Options();
			HttpGet httpRequest = null;
	        try {
	        	Log.i(TAG, "Image File exists? = "+imageFile.exists()+" url = "+url+" FileName = "+fileName);
	        	if (imageFile.exists()) {
	        		options.inJustDecodeBounds = true;
	        		BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath()+"/SeniorWellness/"+fileName, options);
	        		Boolean scaleByHeight = Math.abs(options.outHeight - 100) >= Math.abs(options.outWidth - 100);
	        		if(options.outHeight * options.outWidth * 2 >= 16384){
	        		    double sampleSize = scaleByHeight? options.outHeight / 100: options.outWidth / 100;
	        		    options.inSampleSize = (int)Math.pow(2d, Math.floor(Math.log(sampleSize)/Math.log(2d)));
	        		}
	        		options.inJustDecodeBounds = false; options.inTempStorage = new byte[1024];options.inPurgeable=true;
	        		Bitmap bm = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath()+"/SeniorWellness/"+fileName, options);
	        		friendImage.setImageBitmap(bm);
	        	}
	        	else {
		        	httpRequest = new HttpGet(url);
		        	DefaultHttpClient httpclient = new DefaultHttpClient();
		            HttpResponse response = (HttpResponse) httpclient.execute(httpRequest);
	
		            HttpEntity entity = response.getEntity();
		            BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity); 
		            InputStream instream = bufHttpEntity.getContent();
		            
		            options.inJustDecodeBounds = true;
	        		BitmapFactory.decodeStream(instream, null, options);
	        		Boolean scaleByHeight = Math.abs(options.outHeight - 100) >= Math.abs(options.outWidth - 100);
	        		if(options.outHeight * options.outWidth * 2 >= 16384){
	        		    double sampleSize = scaleByHeight? options.outHeight / 100: options.outWidth / 100;
	        		    options.inSampleSize = (int)Math.pow(2d, Math.floor(Math.log(sampleSize)/Math.log(2d)));
	        		}
	        		options.inJustDecodeBounds = false; options.inTempStorage = new byte[1024];  options.inPurgeable=true;
		            Bitmap bm = BitmapFactory.decodeStream(instream, null, options);
		            friendImage.setImageBitmap(bm);

					DownloadPictures pictures = new DownloadPictures(FriendsShare.this, url, fileName);
					pictures.start();
	        	}
	        } catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			TextView friendsName = new TextView(this);
			LayoutParams friendsNameParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
			friendsNameParams.weight = 60;
			friendsNameParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
			friendsName.setLayoutParams(friendsNameParams);
			friendsName.setPadding(10, 0, 0, 0);
			friendsName.setTextSize(20);
			friendsName.setTypeface(null, Typeface.BOLD);
			friendsName.setTextColor(Color.BLACK);
			friendsName.setText(friendsData.get(i).get("name").toString());
			
			ImageView checkImage = new ImageView(this);
			checkImage.setTag("checkImage");
			LayoutParams checkImageParams = new LinearLayout.LayoutParams(37,33);
			checkImageParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
			checkImage.setLayoutParams(checkImageParams);
			checkImage.setImageResource(R.drawable.checkboxselected75x66);
			if (friendsData.get(i).get("share").toString().equalsIgnoreCase("share")) {
				checkImage.setImageResource(R.drawable.checkboxselected75x66);
			}
			else {
				checkImage.setImageResource(R.drawable.checkbox75x66);
			}
			innerLayout.addView(friendImage);
			innerLayout.addView(friendsName);
			innerLayout.addView(checkImage);
			LinearLayout border = new LinearLayout(FriendsShare.this);
			LinearLayout.LayoutParams paramsInner1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,1);
			border.setBackgroundColor(Color.BLACK);
			border.setLayoutParams(paramsInner1);
			outerLayout.addView(innerLayout);
			outerLayout.addView(border);
		}
		TagLayout.addView(outerLayout);
	}
	
	private Handler messageFromThread = new Handler() {
    	@Override
    	public void handleMessage(Message msg) {
    		Log.i(TAG, "In Handler for FriendsShare thead, result= "+msg.what+" arg1 = "+msg.arg1);
    		if (msg.what == 200 & msg.arg1 == 1) {
    			Intent resultIntent = new Intent();
				Bundle bundle = new Bundle();
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
			    ObjectOutputStream oos;
				try {
					oos = new ObjectOutputStream(bos);
					oos.writeObject(friendsData);
				    oos.flush();
				    oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			    
				bundle.putByteArray("friendsData", bos.toByteArray());
				resultIntent.putExtras(bundle);
				setResult(Activity.RESULT_OK, resultIntent);
    			saveFriendsObject(bos.toByteArray());
    			finish();
    		}
    		else {
    			LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
				View layout = inflater.inflate(R.layout.toasttext, (ViewGroup) findViewById(R.id.toast_layout_root));
				TextView text = (TextView) layout.findViewById(R.id.text);
				text.setText("Error occurred in updating your information. Please try again!!!");
				Toast toast = new Toast(getApplicationContext());
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				toast.setDuration(1000);
				toast.setView(layout);
				toast.show();
    		}
    	}
	};
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	Intent resultIntent = new Intent();
			Bundle bundle = new Bundle();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
		    ObjectOutputStream oos;
			try {
				oos = new ObjectOutputStream(bos);
				oos.writeObject(friendsData);
			    oos.flush();
			    oos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			bundle.putByteArray("friendsData", bos.toByteArray());
			resultIntent.putExtras(bundle);
			setResult(Activity.RESULT_OK, resultIntent);
			finish();
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
    private boolean saveFriendsObject(byte[] bytes) {
    	
    	SharedPreferences settings =  getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("friendsDataLength", bytes.length);
        editor.commit();
        
    	FileOutputStream fos;
		try {
			fos = openFileOutput("friendData", Context.MODE_PRIVATE);
			fos.write(bytes);
	    	fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	return true;
    }
}
