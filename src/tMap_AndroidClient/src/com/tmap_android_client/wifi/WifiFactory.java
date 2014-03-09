package com.tmap_android_client.wifi;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

public class WifiFactory {
	
	public static WifiSample combineScanResult(ArrayList<Map<String, Float>> scanSet) {
		int scanTimes = scanSet.size();
		Map<String, Float> baseSample = scanSet.get(0);
    	for (int i = 1; i < scanTimes; ++i) {
    		Map<String, Float> sampleList = scanSet.get(i);
    		for (Entry<String, Float> entry: sampleList.entrySet()) {
    			String tmp = entry.getKey();
    			if (baseSample.containsKey(tmp)) {
    				baseSample.put(tmp, baseSample.get(tmp) + entry.getValue());
    			} else {
    				baseSample.put(tmp, entry.getValue());
    			}
    		}
    	}
    	
    	for (Entry<String, Float> entry: baseSample.entrySet()) {
    		baseSample.put(entry.getKey(), entry.getValue() / scanTimes);
    	}
    	
    	
    	WifiSample ws = new WifiSample();
    	ws.buildingId = -1;
    	ws.packFingerPrint(baseSample);
    	
    	return ws;
	}
}
