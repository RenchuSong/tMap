package com.tmap_android_client.datatransfer;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class HttpUtils {
	private HttpUtils() {}
	private static HttpUtils instance = null;
	public static HttpUtils getInstance() {
		if (instance == null) {
			synchronized(HttpUtils.class) {
				instance = new HttpUtils();
			}
		}
		return instance;
	}
	
	// get image from url
	public final static Bitmap getImage(String strUrl) throws Exception {
		URL url=new URL(strUrl);
		HttpURLConnection   uc = (HttpURLConnection)url.openConnection();
		uc.setDoInput(true);
		uc.connect();
		InputStream  is = uc.getInputStream();
		BufferedInputStream bis = new BufferedInputStream(is);
		Bitmap bmp = BitmapFactory.decodeStream(bis);   
		System.out.println("lenght:1"+uc.getContentLength());
		uc.disconnect();
		bis.close();
		is.close();
		return bmp;
	}
	
//	public String getData(String url) throws Exception{
//		StringBuffer sb = new StringBuffer();
//		HttpClient httpClient = new DefaultHttpClient();
//		HttpGet httpGet = new HttpGet(url);
//		HttpResponse response = httpClient.execute(httpGet);
//		HttpEntity httpEntity = response.getEntity();
//		if(httpEntity != null){
//			InputStream in = httpEntity.getContent();
//			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//			String line = null;
//			while((line= reader.readLine())!=null){
//				sb.append(line);
//			}
//		}
//		return sb.toString();
//	}
	
	public String postData(String url, String jsonData) throws Exception{		
		HttpPost httpRequest = new HttpPost(url);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("json", jsonData));

		HttpEntity httpEntity = new UrlEncodedFormEntity(params,"utf-8");
		httpRequest.setEntity(httpEntity); 
		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse httpResponse = httpClient.execute(httpRequest);
		if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
			String result = EntityUtils.toString(httpResponse.getEntity());
			return result;
		}else{
			Log.i("dataing", "request error");
		}

		return null;
	}
}
