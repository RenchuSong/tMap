package com.tmap_android_client.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.example.tmap_androidclient.ModelCreator;
import com.example.tmap_androidclient.R;
import com.tmap_android_client.control.Environment;
import com.tmap_android_client.control.ExitApplication;
import com.tmap_android_client.datatransfer.HttpUtils;
import com.tmap_android_client.datatransfer.JsonUtils;
import com.tmap_android_client.opengl.Box;
import com.tmap_android_client.opengl.Geometry;
import com.tmap_android_client.opengl.ObjectDescription;
import com.tmap_android_client.wifi.Location;
import com.tmap_android_client.wifi.WifiFactory;
import com.tmap_android_client.wifi.WifiSample;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.TransitionDrawable;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Settings extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		this.setContentView(R.layout.settings);
	}
	
}
