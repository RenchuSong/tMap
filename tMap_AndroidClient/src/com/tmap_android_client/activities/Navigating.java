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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Navigating extends BaseActivity implements SensorActivity {
	
	private static final int ORIENTATION_SENSOR_ID = 0;
	private static final int ACCELEROMETER_SENSOR_ID = 1;
	
	
	// default as 3d mode
	private boolean mode3D = true;
	// 3d map loading complete
	private boolean map3DComplete = false;
	// 3d map direction adjusting
	private boolean adjusting = false;
	
	// step counter related
	private final float lowThreshold = 90, highThreshold = 105, delta = 50;
	private float stepMax = 50, stepMin = 150;
	private int stepState = 0;
	
	// 3d accelerate meter sensor
	private BaseSensor oriSensor = null, accSensor = null;
	
	// UI components
	private RelativeLayout panel;	// panel layer
	private LinearLayout mapLayer;	// map layer
	private Map3DSurfaceView mapSurface = null;	// 3d map model layer
	private Button leftButtonBtn;
	
	// HTTP results
	private String modelPack;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.navigate_page);
        
        // Get UI components
        this.panel = (RelativeLayout) this.findViewById(R.id.panel_container);
        this.mapLayer = (LinearLayout) this.findViewById(R.id.map_container);
        this.leftButtonBtn = (Button) this.findViewById(R.id.left_button_panel);
        
        this.leftButtonBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (mapSurface != null) {
					Environment.getInstance().orientationAdjusting = !Environment.getInstance().orientationAdjusting;
					/** TODO change view image */
				}
			}
        	
        });
        
        // get bundle data
        getBundleData();
        
        // load map
        loadMap();
        
        // bind orientation meter sensor
        oriSensor = new BaseSensor(this, ORIENTATION_SENSOR_ID);
        oriSensor.bindSensorType(Sensor.TYPE_ORIENTATION);
        oriSensor.sensorRelease();
        
        // bind accelerometer sensor
        accSensor = new BaseSensor(this, ACCELEROMETER_SENSOR_ID);
        accSensor.bindSensorType(Sensor.TYPE_ACCELEROMETER);
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
			// TODO load 2d model
		}
	}
	
	// load 3d model
	private void initiate3DModel() {
		// objs
		ObjectDescription[] objs = JsonUtils.parseModelList(this.modelPack);
		if (objs != null) {
        	ArrayList<Geometry> geoList = (new ObjectDescription()).createGeometryList(objs);
        	mapSurface = new Map3DSurfaceView(this, geoList);
        	mapSurface.requestFocus();
			mapSurface.setFocusableInTouchMode(true);
        	mapLayer.addView(mapSurface);
        	map3DComplete = true;
        	// start orientation sensing
        	oriSensor.sensorResume();
        	// start accelermeter sensing
        	accSensor.sensorResume();
        }
	}
	
	@Override
	public void SensorChanged(int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case ORIENTATION_SENSOR_ID:
			if (this.mode3D && this.map3DComplete) {
				// Y direction
				float uper = -90 - oriSensor.sensorValues[1];
					
				// bias
				float orientationBias = Environment.getInstance(this).orientationBias;
		       	// camera matrix
				float[] camera = new float[]{
						Environment.getInstance(this).x, 
						Environment.getInstance(this).y, 
						1.7f, 
						Environment.getInstance(this).x + (float)(Math.sin((oriSensor.sensorValues[0] + orientationBias) / 180 * Math.PI)*Math.cos(uper / 180 * Math.PI)),
						Environment.getInstance(this).y + (float)(Math.cos((oriSensor.sensorValues[0] + orientationBias) / 180 * Math.PI)*Math.cos(uper / 180 * Math.PI)), 
						1.7f + (float)Math.sin(uper / 180 * Math.PI), 
						0, 
						0, 
						3
				};
					
				mapSurface.setCamera(camera);
				Environment.getInstance().direction = oriSensor.sensorValues[0] + orientationBias;
			}
			break;
		case ACCELEROMETER_SENSOR_ID:
			if (this.mode3D && this.map3DComplete) {
				float x = 	accSensor.sensorValues[0] * accSensor.sensorValues[0] +
							accSensor.sensorValues[1] * accSensor.sensorValues[1] +
							accSensor.sensorValues[2] * accSensor.sensorValues[2];
				if (stepState == 0) {
					if (x > this.highThreshold) {
						stepState = 1;
						if (x > stepMax) {
							stepMax = x;
						}
					}
				} else if (stepState == 1) {
					if (x < this.lowThreshold) {
						stepState = 2;
					} else if (x > stepMax) {
						stepMax = x;
					}
				} else if (stepState == 2) {
					if (x > this.highThreshold) {
						if (stepMax - stepMin > delta) {			            	
							if (mapSurface != null) {
								mapSurface.stepFurther();
							}
						}
						stepMax = 50;
						stepMin = 150;
						stepState = 1;
					} else if (x < stepMin) {
						stepMin = x;
					}
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

	@Override
    protected void onResume() {
        super.onResume();
        if (mapSurface != null) {
        	mapSurface.onResume();
        }
        
        if (oriSensor != null) {
        	oriSensor.sensorResume();
        }
        if (accSensor != null) {
        	accSensor.sensorResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapSurface != null) {
        	mapSurface.onPause();
        }
        
        if (oriSensor != null) {
        	oriSensor.sensorRelease();
        }
        if (accSensor != null) {
        	accSensor.sensorRelease();
        }
        
    }    
}
