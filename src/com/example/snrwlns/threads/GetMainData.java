package com.example.snrwlns.threads;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class GetMainData extends Thread implements Runnable{
	
	protected Context context;
	protected String userId;
	protected Handler handler;
	protected List<HashMap<String, String>> friendsData;
	protected HashMap<String, ArrayList<List<String>>> meData;
	protected String responseData;
	private boolean done = false;
	private CyclicBarrier barrier;
	public static final String TAG 			= "SNRWLNS";
	public static final String PREFS_NAME 	= "SNRWLNS";
	
	public GetMainData(Context context, String userId, Handler handler) {
		this.context = context;
		this.userId = userId;
		this.handler = handler;
	}
	
	@Override
	public void run() {
		GetEventDataObject edo = null;
		GetFriendDataObject fdo = null;
		GetMeDataObject mdo = null;
		
        try {
            barrier = new CyclicBarrier(3, new Runnable() {
            	public void run() {
            		done = true;
            		Log.d(TAG, "Barrier complete");
            	}
            });
            
        	edo = new GetEventDataObject(barrier, context, userId, messageFromThread);
        	edo.start();
        	
        	fdo = new GetFriendDataObject(barrier, context, userId, messageFromThread);
        	fdo.start();
        	
        	mdo = new GetMeDataObject(barrier, context, userId, messageFromThread);
        	mdo.start();
        	
            Thread.sleep(1000);
        } 
        catch (Throwable t) {
            t.printStackTrace ();
        } 
        finally {
        	Log.i(TAG, "Finally loop");
        	long start = System.currentTimeMillis();
        	while (mainUriBytes ==null); while (friendsUriBytes ==null); while (meDataBytes ==null);
        	Log.i(TAG, "GET MAIN DATA took = "+(System.currentTimeMillis()-start));
    		isDone();
        }
	}
	
	protected byte[] mainUriBytes, friendsUriBytes, meDataBytes;
    private Handler messageFromThread = new Handler() {
    	@Override
    	public void handleMessage(Message msg) {
    		Log.i(TAG, "In Handler for diary thead, result= "+msg.what);
    		if (msg.what == 2001) {
    			mainUriBytes = msg.getData().getByteArray("mainData");
    			Log.i(TAG, ""+mainUriBytes.length);
    		}
    		else if (msg.what == 2002) {
    			friendsUriBytes = msg.getData().getByteArray("friendsData");
    			Log.i(TAG, ""+friendsUriBytes.length);
    		}
    		else if (msg.what == 2003) {
    			meDataBytes = msg.getData().getByteArray("meData");
    			Log.i(TAG, ""+meDataBytes.length);

    		}
    		else {
    			Log.e(TAG, "GetMainData: Some error occurred and msg.what = "+msg.what);
    		}
    	}
    	
    };
    
    public void sendMessageToMainClass() {
    	Message msg = new Message();
		msg.what = 200;
		msg.arg1 = 121;
		Bundle bundle = new Bundle();
		bundle.putByteArray("mainData", mainUriBytes);
		bundle.putByteArray("friendsData", friendsUriBytes);
		bundle.putByteArray("meData", meDataBytes);
		msg.setData(bundle);
		handler.sendMessage(msg);
    }
    
	@SuppressWarnings("unchecked")
	public boolean isDone() {
		Log.i(TAG, "Checking done = "+done+" Size of friendsUriBytes = "+friendsUriBytes.length);
		if (done) {
			
			ObjectInputStream ois;
			try {
				ois = new ObjectInputStream(new ByteArrayInputStream(friendsUriBytes));
				friendsData = (List<HashMap<String, String>>) ois.readObject();
				Log.i(TAG, "friendsData = "+friendsData);
			} catch (StreamCorruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			sendMessageToMainClass();
		}
		return done;
	}
}
