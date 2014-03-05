package com.tmap_android_client.opengl;

import java.util.ArrayList;

public class ObjectDescription {
	public String type;		// rotator, box, cylinder, plane, director
	public float[] data;	// rotate x, y, z for rotator
							// params, rotate, color r g b for box, cylinder ... (more objects)
							// vertices, rotate, color r g b for plane
							// params for director
	public int texId = -1;	// > 0 if needed
	
	// Decode geometry json array into Geometry ArrayList to render
	public ArrayList<Geometry> createGeometryList(ObjectDescription[] objs) {
		ArrayList<Geometry> geoList = new ArrayList<Geometry>();
		for (int i = 0; i < objs.length; ++i) {
			ObjectDescription os = objs[i];
			if (os.type.equalsIgnoreCase("Box")) {
				Box box = null;
				if (os.data.length == 12) {
					box = new Box(os.data[0], os.data[1], os.data[2],
							os.data[3], os.data[4], os.data[5],
							os.data[9], os.data[10], os.data[11]);
				} else {
					box = new Box(os.data[0], os.data[1], os.data[2],
							os.data[3], os.data[4], os.data[5],
							os.texId);
				}
				box.setRotate(os.data[6], os.data[7], os.data[8]);
				geoList.add(box);
			}
			if (os.type.equalsIgnoreCase("Cylinder")) {
				Cylinder cy = null;
				if (os.data.length == 11) {
					cy = new Cylinder(os.data[0], os.data[1], os.data[2],
							os.data[3], os.data[4],
							os.data[8], os.data[9], os.data[10]);
				} else {
					cy = new Cylinder(os.data[0], os.data[1], os.data[2],
							os.data[3], os.data[4],
							os.texId);
				}
				cy.setRotate(os.data[5], os.data[6], os.data[7]);
				geoList.add(cy);
			}
			if (os.type.equalsIgnoreCase("Plane")) {
				if (os.texId != -1) {
					MaterialPlane mp = new MaterialPlane(os.data, os.texId);
					geoList.add(mp);
				} else {
					int l = os.data.length - 3;
					float[] vertices = new float[l];
					for (int j = 0; j < l; ++j) {
						vertices[j] = os.data[j];
					}
					ColorPlane p = new ColorPlane(vertices, os.data[l], os.data[l + 1], os.data[l + 2]);
					geoList.add(p);
				}
			}
			if (os.type.equalsIgnoreCase("Director")) {
				Director dr = new Director(os.data[0], os.data[1], os.data[2], 
						os.data[3], os.data[4], os.data[5]);
				geoList.add(dr);
			}
		}
		return geoList;
	}
}
