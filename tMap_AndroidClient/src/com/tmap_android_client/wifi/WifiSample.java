package com.tmap_android_client.wifi;

import java.util.List;
import java.util.Map;

import android.util.Log;

import com.tmap_android_client.datatransfer.JsonUtils;

public class WifiSample {
	public int id;
	public int buildingId;
	public int floor;
	public int x, y;
	public String fingerPrintPack = null;
	
	public void packFingerPrint(List<WifiItem> wifiList) {
		this.fingerPrintPack = JsonUtils.packListToJson(wifiList);
		Log.d("test", this.fingerPrintPack);
	}
	
	public void packFingerPrint2(Map<String, Integer> wifiList) {
		this.fingerPrintPack = JsonUtils.packMapToJson(wifiList);
		Log.d("test", this.fingerPrintPack);
	}
	
}