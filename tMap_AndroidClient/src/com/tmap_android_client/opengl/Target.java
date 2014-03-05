package com.tmap_android_client.opengl;

import javax.microedition.khronos.opengles.GL10;

public class Target implements Geometry {
	private Cylinder cylinder = null;
	private Ball ball = null;
	
	private float x, y, z;
	
	public Target(float x, float y, float z) {
		this.x = x; this.y = y; this.z = z;
		cylinder = new Cylinder(0, 0, 0, 0.05f, 1.3f, 0.7f, 0.7f, 0.7f);
		ball = new Ball(0, 0, 1.5f, 0.3f, 0.8f, 0, 0);
	}
	
	@Override
	public void drawSelf(GL10 gl) {
		// TODO Auto-generated method stub
		gl.glPushMatrix();
		gl.glTranslatef(x, y, z);
		cylinder.drawSelf(gl);
		ball.drawSelf(gl);
		gl.glPopMatrix();
	}

}
