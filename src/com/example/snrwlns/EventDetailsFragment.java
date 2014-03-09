package com.example.snrwlns;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.snrwlns.threads.BookMarkEvent;
import com.example.snrwlns.threads.DownloadPictures;
import com.example.snrwlns.threads.GetThreadsData;
/*
 * Called when the activity is first created. 
 */
public class EventDetailsFragment extends Fragment {
    
	public static final String TAG = "SNRWLNS";
	public static final String PREFS_NAME = "SNRWLNS";
	public static final int messageActivity = 300;
	private String userId;
	private Integer eventId;
	private byte[] previousData;
	private LinkedHashMap<Integer, LinkedHashMap<String, String>> adapterData;
	private ImageLoader imageLoader;
	private View viewer = null;
	boolean checkServer = true;
	private HashMap<String, String> friendsDataMap;
	private ArrayList<String> eventPictureList;
	private ArrayList<String> eventVideoList;
	private String eventResponseString;
	private List<HashMap<String, String>> eventMessageData;
	private String eventNameString;
	
	@Override
	 public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		imageLoader = new ImageLoader(getActivity(), R.drawable.event100x100);
		Log.i(TAG, "Event Details Fragment");
		viewer = inflater.inflate(R.layout.eventdetailsfragment, container, false);
	    eventId = Integer.parseInt(getArguments().getString("eventId"));
	    SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
	    SharedPreferences.Editor editor = settings.edit();
	    editor.putInt("eventIdDetailsFragment", eventId);
	    editor.commit();
		return viewer;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onResume() {
		super.onResume();
		Log.i(TAG, "onResume for eventdetails fragment");
		if (getArguments().getBoolean("fromMe")) {
	    	Log.d(TAG, "From Me");
	    	previousData = getArguments().getByteArray("data");
	    }
	    else {
	    	previousData = getUserEventDetailsObject();
	    }
	    
	    {
			ObjectInputStream ois;
			try {
				ois = new ObjectInputStream(new ByteArrayInputStream(previousData));
				adapterData = (LinkedHashMap<Integer, LinkedHashMap<String, String>>) ois.readObject();
				Log.i(TAG, "~~~EVENT Details to be show = "+adapterData);
				Log.i(TAG, "~~~EVENT Details at eventIdition = "+adapterData.get(eventId));
			} catch (StreamCorruptedException e) {
				Log.e(TAG, "Message = "+e.getMessage());
				Log.e(TAG, "Message = "+e.getLocalizedMessage());
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	    SharedPreferences settings =  getActivity().getSharedPreferences(PREFS_NAME, 0);
		userId = settings.getString("userId", "100");
			
		ProgressBar progress0 = (ProgressBar) viewer.findViewById(R.id.progress0);
		progress0.setVisibility(ProgressBar.VISIBLE);
		ProgressBar progress1 = (ProgressBar) viewer.findViewById(R.id.progress1);
		progress1.setVisibility(ProgressBar.VISIBLE);
		ProgressBar progress2 = (ProgressBar) viewer.findViewById(R.id.progress2);
		progress2.setVisibility(ProgressBar.VISIBLE);
		ProgressBar progress3 = (ProgressBar) viewer.findViewById(R.id.progress3);
		progress3.setVisibility(ProgressBar.VISIBLE);
		
		ImageButton signup = (ImageButton) viewer.findViewById(R.id.signUpImage);
		signup.setVisibility(View.VISIBLE);
		if (adapterData.get(eventId).get("response").equalsIgnoreCase("no")) {
			progress0.setVisibility(ProgressBar.GONE);
			TextView eventSignDate = (TextView) viewer.findViewById(R.id.eventSignDate);
			eventSignDate.setVisibility(View.GONE);
			ImageView newMessage = (ImageView) viewer.findViewById(R.id.sendMessage);
			newMessage.setOnClickListener(new OnClickListener() {
					
				@Override
				public void onClick(View v) {
					Intent alertMessage = new Intent(getActivity(), EventMessageAlert.class);
					Bundle bundle = new Bundle();
					bundle.putString("eventName", adapterData.get(eventId).get("name").toString());
					alertMessage.putExtras(bundle);
					startActivity(alertMessage);
				}
			});
			
			signup.setImageResource(R.drawable.signupimage);
			signup.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent signOut = new Intent(getActivity(), EventSignupConfirmation.class);
					Bundle bundle = new Bundle();
					bundle.putString("userId",userId);
					bundle.putString("eventId", String.valueOf(eventId));
					signOut.putExtras(bundle);
					getActivity().startActivityForResult( signOut,  Main.CONFIRM_SIGNUP_EVENT);
				}
			});
		}
		else {
			progress0.setVisibility(ProgressBar.GONE);
			TextView eventSignDate = (TextView) viewer.findViewById(R.id.eventSignDate);
			eventSignDate.setVisibility(View.VISIBLE);
			eventSignDate.setText("you signed up on "+adapterData.get(eventId).get("eventSignDate").toString());
			ImageView newMessage = (ImageView) viewer.findViewById(R.id.sendMessage);
			newMessage.setOnClickListener(new OnClickListener() {
					
				@Override
				public void onClick(View v) {
					Intent message = new Intent(getActivity(), EventMessages.class);
					Bundle bundle = new Bundle();
					bundle.putString("userId",userId);
					bundle.putString("eventId", String.valueOf(eventId));
					message.putExtras(bundle);
					startActivityForResult(message, 127);
				}
			});
			signup.setImageResource(R.drawable.signoutimage);
			signup.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent signOut = new Intent(getActivity(), ConfirmSignOut.class);
					Bundle bundle = new Bundle();
					bundle.putString("userId",userId);
					bundle.putString("eventId", String.valueOf(eventId));
					signOut.putExtras(bundle);
					getActivity().startActivityForResult(signOut, Main.CONFIRM_SIGNOUT_EVENT);
				}
			});
		}
		
		
		
		TextView eventName = (TextView) viewer.findViewById(R.id.eventDetailHeaderText);
		eventName.setText(adapterData.get(eventId).get("name").toString());
		eventNameString = adapterData.get(eventId).get("name").toString();
		
		TextView eventLocation = (TextView) viewer.findViewById(R.id.locationText);
		eventLocation.setText(adapterData.get(eventId).get("location").toString());
		
		TextView eventAddress = (TextView) viewer.findViewById(R.id.addressText);
		SpannableString content = new SpannableString(adapterData.get(eventId).get("address").toString());
		content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
		eventAddress.setText(content);
			
		TextView eventZip = (TextView) viewer.findViewById(R.id.zipText);
		content = new SpannableString(adapterData.get(eventId).get("city").toString()+", "+adapterData.get(eventId).get("state").toString()+" "+adapterData.get(eventId).get("zip").toString());
		content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
		eventZip.setText(content);
		
		TextView timeText = (TextView) viewer.findViewById(R.id.timeText);
		timeText.setText(adapterData.get(eventId).get("date").toString()+"  "+adapterData.get(eventId).get("time").toString());
		
		if (adapterData.get(eventId).get("contact").toString().equalsIgnoreCase("NA")) {
			TextView phoneText = (TextView) viewer.findViewById(R.id.phoneText);
			phoneText.setText(adapterData.get(eventId).get("contact").toString());
			LinearLayout contactLinearLayout = (LinearLayout) viewer.findViewById(R.id.contactLinearLayout);
			contactLinearLayout.setClickable(false);
		}
		else {
			TextView phoneText = (TextView) viewer.findViewById(R.id.phoneText);
			content = new SpannableString(adapterData.get(eventId).get("contact").toString());
			content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
			phoneText.setText(content);
		}
		
			
		TextView countText = (TextView) viewer.findViewById(R.id.friendsCountText);
		countText.setText(adapterData.get(eventId).get("count").toString()+" FRIENDS SIGNED UP");
			
		TextView descriptionText = (TextView) viewer.findViewById(R.id.descriptionText);
		descriptionText.setText(adapterData.get(eventId).get("description").toString());
			
		TextView webText = (TextView) viewer.findViewById(R.id.webAddressText);
		content = new SpannableString(adapterData.get(eventId).get("web").toString().replace("http://", ""));
		content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
		webText.setText(content);
			
		TextView costText = (TextView) viewer.findViewById(R.id.costText);
		costText.setText(adapterData.get(eventId).get("cost").toString());
			
		TextView pictureCountText = (TextView) viewer.findViewById(R.id.picturesCountText);
		pictureCountText.setText(adapterData.get(eventId).get("photos").toString()+" PHOTOS");
			
		TextView videoCountText = (TextView) viewer.findViewById(R.id.videosCountText);
		videoCountText.setText(adapterData.get(eventId).get("videos").toString()+" VIDEOS");
		
		ImageView share = (ImageView)viewer.findViewById(R.id.shareEvent);
		share.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_SEND); 
			    i.setType("text/plain"); 
			    i.putExtra(Intent.EXTRA_EMAIL  , new String[]{""}); 
			    i.putExtra(Intent.EXTRA_SUBJECT, adapterData.get(eventId).get("name").toString()); 
			    i.putExtra(Intent.EXTRA_TEXT   , adapterData.get(eventId).get("name").toString() +"\n\n"+ adapterData.get(eventId).get("description").toString() +"\n\n"
			    		+adapterData.get(eventId).get("address").toString() + adapterData.get(eventId).get("city").toString()+", "+adapterData.get(eventId).get("state").toString()+" "+adapterData.get(eventId).get("zip").toString());
			    try { 
			        startActivity(Intent.createChooser(i, "Send mail/text...")); 
			    } catch (android.content.ActivityNotFoundException ex) { 
			        LayoutInflater inflater = LayoutInflater.from(getActivity());
    				View layout = inflater.inflate(R.layout.toasttext, (ViewGroup) getActivity().findViewById(R.id.toast_layout_root));
    				TextView text = (TextView) layout.findViewById(R.id.text);
    				text.setText("There are no email clients installed. Please use another email/text application");
    				Toast toast = new Toast(getActivity());
    				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
    				toast.setDuration(1000);
    				toast.setView(layout);
    				toast.show();
			    } 
			}
		});
		final ImageView bookMark = (ImageView) viewer.findViewById(R.id.saveEventImage);
		if (adapterData.get(eventId).get("bookmark").equalsIgnoreCase("Y")) {
			bookMark.setImageResource(R.drawable.bookmarked52x50);
			bookMark.setTag("bookmarked");
		}
		else {
			bookMark.setImageResource(R.drawable.bookmark52x50);
			bookMark.setTag("bookmark");
		}
		bookMark.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (bookMark.getTag().toString().equalsIgnoreCase("bookmark")) {
					bookMark.setImageResource(R.drawable.bookmarked52x50);
					bookMark.setTag("bookmarked");
					BookMarkEvent eventBookMark = new BookMarkEvent(getActivity(), userId, String.valueOf(eventId), true, messageFromThread);
					eventBookMark.start();
				}
				else {
					bookMark.setImageResource(R.drawable.bookmark52x50);
					bookMark.setTag("bookmark");
					BookMarkEvent eventBookMark = new BookMarkEvent(getActivity(), userId, String.valueOf(eventId), false, messageFromThread);
					eventBookMark.start();
				}
			}
		});
		
		GetThreadsData thread = new GetThreadsData(getActivity(), adapterData.get(eventId).get("id").toString(), messageFromChildThread);
		thread.start();
	}
	
	private Handler messageFromThread = new Handler() {
		@Override
    	public void handleMessage(Message msg) {
			if ((msg.what == 200 & msg.arg1 == 198) | (msg.what == 200 & msg.arg1 == 199)) {
				
				if ((msg.what == 200 & msg.arg1 == 199)) {
					//Log.i(TAG, "Your event was successfully un-bookmarked!");
				}
				else {
					//Log.i(TAG, "Your event was successfully bookmarked!");
				}
			}
			else {
				LayoutInflater inflater = LayoutInflater.from(getActivity());
				View layout = inflater.inflate(R.layout.toasttext, (ViewGroup) viewer.findViewById(R.id.toast_layout_root));
				TextView text = (TextView) layout.findViewById(R.id.text);
				text.setText("ERROR: Please try again to bookmark your event.");
				Toast toast = new Toast(getActivity());
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				toast.setDuration(1000);
				toast.setView(layout);
				toast.show();
				ImageView bookMark = (ImageView) viewer.findViewById(R.id.saveEventImage);
				if (bookMark.getTag().toString().equalsIgnoreCase("bookmark")) {
					bookMark.setImageResource(R.drawable.bookmarked52x50);
					bookMark.setTag("bookmarked");
				}
				else {
					bookMark.setImageResource(R.drawable.bookmark52x50);
					bookMark.setTag("bookmark");
				}
			}
		}
	};
	
	@SuppressWarnings("unchecked")
    private Handler messageFromChildThread = new Handler() {
		@Override
    	public void handleMessage(Message msg) {
    		Log.d(TAG, "Data being return from ALL threads of event details fragment");
    		final byte[] friendsData = msg.getData().getByteArray("friendsUriData");
    		if (friendsData != null) {
    			{
        			ObjectInputStream ois;
        			try {
        				ois = new ObjectInputStream(new ByteArrayInputStream(friendsData));
        				friendsDataMap = (HashMap<String, String>) ois.readObject();
        				//Log.i(TAG, "~~~FRIEND DATA MAP= "+friendsDataMap);
        			} catch (StreamCorruptedException e) {
        				e.printStackTrace();
        			} catch (IOException e) {
        				e.printStackTrace();
        			} catch (ClassNotFoundException e) {
        				e.printStackTrace();
        			}
        		}
    			
    			TextView countText = (TextView) viewer.findViewById(R.id.friendsCountText);
    			if (friendsDataMap.size() == 1) {
    				countText.setText(friendsDataMap.size()+" FRIEND SIGNED UP");
    			}
    			else {
    				countText.setText(friendsDataMap.size()+" FRIENDS SIGNED UP");
    			}
    			
    			LinearLayout friendsPictureLinearLayout = (LinearLayout) viewer.findViewById(R.id.friendsPictureLinearLayout);
    			friendsPictureLinearLayout.removeAllViews();
    			friendsPictureLinearLayout.setOnClickListener(new OnClickListener() {
    				
    				@Override
    				public void onClick(View v) {
    					Intent intent = new Intent(getActivity(), FriendsPhotoView.class);
    					Bundle bundle = new Bundle();
    					bundle.putInt("count", friendsDataMap.size());
    					bundle.putByteArray("eventPhotosUri", friendsData);
    					intent.putExtras(bundle);
    					getActivity().startActivityForResult(intent, Main.OPEN_FRIEND_VIEW_FROM_EVENT);

    				}
    			});
    			
    			Iterator<Entry<String, String>> it = friendsDataMap.entrySet().iterator();
    			while (it.hasNext() & EventDetailsFragment.this.isAdded()) {
    		        Entry<String, String> pairs = it.next();
    				
    				String url = pairs.getValue();
    				String fileName = url.substring(url.lastIndexOf("/")+1);
    				url = url.replace(" ", "%20");
    				File imageFile = new File(Environment.getExternalStorageDirectory().getPath()+"/SeniorWellness/"+fileName);
    				ImageView eventImageView = new ImageView(getActivity());
			        eventImageView.setLayoutParams(new RelativeLayout.LayoutParams(100, 100));
			        eventImageView.setPadding(2, 2, 2, 2);
			        eventImageView.setAdjustViewBounds(true);
			        eventImageView.setScaleType(ScaleType.CENTER_CROP);
    	        	if (imageFile.exists()) {
    			        
    	        		BitmapFactory.Options options = new BitmapFactory.Options();
    	        		options.inJustDecodeBounds = true;
    	        		BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
    	        		Boolean scaleByHeight = Math.abs(options.outHeight - 100) >= Math.abs(options.outWidth - 100);
    	        		if(options.outHeight * options.outWidth * 2 >= 16384){
    	        		    double sampleSize = scaleByHeight? options.outHeight / 100: options.outWidth / 100;
    	        		    options.inSampleSize = (int)Math.pow(2d, Math.floor(Math.log(sampleSize)/Math.log(2d)));
    	        		}
    	        		options.inJustDecodeBounds = false; options.inTempStorage = new byte[1024]; options.inPurgeable=true; options.inInputShareable=true;options.inDither=false;
    	        		Bitmap bm = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
    	        		if (bm==null) {
    	        			Log.e(TAG, "Url = "+url);
    	        			Log.e(TAG, "Sample Size = "+options.inSampleSize);
    	        			Log.e(TAG, "path = "+imageFile.getAbsolutePath());
    	        			Log.e(TAG, "Name = "+imageFile.getName());
    	        			BitmapFactory.Options options1 = new BitmapFactory.Options();
    	        			options1.inSampleSize = 64;
							System.gc();
							bm = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options1);
							if (bm ==null) {
								Log.e(TAG, "BITMAP is STILL NULL");
							}
    	        		}
    	        		else {
    	        			Log.d(TAG, "Url = "+url);
    	        			Log.d(TAG, "Sample Size = "+options.inSampleSize);
    	        			Log.d(TAG, "path = "+imageFile.getAbsolutePath());
    	        			Log.d(TAG, "Name = "+imageFile.getName());
    	        		}
    	        		eventImageView.setImageBitmap(bm);
    	        		friendsPictureLinearLayout.addView(eventImageView);
    	        	}
    	        	else {
    			        imageLoader.DisplayImage(url, getActivity(), eventImageView);
    			        friendsPictureLinearLayout.addView(eventImageView);
    			        DownloadPictures pictures = new DownloadPictures(getActivity(), url, fileName);
    					pictures.start();
    	        	}
    		    }
    		}
    		
			
			final byte[] eventPictures = msg.getData().getByteArray("eventPicturesUri");
			
			if (eventPictures != null) {
				//Log.i(TAG, "eventPictures isze = "+eventPictures.length);
				{
	    			ObjectInputStream ois;
	    			try {
	    				ois = new ObjectInputStream(new ByteArrayInputStream(eventPictures));
	    				eventPictureList = (ArrayList<String>) ois.readObject();
	    				//Log.i(TAG, "~~~EVENT Picture LIST = "+eventPictureList);
	    			} catch (StreamCorruptedException e) {
	    				e.printStackTrace();
	    			} catch (IOException e) {
	    				e.printStackTrace();
	    			} catch (ClassNotFoundException e) {
	    				e.printStackTrace();
	    			}
	    		}
				
				//Log.i(TAG, "No. of event pictures are = "+eventPictureList.size());
				TextView pictureCountText = (TextView) viewer.findViewById(R.id.picturesCountText);
				pictureCountText.setText(eventPictureList.size()/2+" PHOTOS");
				LinearLayout eventPicturesLinearLayout = (LinearLayout) viewer.findViewById(R.id.eventPicturesLinearLayout);
				eventPicturesLinearLayout.removeAllViews();
				eventPicturesLinearLayout.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(getActivity(), PhotoViewG.class);
						Bundle bundle = new Bundle();
						bundle.putInt("count", (eventPictureList.size()/2));
						bundle.putByteArray("eventPhotosUri", eventPictures);
						intent.putExtras(bundle);
						startActivity(intent);
					}
				});
				
				Iterator<String> it1 = eventPictureList.iterator();
				while (it1.hasNext() & EventDetailsFragment.this.isAdded()) {
			        it1.next(); // just bypassing the thumbnail url
					String imageThumbnail = it1.next();
					String url = imageThumbnail;
					String fileName = url.substring(url.lastIndexOf("/")+1);
					url = url.replace(" ", "%20");
					File imageFile = new File(Environment.getExternalStorageDirectory().getPath()+"/SeniorWellness/"+fileName);
					ImageView eventImageView = new ImageView(getActivity());
			        eventImageView.setLayoutParams(new RelativeLayout.LayoutParams(100, 100));
			        eventImageView.setPadding(5, 2, 5, 2);
		        	if (imageFile.exists()) {
				        
		        		BitmapFactory.Options options = new BitmapFactory.Options();
		        		options.inJustDecodeBounds = true;
		        		BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath()+"/SeniorWellness/"+fileName, options);
		        		Boolean scaleByHeight = Math.abs(options.outHeight - 100) >= Math.abs(options.outWidth - 100);
		        		if(options.outHeight * options.outWidth * 2 >= 16384){
		        		    double sampleSize = scaleByHeight? options.outHeight / 100: options.outWidth / 100;
		        		    options.inSampleSize = (int)Math.pow(2d, Math.floor(Math.log(sampleSize)/Math.log(2d)));
		        		}
		        		options.inJustDecodeBounds = false; options.inTempStorage = new byte[1024];  options.inPurgeable=true;
		        		Bitmap bm = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath()+"/SeniorWellness/"+fileName, options);
		        		eventImageView.setImageBitmap(bm);
		        		eventPicturesLinearLayout.addView(eventImageView);
		        	}
		        	else {
				        
				        imageLoader.DisplayImage(url, getActivity(), eventImageView);
				        eventPicturesLinearLayout.addView(eventImageView);
				        DownloadPictures pictures = new DownloadPictures(getActivity(), url, fileName);
						pictures.start();
		        	}
			    }
			}
			
			final byte[] eventVideos = msg.getData().getByteArray("eventVideosUri");
			if (eventVideos != null) {
				{
	    			ObjectInputStream ois;
	    			try {
	    				ois = new ObjectInputStream(new ByteArrayInputStream(eventVideos));
	    				eventVideoList = (ArrayList<String>) ois.readObject();
	    				//Log.i(TAG, "~~~EVENT VIDEO LIST = "+eventVideoList);
	    			} catch (StreamCorruptedException e) {
	    				e.printStackTrace();
	    			} catch (IOException e) {
	    				e.printStackTrace();
	    			} catch (ClassNotFoundException e) {
	    				e.printStackTrace();
	    			}
	    		}
				
				TextView videoCountText = (TextView) viewer.findViewById(R.id.videosCountText);
				videoCountText.setText(eventVideoList.size()/2+" VIDEOS");
				LinearLayout eventVideosLinearLayout = (LinearLayout) viewer.findViewById(R.id.eventVideosLinearLayout);
				eventVideosLinearLayout.removeAllViews();
				eventVideosLinearLayout.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(getActivity(), VideoViewG.class);
						Bundle bundle = new Bundle();
						bundle.putInt("count", (eventVideoList.size()/2));
						bundle.putByteArray("eventVideosUri", eventVideos);
						intent.putExtras(bundle);
						startActivity(intent);
					}
				});
				
				Iterator<String> it2 = eventVideoList.iterator();
				while (it2.hasNext() & EventDetailsFragment.this.isAdded()) {
			        ImageView eventImageView = new ImageView(getActivity());
			        eventImageView.setLayoutParams(new RelativeLayout.LayoutParams(100, 100));
			        eventImageView.setPadding(5, 2, 5, 2);
			        
//					String videoUrl = 
						it2.next(); // Bypassing the thumbnail
					String videoThumbnail = it2.next();
			        Options options = new Options();
			        String url = videoThumbnail;
					String fileName = url.substring(url.lastIndexOf("/")+1);
					url = url.replace(" ", "%20");
					File imageFile = new File(Environment.getExternalStorageDirectory().getPath()+"/SeniorWellness/"+fileName);
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
						eventImageView.setImageBitmap(bm);
					}
					else {
						imageLoader.DisplayImage(url, getActivity(), eventImageView);
						DownloadPictures pictures = new DownloadPictures(getActivity(), url, fileName);
						pictures.start();
					}
