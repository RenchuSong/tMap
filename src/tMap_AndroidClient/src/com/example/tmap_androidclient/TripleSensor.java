package com.example.tmap_androidclient;

import com.tmap_android_client.activities.BaseActivity;
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

public class TripleSensor extends BaseActivity implements SensorActivity {
	
	private BaseSensor bs = null;
	int stepCount = 0;
	float lowThreshold = 90, highThreshold = 105, delta = 40;
	float stepMax = 50, stepMin = 150;
	
	public TextView x = null, y = null, z = null, w = null, big = null, small = null, step = null;
	public Button btn = null;
	
	public int state = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_triple_sensor);
		x = (TextView) this.findViewById(R.id.TextView02);
		y = (TextView) this.findViewById(R.id.TextView01);
		z = (TextView) this.findViewById(R.id.buildingId);
		w = (TextView) this.findViewById(R.id.textView1);
		btn = (Button) this.findViewById(R.id.locating);
		
		big = (TextView) this.findViewById(R.id.big);
		small = (TextView) this.findViewById(R.id.small);
		step = (TextView) this.findViewById(R.id.step);
		
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
		if (x > Float.parseFloat(big.getText().toString())) {
			big.setText(x + "");
		}
		if (x < Float.parseFloat(small.getText().toString())) {
			small.setText(x + "");
		}
		
		btn.setBackgroundColor(Color.rgb((int)x, (int)x, (int)x));
		
//		if (state == 0) {
//			if (x > highThreshold) {
//				state = 1;
//			} else if (x < lowThreshold) {
//				state = 3;
//			}
//		} else if (state == 1) {
//			if (x < lowThreshold) {
//				state = 2;
//			}
//		} else if (state == 3) {
//			if (x > highThreshold) {
//				state = 4;
//			}
//		} else if (state == 2) {
//			if (x > 90) {
//				state = 0;
//			}
//		} else if (state == 4) {
//			if (x < 105) {
//				state = 0;
//			}
//		}
//		
//		if (state == 2 || state == 4) {
//			++stepCount;
//			step.setText(stepCount + "");
//		}
		
		if (state == 0) {
			if (x > this.highThreshold) {
				state = 1;
				if (x > stepMax) {
					stepMax = x;
				}
			}
		} else if (state == 1) {
			if (x < this.lowThreshold) {
				state = 2;
			} else if (x > stepMax) {
				stepMax = x;
			}
		} else if (state == 2) {
			if (x > this.highThreshold) {
				if (stepMax - stepMin > delta) {
					++stepCount;
					step.setText(stepCount + "");
					stepMax = 50;
					stepMin = 150;
				}
				
				state = 1;
			} else if (x < stepMin) {
				stepMin = x;
			}
		}
	}
	
}
