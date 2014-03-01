package com.tmap_android_client.activities;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

public class Welcome extends Activity {
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
	}
	
	
}
