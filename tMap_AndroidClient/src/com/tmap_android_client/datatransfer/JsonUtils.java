package com.tmap_android_client.datatransfer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class JsonUtils {
	private JsonUtils() {}
	/*public static Response parseObjectFromJson(String data) {
		Type listType = (Type) new TypeToken<Response>(){}.getType();
		Gson gson = new Gson();
		Response list = gson.fromJson(data, listType);
		return list;
	}*/
	
	public static String packObjToJson(Object obj) {
		Gson gson = new Gson();
		return gson.toJson(obj);
	}
	
	public static String packListToJson(List objs) {
		Gson gson = new Gson();
		return gson.toJson(objs);
	}
	
	public static String packMapToJson(Map objs) {
		Gson gson = new Gson();
		return gson.toJson(objs);
	}
	
	public static Response parseResponse(String data) {
		Gson gson = new Gson();
		return gson.fromJson(data, Response.class);
	}
}
