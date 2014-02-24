package com.tmap_android_client.sensor;

import com.tmap_android_client.activities.SensorActivity;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class BaseSensor implements SensorEventListener {
	private int id;
	private SensorActivity bindActivity;
	private SensorManager sensorManager = null;
	private Sensor sensor = null;
	public float[] sensorValues;		// sensor values
	
	// Construct with an activity
	public BaseSensor(SensorActivity bindActivity, int id) {
		this.id = id;
		this.bindActivity = bindActivity;
	}
	
	// Bind sensor to a specific sensor
	public void bindSensorType(int SENSOR_TYPE) {
		sensorManager = (SensorManager) this.bindActivity.getSystemService(Activity.SENSOR_SERVICE);
		sensor = sensorManager.getDefaultSensor(SENSOR_TYPE);
	}
	
	// Call when the activity pauses
	public void activityPause() {
		sensorManager.unregisterListener(this);
	}
	
	public void activityResume() {
		sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
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
