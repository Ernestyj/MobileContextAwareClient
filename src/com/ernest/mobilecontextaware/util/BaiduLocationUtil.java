package com.ernest.mobilecontextaware.util;

import android.content.Context;
import android.os.Handler;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.ernest.mobilecontextaware.activity.ContextAwareApplicaiton;

public class BaiduLocationUtil {
	private final static String TAG = "BaiduLocationUtil";
	
	public final static int NEW_LOC_MSG = 0x100;
	private final static String COORTYPE = "bd09ll";//Baidu encoded latitude & longtitude
	private final static int REQ_INTERVAL = 1000 * 60 * 5;//5min
	private final static int MAX_POI_NUM = 10;
	private final static float POI_DISDANCE = 5000f;
	
	private LocationClient mLocationClient;
	private LocationClientOption mOption;
	private BDLocation mCurLocation;
	
	public BaiduLocationUtil(Context context){
		mLocationClient = new LocationClient(context);
		mOption = new LocationClientOption();
		initLocationOption();
		mLocationClient.setLocOption(mOption);
		mLocationClient.registerLocationListener(new BDLocationListener() {
			@Override
			public void onReceiveLocation(BDLocation location) {
				if(null == location) return;
				mCurLocation = location;
				SharedDataUtil.WriteCurLocationInfos(ContextAwareApplicaiton.GetInstance().getApplicationContext(), location);
				//Log.i(TAG, "WriteCurLocationInfos() done.");
				Handler handler = ContextAwareApplicaiton.GetInstance().getLocationHandler();
				if(null != handler) handler.sendEmptyMessage(NEW_LOC_MSG);
			}
			@Override
			public void onReceivePoi(BDLocation poiLocation) { }
		});
		mLocationClient.start();
		if (mLocationClient != null && mLocationClient.isStarted()){
			mLocationClient.requestLocation();
		}
	}
	
	public int requetLocation(){
		return mLocationClient.requestLocation();
	}
	
	public BDLocation getCurLocation(){ return mCurLocation; }
	
	public void start(){
		if(null != mLocationClient) mLocationClient.start(); 
	}
	
	public void stop(){
		if(null != mLocationClient) mLocationClient.stop();
	}
	
	public static String GetSourceType(int locType){
		switch (locType) {
		case BDLocation.TypeGpsLocation: //61
			return "GPS";
		case BDLocation.TypeNetWorkLocation: //161
			return "Network";
		case BDLocation.OPERATORS_TYPE_MOBILE:
			return "Mobile";
		case BDLocation.OPERATORS_TYPE_UNICOM:
			return "Unicom";
		case BDLocation.OPERATORS_TYPE_TELECOMU:
			return "Telecomu";
		case BDLocation.OPERATORS_TYPE_UNKONW:
			return "Unknown";
		default:
			return "Others";
		}
	}
	
	private void initLocationOption(){
		mOption.setLocationMode(LocationMode.Hight_Accuracy);//GPS + Network locating
		mOption.setOpenGps(true);
		mOption.setAddrType("all");//locating results include all address infos
		mOption.setCoorType(COORTYPE);
		mOption.setScanSpan(REQ_INTERVAL);
		mOption.setPoiNumber(MAX_POI_NUM);
		mOption.setPoiDistance(POI_DISDANCE);
		mOption.setPoiExtraInfo(true); 	//include detailed infos of POI, such as PhoneNum, Address
		mOption.setIsNeedAddress(true); 	//include address infos
		mOption.setNeedDeviceDirect(true);	//include phone head direction
	}

}
