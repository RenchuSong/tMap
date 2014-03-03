package com.tmap_android_client.wifi;

import java.util.List;
import java.util.Map;

import android.util.Log;

import com.tmap_android_client.datatransfer.JsonUtils;

public class WifiSample {
	public int id;
	public int buildingId = 0;
	public int floor = 0;
	public float x = 0, y = 0;
	public String fingerPrintPack = null;
	
//	public void packFingerPrint(List<WifiItem> wifiList) {
//		this.fingerPrintPack = JsonUtils.packListToJson(wifiList);
//		Log.d("test", this.fingerPrintPack);
//	}
//	

	public void packFingerPrint(Map<String, Float> wifiList) {
		this.fingerPrintPack = JsonUtils.packMapToJson(wifiList);
	}
	
	public void packFingerPrint2(Map<String, Integer> wifiList) {
		this.fingerPrintPack = JsonUtils.packMapToJson(wifiList);
		Log.d("test", this.fingerPrintPack);
	}
	
	public boolean valid() {
		return !(this.buildingId == 0 && this.floor == 0 && this.x == 0 && this.y == 0);
	}
}