package com.tmap_android_client.activities;

import com.example.tmap_androidclient.R;
import com.tmap_android_client.control.Environment;
import com.tmap_android_client.datatransfer.HttpUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SearchResult extends BaseActivity {
	private Button search;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.search_result);
        
        search = (Button) this.findViewById(R.id.go_search);

        search.setOnClickListener(
        	new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					
				}
        	}
        );
	}
	
	Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch(msg.what){
            case R.integer.MSG_COMPLETE:
            	
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
            	String url = Environment.getInstance(SearchResult.this).serverURL + "tMap/business/getBusinessItem/";
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
}
