package com.example.snrwlns.threads;

import java.io.FileNotFoundException;
import java.io.IOException;
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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class GetSearchDataObject extends Thread implements Runnable {
	
	protected String userId, searchString;
	protected Context context;
	protected Handler handler;
	protected ArrayList<HashMap<String, String>> searchData;
	protected String TAG = "SNRWLNS";
	protected String serverLink = "http://arcweb.arctry.com:8080/snrwlns_server/Main?getSearchEventsDataForPhone";
	protected static final String demoServerLink = "http://arcweb.arctry.com:8080/snrwlns_server1/Main?getSearchEventsDataForPhone";
	public static final String PREFS_NAME = "SNRWLNS";
	protected byte[] data;
	
	public GetSearchDataObject (Context context, String userId, String searchString, Handler handler) {
		this.userId = userId;
		this.searchString = searchString;
		this.context = context;
		this.handler = handler;
	}
	
	@Override	
	public void run() {
		Log.i(TAG, "Entering the thread to search Events item to server");
		SharedPreferences settings =  context.getSharedPreferences(PREFS_NAME, 0);
		if (settings.getString("serverLink", "demoServer").equalsIgnoreCase("demoServer")) {
			Log.i(TAG, "Demo Server");
		}
		HttpPost postMethod = new HttpPost(settings.getString("serverLink", "demoServer").equalsIgnoreCase("demoServer")? demoServerLink:serverLink);
		DefaultHttpClient httpClient = new DefaultHttpClient();
		Message msg = new Message();
		try {
			sleep(10);
			postMethod.setHeader("getSearchEventsDataForPhone", "getSearchEventsDataForPhone");
			postMethod.setHeader("userId", userId);
			postMethod.setHeader("searchString", searchString);
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

			bundle.putByteArray("searchData", data);
			msg.what = 200;
			msg.arg1 = 131;
			msg.setData(bundle);

		} catch(ConnectException e) {
			Log.e(TAG, "Get Search Data: Socket exception occured");
			Log.e(TAG, "Get Search Data: Get Events Search was not sent to server");
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
