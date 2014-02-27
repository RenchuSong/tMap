package com.tmap_android_client.opengl;

import javax.microedition.khronos.opengles.GL10;

public class Box implements Geometry{
	public float cornerX, cornerY, cornerZ, lenX, lenY, lenZ;
	public float rotateX = 0, rotateY = 0, rotateZ = 0;
	public float red, green, blue;
	public int texId;
	public boolean colorBox = true;
	
	public Box(float cornerX, float cornerY, float cornerZ, float lenX, float lenY, float lenZ, float red, float green, float blue) {
		this.cornerX = cornerX;
		this.cornerY = cornerY;
		this.cornerZ = cornerZ;
		
		this.lenX = lenX;
		this.lenY = lenY;
		this.lenZ = lenZ;
		
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	public Box(float cornerX, float cornerY, float cornerZ, float lenX, float lenY, float lenZ, int texId) {
		this.cornerX = cornerX;
		this.cornerY = cornerY;
		this.cornerZ = cornerZ;
		
		this.lenX = lenX;
		this.lenY = lenY;
		this.lenZ = lenZ;
		
		this.texId = texId;
		this.colorBox = false;
	}
	
	public void setRotate(float rotateX, float rotateY, float rotateZ) {
		this.rotateX = rotateX;
		this.rotateY = rotateY;
		this.rotateZ = rotateZ;
	}
	
	@Override
	public void drawSelf(GL10 gl) {
		// TODO Auto-generated method stub
		gl.glPushMatrix();
		gl.glTranslatef(this.cornerX, this.cornerY, this.cornerZ);
		gl.glRotatef(this.rotateX, 1, 0, 0);
		gl.glRotatef(this.rotateY, 0, 1, 0);
		gl.glRotatef(this.rotateZ, 0, 0, 1);
		
		Geometry plane = null;
		
		//bottom
    	float[] vertices = new float[] {
    			this.lenX, this.lenY, 0,
    			this.lenX, 0, 0,
    			0, 0, 0,
    			0, 0, 0,
    			0, this.lenY, 0,
    			this.lenX, this.lenY, 0,
    	};
    	
    	plane = this.colorBox ?	new ColorPlane(vertices, this.red, this.green, this.blue)
    							: new MaterialPlane(vertices, this.texId);
    	plane.drawSelf(gl);
    	
    	//up
    	vertices = new float[] {
    			this.lenX, this.lenY, this.lenZ,
    			0, this.lenY, this.lenZ,
    			0, 0, this.lenZ,
    			0, 0, this.lenZ,
    			this.lenX, 0, this.lenZ,
    			this.lenX, this.lenY, this.lenZ,
    	};
    	
    	plane = this.colorBox ?	new ColorPlane(vertices, this.red, this.green, this.blue)
    							: new MaterialPlane(vertices, this.texId);
    	plane.drawSelf(gl);
    	
    	//front
    	vertices = new float[] {
    			0, 0, 0,
    			this.lenX, 0, 0,
    			this.lenX, 0, this.lenZ,
    			this.lenX, 0, this.lenZ,
    			0, 0, this.lenZ,
    			0, 0, 0,
    	};
    	
    	plane = this.colorBox ?	new ColorPlane(vertices, this.red, this.green, this.blue)
    							: new MaterialPlane(vertices, this.texId);
    	plane.drawSelf(gl);
    	
    	//back
    	vertices = new float[] {
    			0, this.lenY, 0,
    			0, this.lenY, this.lenZ,
    			this.lenX, this.lenY, this.lenZ,
    			this.lenX, this.lenY, this.lenZ,
    			this.lenX, this.lenY, 0,
    			0, this.lenY, 0,
    	};
    	
    	plane = this.colorBox ?	new ColorPlane(vertices, this.red, this.green, this.blue)
    							: new MaterialPlane(vertices, this.texId);
    	plane.drawSelf(gl);

    	//left
    	vertices = new float[] {
    			0, this.lenY, 0,
    			0, 0, 0,
    			0, 0, this.lenZ,
    			0, 0, this.lenZ,
    			0, this.lenY, this.lenZ,
    			0, this.lenY, 0,
    	};
    	
    	plane = this.colorBox ?	new ColorPlane(vertices, this.red, this.green, this.blue)
    							: new MaterialPlane(vertices, this.texId);
    	plane.drawSelf(gl);

    	//right
    	vertices = new float[] {
    			this.lenX, this.lenY, 0,
    			this.lenX, this.lenY, this.lenZ,
    			this.lenX, 0, this.lenZ,
    			this.lenX, 0, this.lenZ,
    			this.lenX, 0, 0,
    			this.lenX, this.lenY, 0,
    	};
    	
    	plane = this.colorBox ?	new ColorPlane(vertices, this.red, this.green, this.blue)
    							: new MaterialPlane(vertices, this.texId);
    	plane.drawSelf(gl);
		gl.glPopMatrix();
	}
}
