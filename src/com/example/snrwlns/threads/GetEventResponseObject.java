package com.example.snrwlns.threads;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.net.ConnectException;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

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


public class GetEventResponseObject extends Thread implements Runnable {
	
	protected String userId, eventId, responseString;
	protected Handler handler;
	protected Context context;
	protected CyclicBarrier barrier;
	protected byte[] data;
	protected String TAG = "SNRWLNS";
	protected String serverLink = "http://arcweb.arctry.com:8080/snrwlns_server/Main?getEventResponse";
	protected static final String demoServerLink = "http://arcweb.arctry.com:8080/snrwlns_server1/Main?getEventResponse";
	public static final String PREFS_NAME = "SNRWLNS";
	
	public GetEventResponseObject (CyclicBarrier barrier, Context context, String userId, String eventId, Handler handler) {
		this.userId = userId;
		this.eventId = eventId;
		this.context = context;
		this.handler = handler;
		this.barrier = barrier;
	}
	
    @Override
	public void run() {
		Log.i(TAG, "Entering the thread GetEventResponseObject with userID = "+userId+" and eventId = "+eventId);
		SharedPreferences settings =  context.getSharedPreferences(PREFS_NAME, 0);
		if (settings.getString("serverLink", "demoServer").equalsIgnoreCase("demoServer")) {
			Log.i(TAG, "Demo Server");
		}
		HttpPost postMethod = new HttpPost(settings.getString("serverLink", "demoServer").equalsIgnoreCase("demoServer")? demoServerLink:serverLink);
		DefaultHttpClient httpClient = new DefaultHttpClient();
		Message msg = new Message();
		try {
			sleep(100);
			postMethod.setHeader("getEventResponse", "getEventResponse");
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
			{
				ObjectInputStream ois;
				try {
					ois = new ObjectInputStream(new ByteArrayInputStream(data));
					responseString = (String) ois.readObject();
				} catch (StreamCorruptedException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
            Log.i(TAG, "Event Response String ===>"+responseString);
			Bundle bundle = new Bundle();
			bundle.putString("eventResponseString", responseString);
				msg.what = 104;
				msg.setData(bundle);

			try {
				barrier.await();
			} 
			catch (InterruptedException ex){
				ex.printStackTrace();
				return;
			} catch (BrokenBarrierException e) {
				e.printStackTrace();
			}
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
