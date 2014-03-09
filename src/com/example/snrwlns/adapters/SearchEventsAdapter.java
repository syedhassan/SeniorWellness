package com.example.snrwlns.adapters;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.snrwlns.R;

public 	class SearchEventsAdapter extends BaseAdapter {
	private LayoutInflater mInflater;      
    static ViewHolder holder;
    private String TAG = "SNRWLNS";
    private ArrayList<HashMap<String, String>> searchData;

    public SearchEventsAdapter(Context context, ArrayList<HashMap<String, String>> notificationsData) {
        try {
            mInflater = LayoutInflater.from(context);
            this.searchData =  new ArrayList<HashMap<String, String>>();
            this.searchData.addAll(notificationsData);
        } catch (Exception e) {
            Log.v(TAG, "Error = " + e);
        }
    }
    
    @Override
	public int getCount() {
        return searchData.size();
    }
    
    @Override
	public View getView(int position, View convertView, ViewGroup parent) {
    	
    	if (convertView == null) {
	        convertView = mInflater.inflate(R.layout.searchrow, parent, false);
	        holder = new ViewHolder();
	        holder.notificationName = (TextView) convertView.findViewById(R.id.searchText);
	        holder.notificationName.setText(searchData.get(position).get("searchEventName").toString());
	        holder.notificationName.setTag(searchData.get(position).get("searchEventId").toString());
	        convertView.setTag(holder);
	        
    	} else {
	        holder.notificationName = (TextView) convertView.findViewById(R.id.searchText);
	        holder.notificationName.setText(searchData.get(position).get("searchEventName").toString());
	        holder.notificationName.setTag(searchData.get(position).get("searchEventId").toString());
    	}
    	
    	return convertView;
    } 
	static class ViewHolder {
	    TextView notificationName;
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
