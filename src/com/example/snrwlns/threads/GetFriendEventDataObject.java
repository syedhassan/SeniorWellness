package com.example.snrwlns.threads;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.httpclient.HttpException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class GetFriendEventDataObject extends Thread implements Runnable {
	
	protected String userId;
	protected Context context;
	protected Handler handler;
	protected HashMap<String, ArrayList<List<String>>> friendEventData;
	protected String TAG = "SNRWLNS";
	protected String serverLink = "http://arcweb.arctry.com:8080/snrwlns_server/Main?getFriendEventDataForPhone";
	protected static final String demoServerLink = "http://arcweb.arctry.com:8080/snrwlns_server1/Main?getFriendEventDataForPhone";
	public static final String PREFS_NAME = "SNRWLNS";
	protected byte[] data;
	public GetFriendEventDataObject (Context context, String userId, Handler handler) {
		this.userId = userId;
		this.context = context;
		this.handler = handler;
	}
	
	@Override
	public void run() {
		Log.i(TAG, "Entering the thread to send Get Friend's data item from server for the friend with userId = "+userId);
		SharedPreferences settings =  context.getSharedPreferences(PREFS_NAME, 0);
		if (settings.getString("serverLink", "demoServer").equalsIgnoreCase("demoServer")) {
			Log.i(TAG, "Demo Server");
		}
		HttpPost postMethod = new HttpPost(settings.getString("serverLink", "demoServer").equalsIgnoreCase("demoServer")? demoServerLink:serverLink);
		DefaultHttpClient httpClient = new DefaultHttpClient();
		Message msg = new Message();
		try {
			sleep(10);
			postMethod.setHeader("getFriendEventDataForPhone", "getFriendEventDataForPhone");
			postMethod.setHeader("userId", userId);
			httpClient.execute(postMethod, new ResponseHandler<Void>() {
			      public Void handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
			        HttpEntity resp_entity = response.getEntity();
			        if (resp_entity != null) {
			          try {
			            data = EntityUtils.toByteArray(resp_entity);
			            saveFriendsDetailsObject(data);
			          }
			          catch (Exception e) {
			            Log.e(getClass().getSimpleName(), "problem processing post response", e);
			          }
			        }
			        else {
			          throw new IOException(
			              new StringBuffer()
			                  .append("HTTP response : ").append(response.getStatusLine())
			                  .toString());
			        }
			        return null;
			      }
			    });
			Bundle bundle = new Bundle();
			bundle.putByteArray("friendsData", data);
			msg.what = 389;
			msg.obj=userId;
			msg.setData(bundle);
		} catch(ConnectException e) {
			Log.e(TAG, "Get Friend Event Data: Socket exception occured");
			Log.e(TAG, "Get Friend Event Data: Get friendEventData item was not sent to server");
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			handler.sendMessage(msg);
		}
	}
	
    private boolean saveFriendsDetailsObject(byte[] friendsData) {
		
		SharedPreferences settings =  context.getSharedPreferences("SNRWLNS", 0);
        SharedPreferences.Editor editor = settings.edit();
        Log.v(TAG, "Event File writing length = "+friendsData.length);
        editor.putInt("friendsDataLength", friendsData.length);
        editor.commit();
        
    	FileOutputStream fos;
		try {
			fos = context.openFileOutput("friendsData", Context.MODE_PRIVATE);
			fos.write(friendsData);
	    	fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	return true;
    }
}
