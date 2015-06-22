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

import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

public class RECOMonitoringActivity extends RECOActivity implements RECOMonitoringListener, OnInitListener {
	
	private RECOMonitoringListAdapter mMonitoringListAdapter;
	private ListView mRegionListView;

	private long mScanPeriod = 1*1000L;         // 1sec
	private long mSleepPeriod = 1*1000L;	   // 1초 스캔, 10초 간격으로 스캔, 60초의 region expiration time은 당사 권장사항입니다.

    private TextToSpeech myTTS;

	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_monitoring);

        myTTS = new TextToSpeech(this, this);

		mRecoManager.setMonitoringListener(this);

		mRecoManager.setScanPeriod(mScanPeriod);    //scan 시간을 설정할 수 있습니다. 기본 값은 1초 입니다.
		mRecoManager.setSleepPeriod(mSleepPeriod);  //scan 후, 다음 scan 시작 전까지의 시간을 설정할 수 있습니다. 기본 값은 10초 입니다.

		mRecoManager.bind(this);
	}

    public void onInit(int status) {
        myTTS.speak("위치정보를 갱신합니다", TextToSpeech.QUEUE_FLUSH, null);
    }

	@Override
	protected void onResume() {
		super.onResume();
		
		mMonitoringListAdapter = new RECOMonitoringListAdapter(this);
		mRegionListView = (ListView)findViewById(R.id.list_monitoring);
		mRegionListView.setAdapter(mMonitoringListAdapter);		// ListView에 Adapter 연결

		mRegionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
				Toast.makeText(RECOMonitoringActivity.this, mMonitoringListAdapter.mMonitoredRegionLists.get(position).getUniqueIdentifier(), Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(RECOMonitoringActivity.this, ContentActivity.class);
				intent.putExtra("id", mMonitoringListAdapter.mMonitoredRegionLists.get(position).getUniqueIdentifier());
				startActivity(intent);
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();	
		this.stop(mRegions);
		this.unbind();
        myTTS.shutdown();
	}

	@Override
	public void onServiceConnect() {
		Log.i("RECOMonitoringActivity", "onServiceConnect");
		this.start(mRegions);
	}

	@Override
	public void didDetermineStateForRegion(RECOBeaconRegionState recoRegionState, RECOBeaconRegion recoRegion) {
        //비콘 신호 지역상태에 변화가 생길때
		Log.i("RECOMonitoringActivity", "didDetermineStateForRegion()");
		Log.i("RECOMonitoringActivity", "region: " + recoRegion.getUniqueIdentifier() + ", state: " + recoRegionState.toString());

        mMonitoringListAdapter.notifyDataSetChanged();
	}

	@Override
	public void didEnterRegion(RECOBeaconRegion recoRegion, Collection<RECOBeacon> beacons) {
        //비콘 신호범위 내로 진입했을때
		Log.i("RECOMonitoringActivity", "didEnterRegion() region:" + recoRegion.getUniqueIdentifier());

		mMonitoringListAdapter.updateRegion(recoRegion, RECOBeaconRegionState.RECOBeaconRegionInside, beacons.size(),
				new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA).format(new Date()));
		mMonitoringListAdapter.notifyDataSetChanged();

		Toast.makeText(this, "Enter " + recoRegion.getUniqueIdentifier(), Toast.LENGTH_SHORT).show();

        if(recoRegion.getUniqueIdentifier().equals("D229")) { myTTS.speak("여기는 디지털관 229호 강의실입니다", TextToSpeech.QUEUE_ADD, null); }
        else if(recoRegion.getUniqueIdentifier().equals("D230")) { myTTS.speak("여기는 디지털관 230호 강의실입니다", TextToSpeech.QUEUE_ADD, null); }
        else if(recoRegion.getUniqueIdentifier().equals("D235")) { myTTS.speak("여기는 디지털관 235호 디지털공학 연구실입니다", TextToSpeech.QUEUE_ADD, null); }
        else if(recoRegion.getUniqueIdentifier().equals("D324")) { myTTS.speak("여기는 디지털관 324호 오병우교수님 연구실입니다", TextToSpeech.QUEUE_ADD, null); }
        else { myTTS.speak("여기는 디지털관 325호 모바일소프트웨어 연구실입니다", TextToSpeech.QUEUE_ADD, null); }
	}

	@Override
	public void didExitRegion(RECOBeaconRegion recoRegion) {
        //비콘 신호범위 밖으로 벗어날때
		Log.i("RECOMonitoringActivity", "didExitRegion() region:" + recoRegion.getUniqueIdentifier());

		mMonitoringListAdapter.updateRegion(recoRegion, RECOBeaconRegionState.RECOBeaconRegionOutside, 0, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA).format(new Date()));
		mMonitoringListAdapter.notifyDataSetChanged();

		Toast.makeText(this, "Exit " + recoRegion.getUniqueIdentifier(), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void didStartMonitoringForRegion(RECOBeaconRegion recoRegion) {
		Log.i("RECOMonitoringActivity", "didStartMonitoringForRegion: " + recoRegion.getUniqueIdentifier());
        //Toast.makeText(this, "monitoring succes", Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void start(ArrayList<RECOBeaconRegion> regions) {
		Log.i("RECOMonitoringActivity", "start");

		for(RECOBeaconRegion region : regions) {
			try {
				region.setRegionExpirationTimeMillis(2*1000L);
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
        Log.i("RECOMonitoringActivity", "onServiceFail");
		return;
	}
	
	@Override
	public void monitoringDidFailForRegion(RECOBeaconRegion region, RECOErrorCode errorCode) {
        Log.i("RECOMonitoringActivity", "monitoringDidFailForRegion");
        Toast.makeText(this, "monitoring fail", Toast.LENGTH_SHORT).show();
		return;
	}

}
