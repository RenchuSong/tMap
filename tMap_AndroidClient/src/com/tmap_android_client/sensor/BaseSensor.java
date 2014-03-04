package com.tmap_android_client.sensor;

import com.tmap_android_client.activities.SensorActivity;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class BaseSensor implements SensorEventListener {
	private int id;
	private SensorActivity bindActivity;
	private SensorManager sensorManager = null;
	private Sensor sensor = null;
	public float[] sensorValues;		// sensor values
	public boolean sensorExist = true;
	
	// Construct with an activity
	public BaseSensor(SensorActivity bindActivity, int id) {
		this.id = id;
		this.bindActivity = bindActivity;
	}
	
	// Bind sensor to a specific sensor
	public void bindSensorType(int SENSOR_TYPE) {
		sensorManager = (SensorManager) ((Activity)this.bindActivity).getSystemService(Activity.SENSOR_SERVICE);
		sensor = sensorManager.getDefaultSensor(SENSOR_TYPE);
		Log.d("dataing", sensor + "");
		if (sensor == null) {
			this.sensorExist = false;
		}
	}
	
	// Call when the activity pauses
	public void sensorRelease() {
		sensorManager.unregisterListener(this);
	}
	
	public void sensorResume() {
		if (this.sensorExist) {
			sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
		}
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		this.sensorValues = event.values;
		this.bindActivity.SensorChanged(this.id);	// call to react on sensor changes
	}

}
