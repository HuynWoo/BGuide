/**
 * The MIT License (MIT)
 * 
 * Copyright (c) 2014-2015 Perples, Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.perples.recosample;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.perples.recosdk.RECOBeacon;
import com.perples.recosdk.RECOBeaconRegion;
import com.perples.recosdk.RECOBeaconRegionState;
import com.perples.recosdk.RECOErrorCode;
import com.perples.recosdk.RECOMonitoringListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

/*
 * RECOMonitoringActivity 클래스는 foreground 상태에서 monitoring을 수행합니다. 
 */
public class RECOMonitoringActivity extends RECOActivity implements RECOMonitoringListener {
	
	private RECOMonitoringListAdapter mMonitoringListAdapter;
	private ListView mRegionListView;

	private long mScanPeriod = 1*1000L;         // 1sec
	private long mSleepPeriod = 1*1000L;	   // 1초 스캔, 10초 간격으로 스캔, 60초의 region expiration time은 당사 권장사항입니다.
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_monitoring);

		//mRecoManager 인스턴스는 여기서 생성됩니다. RECOActivity.onCreate() 메소들르 참고하세요.
		//RECOMonitoringListener 를 설정합니다. (필수)
		mRecoManager.setMonitoringListener(this);

		mRecoManager.setScanPeriod(mScanPeriod);    //scan 시간을 설정할 수 있습니다. 기본 값은 1초 입니다.
		mRecoManager.setSleepPeriod(mSleepPeriod);  //scan 후, 다음 scan 시작 전까지의 시간을 설정할 수 있습니다. 기본 값은 10초 입니다.
		
		/*
		 * RECOServiceConnectListener와 함께 RECOBeaconManager를 bind 합니다. RECOServiceConnectListener는 RECOActivity에 구현되어 있습니다.
		 * monitoring 및 ranging 기능을 사용하기 위해서는, 이 메소드가 "반드시" 호출되어야 합니다.
		 * bind후에, onServiceConnect() 콜백 메소드가 호출됩니다. 콜백 메소드 호출 이후 monitoring / ranging 작업을 수행하시기 바랍니다.
		 */
		mRecoManager.bind(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		mMonitoringListAdapter = new RECOMonitoringListAdapter(this);
		mRegionListView = (ListView)findViewById(R.id.list_monitoring);
		mRegionListView.setAdapter(mMonitoringListAdapter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();	
		this.stop(mRegions);
		this.unbind();
	}
	
	@Override
	public void onServiceConnect() {
		Log.i("RECOMonitoringActivity", "onServiceConnect");
		this.start(mRegions);
		//Write the code when RECOBeaconManager is bound to RECOBeaconService
	}

	@Override
	public void didDetermineStateForRegion(RECOBeaconRegionState recoRegionState, RECOBeaconRegion recoRegion) {
		Log.i("RECOMonitoringActivity", "didDetermineStateForRegion()");
		Log.i("RECOMonitoringActivity", "region: " + recoRegion.getUniqueIdentifier() + ", state: " + recoRegionState.toString());
        //monitoring 시작 후에 monitoring 중인 region에 들어가거나 나올 경우
        //(region 의 상태에 변화가 생긴 경우) 이 callback 메소드가 호출됩니다.
        //didEnterRegion, didExitRegion callback 메소드와 함께 호출됩니다.
        //region 상태 변화시 코드 작성
        //mMonitoringListAdapter.updateAllBeacons(recoBeacons);
        mMonitoringListAdapter.notifyDataSetChanged();
	}

	@Override
	public void didEnterRegion(RECOBeaconRegion recoRegion, Collection<RECOBeacon> beacons) {
		/*
		 * 최초 실행시, 이 콜백 메소드는 호출되지 않습니다. 
		 * didDetermineStateForRegion() 콜백 메소드를 통해 region 상태를 확인할 수 있습니다.
		 */
		// region 입장 시 코드
		Log.i("RECOMonitoringActivity", "didEnterRegion() region:" + recoRegion.getUniqueIdentifier());

        Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT).show();

		mMonitoringListAdapter.updateRegion(recoRegion, RECOBeaconRegionState.RECOBeaconRegionInside, beacons.size(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA).format(new Date()));
		mMonitoringListAdapter.notifyDataSetChanged();
	}

	@Override
	public void didExitRegion(RECOBeaconRegion recoRegion) {
		/*
		 * 최초 실행시, 이 콜백 메소드는 호출되지 않습니다.
		 * didDetermineStateForRegion() 콜백 메소드를 통해 region 상태를 확인할 수 있습니다.
		 */
		
		Log.i("RECOMonitoringActivity", "didExitRegion() region:" + recoRegion.getUniqueIdentifier());

        Toast.makeText(this, "Byebye", Toast.LENGTH_SHORT).show();

		mMonitoringListAdapter.updateRegion(recoRegion, RECOBeaconRegionState.RECOBeaconRegionOutside, 0, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA).format(new Date()));
		mMonitoringListAdapter.notifyDataSetChanged();
		//Write the code when the device is exit the region
	}

	@Override
	public void didStartMonitoringForRegion(RECOBeaconRegion recoRegion) {
		Log.i("RECOMonitoringActivity", "didStartMonitoringForRegion: " + recoRegion.getUniqueIdentifier());
		//Write the code when starting monitoring the region is started successfully
        Toast.makeText(this, "monitoring succes", Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void start(ArrayList<RECOBeaconRegion> regions) {
		Log.i("RECOMonitoringActivity", "start");

		for(RECOBeaconRegion region : regions) {
			try {
				region.setRegionExpirationTimeMillis(60*1000L);     // 60s
				mRecoManager.startMonitoringForRegion(region);
			} catch (RemoteException e) {
				Log.i("RECOMonitoringActivity", "Remote Exception");
				e.printStackTrace();
			} catch (NullPointerException e) {
				Log.i("RECOMonitoringActivity", "Null Pointer Exception");
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void stop(ArrayList<RECOBeaconRegion> regions) {
		for(RECOBeaconRegion region : regions) {
			try {
				mRecoManager.stopMonitoringForRegion(region);
			} catch (RemoteException e) {
				Log.i("RECOMonitoringActivity", "Remote Exception");
				e.printStackTrace();
			} catch (NullPointerException e) {
				Log.i("RECOMonitoringActivity", "Null Pointer Exception");
				e.printStackTrace();
			}
		}
	}
	
	private void unbind() {
		try {
			mRecoManager.unbind();
		} catch (RemoteException e) {
			Log.i("RECOMonitoringActivity", "Remote Exception");
			e.printStackTrace();
		}

        Log.i("RECOMonitoringActivity", "unbind");
	}
	
	@Override
	public void onServiceFail(RECOErrorCode errorCode) {
		//Write the code when the RECOBeaconService is failed.
		//See the RECOErrorCode in the documents.
        Log.i("RECOMonitoringActivity", "onServiceFail");
		return;
	}
	
	@Override
	public void monitoringDidFailForRegion(RECOBeaconRegion region, RECOErrorCode errorCode) {
		//Write the code when the RECOBeaconService is failed to monitor the region.
		//See the RECOErrorCode in the documents.
        //monitoring이 정상적으로 시작하지 못했을 경우 이 callback 메소드가 호출됩니다.
        //RECOErrorCode는 "Error Code" 를 확인하시기 바랍니다.
        //monitoring 실패 시 코드 작성
        Log.i("RECOMonitoringActivity", "monitoringDidFailForRegion");

        Toast.makeText(this, "monitoring fail", Toast.LENGTH_SHORT).show();

		return;
	}

}
