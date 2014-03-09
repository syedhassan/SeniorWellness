package com.example.snrwlns.adapters;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

public 	class FriendsAdapter extends BaseAdapter {
	private LayoutInflater mInflater;      
    static ViewHolder holder;
    private String TAG = "SNRWLNS";
    private Activity context;
    private List<HashMap<String, String>> friendsData;
    public ImageLoader imageLoader;

    public FriendsAdapter(Activity context, List<HashMap<String, String>> friendsData) {
        try {
            mInflater = LayoutInflater.from(context);
            this.context = context;
            this.friendsData =  new ArrayList<HashMap<String, String>>();
            this.friendsData.addAll(friendsData);
            imageLoader = new ImageLoader(context, R.drawable.profiledefault);
        } catch (Exception e) {
            Log.v(TAG, "Error = " + e);
        }
    }
    
    @Override
	public int getCount() {
        return friendsData.size();
    }
    
    @Override
	public View getView(int position, View convertView, ViewGroup arg2) {
    	
    	if (convertView == null) {
	        convertView = mInflater.inflate(R.layout.friendslayout, null);
	        holder = new ViewHolder();
	        holder.friendsImage = (ImageView) convertView.findViewById(R.id.friendImage);
	        holder.friendsNumber = (TextView) convertView.findViewById(R.id.friendsNumber);
	        holder.friendsName = (TextView) convertView.findViewById(R.id.friendsName);
	        
	        holder.friendsImage.setAdjustViewBounds(true);
	        holder.friendsImage.setScaleType(ImageView.ScaleType.CENTER_CROP);

        	String url = friendsData.get(position).get("uri").toString();
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
        		holder.friendsImage.setImageBitmap(bm);
        	}
        	else {
        		imageLoader.DisplayImage(url, context, holder.friendsImage);
        	}
        	holder.friendsImage.setTag(friendsData.get(position).get("number").toString()+"$#~"+friendsData.get(position).get("email").toString());
	        holder.friendsNumber.setText(friendsData.get(position).get("number").toString());
	        holder.friendsName.setText(friendsData.get(position).get("name").toString());
	        convertView.setTag(holder);
	        
    	} else {
	        holder.friendsImage = (ImageView) convertView.findViewById(R.id.friendImage);
	        holder.friendsNumber = (TextView) convertView.findViewById(R.id.friendsNumber);
	        holder.friendsName = (TextView) convertView.findViewById(R.id.friendsName);
	        holder.friendsImage.setAdjustViewBounds(true);
	        holder.friendsImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        	String url = friendsData.get(position).get("uri").toString();
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
        		holder.friendsImage.setImageBitmap(bm);
        	}
        	else {
        		imageLoader.DisplayImage(url, context, holder.friendsImage);
        	}
        	holder.friendsImage.setTag(friendsData.get(position).get("number").toString()+"$#~"+friendsData.get(position).get("email").toString());
			holder.friendsNumber.setText(friendsData.get(position).get("number").toString());
			holder.friendsName.setText(friendsData.get(position).get("name").toString());
    	}
    	
    	return convertView;
    } 
	static class ViewHolder {
	    TextView friendsName, friendsNumber;
	    ImageView friendsImage;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}
	
	@Override
	public long getItemId(int position) {
		return 0;
	}
}
