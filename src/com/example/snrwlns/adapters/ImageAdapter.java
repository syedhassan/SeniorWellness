package com.example.snrwlns.adapters;

import java.io.File;
import java.util.HashMap;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.snrwlns.ImageLoader;
import com.example.snrwlns.R;

public class ImageAdapter extends BaseAdapter {
	
	private HashMap<String, Bitmap> cache = new HashMap<String, Bitmap>();
	private LayoutInflater mInflater;
	private Activity activity;
	private String[] names, ids, url;
	private ImageLoader imageLoader;
	
	public ImageAdapter(Activity activity, String names[], String[] ids, String url[]) {
		imageLoader = new ImageLoader(activity, R.drawable.profiledefault);
		this.activity = activity;
		mInflater = LayoutInflater.from(activity);
		this.names = names;
		this.ids = ids;
		this.url = url;
	}
	
	@Override
	public int getCount() {
		return names.length;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		final ImageView friendPicture; final TextView friendName ;
		if (convertView == null) {
	        convertView = mInflater.inflate(R.layout.eventfriendphotolayoutrow, null);
	        friendPicture = (ImageView) convertView.findViewById(R.id.friendPicture);
	        friendName = (TextView) convertView.findViewById(R.id.friendName);
		}
		else {
			friendPicture = (ImageView) convertView.findViewById(R.id.friendPicture);
			friendName = (TextView) convertView.findViewById(R.id.friendName);
		}
		friendName.setText(names[position]);
		String fileName = url[position].substring(url[position].lastIndexOf("/")+1);
		url[position] = url[position].replace(" ", "%20");
    	File imageFile = new File(Environment.getExternalStorageDirectory().getPath()+"/SeniorWellness/"+fileName);
    	BitmapFactory.Options options = new BitmapFactory.Options();
    	if (imageFile.exists()) {
    		options.inJustDecodeBounds = true;
    		BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath()+"/SeniorWellness/"+fileName, options);
    		Boolean scaleByHeight = Math.abs(options.outHeight - 100) >= Math.abs(options.outWidth - 100);
    		if(options.outHeight * options.outWidth * 2 >= 16384){
    		    double sampleSize = scaleByHeight? options.outHeight / 100: options.outWidth / 100;
    		    options.inSampleSize = (int)Math.pow(2d, Math.floor(Math.log(sampleSize)/Math.log(2d)));
    		}
    		options.inJustDecodeBounds = false; options.inTempStorage = new byte[1024];  options.inPurgeable=true;
    		Bitmap bm = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath()+"/SeniorWellness/"+fileName, options);
    		friendPicture.setImageBitmap(bm);
    	}
    	else {
    		imageLoader.DisplayImage(url[position], activity, friendPicture);
    	}
        friendPicture.setTag(ids[position]);
		return convertView;
	}
	
	public void clearCache() {
	  cache.clear();
	}
}