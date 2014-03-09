package com.example.snrwlns;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.snrwlns.c2dm.C2DMessaging;
import com.example.snrwlns.threads.GetEventDataObject2;
import com.example.snrwlns.threads.GetEventTaggingObject;
import com.example.snrwlns.threads.GetFriendEventDataObject;
import com.example.snrwlns.threads.GetMainData;
import com.example.snrwlns.threads.GetMeDataObject;
import com.example.snrwlns.threads.GetThreadsData;

public class Main extends FragmentActivity{
    /** Called when the activity is first created. */
	public static final String TAG 													= "SNRWLNS";
	public static final String PREFS_NAME 											= "SNRWLNS";
	public static final String SENDERS_EMAIL_ID										= "snrwlns.c2dm@gmail.com";
	public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE 					= 0;
	public static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE 					= 1;
	public static final int CONFIRM_SIGNUP_EVENT 									= 11;
	public static final int CONFIRM_SIGNOUT_EVENT 									= 12;
	public static final int OPEN_FRIEND_VIEW_FROM_EVENT								= 21;
	public static final int OPEN_CUSTOM_MENU										= 23;
	public static final int MENU_OPEN_MY_ACCOUNT 									= 2;
	public static final int MENU_OPEN_FRIENDS 										= 3;
	public static final int MENU_OPEN_NOTIFICATIONS 								= 4;
	public static final int OPEN_FRIEND_EVENT 										= 5;
	public static final int SEARCH_ACTIVITY 										= 6;
	public static final int DIALOG_REALLY_EXIT_ID 									= 7;
	public static final int MEDIA_IMAGE												= 0;
	public static final int MEDIA_VIDEO 											= 1;
	private String cameraMediaFileName, cameraMediaFilePath;
	private int cameraMediaType;
	private Uri imageUri, videoUri;
	private ProgressDialog pd, pd1;
	private SharedPreferences settings;
	private QuickActionWindow window;
	private QuickActionWindowF windowF;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	settings =  getSharedPreferences(PREFS_NAME, 0);
    	if (getSharedPreferences("com.google.android.c2dm", 0).getString("dm_registration", "").equalsIgnoreCase("")) {
    		new Thread() {
    			public void run() {
    				try{
    					C2DMessaging.register(Main.this, SENDERS_EMAIL_ID);
    				} catch (Exception e) {
    					Log.e(TAG, e.getMessage());
    				}
    			}
    		}.start();
    	}

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        ImageView headerTextLayout = (ImageView) findViewById(R.id.headerTextLayout);
        headerTextLayout.setLongClickable(true);
        headerTextLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
//				Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment);
//				if (!fragment.getTag().equalsIgnoreCase("MainFragmet")) {
//					pd = new ProgressDialog(Main.this);
//		    		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//		            pd.setMessage("Working...");
//		            pd.setIndeterminate(true);
//		            pd.setCancelable(false);
//		            pd.show();
//		            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
//		            
//					GetThreadsData thread = new GetThreadsData(Main.this, ""+settings.getInt("eventIdDetailsFragment", 100), messageFromChildThread3);
//					thread.start();
//				}
//				GetMainData thread = new GetMainData(Main.this, settings.getString("userId", ""), messageFromChildThread);
//				thread.start();
				
				SharedPreferences.Editor editor = settings.edit();
		        editor.putString("filterListView", null);
		        editor.commit();
				
				SharedPreferences settings =  getSharedPreferences(PREFS_NAME, 0);
		        GetMainData gmd = new GetMainData(Main.this, settings.getString("userId", "100"), messageFromChildThreadH);
		        gmd.start();
				
