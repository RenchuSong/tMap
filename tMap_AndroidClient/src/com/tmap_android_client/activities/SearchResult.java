package com.tmap_android_client.activities;

import java.util.ArrayList;
import java.util.LinkedList;

import com.example.tmap_androidclient.R;
import com.tmap_android_client.control.Environment;
import com.tmap_android_client.datatransfer.BusinessItem;
import com.tmap_android_client.datatransfer.HttpUtils;
import com.tmap_android_client.datatransfer.JsonUtils;
import com.tmap_android_client.uicomponent.BusinessItemUIComponent;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class SearchResult extends BaseActivity {
	private Button search = null;
	private JsonThread json = null;
	//private ArrayList<String> imgList = null;
	//private ArrayList<ImageView> imgContainer = null;
	private ProgressBar progress = null;
	private LinearLayout resultContainer = null;
	
	private DirectingThread directing = null;
	
	private LinkedList<Bitmap> bitmapList = new LinkedList<Bitmap>();
	private LinkedList<Integer> bitmapUIId = new LinkedList<Integer>();
	
	ArrayList<BusinessItemUIComponent> uiSet = new ArrayList<BusinessItemUIComponent>();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.search_result);
		
		if (!Environment.getInstance(this).located) {
			finish();
		}
		
//		imgList = new ArrayList<String>();
//		imgContainer = new ArrayList<ImageView>();
		search = (Button) this.findViewById(R.id.go_search_inner);
		progress = (ProgressBar) this.findViewById(R.id.loading_search_result);
		resultContainer = (LinearLayout) this.findViewById(R.id.search_result_content);
				
		search.setOnClickListener(
			new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					try {
						String text = ((EditText)findViewById(R.id.searchText_inner)).getText().toString();
						if (text.replaceAll(" ", "").equalsIgnoreCase("")) {
							Toast.makeText(getApplicationContext(), R.string.search_null, Toast.LENGTH_SHORT).show();
							return;
						}
						clearResultList();
						progress.setVisibility(View.VISIBLE);
						json = new JsonThread(null, text);
						new Thread(json).start();
					} catch (Exception e) {
						Toast.makeText(getApplicationContext(), R.string.web_link_fail_hint, Toast.LENGTH_SHORT).show();
					}
				}
			}
		);
		
		Bundle bundle = getIntent().getExtras();
		String searchText = bundle.getString("searchText");
		String type = bundle.getString("type");
		
		
		try {
			progress.setVisibility(View.VISIBLE);
			json = new JsonThread(type, searchText);
			new Thread(json).start();
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), R.string.web_link_fail_hint, Toast.LENGTH_SHORT).show();
		}		
	}
	
	// clear the searching result
	private void clearResultList() {
		resultContainer.removeAllViews();
		bitmapList.clear();
		uiSet.clear();
		bitmapUIId.clear();
//		this.imgContainer.clear();
//		this.imgList.clear();
	}
	// load search result list UI
	private void showResultList() {
		try {
			progress.setVisibility(View.INVISIBLE);
			BusinessItem[] businessObjs = JsonUtils.parseBusinessList(json.data);
			for (final BusinessItem business : businessObjs) {
				final BusinessItemUIComponent businessUIItem = new BusinessItemUIComponent(this, business);
				resultContainer.addView(businessUIItem);
				uiSet.add(businessUIItem);
				businessUIItem.setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							businessUIItem.setBackgroundResource(R.drawable.search_item_bg_press);
							directing = new DirectingThread(business.buildingId, business.floor, business.x, business.y);
							new Thread(directing).start();
						}
						
					}
				);
			}
			for (int i = 0; i < uiSet.size(); ++i) {
				ImageThread imgLoad = new ImageThread(i);
				new Thread(imgLoad).start();
			}
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), R.string.web_link_fail_hint, Toast.LENGTH_SHORT).show();
		}
	}
	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case R.integer.MSG_COMPLETE:
				showResultList();
				break;
			// network failure
			case R.integer.MSG_IMAGE_COMPLETE:
				try {
					synchronized(Handler.class) {
						int id = bitmapUIId.poll();
						Bitmap image = bitmapList.poll();
						uiSet.get(id).setImage(image);
					}
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), R.string.web_link_fail_hint, Toast.LENGTH_SHORT).show();
				}
				
				break;
			case R.integer.MSG_DIRECTING_COMPLETE:
				Environment.getInstance(SearchResult.this).directorPoint = JsonUtils.parseDirectorList(directing.data);
				Intent intent=getIntent();
			    setResult(RESULT_OK, intent);
				finish();
				//Log.v("dataing3", directing.data);
				break;
			case R.integer.MSG_NET_FAIL:
				Toast.makeText(getApplicationContext(), R.string.web_link_fail_hint, Toast.LENGTH_SHORT).show();
				break;
			}
		};
	};
	// network data transfer
	class JsonThread implements Runnable{
		public String type, searchText, data;
		public JsonThread(String type, String searchText) {
			this.type = type;
			this.searchText = searchText;
		}
			
		public void run() {
			try {
				String url = 
						Environment.getInstance(SearchResult.this).serverURL + 
						"tMap/business/getBusinessItem/" + 
						Environment.getInstance(SearchResult.this).buildingId + "/" +
						Environment.getInstance(SearchResult.this).floor + "/";
				
				if (this.type != null) {
					url += this.type;
				}
				
				data = HttpUtils.getInstance().postData(url, (this.searchText != null) ? this.searchText: "");
				handler.sendEmptyMessage(R.integer.MSG_COMPLETE);   
			} catch (Exception e) {
				handler.sendEmptyMessage(R.integer.MSG_NET_FAIL);						 
			}
		}
	}
	
	// item image transfer
	class ImageThread implements Runnable{
		public int id;
		
		public ImageThread(int id) {
			this.id = id;
		}
	
		public void run() {
			try {
				String url = Environment.getInstance(SearchResult.this).serverURL + "tMap/resources/business_item_logo/";
				url += uiSet.get(id).imageURL;
				Bitmap image = HttpUtils.getInstance().getImage(url);
				synchronized(ImageThread.class) {
					bitmapUIId.offer(this.id);
					bitmapList.offer(image);
					handler.sendEmptyMessage(R.integer.MSG_IMAGE_COMPLETE); 
				}
			} catch (Exception e) {
				handler.sendEmptyMessage(R.integer.MSG_NET_FAIL);						 
			}
		}
	}
	
	// directing thread
	class DirectingThread implements Runnable {
		public int buildingId, floor;
		public float x, y;
		
		public String data;
		
		public DirectingThread(int buildingId, int floor, float x, float y) {
			this.buildingId = buildingId;
			this.floor = floor;
			this.x = x;
			this.y = y;
		}
		
		public void run() {
			try {
				String url = 
						Environment.getInstance(SearchResult.this).serverURL + 
						"tMap/building/shortestPath/" + 
						Environment.getInstance(SearchResult.this).buildingId + "/" +
						Environment.getInstance(SearchResult.this).floor + "/" + 
						Environment.getInstance(SearchResult.this).x + "/" + 
						Environment.getInstance(SearchResult.this).y + "/" + 
						this.floor + "/" + this.x + "/" + this.y;
				
				data = HttpUtils.getInstance().postData(url, "");
				
				handler.sendEmptyMessage(R.integer.MSG_DIRECTING_COMPLETE);   
			} catch (Exception e) {
				handler.sendEmptyMessage(R.integer.MSG_NET_FAIL);						 
			}
		}
	}
}
