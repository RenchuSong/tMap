package com.tmap_android_client.activities;

import com.example.tmap_androidclient.R;
import com.tmap_android_client.control.Environment;
import com.tmap_android_client.datatransfer.HttpUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
		Log.d("dataing3", search + "");
		
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
					String text = ((EditText) findViewById(R.id.searchText)).getText().toString();
					if (text.replaceAll(" ", "").equalsIgnoreCase("")) {
						Toast.makeText(getApplicationContext(), R.string.search_null, Toast.LENGTH_SHORT).show();
						return;
					}
					
					Intent intent = new Intent(SearchGate.this, SearchResult.class);
					Bundle bundle = new Bundle();
					bundle.putString("searchText", text);
					bundle.putString("type", null);
					intent.putExtras(bundle); 
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
						bundle.putString("type", getString(R.string.type_1));
						intent.putExtras(bundle); 
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
						bundle.putString("type", getString(R.string.type_2));
						intent.putExtras(bundle); 
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
						bundle.putString("type", getString(R.string.type_3));
						intent.putExtras(bundle); 
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
						bundle.putString("type", getString(R.string.type_4));
						intent.putExtras(bundle); 
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
						bundle.putString("type", getString(R.string.type_5));
						intent.putExtras(bundle); 
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
						bundle.putString("type", getString(R.string.type_6));
						intent.putExtras(bundle); 
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
						bundle.putString("type", getString(R.string.type_7));
						intent.putExtras(bundle); 
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
						bundle.putString("type", getString(R.string.type_8));
						intent.putExtras(bundle); 
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
						bundle.putString("type", getString(R.string.type_9));
						intent.putExtras(bundle); 
						startActivityForResult(intent, SEARCH_CODE);
					}
				}
			);
		
	}
	
	// callback function from other activities
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == SEARCH_CODE){
			if(resultCode == RESULT_CANCELED) {
				// DO NOTHING
			} else if (resultCode == RESULT_OK) {
				Intent intent=getIntent();
			    setResult(RESULT_OK, intent);
				finish(); 
			}
		}
	}
}
