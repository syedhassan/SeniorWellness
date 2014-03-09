package com.example.snrwlns;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.example.snrwlns.adapters.EventsAdapter;
import com.example.snrwlns.adapters.FriendsAdapter;
import com.example.snrwlns.threads.GetEventDataObject2;
import com.example.snrwlns.threads.GetFriendEventDataObject;

public class MainFragment extends Fragment {
	
	private View viewer = null;
	public static final String TAG 													= "SNRWLNS";
	public static final String PREFS_NAME 											= "SNRWLNS";
	private ProgressDialog pd, pd1;
	private LinkedHashMap<Integer, LinkedHashMap<String, String>> adapterData;
	private ArrayList<HashMap<String, String>> friendsData;
	private LinkedHashMap<String, ArrayList<List<String>>> meData;
	public EventsAdapter eventsAdapter;
	private ListView list;
	private TabHost mTabHost;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		Log.d(TAG, "MainFragment onCreate()");
		Log.d(TAG, "MainFragment: Process Dialog is starting");
		pd = new ProgressDialog(getActivity());
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Working...");
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.show();
		SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("currentTab", "eventsTab");
		editor.commit();
	}
	
	private static View createTabView(final Context context, final String text, int resId) {
		View view = LayoutInflater.from(context).inflate(R.layout.tab_row, null);
		ImageView tv = (ImageView) view.findViewById(R.id.pictureTabName123);
		tv.setBackgroundResource(resId);
		return view;
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
    
    private byte[] getFriendDetailsObject(){
    	FileInputStream in;
    	SharedPreferences settings =  getActivity().getSharedPreferences(PREFS_NAME, 0);
    	settings.getInt("userFriendDataLength", 0);
    	byte[] buffer = new byte[settings.getInt("userFriendDataLength", 0)];
		try {
			in = getActivity().openFileInput("userFriendData");
	    	in.read(buffer, 0, buffer.length);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return buffer;
    }
    
    private byte[] getUserInfoObject(){
    	FileInputStream in;
    	SharedPreferences settings =  getActivity().getSharedPreferences(PREFS_NAME, 0);
    	settings.getInt("userAccountDataLength", 0);
    	byte[] buffer = new byte[settings.getInt("userAccountDataLength", 0)];
		try {
			in = getActivity().openFileInput("userAccountData");
	    	in.read(buffer, 0, buffer.length);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return buffer;
    }
    
	@Override
	 public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i(TAG, "Mainfragment: OnCreateView");
		viewer = inflater.inflate(R.layout.mainfragment, container, false);
		
		mTabHost = (TabHost) viewer.findViewById(R.id.tabhost);
		mTabHost.setup();
			 
		TabHost.TabSpec spec = mTabHost.newTabSpec("Events");
		spec.setContent(R.id.eventsListView);
		View tabview = createTabView(mTabHost.getContext(), "Events", R.drawable.eventstabselector);
		spec.setIndicator(tabview);
		mTabHost.addTab(spec);
		
		TabHost.TabSpec spec1 = mTabHost.newTabSpec("Friends");
		spec1.setContent(R.id.friendsListView);
		View tabview1 = createTabView(mTabHost.getContext(), "Friends", R.drawable.friendstabselector);
		spec1.setIndicator(tabview1);
		mTabHost.addTab(spec1);
		
		TabHost.TabSpec spec2 = mTabHost.newTabSpec("Me");
		spec2.setContent(R.id.meView);
		View tabview2 = createTabView(mTabHost.getContext(), "Friends", R.drawable.mestabselector);
		spec2.setIndicator(tabview2);
	
		mTabHost.addTab(spec2);
		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
			
			@Override
			public void onTabChanged(String tabId) {
				int i = mTabHost.getCurrentTab();
				SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
    	        SharedPreferences.Editor editor = settings.edit();
				if (i==0) {
					Log.i(TAG, "SETTING THE EVENTS TAB");
					editor.putString("currentTab", "eventsTab");
				}
				else if (i==1) {
					Log.i(TAG, "SETTING THE FRIENDS TAB");
					editor.putString("currentTab", "friendsTab");
				}
				else if (i==2) {
					Log.i(TAG, "SETTING THE ME TAB");
					editor.putString("currentTab", "meTab");
				}
				else {
					Log.i(TAG, "SETTING THE EVENTS TAB");
					editor.putString("currentTab", "eventsTab");
				}
				editor.commit();
			}
		});
		
		
		SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
		if (settings.getString("currentTab", "eventsTab").equalsIgnoreCase("eventsTab")) {
			mTabHost.setCurrentTab(0);
		}
		else if (settings.getString("currentTab", "eventsTab").equalsIgnoreCase("friendsTab")) {
			mTabHost.setCurrentTab(1);
		}
		else if (settings.getString("currentTab", "eventsTab").equalsIgnoreCase("meTab")) {
			mTabHost.setCurrentTab(2);
		}
		else {
			Log.e(TAG, "YOU SHOULDN\"T HAVE COME HERE");
			mTabHost.setCurrentTab(0);
		}
		
		return viewer;
	}
	
	@SuppressWarnings("unchecked")
	@Override
    public void onResume() {
    	super.onResume();
    	Log.v(TAG, "Resuming MainFragment!");
		
		final byte[] data = getUserEventDetailsObject();
		byte[] data1 = getFriendDetailsObject();
		byte[] data2 = getUserInfoObject();
		
		final SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
		if (settings.getString("currentTab", "eventsTab").equalsIgnoreCase("eventsTab")) {
			mTabHost.setCurrentTab(0);
		}
		else if (settings.getString("currentTab", "eventsTab").equalsIgnoreCase("friendsTab")) {
			mTabHost.setCurrentTab(1);
		}
		else if (settings.getString("currentTab", "eventsTab").equalsIgnoreCase("meTab")) {
			mTabHost.setCurrentTab(2);
		}
		else {
			Log.e(TAG, "YOU SHOULDN\"T HAVE COME HERE");
			mTabHost.setCurrentTab(0);
		}
		
		{
			ObjectInputStream ois;
			try {
				ois = new ObjectInputStream(new ByteArrayInputStream(data));
				adapterData = (LinkedHashMap<Integer, LinkedHashMap<String, String>>) ois.readObject();	
				Log.i(TAG, "EVENT Data = "+adapterData);
			} catch (StreamCorruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		String filter = null;
		if (settings.getString("filterListView", null) != null) {
			filter = settings.getString("filterListView", null);
			Log.i(TAG, "Filter applied was = "+filter);
			if (!filter.equalsIgnoreCase("ALL EVENTS")) {
				Iterator<Entry<Integer, LinkedHashMap<String, String>>> firstIt = adapterData.entrySet().iterator();
				while(firstIt.hasNext()) {
					Entry<Integer, LinkedHashMap<String, String>> row = firstIt.next();
					HashMap<String, String> rowData = row.getValue();
					Iterator<Entry<String, String>> secondIt = rowData.entrySet().iterator();
					while(secondIt.hasNext()) {
						Entry<String, String> row2 = secondIt.next();
						if (row2.getKey().equalsIgnoreCase("eventType")) {
							if (!row2.getValue().equalsIgnoreCase(filter)) {
								Log.i(TAG, "Removing Event id = "+row.getKey());
								firstIt.remove();
							}
						}
					}
				}
			}
			
//			SharedPreferences.Editor editor = settings.edit();
//	        editor.putString("filterListView", null);
//	        editor.commit();
		}
		
		if (adapterData.size() == 0) {
			((LinearLayout) getActivity().findViewById(R.id.sorryTextlayout)).setVisibility(View.VISIBLE);
			if (filter != null)
				((TextView) getActivity().findViewById(R.id.sorryText)).setText("Sorry! :-(\n\n\nThere are no events of "+filter.toUpperCase()+" category.");
		}
		else {
			((LinearLayout) getActivity().findViewById(R.id.sorryTextlayout)).setVisibility(View.GONE);
		}
		list = (ListView) viewer.findViewById(R.id.eventsListView);
		eventsAdapter =  new EventsAdapter(getActivity(), adapterData);
		list.setAdapter(eventsAdapter);
		((BaseAdapter) ((ListView) viewer.findViewById(R.id.eventsListView)).getAdapter()).notifyDataSetChanged();
			
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adp, View v, int position, long id) {

				Log.i(TAG, "EventId = "+adapterData.keySet().toArray()[position]);
				Integer eventId = (Integer) adapterData.keySet().toArray()[position];
		    	Fragment eventDetailsFragment = new EventDetailsFragment();
		    	Bundle eventDetailsBundle = new Bundle();
		    	eventDetailsBundle.putByteArray("data", data);
		    	eventDetailsBundle.putString("eventId", String.valueOf(eventId));
		    	eventDetailsFragment.setArguments(eventDetailsBundle);
		    	FragmentTransaction ft = getFragmentManager().beginTransaction();
		        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		    	Log.i(TAG, ""+ft.isEmpty());
		        ft.replace(R.id.fragment, eventDetailsFragment, "EventDetailsFragment");
		        ft.setBreadCrumbShortTitle("EventDetailsFragment");
		        ft.addToBackStack(null);
		        ft.commit();
			}
		});
		
		{
			ObjectInputStream ois;
			try {
				ois = new ObjectInputStream(new ByteArrayInputStream(data1));
				friendsData = (ArrayList<HashMap<String, String>>) ois.readObject();
			} catch (StreamCorruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		FriendsAdapter friendAdapter = new FriendsAdapter(getActivity(), friendsData);
		ListView list1 = (ListView) viewer.findViewById(R.id.friendsListView);
		list1.setAdapter(friendAdapter);
		list1.setOnItemClickListener(new OnItemClickListener() {

		 @Override
			public void onItemClick(AdapterView<?> adp, View v, int position, long id) {
			 	Log.i(TAG, "Friend clicked = "+position);
			 	Log.d(TAG, "MainFragment: Process Dialog1 is starting");
			 	pd1 = new ProgressDialog(getActivity());
				pd1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		        pd1.setMessage("Working...");
		        pd1.setIndeterminate(true);
		        pd1.setCancelable(false);
		        pd1.show();
		        String friendsId = friendsData.get(position).get("id").toString();
		        Log.i(TAG, "Friend ID = "+friendsData.get(position).get("id").toString());
		        SharedPreferences.Editor editor = settings.edit();
		        editor.putString("friendsIdFriendsFragment", friendsId);
		        editor.commit();
			 	GetFriendEventDataObject obj = new GetFriendEventDataObject(getActivity(), friendsId, messageFromTagThread);
			 	obj.start();
			}
		});

		{
			ObjectInputStream ois;
			try {
				ois = new ObjectInputStream(new ByteArrayInputStream(data2));
				meData = (LinkedHashMap<String, ArrayList<List<String>>>) ois.readObject();
			} catch (StreamCorruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		if (meData.size() > 0)
			addMeLayoutView(meData);
		 
		if (settings.getBoolean("newNotificationMessage", false) || settings.getBoolean("newNotificationFriendSignUp", false)) {
			SharedPreferences.Editor editor = settings.edit();
        	editor.putBoolean("newNotificationMessage", false);
        	editor.putBoolean("newNotificationFriendSignUp", false);
        	editor.commit();
        	Log.i(TAG, "Notification for a New Friend signup = "+settings.getBoolean("newNotificationFriendSignUp", false));
        	
        	GetEventDataObject2 obj = new GetEventDataObject2(getActivity(), settings.getString("userId", ""), ""+settings.getInt("newNotificationPosition", 0), handler);
        	obj.start();

		}
		else if (settings.getBoolean("newNotificationEvent", false)) {
			SharedPreferences.Editor editor = settings.edit();
        	editor.putBoolean("newNotificationEvent", false);
        	editor.commit();
		}
		if (pd.isShowing())
			pd.dismiss();
    	
    }
	
	public void addMeLayoutView(LinkedHashMap<String, ArrayList<List<String>>> data) {
		LinearLayout v = (LinearLayout) viewer.findViewById(R.id.meView);
		v.removeAllViews();
		LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View vv = vi.inflate(R.layout.melayout, null);
		ImageView imageView = (ImageView) vv.findViewById(R.id.saveEventImage);
		imageView.setImageResource(R.drawable.profiledefault107x106);
		imageView.setAdjustViewBounds(true);
		imageView.setScaleType(ScaleType.CENTER_CROP);

    	String url = data.get("friendInfo").get(0).get(4);
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
    		imageView.setImageBitmap(bm);
    	}	
    	else {
    		ImageLoader imageLoader = new ImageLoader(getActivity(), R.drawable.profiledefault);
    		imageLoader.DisplayImage(url, getActivity(), imageView);
    	}
    	imageView.setTag(data.get("friendInfo").get(0).get(3).toString());
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		vv.setLayoutParams(params);
		addMeViewToFragment(vv, data);
		v.addView(vv);
	}
	
	private void addMeViewToFragment(View vv, LinkedHashMap<String, ArrayList<List<String>>> meData) {
		 TextView upcomingEventsCount = (TextView) vv.findViewById(R.id.upcomingEventsCount);
		 TextView savedEventsCount = (TextView) vv.findViewById(R.id.savedEventsCount);
		 TextView pastEventsCount = (TextView) vv.findViewById(R.id.pastEventsCount);
		 
		 upcomingEventsCount.setText("0 UPCOMING EVENTS");
		 savedEventsCount.setText("0 SAVED EVENTS");
		 pastEventsCount.setText("0 PAST EVENTS");
		 
		 LinearLayout upcomingEventsLinearLayoutText = (LinearLayout) vv.findViewById(R.id.upcomingEventsLinearLayoutText);
		 LinearLayout savedEventsLinearLayoutText = (LinearLayout) vv.findViewById(R.id.savedEventsLinearLayoutText);
		 LinearLayout pastEventsLinearLayoutText = (LinearLayout) vv.findViewById(R.id.pastEventsLinearLayoutText);
		 
		 if (meData.get("upcomingEvents") != null) {
			 upcomingEventsCount.setText(meData.get("upcomingEvents").size()+" UPCOMING EVENTS");
			 for (int i=0;i<meData.get("upcomingEvents").size();i++) {
				 LinearLayout linearLayout = new LinearLayout(getActivity());
				 LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				 linearLayout.setLayoutParams(linearLayoutParams);
				 linearLayout.setPadding(10, 2, 10, 3);
				 linearLayout.setOrientation(LinearLayout.HORIZONTAL);
				 
				 TextView text = new TextView(getActivity());
				 LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				 text.setLayoutParams(textLayoutParams);
				 text.setTextSize(20);
				 text.setText("¥"+" "+meData.get("upcomingEvents").get(i).get(0)+": ");
				 text.setTextColor(Color.rgb(109, 110, 113));
				 linearLayout.addView(text);
				 
				 
				 TextView textEvent = new TextView(getActivity());
				 LinearLayout.LayoutParams textEventLayoutParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				 textEvent.setLayoutParams(textEventLayoutParams);
				 textEvent.setTextSize(20);
				 textEvent.setEllipsize(TruncateAt.MARQUEE);
				 textEvent.setSingleLine();
				 SpannableString content = new SpannableString(meData.get("upcomingEvents").get(i).get(1));
				 content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
				 textEvent.setText(content);
				 textEvent.setTag(meData.get("upcomingEvents").get(i).get(2));
				 textEvent.setTypeface(null, Typeface.BOLD);
				 textEvent.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							Log.d(TAG, "MainFragment: Process Dialog1 is starting");
							pd1 = new ProgressDialog(getActivity());
							pd1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					        pd1.setMessage("Working...");
					        pd1.setIndeterminate(true);
					        pd1.setCancelable(false);
					        pd1.show();
					        SharedPreferences settings =  getActivity().getSharedPreferences(PREFS_NAME, 0);
							GetEventDataObject2 obj = new GetEventDataObject2(getActivity(), settings.getString("userId", "100"), v.getTag().toString(), handler);
							obj.start();
						}
					});
				 linearLayout.addView(textEvent);
				 upcomingEventsLinearLayoutText.addView(linearLayout);
				 
			 }
		 }

		 
		 if (meData.get("savedEvents")!= null) {
			 savedEventsCount.setText(meData.get("savedEvents").size()+" SAVED EVENTS");
			 for (int i=0;i<meData.get("savedEvents").size();i++) {
				 
				 LinearLayout linearLayout = new LinearLayout(getActivity());
				 LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				 linearLayout.setLayoutParams(linearLayoutParams);
				 linearLayout.setPadding(10, 2, 10, 3);
				 linearLayout.setOrientation(LinearLayout.HORIZONTAL);
				 
				 TextView text = new TextView(getActivity());
				 LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				 text.setLayoutParams(textLayoutParams);
				 text.setTextSize(20);
				 text.setText("¥"+" "+meData.get("savedEvents").get(i).get(0)+": ");
				 text.setTextColor(Color.rgb(109, 110, 113));
				 linearLayout.addView(text);
				 
				 TextView textEvent = new TextView(getActivity());
				 LinearLayout.LayoutParams textEventLayoutParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				 textEvent.setLayoutParams(textEventLayoutParams);
				 textEvent.setTextSize(20);
				 textEvent.setEllipsize(TruncateAt.MARQUEE);
				 textEvent.setSingleLine();
				 SpannableString content = new SpannableString(meData.get("savedEvents").get(i).get(1));
				 content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
				 textEvent.setText(content);
				 textEvent.setTag(meData.get("savedEvents").get(i).get(2));
				 textEvent.setTypeface(null, Typeface.BOLD);
				 textEvent.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							Log.d(TAG, "MainFragment: Process Dialog1 is starting");
							pd1 = new ProgressDialog(getActivity());
							pd1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					        pd1.setMessage("Fetching event details...");
					        pd1.setIndeterminate(true);
					        pd1.setCancelable(false);
					        pd1.show();
					        SharedPreferences settings =  getActivity().getSharedPreferences(PREFS_NAME, 0);
							GetEventDataObject2 obj = new GetEventDataObject2(getActivity(), settings.getString("userId", "100"), v.getTag().toString(), handler);
							obj.start();
						}
					});
				 linearLayout.addView(textEvent);
				 savedEventsLinearLayoutText.addView(linearLayout);
			 }
		 }
		 
		 if (meData.get("pastEvents") != null) {
			 pastEventsCount.setText(meData.get("pastEvents").size()+" PAST EVENTS");
			 for (int i=0;i<meData.get("pastEvents").size();i++) {
				 LinearLayout linearLayout = new LinearLayout(getActivity());
				 LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				 linearLayout.setLayoutParams(linearLayoutParams);
				 linearLayout.setPadding(10, 2, 10, 3);
				 linearLayout.setOrientation(LinearLayout.HORIZONTAL);
				 
				 TextView text = new TextView(getActivity());
				 LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				 text.setLayoutParams(textLayoutParams);
				 text.setTextSize(20);
				 text.setText("¥"+" "+meData.get("pastEvents").get(i).get(0)+": ");
				 text.setTextColor(Color.rgb(109, 110, 113));
				 linearLayout.addView(text);
				 
				 TextView textEvent = new TextView(getActivity());
				 LinearLayout.LayoutParams textEventLayoutParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				 textEvent.setLayoutParams(textEventLayoutParams);
				 textEvent.setTextSize(20);
				 textEvent.setEllipsize(TruncateAt.MARQUEE);
				 textEvent.setSingleLine();
				 SpannableString content = new SpannableString(meData.get("pastEvents").get(i).get(1));
				 content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
				 textEvent.setText(content);
				 textEvent.setTag(meData.get("pastEvents").get(i).get(2));
				 textEvent.setTypeface(null, Typeface.BOLD);
				 textEvent.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Log.d(TAG, "MainFragment: Process Dialog1 is starting");
						pd1 = new ProgressDialog(getActivity());
						pd1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				        pd1.setMessage("Working...");
				        pd1.setIndeterminate(true);
				        pd1.setCancelable(false);
				        pd1.show();
				        SharedPreferences settings =  getActivity().getSharedPreferences(PREFS_NAME, 0);
				        GetEventDataObject2 obj = new GetEventDataObject2(getActivity(), settings.getString("userId", "100"), v.getTag().toString(), handler);
						obj.start();
					}
				});

				 linearLayout.addView(textEvent);
				 pastEventsLinearLayoutText.addView(linearLayout);
			 }
		 }
	}
	
	private Handler messageFromTagThread = new Handler() {
    	@Override
    	public void handleMessage(Message msg) {
    		Log.i(TAG, "In Handler for friend data thead, result = "+msg.what);
			pd1.dismiss();
    		if (msg.what == 389) {
    			byte[] data = msg.getData().getByteArray("friendsData");
				Log.i(TAG, "Open the Friends Event activity");
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				Fragment friendsFragment = new FriendsFragment();

    	    	Bundle friendsFragmentBundle = new Bundle();
    	    	friendsFragmentBundle.putByteArray("friendsData", data);
    	    	friendsFragmentBundle.putString("friendUserId", (String)msg.obj);
    	    	friendsFragment.setArguments(friendsFragmentBundle);
    	    	
    	        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
    	        ft.addToBackStack(null);
    	        ft.replace(R.id.fragment, friendsFragment, "FriendsFragment");
    	        ft.setBreadCrumbShortTitle("FriendsFragment");
    	        ft.commit();
    		}
    	}
    };
    
    @Override
    public void onPause() {
    	super.onPause();
    	Log.d(TAG, "MainFragment onPause()");
    }
    
    @Override
    public void onStop() {
    	super.onStop();
    	Log.d(TAG, "MainFragment onStop()");
    }
    
    @Override
    public void onDestroyView() {
    	super.onDestroyView();
    	Log.d(TAG, "MainFragment onDestroyView()");
    }
    
	private Handler handler = new Handler() {
    	@Override
    	public void handleMessage(Message msg) {
    		Log.i(TAG, "Main Fragment: In Handler for MainFragment to get the EventData2 object thead, result = "+msg.arg1+" hand");
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
		    	FragmentTransaction ft = getFragmentManager().beginTransaction();
		    	ft.addToBackStack(null);
		    	ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		    	getFragmentManager().findFragmentByTag("EventDetailsFragment");
		    	
		        ft.replace(R.id.fragment, eventDetailsFragment, "EventDetailsFragment");
		        ft.setBreadCrumbShortTitle("EventDetailsFragment");
		        ft.commit();
    		}
    	}
    };
}
