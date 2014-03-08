package com.tmap_android_client.activities;

import com.tmap_android_client.control.ExitApplication;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.SensorEvent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class BaseActivity extends Activity {
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
		//		WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); 
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        
        ExitApplication.getInstance().addActivity(this);
	}
	
//	public void onConfigurationChanged(Configuration newConfig) { 
//        try { 
//            super.onConfigurationChanged(newConfig);
//            //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); 
//            if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) { 
//                Log.v("Himi", "onConfigurationChanged_ORIENTATION_LANDSCAPE"); 
//            } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) { 
//                Log.v("Himi", "onConfigurationChanged_ORIENTATION_PORTRAIT"); 
//            } 
//        } catch (Exception ex) { 
//        } 
//    } 
	
//	@Override 
//	protected void onResume() { 
//		if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){ 
//			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); 
//		} 
//		super.onResume(); 
//	}
}
