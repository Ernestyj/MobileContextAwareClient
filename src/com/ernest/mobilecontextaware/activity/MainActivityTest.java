package com.ernest.mobilecontextaware.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ernest.mobilecontextaware.R;
import com.ernest.mobilecontextaware.service.SendContextDataService;
import com.ernest.mobilecontextaware.service.SendUserInfoDataService;
import com.ernest.mobilecontextaware.util.SharedDataUtil;

public class MainActivityTest extends ListActivity {
	private final static String TAG = "MainActivityTest";
	
	private ListView mListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "OnCreate() start");
		
		setListAdapter(new ArrayAdapter<String>(MainActivityTest.this, R.layout.text_list_item, 
				getResources().getStringArray(R.array.activity_strings)));
		mListView = getListView();
		mListView.setTextFilterEnabled(true);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id){
				switch (position) {
					case 0:
						startActivity(new Intent(MainActivityTest.this, BaiduMapTest.class));
						break;
					case 1:
						startActivity(new Intent(MainActivityTest.this, SharedDataSensorsWeatherTest.class));
						break;
					case 2:
						break;
					case 3:
						break;
					case 4:
						break;
					case 5:
						break;
					case 6:
						break;
					default:
						break;
				}
			}
		});
		
		if(null == SharedDataUtil.GetIMEI(getApplicationContext())){
			SharedDataUtil.WriteIMEI(getApplicationContext());
			SharedDataUtil.WriteDefaultUserInfo(getApplicationContext());
		}
		
		startService(new Intent(this, SendUserInfoDataService.class));
		startService(new Intent(this, SendContextDataService.class));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_activity_test, menu);
		return true;
	}

}
