package com.tmap_android_client.opengl;

import static com.tmap_android_client.opengl.Constant.TEX_PER_METER;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Director implements Geometry{
	private FloatBuffer   mVertexBuffer;
    private FloatBuffer mTextureBuffer;
    public float x1, x2, y1, y2, z1, z2;
    int vCount=0;
    
    private float dist(float x1, float y1, float x2, float y2) {
    	return (float)Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }
    
    private float calRange(float x1, float y1, float z1, float x2, float y2, float z2) {
    	return (float)Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2) + (z1 - z2) * (z1 - z2)) * TEX_PER_METER;
    }
    
    public Director(float x1, float y1, float z1, float x2, float y2, float z2)
    {
    	this.x1 = x1; this.y1 = y1; this.z1 = z1;
    	this.x2 = x2; this.y2 = y2; this.z2 = z2;
    	
    	float dx = x2 - x1, dy = y2 - y1;
    	float nx = -dy, ny = dx;
    	float d = dist(0, 0, nx, ny);
    	nx = nx * 0.3f / d;
    	ny = ny * 0.3f / d;
    	
    	float[] vertices = new float[] {
    			x1 + nx, y1 + ny, z1,
    			x1 - nx, y1 - ny, z1,
    			x2 - nx, y2 - ny, z2,
    			x2 - nx, y2 - ny, z2,
    			x2 + nx, y2 + ny, z2,
    			x1 + nx, y1 + ny, z1,
    	};
    	
    	
        vCount=vertices.length / 3;
		
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);

        float sRange = 1;
        float tRange = this.calRange(vertices[3], vertices[4], vertices[5], vertices[6], vertices[7], vertices[8]);
        
        float[] texST=
        {
        	0, tRange,
        	sRange,tRange,
        	sRange,0,
        	sRange,0,
        	0,0,
        	0,tRange,
        };
        ByteBuffer tbb = ByteBuffer.allocateDirect(texST.length*4);
        tbb.order(ByteOrder.nativeOrder());
        mTextureBuffer = tbb.asFloatBuffer();
        mTextureBuffer.put(texST);
        mTextureBuffer.position(0);        
    }

    public void drawSelf(GL10 gl)
    {        
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        
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
        gl.glBindTexture(GL10.GL_TEXTURE_2D, MySurfaceView.getMaterialId(0));
		
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
