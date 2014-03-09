package com.example.snrwlns;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.example.snrwlns.threads.GetSearchDataObject;

public class SearchEvents extends FragmentActivity {
	
	/**
	 **  Called when the user confirms sign up.
	 */
	
	private ProgressDialog pd;
	private TextView eventsCountText;
	public static final String TAG = "SNRWLNS";
	public static final String PREFS_NAME = "SNRWLNS";
	private ArrayList<HashMap<String, String>> searchData;
	private EditText searchBox;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    this.setContentView(R.layout.searchevents);
	    
	    this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	    
	    searchBox = (EditText) findViewById(R.id.searchBox);
	    searchBox.setImeOptions(EditorInfo.IME_ACTION_SEARCH);

	    searchBox.setOnEditorActionListener(new OnEditorActionListener() {
	    	
	        @Override
	        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
	            if (event != null&& (event.getKeyCode() == KeyEvent.KEYCODE_ENTER | event.getKeyCode() == KeyEvent.FLAG_EDITOR_ACTION)) {
	            	InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	                in.hideSoftInputFromWindow(searchBox.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

	            	if (searchBox.getText().toString().length() > 0) {
	    				pd = new ProgressDialog(SearchEvents.this);
	    				pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	    		        pd.setMessage("Working...");
	    		        pd.setIndeterminate(true);
	    		        pd.setCancelable(false);
	    		        pd.show();
	    		        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
	    		        imm.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
	    		        
	    				SharedPreferences settings =  getSharedPreferences(PREFS_NAME, 0);
	    			    GetSearchDataObject sdo = new GetSearchDataObject(SearchEvents.this, settings.getString("userId", "100"), searchBox.getText().toString(), handler);
	    			    sdo.start();
	    			}
	    			else {
	    				LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
	    				View layout = inflater.inflate(R.layout.toasttext, (ViewGroup) findViewById(R.id.toast_layout_root));
	    				TextView text = (TextView) layout.findViewById(R.id.text);
	    				text.setText("Please enter some text for search..");
	    				Toast toast = new Toast(getApplicationContext());
	    				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
	    				toast.setDuration(1000);
	    				toast.setView(layout);
	    				toast.show();
	    			}
	            }
	            return false;
	        }
	    });

