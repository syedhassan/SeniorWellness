package com.example.snrwlns;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.HashMap;
import java.util.Iterator;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.snrwlns.adapters.ImageAdapter;
import com.example.snrwlns.threads.GetFriendEventDataObject;

public class FriendsPhotoView extends Activity {
	
	private ImageButton cancelButton;
	private byte[] friendsPhotos;
	private int count;
	public static final String TAG = "SNRWLNS";
	protected ImageAdapter adapter;
	private HashMap<String, String> eventVideoList;
	private ProgressDialog pd1;
	
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {

	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.eventfriendphotolayout);
	    friendsPhotos = getIntent().getExtras().getByteArray("eventPhotosUri");	
	    count = getIntent().getExtras().getInt("count");
	    
	    if (count > 1) {
	    	((TextView)findViewById(R.id.signOutText)).setText(count+" FRIENDS SIGNED UP");
	    }
	    else {
	    	((TextView)findViewById(R.id.signOutText)).setText(count+" FRIEND SIGNED UP");
	    }
	    
		{
			ObjectInputStream ois;
			try {
				ois = new ObjectInputStream(new ByteArrayInputStream(friendsPhotos));
				eventVideoList = (HashMap<String, String>) ois.readObject();
			} catch (StreamCorruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		updateView();
	}
	
	
	public void updateView(){
		
		String[] photoUri =  new String[eventVideoList.size()];
		String[] names =  new String[eventVideoList.size()];
		String[] ids =  new String[eventVideoList.size()];
		int counter = 0;
		Iterator<String> it2 = eventVideoList.keySet().iterator();
		while (it2.hasNext()) {
			String nameAndId = it2.next();
			names[counter] = nameAndId.substring(0, nameAndId.indexOf("~#$"));
			ids[counter] = nameAndId.substring(nameAndId.indexOf("~#$")+3);
			photoUri[counter] = eventVideoList.get(nameAndId).replace(" ", "%20");
			counter++;
	    }
		
	    cancelButton = (ImageButton) findViewById(R.id.cancelEventTagging);
	    cancelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	    adapter = new ImageAdapter(FriendsPhotoView.this, names, ids, photoUri);
	    ListView lv = (ListView) findViewById(R.id.friendsLayout);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adp, View v, int position, long id) {
        		pd1 = new ProgressDialog(FriendsPhotoView.this);
				pd1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		        pd1.setMessage("Working...");
		        pd1.setIndeterminate(true);
		        pd1.setCancelable(false);
		        pd1.show();
				ImageView self = (ImageView) v.findViewById(R.id.friendPicture);
                String friendId = self.getTag().toString();
                Log.i(TAG, "The persons userId from teh tag = "+friendId);
                GetFriendEventDataObject obj = new GetFriendEventDataObject(FriendsPhotoView.this, friendId, messageFromTagThread);
			 	obj.start();
			}
		});
	}
	
	private Handler messageFromTagThread = new Handler() {
    	@Override
    	public void handleMessage(Message msg) {
    		Log.i(TAG, "In Handler for friend data thead, result= "+msg.what);
			pd1.dismiss();
    		if (msg.what == 389) {
    			byte[] data = msg.getData().getByteArray("friendsData");
				Log.i(TAG, "Open the Friends Event activity the friend's id = "+msg.obj);
				
				Intent intent = getIntent();
				Bundle bundle = new Bundle();
				bundle.putByteArray("friendData", data);
				bundle.putString("friendUserId", (String)msg.obj);
				intent.putExtras(bundle);
				setResult(Activity.RESULT_OK, intent);
				finish();
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
	   updateView();
	}
	
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	adapter.clearCache();
    }
    
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.e(TAG, "LOW MEMORY");
        adapter.clearCache();
    }
}