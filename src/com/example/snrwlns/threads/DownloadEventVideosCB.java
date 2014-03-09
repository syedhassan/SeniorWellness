package com.example.snrwlns.threads;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
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


public class DownloadEventVideosCB extends Thread implements Runnable{
	protected String eventId;
	protected Handler handler;
	protected Context context;
	private boolean done = false;
	protected ArrayList<String> eventVideosUri;
	protected CyclicBarrier barrier;
	protected static final String TAG = "SNRWLNS";
	protected static final String serverLink = "http://arcweb.arctry.com:8080/snrwlns_server/Main?getEventVideosUri";
	protected static final String demoServerLink = "http://arcweb.arctry.com:8080/snrwlns_server1/Main?getEventVideosUri";
	public static final String PREFS_NAME = "SNRWLNS";
	protected byte[] data;
	
	public DownloadEventVideosCB(CyclicBarrier barrier, Context context, String eventId, Handler handler) {
		this.context = context;
		this.eventId = eventId;
		this.handler = handler;
		this.barrier = barrier;
	}
	
	@Override
	public void run() {
		Log.i(TAG, "Entering the thread to send Get Events item to server");
		SharedPreferences settings =  context.getSharedPreferences(PREFS_NAME, 0);
		if (settings.getString("serverLink", "demoServer").equalsIgnoreCase("demoServer")) {
			Log.i(TAG, "Demo Server");
		}
		HttpPost postMethod = new HttpPost(settings.getString("serverLink", "demoServer").equalsIgnoreCase("demoServer")? demoServerLink:serverLink);
		DefaultHttpClient httpClient = new DefaultHttpClient();
		Message msg = new Message();
		try {
			sleep(10);
			postMethod.setHeader("getEventVideosUri", "getEventVideosUri");
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
			bundle.putByteArray("eventVideosUri", data);
			msg.what = 103;
			msg.setData(bundle);
			try {
				barrier.await();
			} 
			catch (InterruptedException ex){
				ex.printStackTrace();
				return;
			}
			catch (BrokenBarrierException ex) {
				ex.printStackTrace();
				return;
			}
		} catch(ConnectException e) {
			Log.e(TAG, "DownloadEventVideosCB: Socket exception occured");
			Log.e(TAG, "DownloadEventVideosCB: Get Events item was not sent to server");
//			Log.e(TAG, "DownloadEventVideosCB: Response Body:"+responseString);
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
//			post.releaseConnection();
			handler.sendMessage(msg);
		}
	}
	
	public boolean isDone() {
		return done;
	}
}
