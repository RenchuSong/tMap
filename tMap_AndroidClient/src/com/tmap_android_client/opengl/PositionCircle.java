package com.tmap_android_client.opengl;

import javax.microedition.khronos.opengles.GL10;

public class PositionCircle extends Circle {
	float x, y, z;
	public PositionCircle(float x, float y, float z, float radius, boolean up, double red, double green, double blue) {
		super(radius, up, red, green, blue);
		this.x = x; this.y = y; this.z = z;
	}
	
	public PositionCircle(float x, float y, float z, float radius, boolean up, int texId) {
		super(radius, up, texId);
		this.x = x; this.y = y; this.z = z;
	}
	
	public PositionCircle(float radius, boolean up, double red, double green, double blue) {
		super(radius, up, red, green, blue);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void drawSelf(GL10 gl) {
		gl.glPushMatrix();
		gl.glTranslatef(this.x, this.y, this.z);
		
		super.drawSelf(gl);
		
		gl.glPopMatrix();
	}
}
