package com.tmap_android_client.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Circle implements Geometry{
	private boolean up = true;
	private float radius = 0;
	private double red, green, blue;
	private int texId;
	private boolean colorCircle = true;
	
	private FloatBuffer   mVertexBuffer;
    private FloatBuffer mTextureBuffer;
    private IntBuffer   mColorBuffer;
    private FloatBuffer mNormalBuffer;
    int vCount = 0;
	
	public Circle(float radius, boolean up, double red, double green, double blue) {
		this.radius = radius;
		this.up = up;
		this.red = red;
		this.green = green;
		this.blue = blue;
		initCircle();
	}
	
	public Circle(float radius, boolean up, int texId) {
		this.radius = radius;
		this.up = up;
		this.texId = texId;
		this.colorCircle = false;
		initCircle();
	}
	
	private void initCircle() {
		float[] vertices = new float[108 * 3];
		double degree = -5 / 180.0 * Math.PI, dd = 10 / 180.0 * Math.PI;
		if (!up) dd = -dd;
		
		for (int i = 0; i < 36; ++i) {
			vertices[i * 9] = vertices[i * 9 + 1] = vertices[i * 9 + 2] = 0;
			double nextDegree = degree + dd;
			vertices[i * 9 + 3] = (float)(Math.cos(degree) * this.radius);
			vertices[i * 9 + 4] = (float)(Math.sin(degree) * this.radius);
			vertices[i * 9 + 5] = 0;
			vertices[i * 9 + 6] = (float)(Math.cos(nextDegree) * this.radius);
			vertices[i * 9 + 7] = (float)(Math.sin(nextDegree) * this.radius);
			vertices[i * 9 + 8] = 0;
			degree = nextDegree;
		}
		
		vCount=vertices.length / 3;
		
		float[] vbbbb = new float[vertices.length];
        
    	vbbbb[0] = 0;
    	vbbbb[1] = 0;
    	vbbbb[2] = this.up ? 1 : -1;
        
        for (int ii = 3; ii < vertices.length; ++ii) {
        	vbbbb[ii] = vbbbb[ii - 3];
        }
        
        ByteBuffer vbb2 = ByteBuffer.allocateDirect(vertices.length*4);
        vbb2.order(ByteOrder.nativeOrder());
        mNormalBuffer = vbb2.asFloatBuffer();
        mNormalBuffer.put(vbbbb);
        mNormalBuffer.position(0);
        
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);
        
        if (this.colorCircle) {
	        int colors[] = new int[vCount * 4];
	        for (int i = 0; i < vCount; ++i) {
	        	colors[i * 4] = (int) (red * 65535);
	        	colors[i * 4 + 1] = (int) (green * 65535);
	        	colors[i * 4 + 2] = (int) (blue * 65535);
	        	colors[i * 4 + 3] = 0;
	        }
	        
	        ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length*4);
	        cbb.order(ByteOrder.nativeOrder());
	        mColorBuffer = cbb.asIntBuffer();
	        mColorBuffer.put(colors);
	        mColorBuffer.position(0);
        } else {
        	float[] texST= new float[108 * 2];
        	for (int i = 0; i < 36; ++i) {
        		texST[i*6] = texST[i*6 + 1] = 0;
        		texST[i*6 + 2] = 0;
        		texST[i*6 + 3] = 1;
        		texST[i*6 + 4] = texST[i*6 + 5] = 1;
        	}
            
        	ByteBuffer tbb = ByteBuffer.allocateDirect(texST.length*4);
        	tbb.order(ByteOrder.nativeOrder());
            mTextureBuffer = tbb.asFloatBuffer();
            mTextureBuffer.put(texST);
            mTextureBuffer.position(0);      
        }
	}
	
	@Override
	public void drawSelf(GL10 gl) {
		// TODO Auto-generated method stub
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glNormalPointer(GL10.GL_FIXED, 0, mNormalBuffer);
        
		if (this.colorCircle) {
			gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
	        gl.glVertexPointer (
	        		3,				
	        		GL10.GL_FLOAT,	
	        		0, 				
	        		mVertexBuffer	
	        );
			
	        gl.glColorPointer (
	        		4, 				
	        		GL10.GL_FIXED, 	
	        		0, 				
	        		mColorBuffer	
	        );
			
	        gl.glDrawArrays (
	        		GL10.GL_TRIANGLES, 		
	        		0, 			 			
	        		vCount				
	        );
	        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		} else {
			gl.glVertexPointer
	        (
	        		3,				
	        		GL10.GL_FLOAT,	
	        		0, 				
	        		mVertexBuffer	
	        );
	        
	        gl.glEnable(GL10.GL_TEXTURE_2D);   
	        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer);
	        //gl.glBindTexture(GL10.GL_TEXTURE_2D, MySurfaceView.getMaterialId(texId));
	        gl.glBindTexture(GL10.GL_TEXTURE_2D, Map3DSurfaceView.getMaterialId(texId));
			
	        
	        gl.glDrawArrays
	        (
	        		GL10.GL_TRIANGLES, 		
	        		0, 			 			
	        		vCount				
	        );
	        gl.glDisable(GL10.GL_TEXTURE_2D);
	        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		}
	}

}
