package com.ernest.mobilecontextaware.activity;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;

public class ContextAwareApplicaiton extends Application{
	//private final static String TAG = "ContextAwareApplicaiton";
	
	private static final String KEY = "YUYjQfFGOZ9kGRGxra36iMXy";//发布YUYjQfFGOZ9kGRGxra36iMXy 本地：6GiyLBfm0GZGmrbVZahoZSqn
	private static ContextAwareApplicaiton mApplicationInstance;
    private BMapManager mBMapManager;
    private boolean mIsKeyValid = true;
    private Handler mLocationHandler, mWeatherHandler;
    
    @Override
    public void onCreate() {
    	super.onCreate();
    	mApplicationInstance = this;
    	initBMapManager(this);
    }
    
    public static ContextAwareApplicaiton GetInstance() {
		return mApplicationInstance;
	}
    
    public void setBMapManager(BMapManager bMapManager){ mBMapManager = bMapManager; }
    public BMapManager getBMapManager(){ return mBMapManager; }
    public String getKey(){ return KEY; }
    public void setLoactionHandler(Handler handler){ mLocationHandler = handler; }
    public Handler getLocationHandler(){ return mLocationHandler; }
//    public void setWeatherHandler(Handler handler){ mWeatherHandler = handler; }
//    public Handler getWeatherHandler(){ return mWeatherHandler; }
    
    private void initBMapManager(Context context){
    	if (mBMapManager == null) {
            mBMapManager = new BMapManager(context);
        }
        if (!mBMapManager.init(KEY, new MyMKGeneralListener())) {
            Toast.makeText(ContextAwareApplicaiton.GetInstance().getApplicationContext(), 
                    "BMapManager initial error!", Toast.LENGTH_LONG).show();
        }
    }
    
    //Process network error & authentication error
    public static class MyMKGeneralListener implements MKGeneralListener {
        @Override
        public void onGetNetworkState(int iError) {
            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
                Toast.makeText(ContextAwareApplicaiton.GetInstance().getApplicationContext(), 
                		"Network error!", Toast.LENGTH_LONG).show();
            }else if (iError == MKEvent.ERROR_NETWORK_DATA) {
                Toast.makeText(ContextAwareApplicaiton.GetInstance().getApplicationContext(), 
                		"Invalid input!", Toast.LENGTH_LONG).show();
            }
        }
        @Override
        public void onGetPermissionState(int iError) {
            if (iError != 0) {	//Key invalid
                Toast.makeText(ContextAwareApplicaiton.GetInstance().getApplicationContext(), 
                        "Please input correct key in ContextAwareApplicaiton.java, and check network state. Error: " 
                        + iError, Toast.LENGTH_LONG).show();
                ContextAwareApplicaiton.GetInstance().mIsKeyValid = false;
            }else{
            	ContextAwareApplicaiton.GetInstance().mIsKeyValid = true;
            	Toast.makeText(ContextAwareApplicaiton.GetInstance().getApplicationContext(), 
                        "Key is valid.", Toast.LENGTH_LONG).show();
            }
        }
    }
    
}
