package com.tmap_android_client.activities;

import java.util.ArrayList;

import com.example.tmap_androidclient.R;
import com.tmap_android_client.control.Environment;
import com.tmap_android_client.datatransfer.BusinessItem;
import com.tmap_android_client.datatransfer.HttpUtils;
import com.tmap_android_client.datatransfer.JsonUtils;

import android.app.Activity;
import android.content.Intent;
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
	//private ImageThread imgLoad = null;
	private ArrayList<String> imgList = null;
	private ArrayList<ImageView> imgContainer = null;
	private ProgressBar progress = null;
	private LinearLayout resultContainer = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.search_result);
		
		if (!Environment.getInstance(this).located) {
			finish();
		}
		
		imgList = new ArrayList<String>();
		imgContainer = new ArrayList<ImageView>();
		search = (Button) this.findViewById(R.id.go_search_inner);
		progress = (ProgressBar) this.findViewById(R.id.loading_search_result);
		resultContainer = (LinearLayout) this.findViewById(R.id.search_result_content);
				
		search.setOnClickListener(
			new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					try {
						String text = search.getText().toString();
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
		this.imgContainer.clear();
		this.imgList.clear();
	}
	// load search result list UI
	private void showResultList() {
		try {
			progress.setVisibility(View.INVISIBLE);
			BusinessItem[] businessObjs= JsonUtils.parseBusinessList(json.data);
			for (BusinessItem business : businessObjs) {
				imgList.add(business.imageURL);
				Log.d("dataing3", business.imageURL);
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
	
//	// item image transfer
//	class ImageThread implements Runnable{
//		public String type, searchText, data;
//	
//		public void run() {
//			try {
//				String url = Environment.getInstance(SearchResult.this).serverURL + "tMap/business/getBusinessItem/";
//				if (this.type != null) {
//					url += this.type;
//				}
//				data = HttpUtils.getInstance().postData(url, (this.searchText != null) ? this.searchText: "");
//				handler.sendEmptyMessage(R.integer.MSG_COMPLETE);   
//			} catch (Exception e) {
//				handler.sendEmptyMessage(R.integer.MSG_NET_FAIL);						 
//			}
//		}
//	}
}