				 pd = new ProgressDialog(Main.this);
				 pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		         pd.setMessage("Working...");
		         pd.setIndeterminate(true);
		         pd.setCancelable(false);
		         pd.show();
			}
		});
        
        ImageView cameraIcon = (ImageView) findViewById(R.id.cameraIcon);
        cameraIcon.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i(TAG, "Starting the camera/camcorder app");
				
		    	int[] xy = new int[2];
		    	v.getLocationInWindow(xy);
		    	Rect rect = new Rect(xy[0], xy[1], xy[0]+v.getWidth(), xy[1]+v.getHeight());
		    	window = new QuickActionWindow(Main.this, v, rect, R.layout.quickactionnew);
		    	window.show();
		    	
		    	window.getCameraContext().setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
			           String fileName = "new-photo-name.jpg";
			           ContentValues values = new ContentValues();
			           values.put(MediaStore.Images.Media.TITLE, fileName);
			           values.put(MediaStore.Images.Media.DESCRIPTION,"Image capture by camera");
			           imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
			           Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			           intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			           intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
			           intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			           startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
					}
				});
		    	
		    	window.getVideoContext().setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
			           String fileName = "new-photo-name.3gp";
			           ContentValues values = new ContentValues();
			           values.put(MediaStore.Video.Media.TITLE, fileName);
			           values.put(MediaStore.Video.Media.DESCRIPTION,"Video capture by camera");
			           videoUri = getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
			           Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
			           intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
			           intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
			           startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
						
					}
				});
			}
		});
        
        ImageView searchIcon = (ImageView) findViewById(R.id.searchIcon);
        searchIcon.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				Intent searchActivity = new Intent(Main.this, SearchEvents.class);
				startActivity(searchActivity);
				
			}
		});
        
        if (!settings.getString("serverLink", "demoServer").equalsIgnoreCase("demoServer")) {
        	LinearLayout trialConcept = (LinearLayout) findViewById(R.id.trialConcept);
        	trialConcept.setVisibility(View.GONE);
    	}
	    Intent intent = getIntent();
	    if (Intent.ACTION_SEND.equals(intent.getAction())) {
	    	Bundle extras = intent.getExtras();
	    	if (extras.containsKey(Intent.EXTRA_STREAM)) {
	    		Log.i(TAG, "Data from camera/camcorder is of type = "+intent.getType());
	    		SharedPreferences.Editor editor = settings.edit();
				editor.putBoolean("mediaFromGallery", true);
	        	editor.commit();
	    		if (intent.getType().equalsIgnoreCase("image/jpeg") | intent.getType().equalsIgnoreCase("image/png")) {
	    			cameraMediaType= MEDIA_IMAGE;
	    		}
	    		else {
	    			cameraMediaType= MEDIA_VIDEO;
	    		}
    			try {
	                Uri uri = (Uri) extras.getParcelable(Intent.EXTRA_STREAM);
	                File file = this.convertImageUriToFile(uri, this);
	    	    	cameraMediaFileName = file.getName();
	    	    	cameraMediaFilePath = file.getPath();
	    	    	Log.i(TAG, "File Name = "+cameraMediaFileName+" and File path = "+cameraMediaFilePath+" and cameraMedia Type = "+cameraMediaType);
	    	    	tagMedia();
	                return;
	            } catch (Exception e) {
	            	e.printStackTrace();
	                Log.e(TAG, e.toString());
	            }
	        }
	    	else {
	    		LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
				View layout = inflater.inflate(R.layout.toasttext, (ViewGroup) findViewById(R.id.toast_layout_root));
				TextView text = (TextView) layout.findViewById(R.id.text);
				text.setText("This type of media is not supported in the SignUp app");
				Toast toast = new Toast(getApplicationContext());
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				toast.setDuration(3000);
				toast.setView(layout);
				toast.show();
	    	}
	    	
	    } else if (Intent.ACTION_MAIN.equals(getIntent().getAction())) {
	    	Log.i(TAG, "Normal start");
	    }
	    
        if (settings.getBoolean("searchEventItem", false)) {
			GetEventDataObject2 obj = new GetEventDataObject2(this, settings.getString("userId", ""), ""+settings.getInt("searchEventId", 0), handler);
        	obj.start();
		}
        else {
        	GetMainData gmd = new GetMainData(this, settings.getString("userId", "100"), messageFromChildThread);
            gmd.start();
    		Log.i(TAG, "Staring the Process Dialog");
    		pd = new ProgressDialog(Main.this);
    		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setMessage("Working...");
            pd.setIndeterminate(true);
            pd.setCancelable(false);
            pd.show();
        }
    }
    
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	Log.d(TAG, "Main ActivityResult = "+requestCode);
    	if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
    		if (window.isShowing())
    			window.dismiss();
    	    if (resultCode == RESULT_OK) {
    	        //use imageUri here to access the image
    	    	File file = this.convertImageUriToFile(imageUri, this);
    	    	cameraMediaFileName = file.getName();
    	    	cameraMediaFilePath = file.getPath();
    	    	cameraMediaType= MEDIA_IMAGE;
    	    	tagMedia();

    	    } else if (resultCode == RESULT_CANCELED) {
    	    	Log.i(TAG, "User cancelled it and didn't take a picture");
    	    } else {
    	    	Log.i(TAG, "User cancelled it and didn't take a picture");
    	    }
    	}
    	else if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
    		if (window.isShowing())
    			window.dismiss();
    	    if (resultCode == RESULT_OK) {
    	    	File tmpFile = null;
    	    	try {
    	    	    AssetFileDescriptor videoAsset = getContentResolver().openAssetFileDescriptor(data.getData(), "r");
    	    	    
    	    	    FileInputStream fis = videoAsset.createInputStream();
    	    	    File directory = new File(Environment.getExternalStorageDirectory() + "/video");
    	    	    directory.mkdirs();
    	    	    String videoFileName = System.currentTimeMillis()+".3gp";
    	    	    tmpFile = new File(directory,videoFileName); 
    	    	    FileOutputStream fos = new FileOutputStream(tmpFile);
    	    	    byte[] buf = new byte[1024];
    	    	    int len;
    	    	    while ((len = fis.read(buf)) > 0) {
    	    	        fos.write(buf, 0, len);
    	    	    }       
    	    	    fis.close();
    	    	    fos.close();
    	    	  } catch (IOException e) {
    	    	    e.printStackTrace();
    	    	  }
    	    	
    	    	Log.i(TAG, "URI = "+videoUri.getPath());
    	    	cameraMediaFileName = tmpFile.getName();
    	    	cameraMediaFilePath = tmpFile.getPath();
    	    	Log.i(TAG, "Name = "+cameraMediaFileName);
    	    	Log.i(TAG, "Path = "+cameraMediaFilePath);
    	    	cameraMediaType = MEDIA_VIDEO;
    	    	tagMedia();

    	    } else if (resultCode == RESULT_CANCELED) {
    	    	Log.i(TAG, "User cancelled it and didn't take a video");
    	    } else {
    	    	Log.i(TAG, "User cancelled it and didn't take a video");
    	    }
    	}
    	else if (requestCode == MENU_OPEN_FRIENDS) {
    	    if (resultCode == RESULT_OK) {
    	    	byte[] friendsData = data.getByteArrayExtra("friendsData");
    	    	Log.i(TAG, "Friends back data = "+friendsData.length);

    	    } else if (resultCode == RESULT_CANCELED) {
    	    	LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
				View layout = inflater.inflate(R.layout.toasttext, (ViewGroup) findViewById(R.id.toast_layout_root));
				TextView text = (TextView) layout.findViewById(R.id.text);
				text.setText("Friends Share was not updated");
				Toast toast = new Toast(getApplicationContext());
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				toast.setDuration(1000);
				toast.setView(layout);
				toast.show();
    	    } else {
    	    	LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
				View layout = inflater.inflate(R.layout.toasttext, (ViewGroup) findViewById(R.id.toast_layout_root));
				TextView text = (TextView) layout.findViewById(R.id.text);
				text.setText("Friends Share was not updated");
				Toast toast = new Toast(getApplicationContext());
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				toast.setDuration(1000);
				toast.setView(layout);
				toast.show();
    	    }
    	}
    	else if (requestCode == OPEN_FRIEND_VIEW_FROM_EVENT) {
    		Log.d(TAG, "Should open a friend view after the user clicked his/her name from the pop "+resultCode);
    		if (resultCode == RESULT_OK) {
    			byte[] friendsData = data.getByteArrayExtra("friendData");
    			String friendUserId = data.getStringExtra("friendUserId");
    			Log.i(TAG, "Friends userId = "+friendUserId);
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				Fragment friendsFragment = new FriendsFragment();

    	    	Bundle friendsFragmentBundle = new Bundle();
    	    	friendsFragmentBundle.putByteArray("friendsData", friendsData);
    	    	friendsFragmentBundle.putString("friendUserId", friendUserId);
    	    	friendsFragment.setArguments(friendsFragmentBundle);
    	    	
    	        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
    	        ft.addToBackStack(null);
    	        ft.replace(R.id.fragment, friendsFragment, "FriendsFragment");
    	        ft.setBreadCrumbShortTitle("FriendsFragment");
    	        ft.commit();
    			
    		}
    	}
    	else if( requestCode == CONFIRM_SIGNUP_EVENT ) {
    		Log.d(TAG, "Confirmed Signup = "+resultCode);
    		if (resultCode == RESULT_OK) {
    			int count = data.getIntExtra("count", -1);
    			String eventId = data.getStringExtra("eventId");
    			Log.i(TAG, "Event Id = "+eventId+" and Count = "+count);
    			
    			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
    			GetMeDataObject obj = new GetMeDataObject(null, Main.this, settings.getString("userId", "100"), null);
    			obj.start();
    		}
        }
    	else if( requestCode == CONFIRM_SIGNOUT_EVENT ) {
    		Log.d(TAG, "Confirmed Signout = "+resultCode);
    		if (resultCode == RESULT_OK) {
    			int count = data.getIntExtra("count", -1);
    			String eventId = data.getStringExtra("eventId");
    			Log.i(TAG, "Event Id = "+eventId+" and Count = "+count);
    			GetMeDataObject obj = new GetMeDataObject(null, Main.this, settings.getString("userId", "100"), null);
    			obj.start();

    		}
        }
    	else if (requestCode == MENU_OPEN_NOTIFICATIONS) {
    	    if (resultCode == RESULT_OK) {
    	    	byte[] friendsData = data.getByteArrayExtra("notificationData");
    	    	Log.i(TAG, "Notification back data = "+friendsData.length);

    	    } else if (resultCode == RESULT_CANCELED) {
    	    	LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
				View layout = inflater.inflate(R.layout.toasttext, (ViewGroup) findViewById(R.id.toast_layout_root));
				TextView text = (TextView) layout.findViewById(R.id.text);
				text.setText("Video was not taken");
				Toast toast = new Toast(getApplicationContext());
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				toast.setDuration(1000);
				toast.setView(layout);
				toast.show();
    	    } else {
    	    	LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
				View layout = inflater.inflate(R.layout.toasttext, (ViewGroup) findViewById(R.id.toast_layout_root));
				TextView text = (TextView) layout.findViewById(R.id.text);
				text.setText("Video was not taken");
				Toast toast = new Toast(getApplicationContext());
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				toast.setDuration(1000);
				toast.setView(layout);
				toast.show();
    	    }
    	}
    	else if (requestCode == SEARCH_ACTIVITY) {
    		if (pd.isShowing())
    			pd.dismiss();
    		Log.i(TAG, "Search Activity back data = ");
    	    if (resultCode == RESULT_OK) {
    	    	Log.i(TAG, "Search Activity back data = ");
    	    	byte[] data1 = data.getByteArrayExtra("data");
    	    	String eventId = data.getStringExtra("eventId");
		    	Fragment eventDetailsFragment = new EventDetailsFragment();
		    	Bundle eventDetailsBundle = new Bundle();
		    	eventDetailsBundle.putByteArray("data", data1);
		    	eventDetailsBundle.putBoolean("fromMe", true);
		    	eventDetailsBundle.putString("eventId", eventId);
		    	eventDetailsFragment.setArguments(eventDetailsBundle);
		    	FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		    	ft.addToBackStack(null);
		    	ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		        ft.replace(R.id.fragment, eventDetailsFragment, "EventDetailsFragment");
		        ft.setBreadCrumbShortTitle("EventDetailsFragment");
		        ft.commit();
    	    } else if (resultCode == RESULT_CANCELED) {
    	        Log.i(TAG, "User pressed the hard back button");
    	    } else {
    	    	LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
				View layout = inflater.inflate(R.layout.toasttext, (ViewGroup) findViewById(R.id.toast_layout_root));
				TextView text = (TextView) layout.findViewById(R.id.text);
				text.setText("Something went wrong with the search activity");
				Toast toast = new Toast(getApplicationContext());
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				toast.setDuration(1000);
				toast.setView(layout);
				toast.show();
    	    }
    	}
    	else if (requestCode == OPEN_CUSTOM_MENU) {
    		//Log.v(TAG, "Back from custom menu, check if the filter has been applied, Filter = "+data.getStringExtra("filter")+" Refresh = "+data.getStringExtra("refresh"));
    		if (resultCode == RESULT_OK) {
    			String flag = data.getStringExtra("filter");
    			String refresh = data.getStringExtra("refresh");
    			if (flag != null) {
    				
    				Log.i(TAG, "Filter applied was = "+flag);
    		        SharedPreferences.Editor editor = settings.edit();
    		        editor.putString("filterListView", flag);
    		        
    		        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment);
    				if (!fragment.getTag().equalsIgnoreCase("MainFragmet")) {
    					getSupportFragmentManager().popBackStackImmediate();
    					editor.putString("currentTab", "eventsTab");
    				}
    				editor.commit();
    			}
    			else if (refresh != null) {
    				
    				Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment);
    				Log.i(TAG, "Refreshing = "+fragment.getTag());
    				if (fragment.getTag().equalsIgnoreCase("EventDetailsFragment")) {
    					Log.i(TAG, "EventDetailsFragmet should be refreshed");
    					pd = new ProgressDialog(Main.this);
    		    		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    		            pd.setMessage("Refreshing Event Details. Please wait...");
    		            pd.setIndeterminate(true);
    		            pd.setCancelable(false);
    		            pd.show();
    		            
    					GetThreadsData thread = new GetThreadsData(Main.this, ""+settings.getInt("eventIdDetailsFragment", 100), messageFromRefreshEventDetails);
    					thread.start();
    				}
    				else if (fragment.getTag().equalsIgnoreCase("MainFragment")) {
    					Log.i(TAG, "MainFragmet should be refreshed and the current tab = "+settings.getString("currentTab", "eventsTab"));
    					pd = new ProgressDialog(Main.this);
    		    		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    		    		if (settings.getString("currentTab", "eventsTab").equalsIgnoreCase("eventsTab")) {
    		    			pd.setMessage("Refreshing Event List. Please wait..");
    		    		}
    		    		else if (settings.getString("currentTab", "eventsTab").equalsIgnoreCase("friendsTab")) {
    		    			pd.setMessage("Refreshing Friends List View. Please wait...");
    		    		}
    		    		else if (settings.getString("currentTab", "eventsTab").equalsIgnoreCase("meTab")) {
    		    			pd.setMessage("Refreshing your events view. Please wait...");
    		    		}
    		    		else {
    		    			Log.e(TAG, "YOU SHOULDN\"T HAVE COME HERE ON REFRESHING!!! STRANGE");
    		    			pd.setMessage("Refreshing Event List. Please wait..");
    		    		}
    		    		
    		            pd.setIndeterminate(true);
    		            pd.setCancelable(false);
    		            pd.show();
    		            
    		            GetMainData thread = new GetMainData(Main.this, settings.getString("userId", ""), messageFromRefreshMainScreen);
    					thread.start();
    				}
    				else {
    					Log.i(TAG, "FriendsFragment should be refreshed for friend id = "+settings.getString("friendsIdFriendsFragment", ""));
    					pd = new ProgressDialog(Main.this);
    		    		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    		    		pd.setMessage("Refreshing Friend's Events List. Please wait...");
    		            pd.setIndeterminate(true);
    		            pd.setCancelable(false);
    		            pd.show();
    		            
    		            GetFriendEventDataObject obj = new GetFriendEventDataObject(Main.this, settings.getString("friendsIdFriendsFragment", ""), messageFromFriendsThread);
    				 	obj.start();
    				}
    			}
    			else {
    				Log.i(TAG, "No Filter was applied");
    			}
    		}
    	}
    	else if (requestCode == OPEN_FRIEND_EVENT) {
    		Log.v(TAG, "Back from Friend Event ");
    	    if (resultCode == RESULT_OK) {
    	    	Log.i(TAG, "Search Activity back data = ");
    	    } else if (resultCode == RESULT_CANCELED) {
    	    } else {
    	    }
    	}
    }
	
    private Handler messageFromRefreshEventDetails = new Handler() {
		@Override
    	public void handleMessage(Message msg) {
    		Log.d(TAG, "Data being return from ALL threads of Main scree, messageFromChildThread3");
    		if (pd != null)
    			if (pd.isShowing())
    				pd.dismiss();
    		Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment);
			fragment.onResume();
		}
    };
    private Handler messageFromRefreshMainScreen = new Handler() {
		@Override
    	public void handleMessage(Message msg) {
    		Log.d(TAG, "Data being return from ALL threads of Main scree, messageFromChildThread2");
    		if (pd != null)
    			if (pd.isShowing())
    				pd.dismiss();
    		Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment);
			fragment.onResume();
		}
    };
	private Handler messageFromFriendsThread = new Handler() {
    	@Override
    	public void handleMessage(Message msg) {
    		Log.i(TAG, "In Handler for friend data thead, result = "+msg.what);
			pd.dismiss();
    		if (msg.what == 389) {
				Log.i(TAG, "Refreshing the Friends Event activity");
				Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment);
				fragment.onResume();
    		}
    	}
    };
    
    public File convertImageUriToFile (Uri imageUri, Activity activity)  {
    	Cursor cursor = null;
    	try {
    	    String [] proj={MediaStore.Images.Media.DATA, MediaStore.Images.Media.ORIENTATION};
    	    cursor = activity.managedQuery( imageUri,
    	            proj, // Which columns to return
    	            null,       // WHERE clause; which rows to return (all rows)
    	            null,       // WHERE clause selection arguments (none)
    	            null); // Order-by clause (ascending by name)
    	    int file_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
    	    if (cursor.moveToFirst()) {
    	        return new File(cursor.getString(file_ColumnIndex));
    	    }
    	    return null;
    	} finally {
    	    if (cursor != null) {
    	        cursor.close();
    	    }
    	}
    }
    
    private void tagMedia() {
		// Tagging a pic
    	Time time = new Time();
    	time.setToNow();
    	String phoneTime = time.hour+":"+time.minute+":"+time.second;
    	String phoneDate = time.year+"-"+time.month+"-"+time.monthDay;
    	
    	SharedPreferences settings =  getSharedPreferences(PREFS_NAME, 0);
    	String userId = settings.getString("userId", "");
    	GetEventTaggingObject tag = new GetEventTaggingObject(this, phoneTime, phoneDate, userId, messageFromTagThread);
    	tag.start();
    	Log.i(TAG, "Staring the Process Dialog1");
		pd1 = new ProgressDialog(Main.this);
		pd1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd1.setMessage("Working...");
        pd1.setIndeterminate(true);
        pd1.setCancelable(false);
        pd1.show();
		
	}
    
	private Handler messageFromTagThread = new Handler() {
    	@Override
    	public void handleMessage(Message msg) {
    		Log.i(TAG, "In Handler for diary thead, result= "+msg.what);
			pd1.dismiss();
    		if (msg.what == 200 & msg.arg1 == 117) {
    			byte[] data = msg.getData().getByteArray("eventTagging");
				Log.i(TAG, "Open the Tagging activity -->"+data.length);
				Intent taggingEvent = new Intent(Main.this, EventTagging.class);
				Bundle bundle = new Bundle();
				SharedPreferences settings =  getSharedPreferences(PREFS_NAME, 0);
				bundle.putString("userId",settings.getString("userId", "100"));
//				if (sendData != null)
//					bundle.putString("mediaType", sendData.contentType);
//				else {
					bundle.putString("fileName", cameraMediaFileName);
					bundle.putString("filePath", cameraMediaFilePath);
					bundle.putInt("fileType", cameraMediaType);
//				}
				bundle.putByteArray("data", data);
				taggingEvent.putExtras(bundle);
				startActivity(taggingEvent);
    		}
    	}
    };

    
	private Handler messageFromChildThread = new Handler() {
		@Override
    	public void handleMessage(Message msg) {
    		Log.i(TAG, "In Handler for Main class thread, result = "+msg.what);
    		if (msg.what == 200 & msg.arg1 == 121) {
    			byte[] data = msg.getData().getByteArray("mainData");
    			byte[] friends = msg.getData().getByteArray("friendsData");
    			byte[] me = msg.getData().getByteArray("meData");
    			Log.i(TAG, "Size = "+friends.length);
    			saveFriendsObject(friends);
    			
    			SharedPreferences settings =  getSharedPreferences(PREFS_NAME, 0);
    	        SharedPreferences.Editor editor = settings.edit();
    	        editor.putInt("friendsDataLength", friends.length);
    	        editor.commit();

    	    	Fragment mainFragment = new MainFragment();
    	    	Bundle mainFragmentBundle = new Bundle();
    	    	mainFragmentBundle.putByteArray("mainData", data);
    	    	mainFragmentBundle.putByteArray("friendsData", friends);
    	    	mainFragmentBundle.putByteArray("meData", me);
    	    	mainFragment.setArguments(mainFragmentBundle);
    	    	
    	    	FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    	        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
    	        ft.add(R.id.fragment, mainFragment, "MainFragment");
    	        ft.setBreadCrumbShortTitle("MainFragment");
    	        ft.show(mainFragment);
    	        ft.commit();
    		}
    		pd.dismiss();
    	}
    };
    
