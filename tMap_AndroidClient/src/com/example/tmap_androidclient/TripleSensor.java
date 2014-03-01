package com.example.tmap_androidclient;

import com.tmap_android_client.activities.SensorActivity;
import com.tmap_android_client.sensor.BaseSensor;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;

public class TripleSensor extends SensorActivity {
	
	private BaseSensor bs = null;
	
	public TextView x = null, y = null, z = null, w = null;
	public Button btn = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_triple_sensor);
		x = (TextView) this.findViewById(R.id.TextView02);
		y = (TextView) this.findViewById(R.id.TextView01);
		z = (TextView) this.findViewById(R.id.buildingId);
		w = (TextView) this.findViewById(R.id.textView1);
		btn = (Button) this.findViewById(R.id.locating);
		
		bs = new BaseSensor(this, 0);
		bs.bindSensorType(Sensor.TYPE_ACCELEROMETER);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		bs.sensorRelease();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		bs.sensorResume();
	}

	@Override
	public void SensorChanged(int sensorId) {
		// TODO Auto-generated method stub
		x.setText(bs.sensorValues[0] + "");
		y.setText(bs.sensorValues[1] + "");
		z.setText(bs.sensorValues[2] + "");
		float x = bs.sensorValues[0] * bs.sensorValues[0] +
				bs.sensorValues[1] * bs.sensorValues[1] +
				bs.sensorValues[2] * bs.sensorValues[2];
		w.setText(
				x + 
				"");
		btn.setBackgroundColor(Color.rgb((int)x, (int)x, (int)x));
		
	}
	
}
