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
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
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
	}

	@Override
	public void didDetermineStateForRegion(RECOBeaconRegionState recoRegionState, RECOBeaconRegion recoRegion) {
		Log.i("RECOMonitoringActivity", "didDetermineStateForRegion()");
		Log.i("RECOMonitoringActivity", "region: " + recoRegion.getUniqueIdentifier() + ", state: " + recoRegionState.toString());

        mMonitoringListAdapter.notifyDataSetChanged();
	}

	@Override
	public void didEnterRegion(RECOBeaconRegion recoRegion, Collection<RECOBeacon> beacons) {
		Log.i("RECOMonitoringActivity", "didEnterRegion() region:" + recoRegion.getUniqueIdentifier());

		mMonitoringListAdapter.updateRegion(recoRegion, RECOBeaconRegionState.RECOBeaconRegionInside, beacons.size(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA).format(new Date()));
		mMonitoringListAdapter.notifyDataSetChanged();

        if(recoRegion.getUniqueIdentifier().equals("D325 b1")) {
            Toast.makeText(this, "find b1", Toast.LENGTH_SHORT).show();
            myTTS.speak("여기는 디지털관 325호 b1 입니다", TextToSpeech.QUEUE_FLUSH, null);
        }

        else if(recoRegion.getUniqueIdentifier().equals("D325 b2")) {
            Toast.makeText(this, "find b2", Toast.LENGTH_SHORT).show();
            myTTS.speak("여기는 디지털관 325호 b2 입니다", TextToSpeech.QUEUE_FLUSH, null);
        }

        else if(recoRegion.getUniqueIdentifier().equals("D325 b3")) {
            Toast.makeText(this, "find b3", Toast.LENGTH_SHORT).show();
            myTTS.speak("여기는 디지털관 325호 b3 입니다", TextToSpeech.QUEUE_FLUSH, null);
        }

        else if(recoRegion.getUniqueIdentifier().equals("D325 b4")) {
            Toast.makeText(this, "find b4", Toast.LENGTH_SHORT).show();
            myTTS.speak("여기는 디지털관 325호 b4 입니다", TextToSpeech.QUEUE_FLUSH, null);
        }

        else {
            Toast.makeText(this, "find b5", Toast.LENGTH_SHORT).show();
            myTTS.speak("여기는 디지털관 325호 b5 입니다", TextToSpeech.QUEUE_FLUSH, null);
        }
	}

	@Override
	public void didExitRegion(RECOBeaconRegion recoRegion) {
		Log.i("RECOMonitoringActivity", "didExitRegion() region:" + recoRegion.getUniqueIdentifier());

		mMonitoringListAdapter.updateRegion(recoRegion, RECOBeaconRegionState.RECOBeaconRegionOutside, 0, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA).format(new Date()));
		mMonitoringListAdapter.notifyDataSetChanged();

        if(recoRegion.getUniqueIdentifier().equals("D325 b1")) {
            Toast.makeText(this, "exit b1", Toast.LENGTH_SHORT).show();
        }

        else if(recoRegion.getUniqueIdentifier().equals("D325 b2")) {
            Toast.makeText(this, "exit b2", Toast.LENGTH_SHORT).show();
        }

        else if(recoRegion.getUniqueIdentifier().equals("D325 b3")) {
            Toast.makeText(this, "exit b3", Toast.LENGTH_SHORT).show();
        }

        else if(recoRegion.getUniqueIdentifier().equals("D325 b4")) {
            Toast.makeText(this, "exit b4", Toast.LENGTH_SHORT).show();
        }

        else {
            Toast.makeText(this, "exit b5", Toast.LENGTH_SHORT).show();
        }
	}

	@Override
	public void didStartMonitoringForRegion(RECOBeaconRegion recoRegion) {
		Log.i("RECOMonitoringActivity", "didStartMonitoringForRegion: " + recoRegion.getUniqueIdentifier());

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