//    private Handler messageFromChildThread2 = new Handler() {
//		@Override
//    	public void handleMessage(Message msg) {
//    		Log.i(TAG, "In Handler, messageFromChildThread2, for Main class thread, result = "+msg.what);
//    		if (msg.what == 200 & msg.arg1 == 121) {
//    			Log.d(TAG, "Refresh was successful");
//    			Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment);
//    			byte[] data = msg.getData().getByteArray("mainData");
//    			byte[] friends = msg.getData().getByteArray("friendsData");
//    			byte[] me = msg.getData().getByteArray("meData");
//    			Bundle fragmentBundle = new Bundle();
//    			fragmentBundle.putByteArray("mainData", data);
//    			fragmentBundle.putByteArray("friendsData", friends);
//    			fragmentBundle.putByteArray("meData", me);
//    			fragment.setArguments(fragmentBundle);
//				fragment.onResume();
//    		}
//    		pd.dismiss();
//    	}
//    };
    
    
    @Override
    public void onResume() {
    	
    	Log.i(TAG, "On Resuming MAIN.java");
    	Log.i(TAG, "Check if we resume after taging a picture from gallery = "+settings.getBoolean("mediaFromGallery", false));
    	super.onResume();
    	SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
    	
		if (settings.getBoolean("newNotificationMessage", false) | settings.getBoolean("newNotificationFriendSignUp", false) | settings.getBoolean("newNotificationEvent", false)) {

			SharedPreferences.Editor editor = settings.edit();
			Log.i(TAG, "Notification for a New Friend signup = "+settings.getBoolean("newNotificationFriendSignUp", false));
			Log.i(TAG, "Notification for a New Message = "+settings.getBoolean("newNotificationMessage", false));
			Log.i(TAG, "Notification for a Event Reminder = "+settings.getBoolean("newNotificationEvent", false));
			editor.putBoolean("newNotificationMessage", false);
        	editor.putBoolean("newNotificationFriendSignUp", false);
        	editor.putBoolean("newNotificationEvent", false);
        	editor.commit();

        	GetEventDataObject2 obj = new GetEventDataObject2(this, settings.getString("userId", ""), ""+settings.getInt("newNotificationPosition", 0), handler);
        	obj.start();
		}
		else if (settings.getBoolean("mediaFromGallery", false)) {
			Log.i(TAG, "Starting differently, as we first got hte picture and now we show the event");
			GetMainData gmd = new GetMainData(this, settings.getString("userId", "100"), messageFromChildThread);
            gmd.start();
    		Log.i(TAG, "Staring the Process Dialog after we receive a photo from gallery");
    		pd = new ProgressDialog(Main.this);
    		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setMessage("Working...");
            pd.setIndeterminate(true);
            pd.setCancelable(false);
            pd.show();
            SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean("mediaFromGallery", false);
        	editor.commit();
		}
    }
    
	private Handler handler = new Handler() {
    	@Override
    	public void handleMessage(Message msg) {
    		Log.i(TAG, "MAIN FRAGMENT: In Handler for FriendsFragment thead, result= "+msg.arg1+" hand");
    		if (pd1 != null)
    			if (pd1.isShowing())
    				pd1.dismiss();
    		if (msg.what == 634	) {
    			byte[] data = msg.getData().getByteArray("eventData");
		    	Fragment eventDetailsFragment = new EventDetailsFragment();
		    	Bundle eventDetailsBundle = new Bundle();
		    	eventDetailsBundle.putByteArray("data", data);
		    	eventDetailsBundle.putBoolean("fromMe", true);
		    	eventDetailsBundle.putString("eventId", (String)msg.obj);
		    	eventDetailsFragment.setArguments(eventDetailsBundle);
		    	FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		    	ft.addToBackStack(null);
		    	ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		    	getSupportFragmentManager().findFragmentByTag("EventDetailsFragment");
		    	
		        ft.replace(R.id.fragment, eventDetailsFragment, "EventDetailsFragment");
		        ft.setBreadCrumbShortTitle("EventDetailsFragment");
		        ft.commit();
    		}
    	}
    };
    
    private boolean saveFriendsObject(byte[] friends) {
    	
    	
    	FileOutputStream fos;
		try {
			fos = openFileOutput("friendData", Context.MODE_PRIVATE);
			fos.write(friends);
	    	fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	return true;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
        	Intent customMenu = new Intent(this, CustomMenu.class);
        	startActivityForResult(customMenu, OPEN_CUSTOM_MENU);
        	overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up );
 	    	return true; //always eat it!
        }
        return super.onKeyDown(keyCode, event);
    }
    
	@Override
	public void onBackPressed() {
		int count = getSupportFragmentManager().getBackStackEntryCount();
		Log.i(TAG, "onBackPressed");
		Log.i(TAG, "Count = "+count);
		if (settings.getBoolean("searchEventItem", false)) {
			Log.i(TAG, "Should have come here");
			Main.this.finish();
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean("searchEventItem", false);
			editor.commit();
			finish();
		}
		else {

			if (count ==0) {
				showDialog(DIALOG_REALLY_EXIT_ID);
			}
			else {
				Log.i(TAG, ""+getSupportFragmentManager().popBackStackImmediate());
			}
		}
		 
		//showDialog(DIALOG_REALLY_EXIT_ID);
	    return;
	}

    
    @Override
    protected Dialog onCreateDialog(int id) {
        final Dialog dialog;
        switch(id) {
        case DIALOG_REALLY_EXIT_ID:
            dialog = new AlertDialog.Builder(this).setMessage(
                                "Are you sure you want to exit?")
            .setCancelable(false)
            .setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
        			SharedPreferences.Editor editor = settings.edit();
        	        editor.putString("filterListView", null);
        	        editor.commit();
                    Main.this.finish();
                }
            })
            .setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            }).create();
            break;
        default:
            dialog = null;
        }
        return dialog;
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig){        
        super.onConfigurationChanged(newConfig);
        
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        	((LinearLayout) findViewById(R.id.mainBackGround)).setBackgroundResource(R.drawable.wplandscape854x480);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
        	((LinearLayout) findViewById(R.id.mainBackGround)).setBackgroundResource(R.drawable.wpportrait480x854);
        }
        
    }

	public void myClickHandler(View v) {/*
		LinearLayout outerLayout = (LinearLayout) v.getParent();
		Log.i(TAG, "Child count = "+outerLayout.getChildCount());
		Log.i(TAG, "Condition 1 = "+(outerLayout.getChildAt(0)==null));
		Log.i(TAG, "To String = "+outerLayout.getChildAt(0).toString());
		
		LinearLayout firstLayout = (LinearLayout) outerLayout.getChildAt(0);
		Log.i(TAG, "Condition 2 = "+(outerLayout.getChildAt(1)==null));
		Log.i(TAG, "To String = "+outerLayout.getChildAt(1).toString());
		
		LinearLayout secondLayout = (LinearLayout) outerLayout.getChildAt(0);
		Log.i(TAG, "Condition 3 = "+(outerLayout.getChildAt(1)==null));
		Log.i(TAG, "To String = "+outerLayout.getChildAt(1).toString());
		
//		ImageView imageView = (ImageView)outerLayout.getChildAt(0);
//		imageView.setBackgroundColor(Color.RED);
//		imageView.setPadding(1,1,1,1);

	*/}
	
	public void handleAddressActivity(View v) {
		LinearLayout outerLayout = (LinearLayout) v.getParent();
		LinearLayout innerLayout = (LinearLayout) outerLayout.getChildAt(1);
		TextView addressTextTextView = (TextView) innerLayout.getChildAt(0);
		TextView zipTextTextView = (TextView) innerLayout.getChildAt(1);
		String addressText = addressTextTextView.getText().toString();
		String zipText = zipTextTextView.getText().toString();
//		Toast.makeText(this, addressText+" "+zipText, Toast.LENGTH_SHORT).show();
		
		String uri = String.format("geo:0,0?q="+addressText+" "+zipText);
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
		startActivity(intent);

	}
	
	public void callPhone(View v) {
		
		RelativeLayout outerLayout = (RelativeLayout) v.getParent();
		ImageView friendsImageView  = (ImageView) outerLayout.getChildAt(0);
		String phoneNumber = friendsImageView.getTag().toString();

		String uri = "tel:" + phoneNumber.trim() ;
		Intent intent = new Intent(Intent.ACTION_DIAL);
		intent.setData(Uri.parse(uri));
		startActivity(intent);
	}
	
	public void quickActions(View v) {
		final String friendUserDetail = v.getTag().toString();
		Log.i(TAG, "Friend details = "+friendUserDetail);

		final String phoneNumber = friendUserDetail.substring(0, friendUserDetail.indexOf("$#~"));
		final String email = friendUserDetail.substring(friendUserDetail.indexOf("$#~")+3);
		Log.i(TAG, "email = "+email+"\nphone Number = "+phoneNumber);
		int[] xy = new int[2];
    	v.getLocationInWindow(xy);
    	Log.i(TAG, "X = "+xy[0]+" Y = "+xy[1]);
    	
    	int positionFactor = 100;
    	if (xy[1] < 100) {
    		positionFactor = xy[1]+5;
    	}
    	Rect rect = new Rect(xy[0], xy[1], xy[0]+v.getWidth(), xy[1]-positionFactor);
    	windowF = new QuickActionWindowF(Main.this, v, rect, R.layout.quickactionfriends, R.anim.quickaction);
    	windowF.show();
    	
    	windowF.getCallPhoneContext().setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i(TAG, "Call the phone number of the person");
				String uri = "tel:" + phoneNumber.trim() ;
	   		    Intent intent = new Intent(Intent.ACTION_DIAL);
	   		    intent.setData(Uri.parse(uri));
	   		    startActivity(intent);
			}
		});
    	
    	windowF.getTextContext().setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
	           Log.i(TAG, "Text number of the person");
	           Uri uri = Uri.parse("smsto:" + phoneNumber.trim());
	           Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
	           intent.putExtra("sms_body", "");  
	           startActivity(intent);
			}
		});
    	
    	windowF.getEmailContext().setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
               /* Create the Intent */
               final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

               /* Fill it with Data */
               emailIntent.setType("plain/text");
               emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{email});
               emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
               emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
               //startActivity(Intent.createChooser(emailIntent, "Send mail..."));
               startActivity(emailIntent);
			}
		});
	}
	
		private Handler messageFromChildThreadH = new Handler() {
    	@Override
    	public void handleMessage(Message msg) {
    		Log.i(TAG, "In Handler for MAIN thead, result= "+msg.what);
    		if (msg.what == 200 & msg.arg1 == 121) {
    			byte[] data = msg.getData().getByteArray("mainData");
    			byte[] friends = msg.getData().getByteArray("friendsData");
    			byte[] me = msg.getData().getByteArray("meData");
    			
    			Log.i(TAG, "BEFORE: Friends size is = "+friends.length);
    			saveFriendsObject(friends);
    			
    			SharedPreferences settings =  getSharedPreferences(PREFS_NAME, 0);
    	        SharedPreferences.Editor editor = settings.edit();
    	        editor.putInt("friendsDataLength", friends.length);
    	        editor.commit();

    	    	Fragment mainFragment = new MainFragment();
    	    	
    	    	Bundle mainFragmentBundle = new Bundle();
    	    	mainFragmentBundle.putByteArray("mainData", data);
    	    	Log.i(TAG, "Size = "+friends.length);
    	    	mainFragmentBundle.putByteArray("friendsData", friends);
    	    	mainFragmentBundle.putByteArray("meData", me);
    	    	mainFragment.setArguments(mainFragmentBundle);
    	    	
    	    	FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    	        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
    	        ft.replace(R.id.fragment, mainFragment, "MainFragment");
    	        ft.setBreadCrumbShortTitle("MainFragment");
    	        
    	        ft.commit();
    		}
    		pd.dismiss();
    	}
    };
    
	public void callPhone1(View v) {
		
		LinearLayout outerLayout = (LinearLayout) v.getParent();
		LinearLayout innerLayout = (LinearLayout) outerLayout.getChildAt(2);
		LinearLayout phoneNumberLayout  = (LinearLayout) innerLayout.getChildAt(1);
		TextView phoneNumberTextView = (TextView) phoneNumberLayout.getChildAt(0);
		String phoneNumber = phoneNumberTextView.getText().toString();

		String uri = "tel:" + phoneNumber.trim() ;
		Intent intent = new Intent(Intent.ACTION_DIAL);
		intent.setData(Uri.parse(uri));
		startActivity(intent);
	}
	
	public void openBrowser(View v) {
		LinearLayout outerLayout = (LinearLayout) v.getParent();
		LinearLayout innerLayout = (LinearLayout) outerLayout.getChildAt(8);
		LinearLayout webLayout  = (LinearLayout) innerLayout.getChildAt(1);
		TextView webAddress = (TextView) webLayout.getChildAt(0);
		String web = "http://"+webAddress.getText().toString();
		
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(web));
		startActivity(intent);
	}

}