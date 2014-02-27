package com.example.tmap_androidclient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tmap_android_client.datatransfer.HttpUtils;
import com.tmap_android_client.datatransfer.JsonUtils;
import com.tmap_android_client.datatransfer.Response;
import com.tmap_android_client.wifi.WifiItem;
import com.tmap_android_client.wifi.WifiSample;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	private static final int START = 0x0000;
	private static final int COMPLETE = 0x1000;
	
	public String data = null;
	private TextView tx;
	public JsonThread json;
	public ScanThread scan;
	private Response list = null;
	
	String uri;
	
	public WifiSample ws;
	
	public Button sample;
	public EditText floor, x, y;
	public Button plusX, minusX, plusY, minusY;
	public TextView status;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        
        sample = (Button)this.findViewById(R.id.button5);
        floor = (EditText)this.findViewById(R.id.EditText01);
        x = (EditText)this.findViewById(R.id.EditText02);
        y = (EditText)this.findViewById(R.id.bid);
        status = (TextView) this.findViewById(R.id.textView4);
        
        plusX = (Button) this.findViewById(R.id.upload);
        minusX = (Button) this.findViewById(R.id.load);
        plusY = (Button) this.findViewById(R.id.button3);
        minusY = (Button) this.findViewById(R.id.button4);
        
        plusX.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				x.setText("" + (Integer.parseInt(x.getText().toString()) + 1));
			}
        	
        });
        minusX.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				x.setText("" + (Integer.parseInt(x.getText().toString()) - 1));
			}
        	
        });
        
        plusY.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				y.setText("" + (Integer.parseInt(y.getText().toString()) + 1));
			}
        	
        });
        minusY.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				y.setText("" + (Integer.parseInt(y.getText().toString()) - 1));
			}
        	
        });
                
        
        uri = this.getString(R.string.server_root_url);
        
        ws = new WifiSample();
        
        IntentFilter i = new IntentFilter();
        i.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(new BroadcastReceiver(){
        	@Override
        	public void onReceive(Context c, Intent i){
		        // Code to execute when SCAN_RESULTS_AVAILABLE_ACTION event
				Log.v("dataing", "srcsrc");
		        WifiManager w = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
		        //w.getScanResults(); // Returns a <list> of scanResults

				List<ScanResult> scanResults=w.getScanResults();
			    
				Log.d("dataing", "here");
				
				ws.buildingId = 1;
		        ws.floor = Integer.parseInt(floor.getText().toString());
		        ws.x = Integer.parseInt(x.getText().toString());
		        ws.y = Integer.parseInt(y.getText().toString());
		        
		        Map<String, Integer> irr = new HashMap<String, Integer>();
		        
			    for (ScanResult scanResult : scanResults) {
			    	irr.put(scanResult.BSSID, scanResult.level);			   
			    }
			    
			    ws.packFingerPrint2(irr);
			    
			    Log.v("dataing", ws.fingerPrintPack);
			    
			    json =  new JsonThread();
		        new Thread(json).start();
	        }
        }, i);
        
        sample.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				sample.setEnabled(false);
				// TODO Auto-generated method stub
				//WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
				//wm.startScan();
				scan =  new ScanThread();
				new Thread(scan).start();
			}
        	
        });

        Log.v("srcs", JsonUtils.packObjToJson(ws));
        
        
    }
    
    class ScanThread implements Runnable {
    	public void run() {
    		WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
			wm.startScan();
    	}
    }
    
    class JsonThread implements Runnable{
        public void run() {
            try {
            	handler.sendEmptyMessage(START);
            	Log.d("dataing", uri + "tMap/wifi/addWifi");
                data = HttpUtils.getInstance().postData(uri + "tMap/wifi/addWifi", JsonUtils.packObjToJson(ws));
                if(data != null){
                    Log.e("dataing", data.toString());
                }
                handler.sendEmptyMessage(COMPLETE);
                Log.e("data", "nullljsdflsdf");
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("JsonThread", e.getMessage());
            }
        }
        public String getData(){
            return data;
        }
    }
    
    Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch(msg.what){
            case START:
            	status.setText("sending...");
            	break;
            case COMPLETE:
                data = json.getData();
                sample.setEnabled(true);
                list = JsonUtils.parseResponse(data);
                if (list != null) {
                    status.setText(list.exception + " " + list.response);
                }
                break;
            }
        };
    };
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
