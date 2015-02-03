package com.ernest.mobilecontextaware.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ernest.mobilecontextaware.activity.ContextAwareApplicaiton;
import com.ernest.mobilecontextaware.util.BaiduLocationUtil;
import com.ernest.mobilecontextaware.util.DateTimeUtil;
import com.ernest.mobilecontextaware.util.HttpUtil;
import com.ernest.mobilecontextaware.util.SensorsUtil;
import com.ernest.mobilecontextaware.util.SharedDataUtil;
import com.ernest.mobilecontextaware.util.WeatherWSUtil;

public class SendContextDataService extends IntentService{
	private static final String TAG = "SendContextDataService";
	
	private static final String LOCALHOST = "192.168.137.1:8080";	//Only for test, this is PC ip for android device access
	private static final String URL = "http://" + LOCALHOST + "/ContextAwareServer/addContext.action";//ContextServlet
	private static final String CLOUD_URL = "http://mobilecontextaware.duapp.com/addContext.action";
	private static final int EXECUTE_INTERVAL_MILL = 1000 * 60 * 5;	//5min
	private static final int SENSOR_DELAY_MILL = 600; //600ms 注销传感监听延迟
	
	private Context mContext;
	private BaiduLocationUtil mBaiduLocationUtil;
	private SensorsUtil mSensorsUtil;
	
	public SendContextDataService() { super("SendContextDataService"); }	//only for debug
		 
	@Override
	public void onCreate() {
		super.onCreate();
		
		mContext = ContextAwareApplicaiton.GetInstance().getApplicationContext();//equal to service's own context
		mBaiduLocationUtil = new BaiduLocationUtil(mContext);
		mSensorsUtil = new SensorsUtil(mContext);
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mBaiduLocationUtil.start();
		mSensorsUtil.registerAllSensorListener();
		
		return super.onStartCommand(intent, flags, startId);
	}
	@Override
	public void onDestroy() {
		mBaiduLocationUtil.stop();
		mSensorsUtil.unRegisterSensorListener();
		
		super.onDestroy();
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i(TAG, "onHandleIntent()");
		//ScheduledFuture<?> future;
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(4);
		executor.scheduleAtFixedRate(new Runnable() {	//1
			@Override
			public void run() {
				mSensorsUtil.registerAllSensorListener();
			}
		}, 0, EXECUTE_INTERVAL_MILL, TimeUnit.MILLISECONDS);
		executor.scheduleAtFixedRate(new Runnable() {	//2
			@Override
			public void run() {
				mSensorsUtil.unRegisterSensorListener();
			}
		}, SENSOR_DELAY_MILL, EXECUTE_INTERVAL_MILL, TimeUnit.MILLISECONDS);	//省电注销传感
		executor.scheduleAtFixedRate(new Runnable() {	//3
			@Override
			public void run() {
				//Update weather
				WeatherWSUtil.GetWeather(mContext, SharedDataUtil.GetCurDistrictOrCity(mContext));
				Log.i(TAG, "GetWeather()");
			}
		}, 60 * 10 , 60 * 60 * 3, TimeUnit.SECONDS);	//首次阻塞10min，间隔3h
		executor.scheduleAtFixedRate(new Runnable() {	//4
			@Override
			public void run() {
				//Update location
				mBaiduLocationUtil.start();
				mBaiduLocationUtil.requetLocation();
				Log.i(TAG, "requetLocation()");
				//Add current time
				String datetime = DateTimeUtil.GetCurDateTime();
				List<NameValuePair> params = SharedDataUtil.GetContextNameValuePairList(mContext);
				params.add(new BasicNameValuePair("datetime", datetime));
				//Post context data
				String result = null;
				try {
					result = HttpUtil.Post(CLOUD_URL, params);
					Log.i(TAG, "Response result: " + result);
				} catch (Exception e) { e.printStackTrace(); }
			}
		}, 5000, EXECUTE_INTERVAL_MILL, TimeUnit.MILLISECONDS);	//延迟5s
	}

}