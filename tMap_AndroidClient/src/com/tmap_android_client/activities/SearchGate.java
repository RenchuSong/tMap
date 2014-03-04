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

public class SearchGate extends BaseActivity {
	private Button search, btn_1, btn_2, btn_3, btn_4, btn_5, btn_6, btn_7, btn_8, btn_9;
	private final int SEARCH_CODE = 0x0004;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.search_type);
        
        search = (Button) this.findViewById(R.id.go_search);
        btn_1 = (Button) this.findViewById(R.id.type_1);
        btn_2 = (Button) this.findViewById(R.id.type_2);
        btn_3 = (Button) this.findViewById(R.id.type_3);
        btn_4 = (Button) this.findViewById(R.id.type_4);
        btn_5 = (Button) this.findViewById(R.id.type_5);
        btn_6 = (Button) this.findViewById(R.id.type_6);
        btn_7 = (Button) this.findViewById(R.id.type_7);
        btn_8 = (Button) this.findViewById(R.id.type_8);
        btn_9 = (Button) this.findViewById(R.id.type_9);
        
        search.setOnClickListener(
        	new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(SearchGate.this, SearchResult.class);
					Bundle bundle = new Bundle();
					bundle.putString("searchText", ((EditText) findViewById(R.id.searchText)).getText().toString());
					bundle.putString("type", null);
					startActivityForResult(intent, SEARCH_CODE);
				}
        	}
        );
        
        btn_1.setOnClickListener(
            	new OnClickListener() {
    				@Override
    				public void onClick(View arg0) {
    					// TODO Auto-generated method stub
    					Intent intent = new Intent(SearchGate.this, SearchResult.class);
    					Bundle bundle = new Bundle();
    					bundle.putString("searchText", null);
    					bundle.putString("type", getString(R.string.type_1_map));
    					startActivityForResult(intent, SEARCH_CODE);
    				}
            	}
            );
        
        btn_2.setOnClickListener(
            	new OnClickListener() {
    				@Override
    				public void onClick(View arg0) {
    					// TODO Auto-generated method stub
    					Intent intent = new Intent(SearchGate.this, SearchResult.class);
    					Bundle bundle = new Bundle();
    					bundle.putString("searchText", null);
    					bundle.putString("type", getString(R.string.type_2_map));
    					startActivityForResult(intent, SEARCH_CODE);
    				}
            	}
            );
        
        btn_3.setOnClickListener(
            	new OnClickListener() {
    				@Override
    				public void onClick(View arg0) {
    					// TODO Auto-generated method stub
    					Intent intent = new Intent(SearchGate.this, SearchResult.class);
    					Bundle bundle = new Bundle();
    					bundle.putString("searchText", null);
    					bundle.putString("type", getString(R.string.type_3_map));
    					startActivityForResult(intent, SEARCH_CODE);
    				}
            	}
            );
        
        btn_4.setOnClickListener(
            	new OnClickListener() {
    				@Override
    				public void onClick(View arg0) {
    					// TODO Auto-generated method stub
    					Intent intent = new Intent(SearchGate.this, SearchResult.class);
    					Bundle bundle = new Bundle();
    					bundle.putString("searchText", null);
    					bundle.putString("type", getString(R.string.type_4_map));
    					startActivityForResult(intent, SEARCH_CODE);
    				}
            	}
            );
        
        btn_5.setOnClickListener(
            	new OnClickListener() {
    				@Override
    				public void onClick(View arg0) {
    					// TODO Auto-generated method stub
    					Intent intent = new Intent(SearchGate.this, SearchResult.class);
    					Bundle bundle = new Bundle();
    					bundle.putString("searchText", null);
    					bundle.putString("type", getString(R.string.type_5_map));
    					startActivityForResult(intent, SEARCH_CODE);
    				}
            	}
            );
        
        btn_6.setOnClickListener(
            	new OnClickListener() {
    				@Override
    				public void onClick(View arg0) {
    					// TODO Auto-generated method stub
    					Intent intent = new Intent(SearchGate.this, SearchResult.class);
    					Bundle bundle = new Bundle();
    					bundle.putString("searchText", null);
    					bundle.putString("type", getString(R.string.type_6_map));
    					startActivityForResult(intent, SEARCH_CODE);
    				}
            	}
            );
        
        btn_7.setOnClickListener(
            	new OnClickListener() {
    				@Override
    				public void onClick(View arg0) {
    					// TODO Auto-generated method stub
    					Intent intent = new Intent(SearchGate.this, SearchResult.class);
    					Bundle bundle = new Bundle();
    					bundle.putString("searchText", null);
    					bundle.putString("type", getString(R.string.type_7_map));
    					startActivityForResult(intent, SEARCH_CODE);
    				}
            	}
            );
        
        btn_8.setOnClickListener(
            	new OnClickListener() {
    				@Override
    				public void onClick(View arg0) {
    					// TODO Auto-generated method stub
    					Intent intent = new Intent(SearchGate.this, SearchResult.class);
    					Bundle bundle = new Bundle();
    					bundle.putString("searchText", null);
    					bundle.putString("type", getString(R.string.type_8_map));
    					startActivityForResult(intent, SEARCH_CODE);
    				}
            	}
            );
        
        btn_9.setOnClickListener(
            	new OnClickListener() {
    				@Override
    				public void onClick(View arg0) {
    					// TODO Auto-generated method stub
    					Intent intent = new Intent(SearchGate.this, SearchResult.class);
    					Bundle bundle = new Bundle();
    					bundle.putString("searchText", null);
    					bundle.putString("type", getString(R.string.type_9_map));
    					startActivityForResult(intent, SEARCH_CODE);
    				}
            	}
            );
        
	}
}
