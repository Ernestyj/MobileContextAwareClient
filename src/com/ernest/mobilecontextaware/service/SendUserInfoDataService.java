package com.ernest.mobilecontextaware.service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.ernest.mobilecontextaware.util.HttpUtil;
import com.ernest.mobilecontextaware.util.SharedDataUtil;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class SendUserInfoDataService extends IntentService{
	private static final String TAG = "SendUserInfoDataService";
	
	private static final String LOCALHOST = "192.168.137.1:8080";	//Only for test, this is PC ip for android device access
	private static final String URL = "http://" + LOCALHOST + "/ContextAwareServer/addUser.action";
	private static final String CLOUD_URL = "http://mobilecontextaware.duapp.com/addUser.action";
	
	public SendUserInfoDataService() { super("SendUserInfoDataService"); }	//only for debug

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i(TAG, "onHandleIntent()");
		
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
		final ScheduledFuture<?> future = executor.scheduleAtFixedRate(new Runnable() {	//1
			@Override
			public void run() {
				String result = null;
				try {
					result = HttpUtil.Post(CLOUD_URL, SharedDataUtil.GetUserInfoNameValuePairList(getApplicationContext()));
					Log.i(TAG, "Response result: " + result);
				} catch (Exception e) { e.printStackTrace(); }
			}
		}, 3, 15, TimeUnit.SECONDS); //每15s发送一次，执行3次
		executor.schedule(new Runnable() {	//2
			@Override
			public void run() {
				if(null != future) future.cancel(false);
			}
		}, 45, TimeUnit.SECONDS);	//45s后终止前一线程
	}

}
