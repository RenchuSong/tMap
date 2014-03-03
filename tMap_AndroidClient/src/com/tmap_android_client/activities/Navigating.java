package com.tmap_android_client.activities;

import java.util.ArrayList;

import com.example.tmap_androidclient.ModelCreator;
import com.example.tmap_androidclient.R;
import com.tmap_android_client.control.Environment;
import com.tmap_android_client.control.ExitApplication;
import com.tmap_android_client.datatransfer.HttpUtils;
import com.tmap_android_client.datatransfer.JsonUtils;
import com.tmap_android_client.opengl.Box;
import com.tmap_android_client.opengl.Geometry;
import com.tmap_android_client.opengl.Map3DSurfaceView;
import com.tmap_android_client.opengl.ObjectDescription;
import com.tmap_android_client.sensor.BaseSensor;
import com.tmap_android_client.wifi.WifiFactory;

import android.hardware.Sensor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Navigating extends BaseActivity implements SensorActivity {
	
	private static final int ORIENTATION_SENSOR_ID = 0;
	
	// default as 3d mode
	private boolean mode3D = true;
	// 3d map loading complete
	private boolean map3DComplete = false;
	// 3d accelerate meter sensor
	private BaseSensor accSensor = null;
	
	// UI components
	private RelativeLayout panel;	// panel layer
	private LinearLayout mapLayer;	// map layer
	private Map3DSurfaceView mapSurface = null;	// 3d map model layer
	
	// HTTP results
	private String modelPack;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.navigate_page);
        
        // Get UI components
        this.panel = (RelativeLayout) this.findViewById(R.id.panel_container);
        this.mapLayer = (LinearLayout) this.findViewById(R.id.map_container);
        
        // get bundle data
        getBundleData();
        
        // load map
        loadMap();
        
        // bind accelerate meter sensor
        accSensor = new BaseSensor(this, ORIENTATION_SENSOR_ID);
        accSensor.bindSensorType(Sensor.TYPE_ORIENTATION);
        accSensor.sensorRelease();
        
		
	}

	// get bundle data
	private void getBundleData() {
		Bundle bundle = getIntent().getExtras();
		String action = bundle.getString("action");
		if (action.equalsIgnoreCase("locating")) {
			Environment.getInstance(this).buildingId = bundle.getInt("buildingId");
			Environment.getInstance(this).floor = bundle.getInt("floor");
			Environment.getInstance(this).x = bundle.getFloat("x");
			Environment.getInstance(this).y = bundle.getFloat("y");
		}
	}
	
	// load map
	private void loadMap() {
		if (this.mode3D) {	// load 3d model
            JsonThread loadMap = new JsonThread("building", "getModel", new String[]{Environment.getInstance(this).buildingId + "", Environment.getInstance(this).floor + ""}, "");
            new Thread(loadMap).start();
		} else {
			// load 2d model
		}
	}
	
	// load 3d model
	private void initiate3DModel() {
		// objs
		ObjectDescription[] objs = JsonUtils.parseModelList(this.modelPack);
		if (objs != null) {
        	ArrayList<Geometry> geoList = (new ObjectDescription()).createGeometryList(objs);
        	mapSurface = new Map3DSurfaceView(this, geoList);
        	mapLayer.addView(mapSurface);
        	map3DComplete = true;
        	// start orientation sensing
        	accSensor.sensorResume();
        }
	}
	
	@Override
	public void SensorChanged(int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case ORIENTATION_SENSOR_ID:
			if (id == ORIENTATION_SENSOR_ID) {
				if (this.mode3D && this.map3DComplete) {
					// Y direction
					double uper = -90 - accSensor.sensorValues[1];
					
					// bias
					float orientationBias = Environment.getInstance(this).orientationBias;
		        	// camera matrix
					float[] camera = new float[]{
							Environment.getInstance(this).x, 
							Environment.getInstance(this).y, 
							1, 
							(float)(Math.sin(accSensor.sensorValues[0] / 180 * Math.PI)*Math.cos(uper / 180 * Math.PI)),
							(float)(Math.cos(accSensor.sensorValues[0] / 180 * Math.PI)*Math.cos(uper / 180 * Math.PI)), 
							1 + (float)Math.sin(uper / 180 * Math.PI), 
							0, 
							0, 
							3
					};
		    
					mapSurface.setCamera(camera);
				}
			}
			break;
		}
	}
	
	Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch(msg.what){
            // process data after receiving from server
            case R.integer.MSG_3D_MODEL_LOAD_COMPLETE:
            	// load end, remove progress bar
            	panel.removeView(Navigating.this.findViewById(R.id.loading_map));
            	// construct 3d model
            	initiate3DModel();
            	break;
            case R.integer.MSG_COMPLETE:
            	
            // network failure
            case R.integer.MSG_NET_FAIL:
            	Toast.makeText(getApplicationContext(), R.string.web_link_fail_hint, Toast.LENGTH_SHORT).show();
            	break;
            }
        };
    };
    
	
	// exit program by double click return button
	private long exitTime = 0;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){   
	        if((System.currentTimeMillis()-exitTime) > 2000){  
	            Toast.makeText(getApplicationContext(), R.string.quit_hint, Toast.LENGTH_SHORT).show();                                
	            exitTime = System.currentTimeMillis();   
	        } else {
	            ExitApplication.getInstance().exit();
	        }
	        return true;   
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	// network data transfer
	class JsonThread implements Runnable{
		public String controller = null;
		public String action = null;
		public String[] params = null;
		public String json = null;
		public String data = null;
		
		public JsonThread(String controller, String action, String[] params, String json) {
			this.controller = controller;
			this.action = action;
			this.params = params;
			this.json = json;
		}
		
        public void run() {
            try {
            	String url = Environment.getInstance(Navigating.this).serverURL + "tMap/"+controller+"/" + action;
            	for (String param: params) {
            		url += "/" + param;
            	}
                data = HttpUtils.getInstance().postData(url, json);
                
                // Judge action and set handler message
                if (controller.equalsIgnoreCase("building") && action.equalsIgnoreCase("getModel")) {
                	// model data received
                	modelPack = data;
                	handler.sendEmptyMessage(R.integer.MSG_3D_MODEL_LOAD_COMPLETE);
                } else {
                	handler.sendEmptyMessage(R.integer.MSG_COMPLETE);
                }
                
            } catch (Exception e) {
            	handler.sendEmptyMessage(R.integer.MSG_NET_FAIL);                         
            }
        }
    }

}
