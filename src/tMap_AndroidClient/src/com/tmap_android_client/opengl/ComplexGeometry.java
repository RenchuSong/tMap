package com.tmap_android_client.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

public class ComplexGeometry implements Geometry{

	private IntBuffer   mVertexBuffer;
	private IntBuffer   mNormalBuffer;
    private FloatBuffer mTextureBuffer;
    public float mAngleX;
    public float mAngleY;
    public float mAngleZ;
    int vCount=0;
    int texId;
    
    public void setBaseGeometry(ArrayList<Integer> alVertix, ArrayList<Float> alTexture, int texId) {
    	this.texId=texId;
    	
    	float[] texCoorArray= generateTexCoor();	//todo
        
        vCount=alVertix.size()/3;
    	
        int vertices[]=new int[vCount*3];
    	for(int i=0;i<alVertix.size();i++) {
    		vertices[i]=alVertix.get(i);
    	}
        
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asIntBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);
        
        ByteBuffer nbb = ByteBuffer.allocateDirect(vertices.length*4);
        nbb.order(ByteOrder.nativeOrder());
        mNormalBuffer = nbb.asIntBuffer();
        mNormalBuffer.put(vertices);
        mNormalBuffer.position(0);
        
        float textureCoors[]=new float[alTexture.size()];
        for(int i=0;i<alTexture.size();i++) {
        	textureCoors[i]=alTexture.get(i);
        }
        
        ByteBuffer tbb = ByteBuffer.allocateDirect(textureCoors.length*4);
        tbb.order(ByteOrder.nativeOrder());
        mTextureBuffer = tbb.asFloatBuffer();
        mTextureBuffer.put(textureCoors);
        mTextureBuffer.position(0);
    }

    public void drawSelf(GL10 gl)
    {
    	gl.glRotatef(mAngleZ, 0, 0, 1);
    	gl.glRotatef(mAngleX, 1, 0, 0);
        gl.glRotatef(mAngleY, 0, 1, 0);
        
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glVertexPointer (
        		3,				
        		GL10.GL_FIXED,	
        		0, 				
        		mVertexBuffer	
        );
        
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
        gl.glNormalPointer(GL10.GL_FIXED, 0, mNormalBuffer); 
		
        gl.glEnable(GL10.GL_TEXTURE_2D);   
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, texId);
        
        gl.glDrawArrays (
        	GL10.GL_TRIANGLES, 		
        	0, 			 			
        	vCount					
        );
    }
    
    public float[] generateTexCoor() {
    	float[] a = new float[]{};
    	return a;
    }

}
