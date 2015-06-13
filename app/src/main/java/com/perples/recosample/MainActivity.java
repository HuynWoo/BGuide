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

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity implements OnInitListener{

    private TextToSpeech myTTS;
	//This is a default proximity uuid of the RECO
	public static final String RECO_UUID = "24DDF411-8CF1-440C-87CD-E368DAF9C93E";
	public static final boolean SCAN_RECO_ONLY = true;
	public static final boolean ENABLE_BACKGROUND_RANGING_TIMEOUT = true;
	public static final boolean DISCONTINUOUS_SCAN = true;

	private static final int REQUEST_ENABLE_BT = 1;
	
	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//If a user device turns off bluetooth, request to turn it on.
		//사용자가 블루투스를 켜도록 요청합니다.
		mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = mBluetoothManager.getAdapter();
		
		if(mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
			Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
		}
        Toast.makeText(this, "시작", Toast.LENGTH_SHORT).show();

        myTTS = new TextToSpeech(this, this);
	}

    // TTS 함수
    public void onInit(int status) {
        myTTS.speak("모바일 소프트웨어 프로젝트", TextToSpeech.QUEUE_FLUSH, null);
        myTTS.speak("비콘비콘비콘비콘 비콘비 콘", TextToSpeech.QUEUE_ADD, null);
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
			//사용자가 블루투스 요청을 허용하지 않았을 경우, 어플리케이션은 종료됩니다.
			finish();
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

    //시작하자마자 실행
	@Override
	protected void onResume() {
		Log.i("MainActivity", "onResume()");
		super.onResume();
		
		if(this.isBackgroundMonitoringServiceRunning(this)) {
			ToggleButton toggle = (ToggleButton)findViewById(R.id.backgroundMonitoringToggleButton);
			toggle.setChecked(true);
		}
	}
	
	@Override
	protected void onDestroy() {
		Log.i("MainActivity", "onDestroy");
		super.onDestroy();
        myTTS.shutdown();
	}
		
	public void onMonitoringToggleButtonClicked(View v) {
		ToggleButton toggle = (ToggleButton)v;
		if(toggle.isChecked()) {
			Log.i("MainActivity", "onMonitoringToggleButtonClicked off to on");
			Intent intent = new Intent(this, RECOBackgroundMonitoringService.class);
			startService(intent);
		} else {
			Log.i("MainActivity", "onMonitoringToggleButtonClicked on to off");
			stopService(new Intent(this, RECOBackgroundMonitoringService.class));
		}
	}

	public void onButtonClicked(View v) {
		Button btn = (Button)v;

		if(btn.getId() == R.id.monitoringButton) {
            Toast.makeText(this, "monitoring", Toast.LENGTH_SHORT).show();

			final Intent intent = new Intent(this, RECOMonitoringActivity.class);
			startActivity(intent);
		}
	}
	
	private boolean isBackgroundMonitoringServiceRunning(Context context) {
		ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		for(RunningServiceInfo runningService : am.getRunningServices(Integer.MAX_VALUE)) {
			if(RECOBackgroundMonitoringService.class.getName().equals(runningService.service.getClassName())) {
				return true;
			}
		}
		return false;
	}
}
