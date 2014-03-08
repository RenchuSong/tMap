/**
 * Color Plane
 */
package com.tmap_android_client.opengl;
import static com.tmap_android_client.opengl.Constant.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

public class ColorPlane implements Geometry{
	private FloatBuffer   mVertexBuffer;
    private IntBuffer   mColorBuffer;
    private FloatBuffer mNormalBuffer;
    int vCount=0;
    public float[] vertices;
    public float red, green, blue;
    
    public ColorPlane(float[] vertices, float red, float green, float blue)
    {
    	this(vertices, red, green, blue, 0);
    }
    
    public ColorPlane(float[] vertices, float red, float green, float blue, float alpha)
    {
    	this.vertices = vertices;
    	this.red = red;
    	this.green = green;
    	this.blue = blue;
    	
        vCount=vertices.length / 3;
        
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);
        
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
        

        float[] vbbbb = new float[vertices.length];
        int i = 0, j = 1, k = 2;
        
        float a1 = vertices[j * 3] - vertices[i * 3];
    	float a2 = vertices[j * 3 + 1] - vertices[i * 3 + 1];
    	float a3 = vertices[j * 3 + 2] - vertices[i * 3 + 2];
    	
    	float b1 = vertices[k * 3] - vertices[j * 3];
    	float b2 = vertices[k * 3 + 1] - vertices[j * 3 + 1];
    	float b3 = vertices[k * 3 + 2] - vertices[j * 3 + 2];
    	
    	vbbbb[i * 3] = a2 * b3 - a3 * b2;
    	vbbbb[i * 3 + 1] = a3 * b1 - a1 * b3;
    	vbbbb[i * 3 + 2] = a1 * b2 - a2 * b1;
        
        for (int ii = 3; ii < vertices.length; ++ii) {
        	vbbbb[ii] = vbbbb[ii - 3];
        }
        
        ByteBuffer vbb2 = ByteBuffer.allocateDirect(vertices.length*4);
        vbb2.order(ByteOrder.nativeOrder());
        mNormalBuffer = vbb2.asFloatBuffer();
        mNormalBuffer.put(vbbbb);
        mNormalBuffer.position(0);
    }

    public void drawSelf(GL10 gl) {        
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
        gl.glVertexPointer (
        		3,				
        		GL10.GL_FLOAT,	
        		0, 				
        		mVertexBuffer	
        );
		
        gl.glNormalPointer(GL10.GL_FIXED, 0, mNormalBuffer);
        
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
    }

}
