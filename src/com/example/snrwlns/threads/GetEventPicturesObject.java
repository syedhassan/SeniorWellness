package com.example.snrwlns.threads;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;

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
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class GetEventPicturesObject extends Thread implements Runnable {
	
	protected Context context;
	protected Handler handler;
	protected ArrayList<HashMap<String, String>> searchData;
	protected String TAG = "SNRWLNS";
	protected String serverLink = "http://arcweb.arctry.com:8080/snrwlns_server/Main?getEventPicturesForPhone";
	protected static final String demoServerLink = "http://arcweb.arctry.com:8080/snrwlns_server1/Main?getEventPicturesForPhone";
	public static final String PREFS_NAME = "SNRWLNS";
	protected byte[] data;
	protected ArrayList<String> eventPictures, fileNames;
	
	public GetEventPicturesObject (Context context, Handler handler) {
		this.context = context;
		this.handler = handler;
	}
	
	@Override	
	public void run() {
		Log.i(TAG, "Entering the thread to get Event's picture from server");
		SharedPreferences settings =  context.getSharedPreferences(PREFS_NAME, 0);
		if (settings.getString("serverLink", "demoServer").equalsIgnoreCase("demoServer")) {
			Log.i(TAG, "Demo Server");
		}
		HttpPost postMethod = new HttpPost(settings.getString("serverLink", "demoServer").equalsIgnoreCase("demoServer")? demoServerLink:serverLink);
		DefaultHttpClient httpClient = new DefaultHttpClient();
		try {
			sleep(10);
			postMethod.setHeader("getEventPicturesForPhone", "getEventPicturesForPhone");
			httpClient.execute(postMethod, new ResponseHandler<Void>() {
			      @SuppressWarnings("unchecked")
				public Void handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
			        HttpEntity resp_entity = response.getEntity();
			        if (resp_entity != null) {
			          try {
			            data = EntityUtils.toByteArray(resp_entity);
			            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
			            eventPictures = (ArrayList<String>) ois.readObject();
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
			
			sendMessageToMainClass();

		} catch(ConnectException e) {
			Log.e(TAG, "Get EventPictures Data: Socket exception occured");
			Log.e(TAG, "Get EventPictures Data: Get Events Search was not sent to server");
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void sendMessageToMainClass() {
    	Message msg = new Message();
    	msg.what=200;
		handler.sendMessage(msg);
    }
}
