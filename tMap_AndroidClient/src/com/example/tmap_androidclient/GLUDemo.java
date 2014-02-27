package com.example.tmap_androidclient;

import java.util.ArrayList;

import com.tmap_android_client.activities.SensorActivity;
import com.tmap_android_client.opengl.Box;
import com.tmap_android_client.opengl.ColorPlane;
import com.tmap_android_client.opengl.Cylinder;
import com.tmap_android_client.opengl.Director;
import com.tmap_android_client.opengl.Geometry;
import com.tmap_android_client.opengl.MaterialPlane;
import com.tmap_android_client.opengl.MySurfaceView;
import com.tmap_android_client.sensor.BaseSensor;

import android.app.Activity;
import android.hardware.Sensor;
import android.os.Bundle;

public class GLUDemo extends SensorActivity {
	
	private BaseSensor bs = null;
	
    /** Called when the activity is first created. */
	MySurfaceView mGLSurfaceView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        	float[] vertices1 = new float[] {
        			5, 8, 0,
        			-5, 8, 0,
        			-5, -8, 0,
        			-5, -8, 0,
        			5, -8, 0,
        			5, 8, 0,
        	};
        	
        	MaterialPlane c1 = new MaterialPlane(vertices1, 4);

        	float[] vertices2 = new float[] {
        			5, -8, 0,
            		-5, -8, 0,
            		-5, -8, 6,
            		5, -8, 0,
            		-5, -8, 6,
            		5, -8, 6,
            };
            ColorPlane c2 = new ColorPlane(vertices2, 0.8922f, 0.4941f, 0.7333f);
            
            float[] vertices3 = new float[] {
            		-5, 8, 0,
            		5, 8, 0,
            		5, 8, 6,
            		-5, 8, 0,
            		5, 8, 6,
            		-5, 8, 6,
            };
            ColorPlane c3 = new ColorPlane(vertices3, 0.8922f, 0.4941f, 0);
            
            float[] vertices4 = new float[] {
            		-5, -8, 0,
            		-5, 8, 0,
            		-5, 8, 6,
            		-5, -8, 0,
            		-5, 8, 6,
            		-5, -8, 6,
            };
            ColorPlane c4 = new ColorPlane(vertices4, 0, 0.4941f, 0.7333f);
            
            float[] vertices5 = new float[] {
            		5, -8, 0,
            		5, -8, 6,
            		5, 8, 6,
            		5, -8, 0,
            		5, 8, 6,
            		5, 8, 0,
            };
            ColorPlane c5 = new ColorPlane(vertices5, 0.9137f, 1, 0);
            
        	float[] vertices6 = new float[] {
        			5, 8, 6,
        			5, -8, 6,
        			-5, -8, 6,
        			-5, -8, 6,
        			-5, 8, 6,
        			5, 8, 6,
        	};
        	
        	MaterialPlane c6 = new MaterialPlane(vertices6, 1);
	
        	
        	ArrayList<Geometry> geoList = new ArrayList<Geometry>();
        	geoList.add(c1);
        	geoList.add(new Box(-2, -5, 3, 1, 2, 3, 0.2f, 0.6f, 0.9f));
        	geoList.add(new Cylinder(2, -5, -3, 1, 3.5f, 1, 0.6f, 0.9f));
        	geoList.add(new Director(1, -1, 1, 10, -10, 4));
//        	geoList.add(new Cylinder(2, -5, 3, 1, 3.5f, 5));

        	geoList.add(c2);
        	geoList.add(c3);
        	geoList.add(c4);
        	geoList.add(c5);
        	geoList.add(c6);
        	
        	
        	//float[] camera = new float[]{0, 0, 0, 0, 0, -1, 0, 1, 0};
        	float[] camera = new float[]{0, 0, 2, 1, 1, 2, 0, 0, 3};
        	
            mGLSurfaceView = new MySurfaceView(this, geoList);
            mGLSurfaceView.setCamera(camera);
            mGLSurfaceView.requestFocus();
            mGLSurfaceView.setFocusableInTouchMode(false);
            
            bs = new BaseSensor(this, 0);
    		bs.bindSensorType(Sensor.TYPE_ORIENTATION);
            
            setContentView(mGLSurfaceView);        
        }


		@Override
		public void SensorChanged(int sensorId) {
			// TODO Auto-generated method stub
			/*x.setText(bs.sensorValues[0] + "");
			y.setText(bs.sensorValues[1] + "");
			z.setText(bs.sensorValues[2] + "");
			*/
			//float[] camera = new float[]{0, 0, 0, 0, 0, -1, 0, 1, 0};
			double uper = -90 - bs.sensorValues[1];
        	float[] camera = new float[]{0, 0, 2.5f, (float)(Math.sin(bs.sensorValues[0] / 180 * Math.PI)*Math.cos(uper / 180 * Math.PI)), (float)(Math.cos(bs.sensorValues[0] / 180 * Math.PI)*Math.cos(uper / 180 * Math.PI)), 2.5f + (float)Math.sin(uper / 180 * Math.PI), 0, 0, 3};
    
            mGLSurfaceView.setCamera(camera);
			
		}
    
        @Override
        protected void onResume() {
            super.onResume();
            mGLSurfaceView.onResume();
            bs.sensorResume();
        }

        @Override
        protected void onPause() {
            super.onPause();
            mGLSurfaceView.onPause();
            bs.sensorRelease();
        }    
}