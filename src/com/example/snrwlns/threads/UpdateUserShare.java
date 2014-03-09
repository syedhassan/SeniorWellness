package com.example.snrwlns.threads;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.util.HashMap;

import org.apache.commons.httpclient.HttpException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class UpdateUserShare extends Thread implements Runnable {
	
	protected HashMap<String, String> data;
	protected Handler handler;
	protected Context context;
	protected String userId;
	protected static final String serverLink = "http://arcweb.arctry.com:8080/snrwlns_server/Main?updateUserShare";
	protected static final String demoServerLink = "http://arcweb.arctry.com:8080/snrwlns_server1/Main?updateUserShare";
	public static final String PREFS_NAME = "SNRWLNS";
	protected static final String TAG = "SNRWLNS";
	protected byte[] databytes;
	
	public UpdateUserShare (Context context, String userId, HashMap<String, String> data, Handler handler) {
		this.data = data;
		this.userId = userId;
		this.handler = handler;
		this.context = context;
	}
	
	@Override
	public void run() {
		Log.i(TAG, "Entering the thread to updateUserShare userId = "+userId);
		SharedPreferences settings =  context.getSharedPreferences(PREFS_NAME, 0);
		if (settings.getString("serverLink", "demoServer").equalsIgnoreCase("demoServer")) {
			Log.i(TAG, "Demo Server");
		}
		HttpPost postMethod = new HttpPost(settings.getString("serverLink", "demoServer").equalsIgnoreCase("demoServer")? demoServerLink:serverLink);
		DefaultHttpClient httpClient = new DefaultHttpClient();
//		PostMethod post = new PostMethod(serverLink);
		String responseString="";
		final Message msg = new Message();
		msg.what = 0;
		msg.arg1 = 0;
		msg.obj = "";
		try {
			sleep(10);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
		    ObjectOutputStream oos = new ObjectOutputStream(bos);
		    oos.writeObject(data);
		    oos.flush();
		    oos.close();
		    ByteArrayEntity req_entity = new ByteArrayEntity(bos.toByteArray());
		    
			postMethod.setHeader("updateUserShare", "updateUserShare");
			postMethod.setHeader("userId", userId);
			postMethod.setEntity(req_entity);
			httpClient.execute(postMethod, new ResponseHandler<Void>() {
			      public Void handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
			        HttpEntity resp_entity = response.getEntity();

			        Log.i(TAG, "~"+response.getStatusLine().getStatusCode());
			        Log.i(TAG, "@"+response.getStatusLine().getReasonPhrase());
			        Log.i(TAG, "&"+response.getStatusLine().getProtocolVersion());
			        if (resp_entity != null) {
			          try {
			        	  if (response.getStatusLine().getStatusCode() == 200 & response.getStatusLine().getReasonPhrase().equalsIgnoreCase("OK")) {
			  				msg.what=200;
			  				msg.arg1=1;
			  			}
			  			else {
			  				Log.e(TAG, "Application could not sign in");
			  			}
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
			
		} 
		catch(ConnectException e) {
			Log.e(TAG, "Socket exception occured");
			Log.e(TAG, "Create Account details were not sent to server");
			Log.e(TAG, "Response Body:"+responseString);
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
