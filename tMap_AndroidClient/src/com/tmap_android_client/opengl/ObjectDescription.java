package com.tmap_android_client.opengl;

public class ObjectDescription {
	public String type;		// rotator, box, cylinder, plane, director
	public float[] data;	// rotate x, y, z for rotator
							// params, rotate, color r g b for box, cylinder ... (more objects)
							// vertices, rotate, color r g b for plane
							// params for director
	public int texId = -1;	// > 0 if needed
}
