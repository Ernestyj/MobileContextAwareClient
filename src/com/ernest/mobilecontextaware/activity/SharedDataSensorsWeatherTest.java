package com.ernest.mobilecontextaware.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ernest.mobilecontextaware.R;
import com.ernest.mobilecontextaware.util.SensorsUtil;
import com.ernest.mobilecontextaware.util.SharedDataUtil;
import com.ernest.mobilecontextaware.util.WeatherWSUtil;

public class SharedDataSensorsWeatherTest extends Activity{
	private final static String TAG = "SharedDataAndWeatherTest";
		
//	private ContextAwareApplicaiton mApp;
	private SensorsUtil mSensorsUtil;
	private Button mReadButton, mWeatherButton;
	private TextView mTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "OnCreate() start");
		
//		mApp = (ContextAwareApplicaiton)getApplication();
//		mApp.setWeatherHandler(new Handler(){
//			@Override
//			public void handleMessage(Message msg) {
//				if(msg.what == WeatherWSUtil.NEW_WEATHER_MSG){
//					String weatherCurrent = msg.getData().getString(WeatherWSUtil.WEATHER_CUR);
//					String weatherToday = msg.getData().getString(WeatherWSUtil.WEATHER_TODAY);
//					SharedDataUtil.WriteCurWeatherInfos(getApplicationContext(), weatherCurrent, weatherToday);
//				}
//			}
//		});
		
		setContentView(R.layout.shareddatasensorsweathertest_main);
		mTextView = (TextView)findViewById(R.id.dataview);
		mReadButton = (Button)findViewById(R.id.readbutton);
		mReadButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mTextView.setText(SharedDataUtil.ReadSharedData(getApplicationContext()));
			}
		});
		mWeatherButton = (Button)findViewById(R.id.weatherbutton);
		mWeatherButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				WeatherWSUtil.GetWeather(getApplicationContext(), SharedDataUtil.GetCurDistrictOrCity(getApplicationContext()));
				mTextView.setText(SharedDataUtil.ReadSharedData(getApplicationContext()));
			}
		});
		
		mSensorsUtil = new SensorsUtil(getApplicationContext());
		mSensorsUtil.registerAllSensorListener();
		
		boolean isSuccess = WeatherWSUtil.GetWeather(getApplicationContext(), SharedDataUtil.GetCurDistrictOrCity(getApplicationContext()));
		if(isSuccess){
			Toast.makeText(getApplicationContext(), "成功获取当前天气信息", Toast.LENGTH_LONG).show();
		}else{
			Toast.makeText(getApplicationContext(), "获取当前天气信息失败", Toast.LENGTH_LONG).show();
		}
		mTextView.setText(SharedDataUtil.ReadSharedData(getApplicationContext()));
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mSensorsUtil.registerAllSensorListener();
	}

	@Override
	protected void onPause() {
		mSensorsUtil.unRegisterSensorListener();
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		mSensorsUtil.unRegisterSensorListener();
		super.onStop();
	}
	
}
