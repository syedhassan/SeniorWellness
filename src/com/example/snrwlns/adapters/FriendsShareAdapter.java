package com.example.snrwlns.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
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

import com.example.snrwlns.R;

public 	class FriendsShareAdapter extends BaseAdapter {
	private LayoutInflater mInflater;      
    static ViewHolder holder;
    private String TAG = "SNRWLNS";
    private List<HashMap<String, String>> friendsData;

    public FriendsShareAdapter(Context context, List<HashMap<String, String>> friendsData) {
        try {
            mInflater = LayoutInflater.from(context);
            this.friendsData =  new ArrayList<HashMap<String, String>>();
            this.friendsData.addAll(friendsData);
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
    	String url = friendsData.get(position).get("uri").toString();
    	String fileName = url.substring(url.lastIndexOf("/")+1);
    	url = url.replace(" ", "%20");
    	BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath()+"/SeniorWellness/"+fileName, options);
		Boolean scaleByHeight = Math.abs(options.outHeight - 100) >= Math.abs(options.outWidth - 100);
		if(options.outHeight * options.outWidth * 2 >= 16384){
		    double sampleSize = scaleByHeight? options.outHeight / 100: options.outWidth / 100;
		    options.inSampleSize = (int)Math.pow(2d, Math.floor(Math.log(sampleSize)/Math.log(2d)));
		}
		options.inJustDecodeBounds = false; options.inTempStorage = new byte[1024];options.inPurgeable=true;
		Bitmap bm = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath()+"/SeniorWellness/"+fileName, options);
		
    	if (convertView == null) {
	        convertView = mInflater.inflate(R.layout.friendssharerow, null);
	        holder = new ViewHolder();
	        holder.friendsImage = (ImageView) convertView.findViewById(R.id.friendImage);
	        holder.friendsShareCheck = (ImageView) convertView.findViewById(R.id.checkImage);
	        holder.friendsName = (TextView) convertView.findViewById(R.id.friendName);
	        
    		holder.friendsImage.setImageBitmap(bm);
			
			if (friendsData.get(position).get("share").toString().equalsIgnoreCase("share")) {
				holder.friendsShareCheck.setImageResource(R.drawable.checkboxselected75x66);
			}
			else {
				holder.friendsShareCheck.setImageResource(R.drawable.checkbox75x66);
			}
	        holder.friendsName.setText(friendsData.get(position).get("name").toString());
	        convertView.setTag(holder);
	        
    	} else {
	        holder.friendsImage = (ImageView) convertView.findViewById(R.id.friendImage);
	        holder.friendsShareCheck = (ImageView) convertView.findViewById(R.id.checkImage);
	        holder.friendsName = (TextView) convertView.findViewById(R.id.friendName);
	        
    		holder.friendsImage.setImageBitmap(bm);

			
			if (friendsData.get(position).get("share").toString().equalsIgnoreCase("share")) {
				holder.friendsShareCheck.setImageResource(R.drawable.checkboxselected75x66);
			}
			else {
				holder.friendsShareCheck.setImageResource(R.drawable.checkbox75x66);
			}
			
	        holder.friendsName.setText(friendsData.get(position).get("name").toString());
	        
    	}
    	return convertView;
    } 
	static class ViewHolder {
	    TextView friendsName;
	    ImageView friendsImage, friendsShareCheck;
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
