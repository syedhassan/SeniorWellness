package com.example.snrwlns.adapters;

import java.io.File;
import java.util.LinkedHashMap;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.snrwlns.ImageLoader;
import com.example.snrwlns.R;

public 	class EventsAdapter extends BaseAdapter {
    LayoutInflater mInflater;      
    static ViewHolder holder;
    private String TAG = "SNRWLNS";
    private Activity context;
    private LinkedHashMap<Integer, LinkedHashMap<String, String>> eventData;
    private Integer[] mKeys;
    public ImageLoader imageLoader; 

    public EventsAdapter(Activity context, LinkedHashMap<Integer, LinkedHashMap<String, String>> eventData) {
        try {
            mInflater = LayoutInflater.from(context);
            this.context = context;
            this.eventData =  new LinkedHashMap<Integer, LinkedHashMap<String, String>>();
            this.eventData.putAll(eventData);
            mKeys = eventData.keySet().toArray(new Integer[eventData.size()]);
            imageLoader = new ImageLoader(context, R.drawable.event100x100);

        } catch (Exception e) {
            Log.v(TAG, "Error = " + e);
        }
    }
    
    @Override
	public int getCount() {
        return eventData.size();
    }
    
    @Override
	public View getView(int position, View convertView, ViewGroup arg2) {
    	
    	Integer key = mKeys[position];
    	if (convertView == null) {
	        convertView = mInflater.inflate(R.layout.eventlistlayout, null);
	        holder = new ViewHolder();
	        holder.eventImage = (ImageView) convertView.findViewById(R.id.eventPicture);
	        holder.eventResponseImage = (ImageView) convertView.findViewById(R.id.eventResponseImage);
	        holder.eventPictureType = (ImageView) convertView.findViewById(R.id.eventPictureType);
	        holder.eventAddress = (TextView) convertView.findViewById(R.id.eventAddress);
	        holder.eventDate = (TextView) convertView.findViewById(R.id.eventDate);
	        holder.eventPersonCount = (TextView) convertView.findViewById(R.id.eventPersonCount);
	        holder.eventName = (TextView) convertView.findViewById(R.id.eventName);
	        holder.eventPictureType.setImageResource(R.drawable.eventslabel100x26);
	        holder.eventName.setTextColor(Color.parseColor(eventData.get(key).get("eventTypeColor").toString()));
	        if (eventData.get(key).get("eventType").toString().equalsIgnoreCase("shows")) {
	        	holder.eventPictureType.setImageResource(R.drawable.showlabel100x26);
	        }
	        else if (eventData.get(key).get("eventType").toString().equalsIgnoreCase("classes")) {
	        	holder.eventPictureType.setImageResource(R.drawable.classlabel100x26);
	        }
	        if (eventData.get(key).get("response").toString().equalsIgnoreCase("yes")) {
	        	holder.eventResponseImage.setVisibility(View.VISIBLE);
	        }
	        else if (eventData.get(key).get("response").toString().equalsIgnoreCase("no")) {
	        	holder.eventResponseImage.setVisibility(View.GONE);
	        }
	        holder.eventAddress.setText(eventData.get(key).get("city").toString());
	        holder.eventDate.setText(eventData.get(key).get("date").toString());
	        holder.eventPersonCount.setText(eventData.get(key).get("count").toString());
	        holder.eventName.setText(eventData.get(key).get("name").toString());
	        String url = eventData.get(key).get("picture").toString();
	        
        	String fileName = url.substring(url.lastIndexOf("/")+1);
        	url = url.replace(" ", "%20");
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
        		holder.eventImage.setImageBitmap(bm);
        	}
        	else {
        		imageLoader.DisplayImage(url, context, holder.eventImage);
        	}
	        convertView.setTag(holder);
    	} 
    	else {
	        holder.eventImage = (ImageView) convertView.findViewById(R.id.eventPicture);
	        holder.eventResponseImage = (ImageView) convertView.findViewById(R.id.eventResponseImage);
	        holder.eventPictureType = (ImageView) convertView.findViewById(R.id.eventPictureType);
	        holder.eventAddress = (TextView) convertView.findViewById(R.id.eventAddress);
	        holder.eventDate = (TextView) convertView.findViewById(R.id.eventDate);
	        holder.eventPersonCount = (TextView) convertView.findViewById(R.id.eventPersonCount);
	        holder.eventName = (TextView) convertView.findViewById(R.id.eventName);
	        holder.eventPictureType.setImageResource(R.drawable.eventslabel100x26);
	        holder.eventName.setTextColor(Color.parseColor(eventData.get(key).get("eventTypeColor").toString()));
	        if (eventData.get(key).get("eventType").toString().equalsIgnoreCase("shows")) {
	        	holder.eventPictureType.setImageResource(R.drawable.showlabel100x26);
	        }
	        else if (eventData.get(key).get("eventType").toString().equalsIgnoreCase("classes")) {
	        	holder.eventPictureType.setImageResource(R.drawable.classlabel100x26);
	        }
	        holder.eventAddress.setText(eventData.get(key).get("city").toString());
	        holder.eventDate.setText(eventData.get(key).get("date").toString());
	        holder.eventPersonCount.setText(eventData.get(key).get("count").toString());
	        holder.eventName.setText(eventData.get(key).get("name").toString());
	        if (eventData.get(key).get("response").toString().equalsIgnoreCase("yes")) {
	        	holder.eventResponseImage.setVisibility(View.VISIBLE);
	        }
	        else if (eventData.get(key).get("response").toString().equalsIgnoreCase("no")) {
	        	holder.eventResponseImage.setVisibility(View.GONE);
	        }
        	String url = eventData.get(key).get("picture").toString();
        	String fileName = url.substring(url.lastIndexOf("/")+1);
        	url = url.replace(" ", "%20");
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
        		options.inJustDecodeBounds = false; options.inTempStorage = new byte[1024];options.inPurgeable=true;
        		Bitmap bm = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath()+"/SeniorWellness/"+fileName, options);
        		holder.eventImage.setImageBitmap(bm);
        	}
        	else {
        		imageLoader.DisplayImage(url, context, holder.eventImage);
        	}
    	}
    	return convertView;
    } 
	static class ViewHolder {
	    TextView eventName, eventAddress, eventDate, eventPersonCount;
	    ImageView eventImage, eventPictureType, eventResponseImage;
	}

	@Override
	public Object getItem(int position) {
		return eventData.get(mKeys[position]);
	}
	
	@Override
	public long getItemId(int position) {
		return 0;
	}
}
