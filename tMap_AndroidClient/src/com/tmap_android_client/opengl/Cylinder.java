package com.tmap_android_client.opengl;

import javax.microedition.khronos.opengles.GL10;

public class Cylinder implements Geometry{
	public float centerX, centerY, centerZ, radius, height;
	public float rotateX = 0, rotateY = 0, rotateZ = 0;
	public float red, green, blue;
	public int texId;
	public boolean colorCylinder = true;
	
	public Cylinder(float centerX, float centerY, float centerZ, float radius, float height, float red, float green, float blue) {
		this.centerX = centerX;
		this.centerY = centerY;
		this.centerZ = centerZ;
		
		this.radius = radius;
		this.height = height;
		
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	public Cylinder(float centerX, float centerY, float centerZ, float radius, float height, int texId) {
		this.centerX = centerX;
		this.centerY = centerY;
		this.centerZ = centerZ;
		
		this.radius = radius;
		this.height = height;
		
		this.texId = texId;
		this.colorCylinder = false;
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
		gl.glTranslatef(this.centerX, this.centerY, this.centerZ);
		gl.glRotatef(this.rotateX, 1, 0, 0);
		gl.glRotatef(this.rotateY, 0, 1, 0);
		gl.glRotatef(this.rotateZ, 0, 0, 1);
		
		Geometry plane = null;
		
		//bottom circle
		if (this.colorCylinder) {
			(new Circle(this.radius + 0.005f, false, this.red, this.green, this.blue)).drawSelf(gl);
		} else {
			(new Circle(this.radius + 0.005f, false, this.texId)).drawSelf(gl);
		}
		
		//top circle
		gl.glPushMatrix();
		gl.glTranslatef(0, 0, this.height);
		if (this.colorCylinder) {
			(new Circle(this.radius + 0.005f, true, this.red, this.green, this.blue)).drawSelf(gl);
		} else {
			(new Circle(this.radius + 0.005f, true, this.texId)).drawSelf(gl);
		}
		gl.glPopMatrix();
		
		/*
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
    	*/
		
		float dx = (float) (this.radius * Math.tan(5 / 180.0 * Math.PI));
    	
		//cylinder
		for (int i = 0; i < 36; ++i) {
			float[] vertices = new float[] {
	    			-dx, -this.radius, 0,
	    			dx, -this.radius, 0,
	    			dx, -this.radius, this.height,
	    			dx, -this.radius, this.height,
	    			-dx, -this.radius, this.height,
	    			-dx, -this.radius, 0,
	    	};
			plane = this.colorCylinder ?  new ColorPlane(vertices, this.red, this.green, this.blue)
										: new MaterialPlane(vertices, this.texId);
			plane.drawSelf(gl);
			gl.glRotatef(10, 0, 0, 1);
		}
 	
		gl.glPopMatrix();
		
	}
}
