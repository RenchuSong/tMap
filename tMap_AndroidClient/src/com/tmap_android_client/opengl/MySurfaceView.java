package com.tmap_android_client.opengl;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.example.tmap_androidclient.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.view.MotionEvent;

public class MySurfaceView extends GLSurfaceView{
	public static int 	pointer, m1, m2, m3, m4, m5, 
						m6, m7, m8, m9, m10,
						m11, m12, m13, m14, m15;
	
	private final float TOUCH_SCALE_FACTOR = 180.0f/320;
    private SceneRenderer mRenderer;
    
    private ArrayList<Geometry> geoList = null;	// geometry list
    
    private float mPreviousX;
	
    public float gluX, gluY, gluZ, targetX, targetY, targetZ, upX, upY, upZ;
    public float oriX, oriY, oriZ, angle;
	
	public MySurfaceView(Context context, ArrayList<Geometry> geoList) {
        super(context);
        mRenderer = new SceneRenderer();
        setRenderer(mRenderer);				
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY); 
        this.geoList = geoList;
    }
	
	public void setCamera(float[] args) {	// set camera settings
		this.gluX = args[0];
		this.gluY = args[1];
		this.gluZ = args[2];
		
		this.targetX = args[3];
		this.targetY = args[4];
		this.targetZ = args[5];
		
		this.upX = args[6];
		this.upY = args[7];
		this.upZ = args[8];
	}
	
    @Override 
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        switch (e.getAction()) {
        case MotionEvent.ACTION_MOVE:
            float dx = x - mPreviousX;
            angle += dx * TOUCH_SCALE_FACTOR / 100;
            setCamera(new float[]{(float) (4 - Math.sin(angle) * 13), (float)(6 - Math.cos(angle) * 13), 9f, 3f, 4f, 0f, 0f, 0f, 3f});
            //requestRender();
        }   
        mPreviousX = x;
        return true;
    }

	private class SceneRenderer implements GLSurfaceView.Renderer {   
    	
        public void onDrawFrame(GL10 gl) {
    		gl.glEnable(GL10.GL_CULL_FACE);
    		
            gl.glShadeModel(GL10.GL_SMOOTH);
        	
        	gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
            gl.glMatrixMode(GL10.GL_MODELVIEW);
            gl.glLoadIdentity();    
            
            GLU.gluLookAt (
            		gl, 
            		gluX, gluY, gluZ,
            		targetX, targetY, targetZ,
            		upX, upY, upZ
            );  
            
            // draw geometries
            for (Geometry geo: geoList) {
            	geo.drawSelf(gl);
            }
        }

        public void onSurfaceChanged(GL10 gl, int width, int height) {
        	gl.glViewport(0, 0, width, height);
            gl.glMatrixMode(GL10.GL_PROJECTION);
            gl.glLoadIdentity();
            float ratio = (float) height/width ;
            gl.glFrustumf( -1, 1,-ratio, ratio, 2, 100);
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        	gl.glDisable(GL10.GL_DITHER);
            gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,GL10.GL_FASTEST);
            gl.glClearColor(0,100,0,0);            
            gl.glEnable(GL10.GL_DEPTH_TEST);
            
            // init material 
            pointer = initTexture(gl, R.drawable.pointer);
            m1 = initTexture(gl, R.drawable.m1);
            m2 = initTexture(gl, R.drawable.m2);
            m3 = initTexture(gl, R.drawable.m3);
            m4 = initTexture(gl, R.drawable.m4);
            m5 = initTexture(gl, R.drawable.m5);
            m6 = initTexture(gl, R.drawable.m6);
            m7 = initTexture(gl, R.drawable.m7);
            m8 = initTexture(gl, R.drawable.m8);
            m9 = initTexture(gl, R.drawable.m9);
            m10 = initTexture(gl, R.drawable.m10);
            m11 = initTexture(gl, R.drawable.m11);
            m12 = initTexture(gl, R.drawable.m12);
            m13 = initTexture(gl, R.drawable.m13);
            m14 = initTexture(gl, R.drawable.m14);
            m15 = initTexture(gl, R.drawable.m15);
            
            //gl.glEnable(GL10.GL_LIGHTING);
            //initSunLight(gl);
        }
    }
	
	private void initMaterial(GL10 gl)
	{
        float ambientMaterial[] = {0.4f, 0.4f, 0.4f, 1.0f};
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, ambientMaterial,0);
        float diffuseMaterial[] = {0.8f, 0.8f, 0.8f, 1.0f};
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, diffuseMaterial,0);
        float specularMaterial[] = {0.6f, 0.6f, 0.6f, 1.0f};
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, specularMaterial,0);
        float shininessMaterial[] = {1.5f};
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, shininessMaterial,0);
	}
	
	private void initSunLight(GL10 gl)
	{
		gl.glShadeModel(GL10.GL_SMOOTH);
    	gl.glEnable(GL10.GL_LIGHTING);
    	//initMaterialWhite(gl);
    	float[] positionParams0={0,1,0,0};
    	float[] positionParams1={1,0,0,0};
    	float[] positionParams2={0,0,1,0};
    	//float[] positionParams3={1,1,2,0};
    	//float[] positionParams4={-1,-1,2,0};
    	gl.glDisable(GL10.GL_LIGHT0);	
    	gl.glDisable(GL10.GL_LIGHT1);	
    	gl.glDisable(GL10.GL_LIGHT2);	
    	//gl.glDisable(GL10.GL_LIGHT3);	
    	//gl.glDisable(GL10.GL_LIGHT4);	
    	
    	initLight0(gl);
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, positionParams0,0); 
        
        initLight1(gl);
        gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_POSITION, positionParams1,0); 
        
        initLight2(gl);
        gl.glLightfv(GL10.GL_LIGHT2, GL10.GL_POSITION, positionParams2,0); 
		
        /*initLight3(gl);
        gl.glLightfv(GL10.GL_LIGHT3, GL10.GL_POSITION, positionParams3,0); 
        
        initLight4(gl);
        gl.glLightfv(GL10.GL_LIGHT4, GL10.GL_POSITION, positionParams4,0); */    	
	}
	
	private void initLight0(GL10 gl){
        gl.glEnable(GL10.GL_LIGHT0);
        float[] ambientParams={0.1f,0.1f,0.1f,1.0f};
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, ambientParams,0);            
        float[] diffuseParams={0.5f,0.5f,0.5f,1.0f};
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, diffuseParams,0); 
        float[] specularParams={1.0f,1.0f,1.0f,1.0f};
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, specularParams,0);     
	}
	private void initLight1(GL10 gl)
	{
        gl.glEnable(GL10.GL_LIGHT1);
        float[] ambientParams={0.1f,0.1f,0.1f,1.0f};
        gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_AMBIENT, ambientParams,0);            
        float[] diffuseParams={0.5f,0.1f,0.1f,1.0f};
        gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_DIFFUSE, diffuseParams,0); 
        float[] specularParams={1.0f,0.1f,0.1f,1.0f};
        gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_SPECULAR, specularParams,0);     
	}
	private void initLight2(GL10 gl)
	{
        gl.glEnable(GL10.GL_LIGHT2);
        float[] ambientParams={0.1f,0.1f,0.1f,1.0f};
        gl.glLightfv(GL10.GL_LIGHT2, GL10.GL_AMBIENT, ambientParams,0);            
        float[] diffuseParams={0.1f,0.1f,0.5f,1.0f};
        gl.glLightfv(GL10.GL_LIGHT2, GL10.GL_DIFFUSE, diffuseParams,0); 
        float[] specularParams={0.1f,0.1f,1.0f,1.0f};
        gl.glLightfv(GL10.GL_LIGHT2, GL10.GL_SPECULAR, specularParams,0);     
	}
	private void initLight3(GL10 gl)
	{
        gl.glEnable(GL10.GL_LIGHT3);
        float[] ambientParams={0.1f,0.1f,0.1f,1.0f};
        gl.glLightfv(GL10.GL_LIGHT3, GL10.GL_AMBIENT, ambientParams,0);            
        float[] diffuseParams={0.1f,0.5f,0.1f,1.0f};
        gl.glLightfv(GL10.GL_LIGHT3, GL10.GL_DIFFUSE, diffuseParams,0); 
        float[] specularParams={0.1f,1.0f,0.1f,1.0f};
        gl.glLightfv(GL10.GL_LIGHT3, GL10.GL_SPECULAR, specularParams,0);     
	}
	private void initLight4(GL10 gl)
	{
        gl.glEnable(GL10.GL_LIGHT4);
        float[] ambientParams={0.1f,0.1f,0.1f,1.0f};
        gl.glLightfv(GL10.GL_LIGHT4, GL10.GL_AMBIENT, ambientParams,0);            
        float[] diffuseParams={0.5f,0.5f,0.1f,1.0f};
        gl.glLightfv(GL10.GL_LIGHT4, GL10.GL_DIFFUSE, diffuseParams,0); 
        float[] specularParams={1.0f,1.0f,0.1f,1.0f};
        gl.glLightfv(GL10.GL_LIGHT4, GL10.GL_SPECULAR, specularParams,0);     
	}
	
	
	public int initTexture(GL10 gl,int drawableId) {
		int[] textures = new int[1];
		gl.glGenTextures(1, textures, 0);    
		int currTextureId=textures[0];    
		gl.glBindTexture(GL10.GL_TEXTURE_2D, currTextureId);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_MAG_FILTER,GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,GL10.GL_REPEAT);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,GL10.GL_REPEAT);
        
        InputStream is = this.getResources().openRawResource(drawableId);
        Bitmap bitmapTmp; 
        try {
        	bitmapTmp = BitmapFactory.decodeStream(is);
        }
        finally {
            try {
                is.close();
            } 
            catch(IOException e) {
                e.printStackTrace();
            }
        }
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmapTmp, 0);
        bitmapTmp.recycle(); 
        
        return currTextureId;
	}

	public static int getMaterialId(int texId) {
		switch (texId) {
		case 0:
			return pointer;
		case 1:
			return m1; 
		case 2:
			return m2; 
		case 3:
			return m3; 
		case 4:
			return m4; 
		case 5:
			return m5; 
		case 6:
			return m6; 
		case 7:
			return m7; 
		case 8:
			return m8; 
		case 9:
			return m9; 
		case 10:
			return m10;
		case 11:
			return m11; 
		case 12:
			return m12; 
		case 13:
			return m13; 
		case 14:
			return m14; 
		case 15:
			return m15; 
		default: return m1;	
		}
	}
}
