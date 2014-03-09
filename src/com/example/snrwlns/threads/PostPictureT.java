package com.example.snrwlns.threads;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class PostPictureT extends Thread implements Runnable {
	
	protected String pictureName, eventId, pictureLocation, userId;
	protected Context context;
	public static final String TAG = "SNRWLNS";
	protected static final String serverLink = "http://arcweb.arctry.com:8080/snrwlns_server/Main";
	protected static final String demoServerLink = "http://arcweb.arctry.com:8080/snrwlns_server1/Main";
	public static final String PREFS_NAME = "SNRWLNS";
	
	public PostPictureT(Context context, String pictureName, String pictureLocation, String eventId, String userId){
		this.pictureName = pictureName;
		this.pictureLocation = pictureLocation;
		this.eventId = eventId;
		this.userId = userId;
		this.context = context;
	}
	
	@Override
	public void run() {
		Log.i(TAG, "Entering the post thumbnail thread, "+pictureLocation+" Name = "+pictureName+" and Event id = "+eventId+" and userId = "+userId);
		SharedPreferences settings =  context.getSharedPreferences(PREFS_NAME, 0);
		if (settings.getString("serverLink", "demoServer").equalsIgnoreCase("demoServer")) {
			Log.i(TAG, "Demo Server");
		}
		PostMethod post = new PostMethod(settings.getString("serverLink", "demoServer").equalsIgnoreCase("demoServer")? demoServerLink:serverLink);
		String responseString = "";
		File picture = null;
		try {
			sleep(10);
			picture = new File(pictureLocation);
			post.setRequestEntity(new InputStreamRequestEntity(new FileInputStream(picture), picture.length()));
			post.setRequestHeader("Content-type", "image/jpg");
			post.setQueryString("saveImagesT");
			post.setRequestHeader("fileName", pictureName);
			post.setRequestHeader("eventId", eventId);
			post.setRequestHeader("userId", userId);
			HttpClient httpclient = new HttpClient();
			int result = httpclient.executeMethod(post);
			responseString = post.getResponseBodyAsString();
			Log.i(TAG, "Result opf POST = "+result);
			if (result == 200) {
				Log.i(TAG, "Response status code: " + result);
				Log.i(TAG, "Response body :"+post.getResponseBodyAsString());
			}
			else {
				Log.e(TAG, "Picture was not save on the database");
				Log.e(TAG, "Response Body:"+post.getResponseBodyAsString());
			}
		} catch(ConnectException e) {
			Log.e(TAG, "Socket exception occured");
			Log.e(TAG, "Picture was not save on the database");
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
			post.releaseConnection();
			picture.delete();
		}
	}
}