//			        Bitmap bitmap = null;
//			        byte[] imageData = null;
//			        final int THUMBNAIL_SIZE = 100;
//					bitmap = ThumbnailUtils.createVideoThumbnail("/mnt/sdcard/download/sample.mp4", Thumbnails.MINI_KIND);
//					bitmap = Bitmap.createScaledBitmap(bitmap, THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);
	//
//		            ByteArrayOutputStream baos = new ByteArrayOutputStream();  
//		            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//		            imageData = baos.toByteArray();
//		            OutputStream out;
//					try {
//						out = new FileOutputStream("/mnt/sdcard/download/sana.png");
//			            out.write(imageData);
//			            out.close();
//					} catch (FileNotFoundException e) {
//						e.printStackTrace();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//			        eventImageView.setImageBitmap(bitmap); 
			        eventVideosLinearLayout.addView(eventImageView);
			    }
			}
			eventResponseString  = msg.getData().getString("eventResponseString");
			if (eventResponseString != null) {
				ImageButton signup = (ImageButton) viewer.findViewById(R.id.signUpImage);
				signup.setVisibility(View.VISIBLE);
				if (eventResponseString.equalsIgnoreCase("NO") | eventResponseString.equalsIgnoreCase("null")) {
					TextView eventSignDate = (TextView) viewer.findViewById(R.id.eventSignDate);
					eventSignDate.setVisibility(View.GONE);
					ImageView newMessage = (ImageView) viewer.findViewById(R.id.sendMessage);
					newMessage.setOnClickListener(new OnClickListener() {
							
						@Override
						public void onClick(View v) {
							
							//Give an alert popup with back button!
							Intent alertMessage = new Intent(getActivity(), EventMessageAlert.class);
							Bundle bundle = new Bundle();
							//Log.d(TAG, "EVT NM = "+eventNameString);
							bundle.putString("eventName", eventNameString);
							alertMessage.putExtras(bundle);
							startActivity(alertMessage);
						}
					});
					//Signup button has to be present
					
					signup.setImageResource(R.drawable.signupimage);
					signup.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							Intent signOut = new Intent(getActivity(), EventSignupConfirmation.class);
							Bundle bundle = new Bundle();
							bundle.putString("userId",userId);
							bundle.putString("eventId", String.valueOf(eventId));
							signOut.putExtras(bundle);
							getActivity().startActivityForResult( signOut,  Main.CONFIRM_SIGNUP_EVENT);
						}
					});
				}
				else {
					TextView eventSignDate = (TextView) viewer.findViewById(R.id.eventSignDate);
					eventSignDate.setVisibility(View.VISIBLE);
					eventSignDate.setText("you signed up on "+adapterData.get(eventId).get("eventSignDate").toString());
					ImageView newMessage = (ImageView) viewer.findViewById(R.id.sendMessage);
					newMessage.setOnClickListener(new OnClickListener() {
							
						@Override
						public void onClick(View v) {
							
							Intent message = new Intent(getActivity(), EventMessages.class);
							Bundle bundle = new Bundle();
							bundle.putString("userId",userId);
							bundle.putString("eventId", String.valueOf(eventId));
							message.putExtras(bundle);
							startActivityForResult(message, 127);
						}
					});
					//Signout button has to be present
					signup.setImageResource(R.drawable.signoutimage);
					signup.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							//Log.i(TAG, "Open the SignOut screen");
							Intent signOut = new Intent(getActivity(), ConfirmSignOut.class);
							Bundle bundle = new Bundle();
							bundle.putString("userId",userId);
							bundle.putString("eventId", String.valueOf(eventId));
							signOut.putExtras(bundle);
							getActivity().startActivityForResult(signOut, Main.CONFIRM_SIGNOUT_EVENT);
						}
					});
				}
			}
			
			byte[] eventMessages = msg.getData().getByteArray("eventMessage");
			if (eventMessages != null) {
				{
	    			ObjectInputStream ois;
	    			try {
	    				ois = new ObjectInputStream(new ByteArrayInputStream(eventMessages));
	    				eventMessageData = (ArrayList<HashMap<String, String>>) ois.readObject();
	    				//Log.i(TAG, "~~~EVENT MESSAGRE DATA = "+eventMessageData);
	    			} catch (StreamCorruptedException e) {
	    				e.printStackTrace();
	    			} catch (IOException e) {
	    				e.printStackTrace();
	    			} catch (ClassNotFoundException e) {
	    				e.printStackTrace();
	    			}
	    		}
				
				TextView messageCountText = (TextView) viewer.findViewById(R.id.messageCountText);
				messageCountText.setText(eventMessageData.size()+" POSTED NOTE(S)");
				
				LinearLayout eventMessagesLinearLayout = (LinearLayout) viewer.findViewById(R.id.eventMessagesLinearLayout);
				eventMessagesLinearLayout.removeAllViews();
				
				for(int i=0;i<eventMessageData.size() & EventDetailsFragment.this.isAdded();i++){
					LinearLayout eventMessageInnerLayout = new LinearLayout(getActivity());
					eventMessageInnerLayout.setOrientation(LinearLayout.HORIZONTAL);
					eventMessageInnerLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
					
					ImageView image = new ImageView(getActivity());
					LinearLayout.LayoutParams imageLayoutParams = new LinearLayout.LayoutParams(75, 75);
					imageLayoutParams.weight = 20;
					imageLayoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
					image.setLayoutParams(imageLayoutParams);
					image.setPadding(0, 10, 0, 10);
					
			        HttpGet httpRequest = null;
			        BitmapFactory.Options options = new BitmapFactory.Options();
			        try {
			        	String url = eventMessageData.get(i).get("userImage");
			        	//Log.e(TAG, "User image = "+url);
			        	String fileName = url.substring(url.lastIndexOf("/")+1);
			        	url = url.replace(" ", "%20");
			        	//Log.e(TAG, "Filename image = "+fileName);
			        	File imageFile = new File(Environment.getExternalStorageDirectory().getPath()+"/SeniorWellness/"+fileName);
			        	if (imageFile.exists()) {
			        		options.inJustDecodeBounds = true;
			        		BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath()+"/SeniorWellness/"+fileName, options);
			        		Boolean scaleByHeight = Math.abs(options.outHeight - 100) >= Math.abs(options.outWidth - 100);
			        		if(options.outHeight * options.outWidth * 2 >= 16384){
			        		    double sampleSize = scaleByHeight? options.outHeight / 100: options.outWidth / 100;
			        		    options.inSampleSize = (int)Math.pow(2d, Math.floor(Math.log(sampleSize)/Math.log(2d)));
			        		}
			        		options.inJustDecodeBounds = false; options.inTempStorage = new byte[1024];  options.inPurgeable=true;
			        		Bitmap bm = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath()+"/SeniorWellness/"+fileName, options);
			        		image.setImageBitmap(bm);
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
			        		options.inJustDecodeBounds = false; options.inTempStorage = new byte[1024]; options.inPurgeable=true; 
				            Bitmap bm = BitmapFactory.decodeStream(instream, null, options);
				            image.setImageBitmap(bm);
							DownloadPictures pictures = new DownloadPictures(getActivity(), url, fileName);
							pictures.start();
			        	}
			        } catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					TextView text = new TextView(getActivity());
					text.setTextSize(18);
					text.setTextColor(Color.parseColor("#6D6E71"));
					LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
					textLayoutParams.weight = 80;
					textLayoutParams.gravity = Gravity.TOP | Gravity.CENTER_VERTICAL;
					text.setLayoutParams(textLayoutParams);
					text.setText(eventMessageData.get(i).get("userName")+" : "+eventMessageData.get(i).get("text"));
					
					eventMessageInnerLayout.addView(image);
					eventMessageInnerLayout.addView(text);
					eventMessagesLinearLayout.addView(eventMessageInnerLayout);
					if (i != eventMessageData.size()-1 ){
						TextView border = new TextView(getActivity());
						LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 2);
						border.setLayoutParams(textParams);
						border.setBackgroundColor(Color.DKGRAY);
						eventMessagesLinearLayout.addView(border);
					}
				}
			}
			
			ProgressBar progress0 = (ProgressBar) viewer.findViewById(R.id.progress0);
			progress0.setVisibility(ProgressBar.GONE);
			ProgressBar progress1 = (ProgressBar) viewer.findViewById(R.id.progress1);
			progress1.setVisibility(ProgressBar.GONE);
			ProgressBar progress2 = (ProgressBar) viewer.findViewById(R.id.progress2);
			progress2.setVisibility(ProgressBar.GONE);
			ProgressBar progress3 = (ProgressBar) viewer.findViewById(R.id.progress3);
			progress3.setVisibility(ProgressBar.GONE);
			
    	}
    };
	
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	Log.d(TAG, "EventDetailsFragment -->");
    }
    
	public void myClickHandler(View v) {/*
		LinearLayout outerLayout = (LinearLayout) v.getParent();
		//Log.i(TAG, "Child count = "+outerLayout.getChildCount());
		//Log.i(TAG, "Condition 1 = "+(outerLayout.getChildAt(0)==null));
		//Log.i(TAG, "To String = "+outerLayout.getChildAt(0).toString());
		
		LinearLayout firstLayout = (LinearLayout) outerLayout.getChildAt(0);
		//Log.i(TAG, "Condition 2 = "+(outerLayout.getChildAt(1)==null));
		//Log.i(TAG, "To String = "+outerLayout.getChildAt(1).toString());
		
		LinearLayout secondLayout = (LinearLayout) outerLayout.getChildAt(0);
		//Log.i(TAG, "Condition 3 = "+(outerLayout.getChildAt(1)==null));
		//Log.i(TAG, "To String = "+outerLayout.getChildAt(1).toString());
		
//		ImageView imageView = (ImageView)outerLayout.getChildAt(0);
//		imageView.setBackgroundColor(Color.RED);
//		imageView.setPadding(1,1,1,1);
		*/
	}
	
    private byte[] getUserEventDetailsObject(){
    	FileInputStream in;
    	SharedPreferences settings =  getActivity().getSharedPreferences(PREFS_NAME, 0);
    	settings.getInt("userEventDataLength", 0);
    	byte[] buffer = new byte[settings.getInt("userEventDataLength", 0)];
		try {
			in = getActivity().openFileInput("userEventData");
	    	in.read(buffer, 0, buffer.length);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return buffer;
    }
}