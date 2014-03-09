package com.example.snrwlns.threads;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ConnectException;

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

public class C2dmGetEventDataObject extends Thread implements Runnable {
	
	
	protected String userId;
	protected Context context;
	protected Handler handler;
	protected String TAG = "SNRWLNS";
	protected String 				  serverLink = "http://arcweb.arctry.com:8080/snrwlns_server/Main?getEventsDataForPhone";
	protected static final String demoServerLink = "http://arcweb.arctry.com:8080/snrwlns_server1/Main?getEventsDataForPhone";
	public static final String PREFS_NAME = "SNRWLNS";
	protected byte[] data;
	
	public C2dmGetEventDataObject (Context context, String userId, Handler handler) {
		this.userId = userId;
		this.context = context;
		this.handler = handler;
	}
	
	@Override
	public void run() {
		Log.i(TAG, "Entering the thread for C2dmGetEventDataObject");
		SharedPreferences settings =  context.getSharedPreferences(PREFS_NAME, 0);
		if (settings.getString("serverLink", "demoServer").equalsIgnoreCase("demoServer")) {
			Log.i(TAG, "Demo Server");
		}
		HttpPost postMethod = new HttpPost(settings.getString("serverLink", "demoServer").equalsIgnoreCase("demoServer")? demoServerLink:serverLink);
		DefaultHttpClient httpClient = new DefaultHttpClient();
		Message msg = new Message();
		try {
			sleep(100);
			postMethod.setHeader("getEventsDataForPhone", "getEventsDataForPhone");
			postMethod.setHeader("userId", userId);
			httpClient.execute(postMethod, new ResponseHandler<Void>() {
			      public Void handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
			        HttpEntity resp_entity = response.getEntity();
			        if (resp_entity != null) {
			          try {
			            data = EntityUtils.toByteArray(resp_entity);
			            saveUserEventDetailsObject(data);

			          }
			          catch (Exception e) {
			            Log.e(TAG, "problem processing post response", e);
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
			bundle.putByteArray("mainData", data);
			msg.what = 2001;
			msg.setData(bundle);

		} catch(ConnectException e) {
			Log.e(TAG, "Get Events: Socket exception occured");
			Log.e(TAG, "Get Events: Get Events item was not sent to server");
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
	
    private boolean saveUserEventDetailsObject(byte[] userEventData) {
		
		SharedPreferences settings =  context.getSharedPreferences("SNRWLNS", 0);
        SharedPreferences.Editor editor = settings.edit();
        Log.v(TAG, "Event File writing length = "+userEventData.length);
        editor.putInt("userEventDataLength", userEventData.length);
        editor.commit();
        
    	FileOutputStream fos;
		try {
			fos = context.openFileOutput("userEventData", Context.MODE_PRIVATE);
			fos.write(userEventData);
	    	fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	return true;
    }
}
