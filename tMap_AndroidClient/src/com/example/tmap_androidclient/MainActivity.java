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

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {
	private static final int COMPLETE = 0x1000;
	public String data = null;
	private TextView tx;
	public JsonThread json;
	private Response list = null;
	
	String uri;
	
	public WifiSample ws;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tx = (TextView) this.findViewById(R.id.show);
        
        uri = this.getString(R.string.server_root_url);
        
        ws = new WifiSample();
        ws.id = 0;
        ws.buildingId = 1;
        ws.x = ws.y = 5;
        ws.floor = 2;
        
        Map<String, Integer> irr = new HashMap<String, Integer>();
        irr.put("000sdfdsfs", 12);
        irr.put("111ssfs", 1);
        irr.put("2222323s", -20);
        ws.packFingerPrint2(irr);
        /*
        List<WifiItem> iss = new ArrayList<WifiItem>();
        
        iss.add(new WifiItem("sdfdsfs", 12));
        iss.add(new WifiItem("ssfs", 1));
        iss.add(new WifiItem("2323s", -20));
        
        ws.packFingerPrint(iss);
        */
        Log.v("srcs", JsonUtils.packObjToJson(ws));
        
        json =  new JsonThread();
        new Thread(json).start();
    }

    class JsonThread implements Runnable{
        public void run() {
            try {
            	Log.d("dataing", uri + "tMap/wifi/addWifi");
                data = HttpUtils.getInstance().postData(uri + "tMap/wifi/addWifi", JsonUtils.packObjToJson(ws));
                if(data != null){
                    Log.e("dataing", data.toString());
                }
                //handler.sendEmptyMessage(COMPLETE);
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
            case COMPLETE:
                data = json.getData();
                
                list = JsonUtils.parseResponse(data);
                //data = list.response;
                //Log.e("data", ""+data.toString());
                tx.setText(list.exception + " " + list.response);
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
