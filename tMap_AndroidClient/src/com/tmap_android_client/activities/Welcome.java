package com.tmap_android_client.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.example.tmap_androidclient.ModelCreator;
import com.example.tmap_androidclient.R;
import com.tmap_android_client.control.Environment;
import com.tmap_android_client.control.ExitApplication;
import com.tmap_android_client.datatransfer.HttpUtils;
import com.tmap_android_client.datatransfer.JsonUtils;
import com.tmap_android_client.opengl.Box;
import com.tmap_android_client.opengl.Geometry;
import com.tmap_android_client.opengl.ObjectDescription;
import com.tmap_android_client.wifi.Location;
import com.tmap_android_client.wifi.WifiFactory;
import com.tmap_android_client.wifi.WifiSample;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.TransitionDrawable;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Welcome extends BaseActivity {
	private String serverURL = null;
	private ArrayList<Map<String, Float>> scanSet = null;
	private WifiManager wm = null;
	private int scanCounter = 0;
	private JsonThread json = null;
	private WifiSample ws;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        this.setContentView(R.layout.welcome);
        
        ImageView imageView = (ImageView) findViewById(R.id.welcome_page);  
        TransitionDrawable transitionDrawable = (TransitionDrawable) imageView.getDrawable();  
        transitionDrawable.startTransition(500);  
        
        // Config
        // Get server URL from config
        serverURL = Environment.getInstance(this).serverURL;
        
        scanCounter = Integer.parseInt(this.getString(R.integer.INIT_SAMPLE_UNIT_TIMES));
     
        // Init scan set
        scanSet = new ArrayList<Map<String, Float>>();
        
        // Resigter receiver to guarantee scan results being used after finishing scan
        IntentFilter i = new IntentFilter();
        i.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(new BroadcastReceiver() {
        	@Override
        	public void onReceive(Context c, Intent i){
		        WifiManager w = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
				List<ScanResult> scanResults=w.getScanResults();
		        
		        Map<String, Float> scanResultList = new HashMap<String, Float>();
		        
			    for (ScanResult scanResult : scanResults) {
			    	scanResultList.put(scanResult.BSSID, (float)scanResult.level);			   
			    }
			    
			    scanSet.add(scanResultList);
			    if (scanCounter > 0) {
			    	scanCounter--;
			    	Log.v("dataing2", "" + scanCounter);
			    	wm.startScan();
			    } else {
			    	json = new JsonThread();
			    	new Thread(json).start();
			    }
	        }
        }, i);
        
        locating();
	}
	
	private void locating() {
		try {
        	Initiate init = new Initiate();
            new Thread(init).start();
        } catch (Exception e) {
        	Toast.makeText(getApplicationContext(), R.string.web_link_fail_hint, Toast.LENGTH_SHORT).show();
        }
	}
	
	Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch(msg.what){
            case R.integer.MSG_COMPLETE:
            	Log.v("dataing2", json.data);
            	//location.setText(Welcome.this.ws.x + " " + Welcome.this.ws.y);
            	if (ws.valid()) {
            		Welcome.this.gotoNavigating();
            		finish();
            	} else {
            		locating();
            	}
                break;
            case R.integer.MSG_NET_FAIL:
            	Toast.makeText(getApplicationContext(), R.string.web_link_fail_hint, Toast.LENGTH_SHORT).show();
            	break;
            }

        };
    };
    
    private void gotoNavigating() {
    	Intent intent = new Intent(Welcome.this, Navigating.class);
    	Bundle bundle=new Bundle();
    	bundle.putString("action", "locating");
    	bundle.putInt("buildingId", ws.buildingId);
    	bundle.putInt("floor", ws.floor);
    	bundle.putFloat("x", ws.x);
    	bundle.putFloat("y", ws.y);
    	intent.putExtras(bundle); 
    	this.startActivity(intent);
    }
	
	class Initiate implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
			wm.startScan();
		}
	}
	
	class JsonThread implements Runnable{
		public String data = null;
		
        public void run() {
        	ws = WifiFactory.combineScanResult(scanSet);
        	
            try {
                data = HttpUtils.getInstance().postData(serverURL + "tMap/wifi/wifiJudgePosition", JsonUtils.packObjToJson(ws));
                if(data != null){
                	ws = JsonUtils.parseWifiSample(data);
                } else {
                	ws = null;
                }
                handler.sendEmptyMessage(R.integer.MSG_COMPLETE);
            } catch (Exception e) {
            	handler.sendEmptyMessage(R.integer.MSG_NET_FAIL);                         
            }
        }
        
    }
}
