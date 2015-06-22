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
import android.os.Bundle;

import com.perples.recosdk.RECOBeaconManager;
import com.perples.recosdk.RECOBeaconRegion;
import com.perples.recosdk.RECOServiceConnectListener;

import java.util.ArrayList;

public abstract class RECOActivity extends Activity implements RECOServiceConnectListener {
	protected RECOBeaconManager mRecoManager;
	protected ArrayList<RECOBeaconRegion> mRegions;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mRecoManager = RECOBeaconManager.getInstance(getApplicationContext(), MainActivity.SCAN_RECO_ONLY, MainActivity.ENABLE_BACKGROUND_RANGING_TIMEOUT);
		mRegions = this.generateBeaconRegion();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	private ArrayList<RECOBeaconRegion> generateBeaconRegion() {        //비콘 추가
		ArrayList<RECOBeaconRegion> regions = new ArrayList<RECOBeaconRegion>();
//
//        regions.add(new RECOBeaconRegion(MainActivity.RECO_UUID, 19522, 18241, "D229"));
//        regions.add(new RECOBeaconRegion(MainActivity.RECO_UUID, 19522, 18242, "D230"));
//        regions.add(new RECOBeaconRegion(MainActivity.RECO_UUID, 19522, 18244, "D235"));
		regions.add(new RECOBeaconRegion(MainActivity.RECO_UUID, 19522, 18243, "D229"));
		regions.add(new RECOBeaconRegion(MainActivity.RECO_UUID, 19522, 18246, "D230"));
		regions.add(new RECOBeaconRegion(MainActivity.RECO_UUID, 19522, 18760, "D235"));
        regions.add(new RECOBeaconRegion(MainActivity.RECO_UUID, 19522, 18248, "D324"));
        regions.add(new RECOBeaconRegion(MainActivity.RECO_UUID, 19522, 18249, "D325"));

		return regions;
	}
	
	protected abstract void start(ArrayList<RECOBeaconRegion> regions);
	protected abstract void stop(ArrayList<RECOBeaconRegion> regions);
}
