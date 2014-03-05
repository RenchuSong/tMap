package com.tmap_android_client.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

public class Ball implements Geometry{
	private FloatBuffer   mVertexBuffer;
	private FloatBuffer   mNormalBuffer;
	private IntBuffer   mColorBuffer;

    int vCount=0;
    double red, green, blue;
    float x, y, z;
    
    public Ball(float x, float y, float z, float scale, double red, double green, double blue) 
    {
    	this.x = x; this.y = y; this.z = z;
    	this.red = red;
    	this.green = green;
    	this.blue = blue;
    
    	final float angleSpan=11.25f;
    	
    	ArrayList<Float> alVertix=new ArrayList<Float>();
    	
        for(float vAngle=90;vAngle>-90;vAngle=vAngle-angleSpan)
        {
        	for(float hAngle=360;hAngle>0;hAngle=hAngle-angleSpan)
        	{
        		
        		double xozLength=scale*Math.cos(Math.toRadians(vAngle));
        		float x1=(float)(xozLength*Math.cos(Math.toRadians(hAngle)));
        		float z1=(float)(xozLength*Math.sin(Math.toRadians(hAngle)));
        		float y1=(float)(scale*Math.sin(Math.toRadians(vAngle)));
        		
        		xozLength=scale*Math.cos(Math.toRadians(vAngle-angleSpan));
        		float x2=(float)(xozLength*Math.cos(Math.toRadians(hAngle)));
        		float z2=(float)(xozLength*Math.sin(Math.toRadians(hAngle)));
        		float y2=(float)(scale*Math.sin(Math.toRadians(vAngle-angleSpan)));
        		
        		xozLength=scale*Math.cos(Math.toRadians(vAngle-angleSpan));
        		float x3=(float)(xozLength*Math.cos(Math.toRadians(hAngle-angleSpan)));
        		float z3=(float)(xozLength*Math.sin(Math.toRadians(hAngle-angleSpan)));
        		float y3=(float)(scale*Math.sin(Math.toRadians(vAngle-angleSpan)));
        		
        		xozLength=scale*Math.cos(Math.toRadians(vAngle));
        		float x4=(float)(xozLength*Math.cos(Math.toRadians(hAngle-angleSpan)));
        		float z4=(float)(xozLength*Math.sin(Math.toRadians(hAngle-angleSpan)));
        		float y4=(float)(scale*Math.sin(Math.toRadians(vAngle)));   
        		
        		alVertix.add(x1);alVertix.add(y1);alVertix.add(z1);
        		alVertix.add(x2);alVertix.add(y2);alVertix.add(z2);
        		alVertix.add(x4);alVertix.add(y4);alVertix.add(z4);        		
        		alVertix.add(x4);alVertix.add(y4);alVertix.add(z4);
        		alVertix.add(x2);alVertix.add(y2);alVertix.add(z2);
        		alVertix.add(x3);alVertix.add(y3);alVertix.add(z3); 
   		
        	}
        } 	
        
        
        
        vCount=alVertix.size()/3;
    	
        float vertices[]=new float[vCount*3];
    	for(int i=0;i<alVertix.size();i++)
    	{
    		vertices[i]=alVertix.get(i);
    	}
        
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);
        
        ByteBuffer nbb = ByteBuffer.allocateDirect(vertices.length*4);
        nbb.order(ByteOrder.nativeOrder());
        mNormalBuffer = vbb.asFloatBuffer();
        mNormalBuffer.put(vertices);
        mNormalBuffer.position(0);
        
        int colors[] = new int[vCount * 4];
        for (int i = 0; i < vCount; ++i) {
        	colors[i * 4] = (int) (this.red * 65535);
        	colors[i * 4 + 1] = (int) (this.green * 65535);
        	colors[i * 4 + 2] = (int) (this.blue * 65535);
        	colors[i * 4 + 3] = 0;
        }
        
        ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length*4);
        cbb.order(ByteOrder.nativeOrder());
        mColorBuffer = cbb.asIntBuffer();
        mColorBuffer.put(colors);
        mColorBuffer.position(0);
    }

    @Override
    public void drawSelf(GL10 gl)
    {
    	gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
    	gl.glPushMatrix();
		gl.glTranslatef(this.x, this.y, this.z);
		
        
//        gl.glVertexPointer
//        (
//        		3,				
//        		GL10.GL_FIXED,	
//        		0, 				
//        		mVertexBuffer	
//        );
        gl.glVertexPointer (
        		3,				
        		GL10.GL_FLOAT,	
        		0, 				
        		mVertexBuffer	
        );
        
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
        gl.glNormalPointer(GL10.GL_FLOAT, 0, mNormalBuffer); 
//		
//        gl.glEnable(GL10.GL_TEXTURE_2D);   
//        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
//        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer);
//        gl.glBindTexture(GL10.GL_TEXTURE_2D, texId);
//        
        gl.glColorPointer (
        		4, 				
        		GL10.GL_FIXED, 	
        		0, 				
        		mColorBuffer	
        );
        
        gl.glDrawArrays
        (
        		GL10.GL_TRIANGLES, 		
        		0, 			 			
        		vCount					
        );
        
        gl.glPopMatrix();
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
    }
    
//    public float[] generateTexCoor(int bw,int bh)
//    {
//    	float[] result=new float[bw*bh*6*2]; 
//    	float sizew=1.0f/bw;//
//    	float sizeh=1.0f/bh;//
//    	int c=0;
//    	for(int i=0;i<bh;i++)
//    	{
//    		for(int j=0;j<bw;j++)
//    		{
//    			float s=j*sizew;
//    			float t=i*sizeh;
//    			
//    			result[c++]=s;
//    			result[c++]=t;
//    			
//    			result[c++]=s;
//    			result[c++]=t+sizeh;
//    			
//    			result[c++]=s+sizew;
//    			result[c++]=t;
//    			
//    			
//    			result[c++]=s+sizew;
//    			result[c++]=t;
//    			
//    			result[c++]=s;
//    			result[c++]=t+sizeh;
//    			
//    			result[c++]=s+sizew;
//    			result[c++]=t+sizeh;    			
//    		}
//    	}
//    	return result;
//    }

}
