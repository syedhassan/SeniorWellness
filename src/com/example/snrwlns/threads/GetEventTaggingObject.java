package com.example.snrwlns.threads;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class GetEventTaggingObject extends Thread implements Runnable {
	
	protected String phoneTime, phoneDate, userId;
	protected Handler handler;
	protected Context context;
	protected HashMap<Integer, String> tagData;
	private static HttpClient mHttpClient;
	public static final int HTTP_TIMEOUT = 30 * 1000;
	private boolean done = false;
	protected String TAG = "SNRWLNS";
	protected String serverLink = "http://arcweb.arctry.com:8080/snrwlns_server/Main";
	protected static final String demoServerLink = "http://arcweb.arctry.com:8080/snrwlns_server1/Main";
	public static final String PREFS_NAME = "SNRWLNS";
	protected byte[] data;
	
	public GetEventTaggingObject (Context context, String phoneTime, String phoneDate, String userId, Handler handler) {
		this.phoneTime = phoneTime;
		this.phoneDate = phoneDate;
		this.userId = userId;
		this.context = context;
		this.handler = handler;
	}
	
	/**
     * Get our single instance of our HttpClient object.
     *
     * @return an HttpClient object with connection parameters set
     */
    private static HttpClient getHttpClient() {
        if (mHttpClient == null) {
            mHttpClient = new DefaultHttpClient();
            final HttpParams params = mHttpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(params, HTTP_TIMEOUT);
            HttpConnectionParams.setSoTimeout(params, HTTP_TIMEOUT);
            ConnManagerParams.setTimeout(params, HTTP_TIMEOUT);
        }
        return mHttpClient;
    }
    
	@Override
	public void run() {
		Log.i(TAG, "GetEventTaggingObject thread");
		double startTime = System.currentTimeMillis();
		HttpClient client = getHttpClient();
		HttpGet request = new HttpGet();
		Message msg = new Message();
		try {
			SharedPreferences settings =  context.getSharedPreferences(PREFS_NAME, 0);
			if (settings.getString("serverLink", "demoServer").equalsIgnoreCase("demoServer")) {
				Log.i(TAG, "Demo Server");
				request.setURI(new URI(serverLink+"?getEventsForTagging&timeFromPhone="+phoneTime+"&dateFromPhone="+phoneDate+"&userId="+userId));
			}
			else {
				request.setURI(new URI(demoServerLink+"?getEventsForTagging&timeFromPhone="+phoneTime+"&dateFromPhone="+phoneDate+"&userId="+userId));
			}
			
			HttpResponse response = client.execute(request);
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
			Bundle bundle = new Bundle();
			bundle.putByteArray("eventTagging", data);
			int statusCode = response.getStatusLine().getStatusCode();
			Log.i(TAG, "GetEventTaggingObject: Status code = "+statusCode);
			if (statusCode == 200) {
				msg.what = 200;
				msg.arg1 = 117;
				msg.setData(bundle);
			}
			else {
				Log.e(TAG, "GetEventTaggingObject");
				msg.what = 0;
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		finally {
			Log.i(TAG, "Time  elapsed = "+(System.currentTimeMillis()-startTime)+" millisecs");
			handler.sendMessage(msg);
		}
	}
    
	public boolean isDone() {
		return done;
	}
}
