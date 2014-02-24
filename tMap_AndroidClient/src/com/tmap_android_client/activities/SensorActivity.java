package com.tmap_android_client.activities;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.SensorEvent;
import android.os.Bundle;

public abstract class SensorActivity extends Activity {
	public abstract void SensorChanged(int id);
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
	}
}
