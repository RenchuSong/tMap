package com.example.tmap_androidclient;

import java.util.ArrayList;

import com.example.tmap_androidclient.MainActivity.JsonThread;
import com.tmap_android_client.datatransfer.HttpUtils;
import com.tmap_android_client.datatransfer.JsonUtils;
import com.tmap_android_client.datatransfer.Response;
import com.tmap_android_client.opengl.Box;
import com.tmap_android_client.opengl.ColorPlane;
import com.tmap_android_client.opengl.Cylinder;
import com.tmap_android_client.opengl.Director;
import com.tmap_android_client.opengl.Geometry;
import com.tmap_android_client.opengl.MaterialPlane;
import com.tmap_android_client.opengl.MySurfaceView;
import com.tmap_android_client.opengl.ObjectDescription;
import com.tmap_android_client.sensor.BaseSensor;
import com.tmap_android_client.wifi.WifiSample;

import android.app.Activity;
import android.hardware.Sensor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ModelCreator extends Activity implements OnClickListener{
	private static final int START = 0x0000;
	private static final int COMPLETE = 0x1000;
	private static final int COMPLETE2 = 0x2000;
	public ArrayList<Geometry> geoList;
	
	Button load, upload;
	EditText bid, fid, x,y,z;
	public String data = null;
	String uri;
	public JsonThread json;
	private Response list = null;
	public ArrayList<ObjectDescription> objs;
	
    /** Called when the activity is first created. */
	MySurfaceView mGLSurfaceView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.model_creator);
        load = (Button) this.findViewById(R.id.load);
        upload = (Button) this.findViewById(R.id.upload);
        bid = (EditText) this.findViewById(R.id.bid);
        fid = (EditText) this.findViewById(R.id.fid);
        x = (EditText) this.findViewById(R.id.x);
        y = (EditText) this.findViewById(R.id.y);
        z = (EditText) this.findViewById(R.id.z);
        uri = this.getString(R.string.server_root_url);
        
        
        load.setOnClickListener(this);
        upload.setOnClickListener(this);
        
        //creat model region===================================================
        geoList = new ArrayList<Geometry>();
    	
        /*
         *  This is Boobo's Lab's model
         */
        /*	//floor
        	float[] vertices1 = new float[] {
        			0, 0, 0,
        			8.3f, 0, 0,
        			8.3f, 12.5f, 0,
        			8.3f, 12.5f, 0,
        			0, 12.5f, 0,
        			0, 0, 0
        	};
        	
        	MaterialPlane c1 = new MaterialPlane(vertices1, 8);

        	geoList.add(c1);
        	
        	// four walls
        	geoList.add(new Box(0, 0, 0, 8.3f, 0.1f, 3, 1, 1, 1));
        	
        	geoList.add(new Box(0, 0, 0, 0.1f, 12.5f, 1, 1, 1, 1));
        	geoList.add(new Box(0, 4, 1, 0.1f, 0.5f, 2, 1, 1, 1));
        	geoList.add(new Box(0, 8, 1, 0.1f, 0.5f, 2, 1, 1, 1));
        	
        	geoList.add(new Box(8.2f, 0, 0, 0.1f, 10.5f, 3, 1, 1, 1));
        	
        	geoList.add(new Box(0, 12.4f, 0, 8.3f, 0.1f, 3, 1, 1, 1));
        	
        	//cells
        	geoList.add(new Box(3.5f, 0, 0, 0.05f, 1.6f, 1.2f, 12));
        	geoList.add(new Box(3.5f, 11, 0, 0.05f, 1.6f, 1.2f, 12));
        	geoList.add(new Box(4.8f, 6.9f, 0, 0.05f, 1.6f, 1.2f, 12));
        	
        	geoList.add(new Box(3.5f, 2.6f, 0, 0.05f, 3.2f, 1.2f, 12));
        	geoList.add(new Box(3.5f, 6.8f, 0, 0.05f, 3.2f, 1.2f, 12));
        	geoList.add(new Box(4.8f, 2.8f, 0, 0.05f, 3.2f, 1.2f, 12));
        	
        	
        	geoList.add(new Box(0, 4.2f, 0, 3.5f, 0.05f, 1.2f, 12));
        	geoList.add(new Box(4.8f, 4.4f, 0, 3.5f, 0.05f, 1.2f, 12));
        	
        	geoList.add(new Box(0, 8.3f, 0, 3.5f, 0.05f, 1.2f, 12));
        	geoList.add(new Box(4.8f, 8.5f, 0, 3.5f, 0.05f, 1.2f, 12));
        	
        	// closet
        	geoList.add(new Box(3.7f, 12, 0, 1, 0.55f, 2, 11));
        	
        	// server
        	geoList.add(new Box(7.8f, 2, 0, 0.5f, 0.8f, 2, 10));
        	*/
        	
        //end creating=========================================================
        	objs = packGeoList(geoList);
        	
        	
        	//float[] camera = new float[]{0, 0, 0, 0, 0, -1, 0, 1, 0};
        	float[] camera = new float[]{1, -6, 9f, 3, 4, 0, 0, 0, 3};
        	
            mGLSurfaceView = new MySurfaceView(this, geoList);
            mGLSurfaceView.oriX = 1;
        	mGLSurfaceView.oriY = -6;
        	mGLSurfaceView.oriZ = 9f;
        	mGLSurfaceView.angle = (float) Math.atan((3 - 1) / (float)(9 + 3));
        	
            mGLSurfaceView.setCamera(camera);
            
            mGLSurfaceView.requestFocus();
            mGLSurfaceView.setFocusableInTouchMode(true);
            LinearLayout ll = (LinearLayout) this.findViewById(R.id.model_creator_linear);
            ll.addView(mGLSurfaceView);
            
        }
    	
    	public ArrayList<ObjectDescription> packGeoList(ArrayList<Geometry> geoList) {
    		ArrayList<ObjectDescription> result = new ArrayList<ObjectDescription>();
    		ObjectDescription os2 = new ObjectDescription();
    		os2.type = "rotator";
    		os2.data = new float[] {
    				Integer.parseInt(x.getText().toString()),
    				Integer.parseInt(y.getText().toString()),
    				Integer.parseInt(z.getText().toString()),
    				
    		};
    		result.add(os2);
    		for (int i = 0; i < geoList.size(); ++i) {
    			Geometry geo = geoList.get(i);
    			ObjectDescription os = new ObjectDescription();
    			if (geo instanceof Box) {
    				os.type = "Box";
    				if (((Box) geo).colorBox) {
    					os.data = new float[] {
    							((Box) geo).cornerX, ((Box) geo).cornerY, ((Box) geo).cornerZ,
    							((Box) geo).lenX, ((Box) geo).lenY, ((Box) geo).lenZ,
    							((Box) geo).rotateX, ((Box) geo).rotateY, ((Box) geo).rotateZ,
    							((Box) geo).red, ((Box) geo).green, ((Box) geo).blue
    					};
    				} else {
    					os.data = new float[] {
    							((Box) geo).cornerX, ((Box) geo).cornerY, ((Box) geo).cornerZ,
    							((Box) geo).lenX, ((Box) geo).lenY, ((Box) geo).lenZ,
    							((Box) geo).rotateX, ((Box) geo).rotateY, ((Box) geo).rotateZ,
    					};
    					os.texId = ((Box) geo).texId;
    				}
    				
    			}
    			if (geo instanceof Cylinder) {
    				os.type = "Cylinder";
    				if (((Cylinder) geo).colorCylinder) {
    					os.data = new float[] {
    						((Cylinder) geo).centerX, ((Cylinder) geo).centerY, ((Cylinder) geo).centerZ,
    						((Cylinder) geo).radius, ((Cylinder) geo).height,
    						((Cylinder) geo).rotateX, ((Cylinder) geo).rotateY, ((Cylinder) geo).rotateZ,
    						((Cylinder) geo).red, ((Cylinder) geo).green, ((Cylinder) geo).blue,
    					};
    				} else {
    					os.data = new float[] {
        						((Cylinder) geo).centerX, ((Cylinder) geo).centerY, ((Cylinder) geo).centerZ,
        						((Cylinder) geo).radius, ((Cylinder) geo).height,
        						((Cylinder) geo).rotateX, ((Cylinder) geo).rotateY, ((Cylinder) geo).rotateZ,
        					};
    					os.texId = ((Cylinder) geo).texId;
    				}
    			}
    			if (geo instanceof MaterialPlane) {
    				os.type = "Plane";
    				os.data = ((MaterialPlane) geo).vertices;
    				os.texId = ((MaterialPlane) geo).texId;
    			}
    			if (geo instanceof ColorPlane) {
    				os.type = "Plane";
    				int p = ((ColorPlane) geo).vertices.length;
    				os.data = new float[p + 3];
    				
    				for (int j = 0; j < p; ++j) {
    					os.data[j] = ((ColorPlane) geo).vertices[j];
    				}
    				os.data[p] = ((ColorPlane) geo).red;
    				os.data[p + 1] = ((ColorPlane) geo).green;
    				os.data[p + 2] = ((ColorPlane) geo).blue;
    			}
    			if (geo instanceof Director) {
    				os.type = "Director";
    				os.data = new float[] {
    						((Director) geo).x1, ((Director) geo).y1, ((Director) geo).z1,
    						((Director) geo).x2, ((Director) geo).y2, ((Director) geo).z2
    				};
    			}
    			
    			result.add(os);
    		}
    		return result;
    	}

        @Override
        protected void onResume() {
            super.onResume();
            mGLSurfaceView.onResume();
        }

        @Override
        protected void onPause() {
            super.onPause();
            mGLSurfaceView.onPause();
        }


		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (v.getId() == R.id.upload) {
				upload.setEnabled(false);
				json =  new JsonThread();
				json.action = "saveModel";
		        new Thread(json).start();
			}
			if (v.getId() == R.id.load) {
				load.setEnabled(false);
				json =  new JsonThread();
				json.action = "getModel";
		        new Thread(json).start();
			}
		}  
		
		class JsonThread implements Runnable{
			public String action = "";
			
	        public void run() {
	            try {
	            	handler.sendEmptyMessage(START);
	                data = HttpUtils.getInstance().postData(uri + "tMap/building/" + action + "/" + bid.getText().toString() + "/" + fid.getText().toString(), action.equalsIgnoreCase("saveModel") ? JsonUtils.packListToJson(objs) : "");
	                Log.d("dataing", uri + "tMap/building/" + action + "/" + bid.getText().toString() + "/" + fid.getText().toString() + "   "+ JsonUtils.packListToJson(objs));
	                if(data != null){
	                    Log.e("dataing", data.toString());
	                }
	                
	                if (action.equalsIgnoreCase("saveModel")) {
	                	 handler.sendEmptyMessage(COMPLETE);
	                } else {
	                	 handler.sendEmptyMessage(COMPLETE2);
	                }
	            } catch (Exception e) {
	                e.printStackTrace();
	                Log.e("JsonThread", e.getMessage());
	            }
	        }
	        public String getData(){
	            return data;
	        }
	    }
		
		public void updateGeometry() {
			mGLSurfaceView.geoList = this.geoList;
		}
		
		Handler handler = new Handler(){
	        public void handleMessage(android.os.Message msg) {
	            switch(msg.what){
	            case START:
	            	Toast.makeText(ModelCreator.this, "Sending", Toast.LENGTH_SHORT).show();
	            	break;
	            case COMPLETE:
	                data = json.getData();
	                Log.v("dataing", data + "");
	                upload.setEnabled(true);
	                list = JsonUtils.parseResponse(data);
	                if (list != null) {
		                Toast.makeText(ModelCreator.this, list.exception + " " + list.response, Toast.LENGTH_LONG).show();
	                }
	                break;
	            case COMPLETE2:
	                data = json.getData();
	                Log.v("dataing", data + "");
	                load.setEnabled(true);
	                ObjectDescription[] objs = JsonUtils.parseModelList(data);
	                
	                if (objs != null) {
	                	ModelCreator.this.geoList = (new ObjectDescription()).createGeometryList(objs);
	                	ModelCreator.this.updateGeometry();
	                }
	                break;
	            }
	        
	        };
	    };
}
