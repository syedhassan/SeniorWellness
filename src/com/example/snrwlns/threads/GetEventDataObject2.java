package com.example.snrwlns.threads;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
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

public class GetEventDataObject2 extends Thread implements Runnable {
	
	protected String userId, eventId;
	protected Context context;
	protected Handler handler;
	protected List<HashMap<String, String>> eventData;
	protected String TAG = "SNRWLNS";
	protected String serverLink = "http://arcweb.arctry.com:8080/snrwlns_server/Main?getEventsDataForPhone2";
	protected static final String demoServerLink = "http://arcweb.arctry.com:8080/snrwlns_server1/Main?getEventsDataForPhone2";
	public static final String PREFS_NAME = "SNRWLNS";
	protected byte[] data;
	
	public GetEventDataObject2 (Context context, String userId, String eventId, Handler handler) {
		this.userId = userId;
		this.eventId = eventId;
		this.context = context;
		this.handler = handler;
	}
	
	@Override
	public void run() {
		Log.i(TAG, "Entering the thread to send Get Events2 item to server for userId = "+userId+" and eventId = "+eventId);
		SharedPreferences settings =  context.getSharedPreferences(PREFS_NAME, 0);
		if (settings.getString("serverLink", "demoServer").equalsIgnoreCase("demoServer")) {
			Log.i(TAG, "Demo Server");
		}
		HttpPost postMethod = new HttpPost(settings.getString("serverLink", "demoServer").equalsIgnoreCase("demoServer")? demoServerLink:serverLink);
		DefaultHttpClient httpClient = new DefaultHttpClient();
		Message msg = new Message();
		msg.arg1=0;
		try {
			sleep(10);
			postMethod.setHeader("getEventsDataForPhone2", "getEventsDataForPhone2");
			postMethod.setHeader("userId", userId);
			postMethod.setHeader("eventId", eventId);
			httpClient.execute(postMethod, new ResponseHandler<Void>() {
			      public Void handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
			        HttpEntity resp_entity = response.getEntity();
			        if (resp_entity != null) {
			          try {
			            data = EntityUtils.toByteArray(resp_entity);
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
			bundle.putByteArray("eventData", data);
			msg.what = 634;
			msg.obj = eventId;
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
}
