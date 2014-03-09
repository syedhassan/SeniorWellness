package com.example.snrwlns.threads;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

public class GetMeDataObject extends Thread implements Runnable {
	
	protected String userId;
	protected Context context;
	protected Handler handler;
	protected CyclicBarrier barrier;
	protected HashMap<String, ArrayList<List<String>>> meData;
	protected static final String TAG 												= "SNRWLNS";
	protected String serverLink = "http://arcweb.arctry.com:8080/snrwlns_server/Main?getMeDataForPhone";
	protected static final String demoServerLink = "http://arcweb.arctry.com:8080/snrwlns_server1/Main?getMeDataForPhone";
	public static final String PREFS_NAME = "SNRWLNS";
	protected byte[] data;
	
	public GetMeDataObject (CyclicBarrier barrier, Context context, String userId, Handler handler) {
		this.userId = userId;
		this.context = context;
		this.handler = handler;
		this.barrier = barrier;
	}
	
	@Override
	public void run() {
		Log.i(TAG, "Entering the thread GetMeDataObject");
		SharedPreferences settings =  context.getSharedPreferences(PREFS_NAME, 0);
		if (settings.getString("serverLink", "demoServer").equalsIgnoreCase("demoServer")) {
			Log.i(TAG, "Demo Server");
		}
		HttpPost postMethod = new HttpPost(settings.getString("serverLink", "demoServer").equalsIgnoreCase("demoServer")? demoServerLink:serverLink);
		DefaultHttpClient httpClient = new DefaultHttpClient();
		Message msg = new Message();
		try {
			sleep(1000);
			
			postMethod.setHeader("getMeDataForPhone", "getMeDataForPhone");
			postMethod.setHeader("userId", userId);
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

			saveUserInfoObject(data);
	        
			Bundle bundle = new Bundle();
			bundle.putByteArray("meData", data);
			msg.what = 2003;
			msg.setData(bundle);
			if (barrier !=null) {
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
			}
		} catch(ConnectException e) {
			Log.e(TAG, "Get Me Data: Socket exception occured");
			Log.e(TAG, "Get Me Data: Get MeData item was not sent to server");
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
			if (handler != null)
				handler.sendMessage(msg);
		}
	}
	
    private boolean saveUserInfoObject(byte[] userAccountData) {
    	
		
		SharedPreferences settings =  context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        Log.v(TAG, "SAVING LENGTH = "+userAccountData.length);
        editor.putInt("userAccountDataLength", userAccountData.length);
        editor.commit();
        
    	FileOutputStream fos;
		try {
			fos = context.openFileOutput("userAccountData", Context.MODE_PRIVATE);
			fos.write(userAccountData);
	    	fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	return true;
    }
}
