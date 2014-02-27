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

public class MaterialPlane implements Geometry{
	private FloatBuffer   mVertexBuffer;
    private FloatBuffer mTextureBuffer;
    int vCount=0;
    int texId;
    
    private float calRange(float x1, float y1, float z1, float x2, float y2, float z2) {
    	return (float)Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2) + (z1 - z2) * (z1 - z2)) * TEX_PER_METER;
    }
    
    public MaterialPlane(float [] vertices, int texId)
    {
    	this.texId=texId;
    	
        vCount=vertices.length / 3;
		
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);

        float sRange = this.calRange(vertices[0], vertices[1], vertices[2], vertices[3], vertices[4], vertices[5]);
        float tRange = this.calRange(vertices[3], vertices[4], vertices[5], vertices[6], vertices[7], vertices[8]);
        
        
        float[] texST=
        {
        	sRange,0,
        	0,0,
        	0,tRange,
        	0,tRange,
        	sRange,tRange,
        	sRange,0
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
        gl.glBindTexture(GL10.GL_TEXTURE_2D, MySurfaceView.getMaterialId(texId));
		
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
