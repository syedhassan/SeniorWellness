package com.example.snrwlns.adapters;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.snrwlns.R;

public 	class NotificationsAdapter extends BaseAdapter {
	private LayoutInflater mInflater;      
    static ViewHolder holder;
    private String TAG = "SNRWLNS";
    private ArrayList<HashMap<String, String>> notificationsData;

    public NotificationsAdapter(Context context, ArrayList<HashMap<String, String>> notificationsData) {
        try {
            mInflater = LayoutInflater.from(context);
            this.notificationsData =  new ArrayList<HashMap<String, String>>();
            this.notificationsData.addAll(notificationsData);
        } catch (Exception e) {
            Log.v(TAG, "Error = " + e);
        }
    }
    
    @Override
	public int getCount() {
        return notificationsData.size();
    }
    
    @Override
	public View getView(int position, View convertView, ViewGroup arg2) {
    	
    	if (convertView == null) {
	        convertView = mInflater.inflate(R.layout.notificationrow, null);
	        holder = new ViewHolder();
	        holder.notificationsCheck = (ImageView) convertView.findViewById(R.id.checkImage);
	        holder.notificationName = (TextView) convertView.findViewById(R.id.friendName);
	        
			if (notificationsData.get(position).get("notificationShare").toString().equalsIgnoreCase("share")) {
				holder.notificationsCheck.setImageResource(R.drawable.checkboxselected75x66);
			}
			else {
				holder.notificationsCheck.setImageResource(R.drawable.checkbox75x66);
			}
	        holder.notificationName.setText(notificationsData.get(position).get("notificationName").toString());
	        convertView.setTag(holder);
	        
    	} else {
	        holder.notificationsCheck = (ImageView) convertView.findViewById(R.id.checkImage);
	        holder.notificationName = (TextView) convertView.findViewById(R.id.friendName);
	        
			
			if (notificationsData.get(position).get("notificationShare").toString().equalsIgnoreCase("share")) {
				holder.notificationsCheck.setImageResource(R.drawable.checkboxselected75x66);
			}
			else {
				holder.notificationsCheck.setImageResource(R.drawable.checkbox75x66);
			}
			
	        holder.notificationName.setText(notificationsData.get(position).get("notificationName").toString());
	        
    	}
    	
    	return convertView;
    } 
	static class ViewHolder {
	    TextView notificationName;
	    ImageView notificationsCheck;
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
