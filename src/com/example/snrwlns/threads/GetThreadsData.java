package com.example.snrwlns.threads;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class GetThreadsData extends Thread implements Runnable{
	
	protected Context context;
	protected String eventId;
	protected Handler handler;
	protected HashMap<String, String> friendsUriData;
	protected List<HashMap<String, String>> messageData;
	protected ArrayList<String> eventPicturesUriData, eventVideosUriData;
	protected String responseData;
	
	private boolean done = false;
	CyclicBarrier barrier;
	public static final String TAG = "SNRWLNS";
	public static final String PREFS_NAME = "SNRWLNS";
	
	public GetThreadsData(Context context, String eventId, Handler handler) {
		this.context = context;
		this.eventId = eventId;
		this.handler = handler;
	}
	
	@Override
	public void run() {
		DownloadFriendsPicturesCB friendsPictures = null;
		DownloadEventPicturesCB eventPictures = null;
		DownloadEventVideosCB eventVideos = null;
		GetEventMessageObject messages = null;
		
		
        try {
            barrier = new CyclicBarrier(5, new Runnable() {
            	public void run() {
            		done = true;
            	}
            });
        	friendsPictures = new DownloadFriendsPicturesCB(barrier, context, eventId, messageFromThread);
        	friendsPictures.start();
        	
        	eventPictures = new DownloadEventPicturesCB(barrier, context, eventId, messageFromThread);
        	eventPictures.start();
        	
        	eventVideos = new DownloadEventVideosCB(barrier, context, eventId, messageFromThread);
        	eventVideos.start();
        	
        	SharedPreferences settings =  context.getSharedPreferences(PREFS_NAME, 0);
        	
        	GetEventResponseObject obj = new GetEventResponseObject(barrier, context, settings.getString("userId", "100"), eventId, messageFromThread);
        	obj.start();
        	
        	messages = new GetEventMessageObject(barrier, context, eventId, messageFromThread);
        	messages.start();
        	
            Thread.sleep(1000);
        } 
        catch (Throwable t) {
            t.printStackTrace ();
        } 
        finally {
        	Log.i(TAG, "Finally loop");
        	while (friendsUriBytes ==null & eventPicturesUriBytes ==null &  eventVideosUriBytes ==null &  responseString ==null &  messageBytes ==null);
        	isDone();
        }
	}
	
	public byte[] friendsUriBytes, eventPicturesUriBytes, eventVideosUriBytes, messageBytes;
	public String responseString;
    private Handler messageFromThread = new Handler() {
    	@Override
    	public void handleMessage(Message msg) {
    		Log.i(TAG, "In Handler for Event Details thread to return all data to EventDetailsFragment screen result= "+msg.what);
    		if (msg.what == 101) {
    			friendsUriBytes = msg.getData().getByteArray("picturesUri");
				
    		}
    		else if (msg.what == 102) {
    			eventPicturesUriBytes = msg.getData().getByteArray("eventPicturesUri");
    		}
    		else if (msg.what == 103) {
    			eventVideosUriBytes = msg.getData().getByteArray("eventVideosUri");
    		}
    		else if (msg.what == 104) {
    			responseString = msg.getData().getString("eventResponseString");
    		}
    		else if (msg.what == 105) {
    			messageBytes = msg.getData().getByteArray("eventMessages");
    		}
    		else {
    			Log.e(TAG, "GetThreadsData : Some error occurred and msg.what = "+msg.what);
    		}
    	}
    };
    
	public boolean isDone() {
		Message msg = new Message();
		if (done) {
			msg.what = 200;
			msg.arg1 = 121;
			Bundle bundle = new Bundle();

			bundle.putByteArray("friendsUriData", friendsUriBytes);
			bundle.putByteArray("eventPicturesUri", eventPicturesUriBytes);
			bundle.putByteArray("eventVideosUri", eventVideosUriBytes);
			bundle.putString("eventResponseString", responseString);
			bundle.putByteArray("eventMessage", messageBytes);
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
		return done;
	}
}
