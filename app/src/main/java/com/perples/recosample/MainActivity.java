package com.perples.recosample;

import android.app.Activity;
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
	private static final int REQUEST_ENABLE_BT = 1;
	
	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main);
		
		//If a user device turns off bluetooth, request to turn it on.
		//사용자가 블루투스를 켜도록 요청합니다.
		mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = mBluetoothManager.getAdapter();
		
		if(mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
			Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
		}
        Toast.makeText(this, "Service Start", Toast.LENGTH_SHORT).show();

        myTTS = new TextToSpeech(this, this);

        final Intent intent = new Intent(this, RECOMonitoringActivity.class);
        startActivity(intent);

	}

    // TTS 함수
    public void onInit(int status) {
        myTTS.speak("위치안내 서비스를 시작합니다", TextToSpeech.QUEUE_FLUSH, null);
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
	}
	
	@Override
	protected void onDestroy() {
		Log.i("MainActivity", "onDestroy");
		super.onDestroy();
        myTTS.shutdown();
	}
}
