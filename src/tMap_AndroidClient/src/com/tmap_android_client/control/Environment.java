package com.tmap_android_client.control;

import com.example.tmap_androidclient.R;
import com.tmap_android_client.datatransfer.DirectorPoint;

import android.app.Activity;

public class Environment {
	private static Environment instance;
	
	// server URL
	public String serverURL;
	
	// current locating result
	public int buildingId, floor;
	public float x, y, direction;
	public boolean located = false;
	
	// orientation rotate bias
	public float orientationBias = 0;
	public boolean orientationAdjusting = false;
	
	// director list
	public DirectorPoint[] directorPoint = null;
	
	private Environment(Activity activity) {
		this.serverURL = activity.getString(R.string.server_root_url);
	};
	
	public static Environment getInstance(Activity activity) {
		if (instance == null) {
			synchronized(Environment.class) {
				instance = new Environment(activity);
			}
		}
		return instance;
	}
	
	public static Environment getInstance() {
		return instance;
	}
}