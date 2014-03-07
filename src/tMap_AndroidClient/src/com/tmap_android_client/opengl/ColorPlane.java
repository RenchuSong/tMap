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
    }

    public void drawSelf(GL10 gl) {        
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
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
    }

}
