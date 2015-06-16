
package com.perples.recosample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.perples.recosdk.RECOBeaconRegion;
import com.perples.recosdk.RECOBeaconRegionState;

import java.util.ArrayList;
import java.util.HashMap;

public class RECOMonitoringListAdapter extends BaseAdapter {
	private HashMap<RECOBeaconRegion, RECOBeaconRegionState> mMonitoredRegions;
	private HashMap<RECOBeaconRegion, String> mLastUpdateTime;
	private HashMap<RECOBeaconRegion, Integer> mMatchedBeaconCounts;
	private ArrayList<RECOBeaconRegion> mMonitoredRegionLists;

    Context mcontext;
    String recoRegionUniqueID;
    String recoRegionState;
    String recoUpdateTime;
    String recoBeaconCount;

    private LayoutInflater mLayoutInflater;
	
	public RECOMonitoringListAdapter(Context context) {
		super();
		mMonitoredRegions = new HashMap<RECOBeaconRegion, RECOBeaconRegionState>();
		mLastUpdateTime = new HashMap<RECOBeaconRegion, String>();
		mMatchedBeaconCounts = new HashMap<RECOBeaconRegion, Integer>();
		mMonitoredRegionLists = new ArrayList<RECOBeaconRegion>();
        mcontext = context;
		mLayoutInflater = LayoutInflater.from(context);
	}
	
	public void updateRegion(RECOBeaconRegion recoRegion, RECOBeaconRegionState recoState, int beaconCount, String updateTime) {
        //비콘 상태 갱신
		mMonitoredRegions.put(recoRegion, recoState);
		mLastUpdateTime.put(recoRegion, updateTime);
		mMatchedBeaconCounts.put(recoRegion, beaconCount);

		if(!mMonitoredRegionLists.contains(recoRegion)) {
			mMonitoredRegionLists.add(recoRegion);
		}
	}
	
	public void clear() {
		mMonitoredRegions.clear();
	}

	@Override
	public int getCount() {
		return mMonitoredRegions.size();
	}

	@Override
	public Object getItem(int position) {
		return mMonitoredRegions.get(mMonitoredRegionLists.get(position));
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		
		if(convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.list_monitoring_region, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.recoRegionID = (TextView)convertView.findViewById(R.id.region_uniqueID);
			viewHolder.recoRegionState = (TextView)convertView.findViewById(R.id.region_state);
			viewHolder.recoRegionTime = (TextView)convertView.findViewById(R.id.region_update_time);
			viewHolder.recoRegionBeaconCount = (TextView)convertView.findViewById(R.id.region_beacon_count);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		RECOBeaconRegion recoRegion = mMonitoredRegionLists.get(position);
		RECOBeaconRegionState recoState = mMonitoredRegions.get(recoRegion);

		recoRegionUniqueID = recoRegion.getUniqueIdentifier();
		recoRegionState = recoState.toString();
		recoUpdateTime = mLastUpdateTime.get(recoRegion);
		recoBeaconCount = mMatchedBeaconCounts.get(recoRegion).toString();
		
		viewHolder.recoRegionID.setText(recoRegionUniqueID);
		viewHolder.recoRegionState.setText(recoRegionState);
		viewHolder.recoRegionTime.setText(recoUpdateTime);
		viewHolder.recoRegionBeaconCount.setText("# of beacons in the region: " + recoBeaconCount);

		return convertView;
	}
	
	static class ViewHolder {
		TextView recoRegionID;
		TextView recoRegionState;
		TextView recoRegionTime;
		TextView recoRegionBeaconCount;
	}
}
