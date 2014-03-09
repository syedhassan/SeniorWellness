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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.SpannableString;
import android.text.TextUtils.TruncateAt;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.snrwlns.threads.DownloadPictures;
import com.example.snrwlns.threads.GetEventDataObject2;

public class FriendsFragment extends Fragment {
	
	View viewer = null;
	public static final String TAG = "SNRWLNS";
	public static final String PREFS_NAME = "SNRWLNS";
	private LinkedHashMap<String, ArrayList<List<String>>> friendsData;
	private ProgressDialog pd1;
	Bundle bundle;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "Saves Instance state bundle = "+savedInstanceState);
		super.onCreate(savedInstanceState);

	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.i(TAG, "FriendsFragment onActivityCreated");
	}
	
	
	@Override
	 public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i(TAG, "FriendsFragment onCreateView");

		viewer = inflater.inflate(R.layout.mefragment, container, false);
		return viewer;
	}
	
	private Handler handler = new Handler() {
    	@Override
    	public void handleMessage(Message msg) {
    		Log.i(TAG, "In Handler for FriendsFragment thead, result= "+msg.what);
			pd1.dismiss();
    		if (msg.what == 634) {
    			byte[] data = msg.getData().getByteArray("eventData");
		    	Fragment eventDetailsFragment = new EventDetailsFragment();
		    	Bundle eventDetailsBundle = new Bundle();
		    	eventDetailsBundle.putByteArray("data", data);
		    	eventDetailsBundle.putBoolean("fromMe", true);
		    	eventDetailsBundle.putString("eventId", (String)msg.obj);
		    	eventDetailsFragment.setArguments(eventDetailsBundle);
		    	FragmentTransaction ft = getFragmentManager().beginTransaction();
		    	ft.addToBackStack(null);
		    	ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		        ft.replace(R.id.fragment, eventDetailsFragment, "EventDetailsFragment");
		        ft.setBreadCrumbShortTitle("EventDetailsFragment");
		        ft.commit();

    		}
    	}
    };
	
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Remember the current text, to restore if we later restart.
        Log.i(TAG, "ONSAVEDINSTANCESTATE");
    }
    
    
    @Override
    public void onAttach(Activity activity) {
    	super.onAttach(activity);
    	Log.i(TAG, "FriendsFragment onAttach");
    }
    
    @Override
    public void onStart() {
    	super.onStart();
    	Log.i(TAG, "FriendsFragment onStart");
    }
    
    private byte[] getFriendsDataObject(){
    	FileInputStream in;
    	SharedPreferences settings =  getActivity().getSharedPreferences(PREFS_NAME, 0);
    	settings.getInt("friendsDataLength", 0);
    	byte[] buffer = new byte[settings.getInt("friendsDataLength", 0)];
		try {
			in = getActivity().openFileInput("friendsData");
	    	in.read(buffer, 0, buffer.length);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return buffer;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void onResume() {
    	super.onResume();
    	Log.i(TAG, "FriendsFragment onResume");
		byte[] friends = getFriendsDataObject();
		
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(new ByteArrayInputStream(friends));
			friendsData = (LinkedHashMap<String, ArrayList<List<String>>>) ois.readObject();
			
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
    	
    	
    	TextView upcomingEventsCount = (TextView) viewer.findViewById(R.id.upcomingEventsCount);
		TextView savedEventsCount = (TextView) viewer.findViewById(R.id.savedEventsCount);
		TextView pastEventsCount = (TextView) viewer.findViewById(R.id.pastEventsCount);
		
		upcomingEventsCount.setText("0 UPCOMING EVENTS");
		savedEventsCount.setText("0 SAVED EVENTS");
		pastEventsCount.setText("0 PAST EVENTS");
		 
		LinearLayout upcomingEventsLinearLayoutText = (LinearLayout) viewer.findViewById(R.id.upcomingEventsLinearLayoutText);
		LinearLayout savedEventsLinearLayoutText = (LinearLayout) viewer.findViewById(R.id.savedEventsLinearLayoutText);
		LinearLayout pastEventsLinearLayoutText = (LinearLayout) viewer.findViewById(R.id.pastEventsLinearLayoutText);
		 
		TextView friendNameText = (TextView) viewer.findViewById(R.id.friendNameText);
		friendNameText.setText(friendsData.get("friendInfo").get(0).get(0));
		 
		final TextView friendNumberText = (TextView) viewer.findViewById(R.id.friendNumberText);
		friendNumberText.setVisibility(View.GONE);
		SpannableString content = new SpannableString(friendsData.get("friendInfo").get(0).get(1));
		content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
		friendNumberText.setText(content);
		friendNumberText.setOnClickListener(new OnClickListener() {
		
			@Override
			public void onClick(View v) {
				String phoneNumber = friendNumberText.getText().toString();
				String uri = "tel:" + phoneNumber.trim() ;
				Intent intent = new Intent(Intent.ACTION_DIAL);
				intent.setData(Uri.parse(uri));
				startActivity(intent);
				
			}
		});
		 
		TextView friendEmailText = (TextView) viewer.findViewById(R.id.friendEmailText);
		content = new SpannableString(friendsData.get("friendInfo").get(0).get(2));
		content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
		friendEmailText.setText(content);
		RelativeLayout meImageViewLayout = (RelativeLayout) viewer.findViewById(R.id.meImageViewLayout123);
		friendEmailText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text");
                String[] emailAddress = {friendsData.get("friendInfo").get(0).get(2)};
                intent.putExtra(Intent.EXTRA_TEXT, emailAddress);
                startActivity(Intent.createChooser(intent, ""));
			}
		});
		
		ImageView callImage = new ImageView(getActivity());
		callImage.setImageResource(R.drawable.phone32x40);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		callImage.setPadding(2, 2, 0, 0);
		callImage.setLayoutParams(params);
		meImageViewLayout.addView(callImage);
		
		ImageView saveEventImage = (ImageView) viewer.findViewById(R.id.saveEventImage);
		saveEventImage.setAdjustViewBounds(true);
		saveEventImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
		HttpGet httpRequest = null;
		try {
			
			List<String> data = friendsData.get("friendInfo").get(0);
			Iterator<String> it = data.iterator();
			while (it.hasNext()) {
				Log.i(TAG, "details = "+it.next());
			}
	    	String url = friendsData.get("friendInfo").get(0).get(3);
	    	String fileName = url.substring(url.lastIndexOf("/")+1);
	    	url = url.replace(" ", "%20");
	    	File imageFile = new File(Environment.getExternalStorageDirectory().getPath()+"/SeniorWellness/"+fileName);
	    	BitmapFactory.Options options = new BitmapFactory.Options();
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
	    		saveEventImage.setImageBitmap(bm);
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
	            saveEventImage.setImageBitmap(bm);
				DownloadPictures pictures = new DownloadPictures(getActivity(), url, fileName);
				pictures.start();
	    	}
	    	saveEventImage.setTag(friendsData.get("friendInfo").get(0).get(1)+"$#~"+friendsData.get("friendInfo").get(0).get(2));
		} 
		catch (MalformedURLException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		saveEventImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String friendUserDetail = v.getTag().toString();
				Log.i(TAG, "Friend details = "+friendUserDetail);

				final String phoneNumber = friendUserDetail.substring(0, friendUserDetail.indexOf("$#~"));
				final String email = friendUserDetail.substring(friendUserDetail.indexOf("$#~")+3);

				int[] xy = new int[2];
		    	v.getLocationInWindow(xy);
		    	
		    	int positionFactor = 100;
		    	if (xy[1] < 100) {
		    		positionFactor = xy[1]+5;
		    	}
		    	Rect rect = new Rect(xy[0], xy[1], xy[0]+v.getWidth(), xy[1]-positionFactor);
		    	QuickActionWindowF windowF = new QuickActionWindowF(getActivity(), v, rect, R.layout.quickactionfriendsright, R.anim.quickactionfriendsfragment);
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
		});
		
		if (friendsData.get("upcomingEvents") != null) {
			upcomingEventsLinearLayoutText.removeAllViews();
			upcomingEventsCount.setText(friendsData.get("upcomingEvents").size()+" UPCOMING EVENTS");
			for (int i=0;i<friendsData.get("upcomingEvents").size();i++) {
				LinearLayout linearLayout = new LinearLayout(getActivity());
				LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				linearLayout.setLayoutParams(linearLayoutParams);
				linearLayout.setPadding(10, 2, 10, 3);
				linearLayout.setOrientation(LinearLayout.HORIZONTAL);
				 
				TextView text = new TextView(getActivity());
				LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				text.setLayoutParams(textLayoutParams);
				text.setTextSize(20);
				text.setText("¥"+" "+friendsData.get("upcomingEvents").get(i).get(0)+": ");
				text.setTextColor(Color.rgb(109, 110, 113));
				linearLayout.addView(text);
				 
				 TextView textEvent = new TextView(getActivity());
				 LinearLayout.LayoutParams textEventLayoutParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				 textEvent.setLayoutParams(textEventLayoutParams);
				 textEvent.setTextSize(20);
				 textEvent.setEllipsize(TruncateAt.MARQUEE);
				 textEvent.setSingleLine();
				 SpannableString contentText = new SpannableString(friendsData.get("upcomingEvents").get(i).get(1));
				 contentText.setSpan(new UnderlineSpan(), 0, contentText.length(), 0);
				 textEvent.setText(contentText);
				 textEvent.setTypeface(null, Typeface.BOLD);
				 final String eventId = friendsData.get("upcomingEvents").get(i).get(2);
				 textEvent.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						pd1 = new ProgressDialog(getActivity());
						pd1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				        pd1.setMessage("Working...");
				        pd1.setIndeterminate(true);
				        pd1.setCancelable(false);
				        pd1.show();
				        SharedPreferences settings =  getActivity().getSharedPreferences(PREFS_NAME, 0);
						GetEventDataObject2 obj = new GetEventDataObject2(getActivity(), settings.getString("userId", "100"), eventId, handler);
						obj.start();
					}
				 });
				 linearLayout.addView(textEvent);
				 upcomingEventsLinearLayoutText.addView(linearLayout);
			 }
		}
		
		if (friendsData.get("savedEvents")!= null) {
			savedEventsLinearLayoutText.removeAllViews();
			savedEventsCount.setText(friendsData.get("savedEvents").size()+" SAVED EVENTS");
			for (int i=0;i<friendsData.get("savedEvents").size();i++) {
				 
				 LinearLayout linearLayout = new LinearLayout(getActivity());
				 LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				 linearLayout.setLayoutParams(linearLayoutParams);
				 linearLayout.setPadding(10, 2, 10, 3);
				 linearLayout.setOrientation(LinearLayout.HORIZONTAL);
				 
				 TextView text = new TextView(getActivity());
				 LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				 text.setLayoutParams(textLayoutParams);
				 text.setTextSize(20);
				 text.setText("¥"+" "+friendsData.get("savedEvents").get(i).get(0)+": ");
				 text.setTextColor(Color.rgb(109, 110, 113));
				 linearLayout.addView(text);
				 
				 TextView textEvent = new TextView(getActivity());
				 LinearLayout.LayoutParams textEventLayoutParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				 textEvent.setLayoutParams(textEventLayoutParams);
				 textEvent.setTextSize(20);
				 textEvent.setEllipsize(TruncateAt.MARQUEE);
				 textEvent.setSingleLine();
				 SpannableString contentText = new SpannableString(friendsData.get("savedEvents").get(i).get(1));
				 contentText.setSpan(new UnderlineSpan(), 0, contentText.length(), 0);
				 textEvent.setText(contentText);
				 textEvent.setTypeface(null, Typeface.BOLD);
				 final String eventId = friendsData.get("savedEvents").get(i).get(2);
				 textEvent.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						pd1 = new ProgressDialog(getActivity());
						pd1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				        pd1.setMessage("Working...");
				        pd1.setIndeterminate(true);
				        pd1.setCancelable(false);
				        pd1.show();
				        SharedPreferences settings =  getActivity().getSharedPreferences(PREFS_NAME, 0);
						GetEventDataObject2 obj = new GetEventDataObject2(getActivity(), settings.getString("userId", "100"), eventId, handler);
						obj.start();
					}
				 });
				 linearLayout.addView(textEvent);
				 savedEventsLinearLayoutText.addView(linearLayout);
			 }
		}
		 
		if (friendsData.get("pastEvents") != null) {
			pastEventsLinearLayoutText.removeAllViews();
			pastEventsCount.setText(friendsData.get("pastEvents").size()+" PAST EVENTS");
			for (int i=0;i<friendsData.get("pastEvents").size();i++) {
				 LinearLayout linearLayout = new LinearLayout(getActivity());
				 LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				 linearLayout.setLayoutParams(linearLayoutParams);
				 linearLayout.setPadding(10, 2, 10, 3);
				 linearLayout.setOrientation(LinearLayout.HORIZONTAL);
				 
				 TextView text = new TextView(getActivity());
				 LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				 text.setLayoutParams(textLayoutParams);
				 text.setTextSize(20);
				 text.setText("¥"+" "+friendsData.get("pastEvents").get(i).get(0)+": ");
				 text.setTextColor(Color.rgb(109, 110, 113));
				 linearLayout.addView(text);
				 
				 TextView textEvent = new TextView(getActivity());
				 LinearLayout.LayoutParams textEventLayoutParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				 textEvent.setLayoutParams(textEventLayoutParams);
				 textEvent.setTextSize(20);
				 textEvent.setEllipsize(TruncateAt.MARQUEE);
				 textEvent.setSingleLine();
				 SpannableString contentText = new SpannableString(friendsData.get("pastEvents").get(i).get(1));
				 contentText.setSpan(new UnderlineSpan(), 0, contentText.length(), 0);
				 textEvent.setText(contentText);
				 textEvent.setTypeface(null, Typeface.BOLD);
				 final String eventId = friendsData.get("pastEvents").get(i).get(2);
				 textEvent.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						pd1 = new ProgressDialog(getActivity());
						pd1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				        pd1.setMessage("Working...");
				        pd1.setIndeterminate(true);
				        pd1.setCancelable(false);
				        pd1.show();
				        SharedPreferences settings =  getActivity().getSharedPreferences(PREFS_NAME, 0);
						GetEventDataObject2 obj = new GetEventDataObject2(getActivity(), settings.getString("userId", "100"), eventId, handler);
						obj.start();
					}
				 });
				 linearLayout.addView(textEvent);
				 pastEventsLinearLayoutText.addView(linearLayout);
			 }
		}
    	
    	
    }

    @Override
    public void onPause() {
    	super.onPause();
    	Log.i(TAG, "FriendsFragment onPause");
    }
    
    @Override
    public void onStop() {
    	super.onStop();
    	Log.i(TAG, "FriendsFragment onStop");
    }
}