	    eventsCountText = (TextView) findViewById(R.id.eventsCountText);
	    ImageButton searchImage = (ImageButton) findViewById(R.id.searchImage);
	    searchImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String text = searchBox.getText().toString();
				if (text.length() > 0) {
					pd = new ProgressDialog(SearchEvents.this);
					pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			        pd.setMessage("Working...");
			        pd.setIndeterminate(true);
			        pd.setCancelable(false);
			        pd.show();
			        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
			        imm.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
			        
					SharedPreferences settings =  getSharedPreferences(PREFS_NAME, 0);
				    GetSearchDataObject sdo = new GetSearchDataObject(SearchEvents.this, settings.getString("userId", "100"), text, handler);
				    sdo.start();
				}
				else {
					LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
    				View layout = inflater.inflate(R.layout.toasttext, (ViewGroup) findViewById(R.id.toast_layout_root));
    				TextView text1 = (TextView) layout.findViewById(R.id.text);
    				text1.setText("Please enter some text for search..");
    				Toast toast = new Toast(getApplicationContext());
    				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
    				toast.setDuration(1000);
    				toast.setView(layout);
    				toast.show();
				}
			}
		});
	    
	    ImageButton cancelEventSearching = (ImageButton) findViewById(R.id.cancelEventSearching);
	    cancelEventSearching.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	private Handler handler = new Handler() {
    	@SuppressWarnings("unchecked")
		@Override
    	public void handleMessage(Message msg) {
    		Log.i(TAG, "In Handler for SearchEvents thead, result= "+msg.what);
			pd.dismiss();
    		if (msg.what == 200 & msg.arg1 == 131) {
    			((RelativeLayout) findViewById(R.id.eventsCountLinearLayout)).setVisibility(View.VISIBLE);
    			((RelativeLayout) findViewById(R.id.cancelButtons)).setVisibility(View.VISIBLE);
    			byte[] data = msg.getData().getByteArray("searchData");
    			{
    				ObjectInputStream ois;
    				try {
    					ois = new ObjectInputStream(new ByteArrayInputStream(data));
    					searchData = (ArrayList<HashMap<String, String>>) ois.readObject();
    					//Log.i(TAG, "~~~SEARCH DATA = "+searchData);
    				} catch (StreamCorruptedException e) {
    					e.printStackTrace();
    				} catch (IOException e) {
    					e.printStackTrace();
    				} catch (ClassNotFoundException e) {
    					e.printStackTrace();
    				}
    			}
				RelativeLayout TagLayout = (RelativeLayout) findViewById(R.id.searchListLayout);
				TagLayout.removeAllViews();
				LinearLayout outerLayout = new LinearLayout(SearchEvents.this);
				LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
				outerLayout.setLayoutParams(params);
				outerLayout.setOrientation(LinearLayout.VERTICAL);
				outerLayout.removeAllViews();
				eventsCountText.setText(searchData.size()+" EVENTS FOUND");
				for (int i=0; i<searchData.size();i++) {
					LinearLayout innerLayout = new LinearLayout(SearchEvents.this);
					LayoutParams paramsInner = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
					innerLayout.setLayoutParams(paramsInner);
					innerLayout.setPadding(0, 27, 0, 27);
					innerLayout.setOrientation(LinearLayout.HORIZONTAL);
					innerLayout.setTag(searchData.get(i).get("searchEventId").toString());
					innerLayout.setClickable(true);
					innerLayout.setBackgroundResource(R.drawable.viewclick);
					innerLayout.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
						 	Log.i(TAG, " EVENT ID = "+v.getTag().toString());
						 	SearchEvents.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

					        SharedPreferences settings =  SearchEvents.this.getSharedPreferences(PREFS_NAME, 0);
							SharedPreferences.Editor editor = settings.edit();
							editor.putBoolean("searchEventItem", true);
							editor.putInt("searchEventId", Integer.parseInt(v.getTag().toString()));
							editor.commit();

							Intent mainIntent = new Intent(SearchEvents.this, Main.class);
							mainIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			    			startActivity(mainIntent);
						}
					});
					
					TextView friendsName = new TextView(SearchEvents.this);
					LinearLayout.LayoutParams friendsNameParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
					friendsNameParams.weight = 60;
					friendsNameParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
					friendsName.setLayoutParams(friendsNameParams);
					friendsName.setPadding(20, 0, 0, 0);
					friendsName.setTextSize(20);
					friendsName.setTypeface(null, Typeface.BOLD);
					friendsName.setTextColor(Color.parseColor("#415C93"));
					friendsName.setText(searchData.get(i).get("searchEventName").toString());
					
					LinearLayout border = new LinearLayout(SearchEvents.this);
					LinearLayout.LayoutParams paramsInner1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,1);
					border.setBackgroundColor(Color.BLACK);
					border.setLayoutParams(paramsInner1);
					
					innerLayout.addView(friendsName);
					outerLayout.addView(innerLayout);
					outerLayout.addView(border);
				}
				TagLayout.addView(outerLayout);
    		}
    	}
    };
	
    
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == 2) {
			RelativeLayout headerLayout = (RelativeLayout) findViewById(R.id.headerLayout);
		    MarginLayoutParams params = new MarginLayoutParams(headerLayout.getLayoutParams());
		    params.topMargin=20;params.bottomMargin=15;params.leftMargin=12;params.rightMargin=12;
		    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(params);
		    headerLayout.setLayoutParams(layoutParams);
		}
		else {
			RelativeLayout headerLayout = (RelativeLayout) findViewById(R.id.headerLayout);
		    MarginLayoutParams params = new MarginLayoutParams(headerLayout.getLayoutParams());
		    params.topMargin=60;params.bottomMargin=15;params.leftMargin=12;params.rightMargin=12;
		    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(params);
		    headerLayout.setLayoutParams(layoutParams);
		}
	}
	 
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.e(TAG, "SEARCH EVENTS on DESTROY!!!!!!!");
	}	
	 
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	Log.e(TAG, "BACK KEY was pressed");
	    	Intent resultIntent = new Intent();
			setResult(Activity.RESULT_CANCELED, resultIntent);
			finish();
	        return true;
	    }
	    else if (keyCode == KeyEvent.FLAG_EDITOR_ACTION) {
	    	Log.i(TAG, "Done key");
			if (searchBox.getText().toString().length() > 0) {
				pd = new ProgressDialog(SearchEvents.this);
				pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		        pd.setMessage("Working...");
		        pd.setIndeterminate(true);
		        pd.setCancelable(false);
		        pd.show();
		        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
		        imm.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
		        
				SharedPreferences settings =  getSharedPreferences(PREFS_NAME, 0);
			    GetSearchDataObject sdo = new GetSearchDataObject(SearchEvents.this, settings.getString("userId", "100"), searchBox.getText().toString(), handler);
			    sdo.start();
			}
			else {
				LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
				View layout = inflater.inflate(R.layout.toasttext, (ViewGroup) findViewById(R.id.toast_layout_root));
				TextView text = (TextView) layout.findViewById(R.id.text);
				text.setText("Please enter some text for search..");
				Toast toast = new Toast(getApplicationContext());
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				toast.setDuration(1000);
				toast.setView(layout);
				toast.show();
			}
	    }
	    return super.onKeyDown(keyCode, event);
	}
}
